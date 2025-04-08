package com.peacemall.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.api.client.ProductClient;
import com.peacemall.api.client.ShopClient;
import com.peacemall.api.client.UserClient;
import com.peacemall.api.client.WalletClient;
import com.peacemall.common.constant.*;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.*;
import com.peacemall.common.domain.vo.ShopsInfoVO;
import com.peacemall.common.enums.UserRole;
import com.peacemall.common.enums.WalletFlowType;
import com.peacemall.common.utils.RabbitMqHelper;
import com.peacemall.common.utils.UserContext;
import com.peacemall.order.domain.dto.OrderDetailsProductInfoDTO;
import com.peacemall.order.domain.po.OrderDetails;
import com.peacemall.order.domain.po.Orders;
import com.peacemall.order.domain.vo.OrderDetailsVO;
import com.peacemall.order.domain.vo.OrdersHistoryVO;
import com.peacemall.order.enums.OrderStatus;
import com.peacemall.order.enums.ReturnStatus;
import com.peacemall.order.mapper.OrdersMapper;
import com.peacemall.order.service.OrderDetailsService;
import com.peacemall.order.service.OrdersService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    private final OrderDetailsService orderDetailsService;

    private final ProductClient productClient;

    private final RabbitMqHelper rabbitMqHelper;

    private final UserClient userClient;

    private final WalletClient walletClient;

    private final ShopClient shopClient;



    /**
     * 用户批量购买商品
     * @param purchaseDTO 包含用户id，商品list，地址id
     * @return R<String>
     * @author watergun
     */

    @Override
    @GlobalTransactional
    //todo 优惠券考虑
    //todo Redis + Token解决业务幂等问题
    public R<String> createOrders(PurchaseDTO purchaseDTO) {
        log.info("createOrders: {}", purchaseDTO);
        if (purchaseDTO == null){
            log.error("createOrders: purchaseDTO is null");
            return R.error("参数为空");
        }

        //校验用户信息
        Long userId = UserContext.getUserId();
        if (userId == null){
            log.error("createOrders: userId is null");
            return R.error("用户未登录");
        }
        if (!userId.equals(purchaseDTO.getUserId())){
            log.error("createOrders: userId is not match");
            return R.error("用户信息不匹配");
        }

        //查看用户是否有购买物品
        List<PurchaseItem> items = purchaseDTO.getItems();
        if (items == null || items.size() == 0){
            log.error("createOrders: items is null");
            return R.error("商品信息为空");
        }

        //得到所有的productId
        List<Long> productIds = items.stream().map(PurchaseItem::getProductId).collect(Collectors.toList());

        //得到所有的configId，以及商品的数量的Map关系
        Map<Long,Integer> configIdAndPurchaseNum = items.stream().collect(Collectors.toMap(PurchaseItem::getConfigId, PurchaseItem::getQuantity));

        //得到所有configId
        List<Long> configIds = new ArrayList<>(configIdAndPurchaseNum.keySet());

        //根据productId 和 configId得到商品信息
        Map<Long, ProductDetailsDTO> detailsByIds = productClient.getProductDetailsByIds(productIds, configIds);


        //根据商家id对产品进行分组
        Map<Long, List<ProductDetailsDTO>> shopIdAndProductDetails =
                detailsByIds.values().stream().collect(Collectors.groupingBy(ProductDetailsDTO::getShopId));

        // 创建一个Map,用于储存库存变化的信息，后续通过RabbitMQ或者是Feign接口进行库存的扣减
        Map<Long,Integer> configIdAndStockChange = new ConcurrentHashMap<>();

        // 遍历每个商家的产品，生成各自的订单
        for (Map.Entry<Long, List<ProductDetailsDTO>> entry : shopIdAndProductDetails.entrySet()) {
            //商家id
            Long shopId = entry.getKey();
            //该商家的订单信息
            Map<Long, Integer> currentConfigIdAndQuantityMap = new HashMap<>();
            //储存该订单下要保存的订单详情
            List<OrderDetails> orderDetailsList = new ArrayList<>();
            log.info("shopId: {}", shopId);
            List<ProductDetailsDTO> productDetails = entry.getValue();
            log.info("productDetails: {}", productDetails);
            // 验证库存并计算商家订单的总价
            // 检查商品是否属于可购买状态
            BigDecimal totalPrice = BigDecimal.ZERO;
            for (ProductDetailsDTO productDetail : productDetails) {
                //得到当前这个商品的id
                Long productId = productDetail.getProductId();

                //得到当前商品的配置列表
                List<ProductConfigurationDTO> configurations = productDetail.getConfigurations();
                //遍历配置列表，储存要保存的订单详情信息
                for (ProductConfigurationDTO configuration : configurations) {
                    //得到当前配置的id
                    Long configId = configuration.getConfigId();
                    log.info("configId: {}", configId);
                    //得到当前配置的库存
                    Integer stock = configuration.getStock();
                    log.info("stock: {}", stock);
                    //得到当前配置的购买数量
                    Integer purchaseNum = configIdAndPurchaseNum.get(configId);
                    log.info("purchaseNum: {}", purchaseNum);
                    //判断当前的purchaseNum购买数量是否非法
                    if (purchaseNum == null || purchaseNum <= 0){
                        log.error("createOrders: purchaseNum is illegal");
                        return R.error("购买数量非法");
                    }
                    //判断库存是否足够
                    if (stock < purchaseNum) {
                        log.error("createOrders: stock is not enough");
                        return R.error("库存不足");
                    }

                    //计算总价
                    totalPrice = totalPrice.add(configuration.getPrice().multiply(BigDecimal.valueOf(purchaseNum)));
                    log.info("currentPrice: {}", totalPrice);
                    //储藏商品详情的信息
                    OrderDetails orderDetails = new OrderDetails();
                    orderDetails.setProductId(productId);
                    orderDetails.setConfigId(configId);
                    orderDetails.setQuantity(purchaseNum);
                    orderDetails.setPrice(configuration.getPrice());
                    orderDetailsList.add(orderDetails);
                    log.info("orderDetails{}",orderDetails);

                    //扣减商品库存Map的创建
                    configIdAndStockChange.compute(configId, (key, value) -> (value == null) ? -purchaseNum : value - purchaseNum);
                    //储存当前订单的信息
                    currentConfigIdAndQuantityMap.compute(configId, (key, value) -> (value == null) ? -purchaseNum : value - purchaseNum);

                    log.info("configIdAndStockChange: {}", configIdAndStockChange);
                }
            }
            //创建这个商家的订单
            Orders orders = new Orders();
            orders.setUserId(userId);
            orders.setShopId(shopId);
            orders.setAddressId(purchaseDTO.getAddressId());
            //设置订单的原始金额
            //todo 实付金额在支付方法中实现对优惠券的调用
            orders.setOriginalAmount(totalPrice);
            orders.setTotalAmount(totalPrice);

            orders.setStatus(OrderStatus.PENDING_PAYMENT);//待支付
            orders.setReturnStatus(ReturnStatus.NOT_REQUESTED);
            boolean save = this.save(orders);
            if (!save) {
                log.error("createOrders: save orders failed");
                throw new RuntimeException("创建订单失败");
            }
            //订单详情设置订单ID
            Long orderId = orders.getOrderId();
            for (OrderDetails orderDetail : orderDetailsList) {
                orderDetail.setOrderId(orderId);
            }
            //保存当前订单的订单详情
            boolean savedBatch = orderDetailsService.saveBatch(orderDetailsList);
            if (!savedBatch) {
                log.error("createOrders: save order details failed");
                throw new RuntimeException("保存订单详情失败");
            }

            //发送RabbitMq的延迟消息,用来确认订单是否支付，是否要进行库存回滚
            OrderStockMessageDTO orderStockMessageDTO = new OrderStockMessageDTO();
            orderStockMessageDTO.setOrderId(orders.getOrderId());
            orderStockMessageDTO.setStockChangeMap(currentConfigIdAndQuantityMap);
            //发送RabbitMq的延迟消息
            String jsonMessage = JSONUtil.toJsonStr(orderStockMessageDTO);

            rabbitMqHelper.sendDelayMessage(
                    OrderListenerMQConstant.ORDER_DELAY_DIRECT_EXCHANGE,
                    OrderListenerMQConstant.ORDER_DELAY_PAYMENT_STATUS_CHECK_ROUTING_KEY,
                    jsonMessage,
                    600000
            );//延迟10分钟
            //todo 后续延迟要变为10分钟
        }

        // 通过RabbitMQ发送消息,将商品从购物车中删除
        // 根据configIds和userId来进行删除
        log.info("发送购物车删除消息");
        sendDeleteCartMessage(configIds, userId);
        log.info("发送完毕");

        log.info("【库存扣减】开始: {}", configIdAndStockChange);
        //调用openfeign同步扣减库存
        productClient.updateProductConfigurationsQuantity(configIdAndStockChange);
        log.info("【库存扣减】结束: {}", configIdAndStockChange);

        return R.ok("创建订单成功");
    }

    /**
     * 根据订单ID获取订单详情
     * @param orderId 订单ID
     * @return 订单详情信息
     */
    @Override
    public R<OrderDetailsVO> getOrderDetailsById(Long orderId) {
        log.info("查询订单详情, 订单ID: {}", orderId);
        if(orderId == null){
            log.error("订单ID为空");
            return R.error("订单ID为空");
        }

        Long userId = UserContext.getUserId();
        if(userId == null){
            log.error("用户未登录");
            return R.error("用户未登录");
        }

        // 查询订单基本信息
        Orders orders = this.getById(orderId);
        if (orders == null) {
            log.error("订单不存在, 订单ID: {}", orderId);
            return R.error("订单不存在");
        }

        // 复制订单基本信息
        OrderDetailsVO orderDetailsVO = new OrderDetailsVO();
        BeanUtil.copyProperties(orders, orderDetailsVO);

        // 查询订单详情列表
        List<OrderDetails> orderDetailsList = orderDetailsService.getOrderDetailsByOrderId(orderId);
        if (orderDetailsList.isEmpty()) {
            log.warn("订单详情为空, 订单ID: {}", orderId);
            return R.ok(orderDetailsVO);
        }

        // 获取所有商品 ID 和商品配置 ID
        List<Long> productIdList = orderDetailsList.stream()
                .map(OrderDetails::getProductId)
                .distinct()
                .collect(Collectors.toList());

        List<Long> configIdList = orderDetailsList.stream()
                .map(OrderDetails::getConfigId)
                .distinct()
                .collect(Collectors.toList());

        // 远程调用查询商品详情
        Map<Long, ProductDetailsDTO> productDetailsMap = productClient.getProductDetailsByIds(productIdList, configIdList);

        // 得到任意一个productDetailsDTO
        ProductDetailsDTO anyValue = productDetailsMap.values().iterator().next();
        //设置店铺名称
        orderDetailsVO.setShopName(anyValue.getShopName());

        // 组装商品配置 Map
        Map<Long, String> configDetailsMap = productDetailsMap.values().stream()
                .flatMap(product -> product.getConfigurations().stream())
                .collect(Collectors.toMap(ProductConfigurationDTO::getConfigId, ProductConfigurationDTO::getConfiguration));

        // 组装订单详情商品信息列表
        List<OrderDetailsProductInfoDTO> orderProductList = orderDetailsList.stream().map(orderDetail -> {
            OrderDetailsProductInfoDTO productInfoDTO = new OrderDetailsProductInfoDTO();
            BeanUtil.copyProperties(orderDetail, productInfoDTO);

            ProductDetailsDTO productDetailsDTO = productDetailsMap.get(orderDetail.getProductId());
            if (productDetailsDTO != null) {
                productInfoDTO.setName(productDetailsDTO.getName());
                productInfoDTO.setUrl(productDetailsDTO.getUrl());
                productInfoDTO.setBrand(productDetailsDTO.getBrand());
            }

            // 设置商品配置详情
            productInfoDTO.setConfiguration(configDetailsMap.get(orderDetail.getConfigId()));

            return productInfoDTO;
        }).collect(Collectors.toList());

        // 设置订单详情的商品信息
        orderDetailsVO.setOrderItemsList(orderProductList);

        //查询地址信息
        UserAddressDTO addressById = userClient.getUserAddressById(orders.getAddressId());
        orderDetailsVO.setUserAddressDTO(addressById);

        return R.ok(orderDetailsVO);
    }

    @Override
    public R<PageDTO<OrdersHistoryVO>> getOrderHistoryList(int page, int pageSize) {
        log.info("查询订单历史, page: {}, pageSize: {}", page, pageSize);
        if(page <= 0 || pageSize <= 0){
            log.error("分页参数不合法");
            return R.error("分页参数不合法");
        }
        Long userId = UserContext.getUserId();
        if(userId == null){
            log.error("用户未登录");
            return R.error("用户未登录");
        }
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,userId)
                .orderByDesc(Orders::getCreatedAt);
        this.page(ordersPage, queryWrapper);
        List<Orders> ordersList = ordersPage.getRecords();
        Map<Long,Orders> ordersMap = ordersList.stream().collect(Collectors.toMap(Orders::getOrderId, Function.identity()));
        if(ordersList.isEmpty()){
            log.warn("订单历史为空");
            return R.ok(new PageDTO<>());
        }
        List<OrdersHistoryVO> ordersHistoryVOList = new ArrayList<>();
        List<Long> orderIdList = ordersList.stream().map(Orders::getOrderId).collect(Collectors.toList());
        for (Long orderId : orderIdList) {
            log.info("订单id: {}",orderId);
            //todo
            // 查询订单详情列表
            List<OrderDetails> orderDetailsList = orderDetailsService.getOrderDetailsByOrderId(orderId);
            if (orderDetailsList.isEmpty()) {
                log.warn("订单详情为空, 订单ID: {}", orderId);
                continue;
            }

            OrdersHistoryVO ordersHistoryVO = new OrdersHistoryVO();
            BeanUtil.copyProperties(ordersMap.get(orderId),ordersHistoryVO);


            // 获取所有商品 ID 和商品配置 ID
            List<Long> productIdList = orderDetailsList.stream()
                    .map(OrderDetails::getProductId)
                    .distinct()
                    .collect(Collectors.toList());

            List<Long> configIdList = orderDetailsList.stream()
                    .map(OrderDetails::getConfigId)
                    .distinct()
                    .collect(Collectors.toList());

            // 远程调用查询商品详情
            Map<Long, ProductDetailsDTO> productDetailsMap = productClient.getProductDetailsByIds(productIdList, configIdList);

            // 得到任意一个productDetailsDTO
            ProductDetailsDTO anyValue = productDetailsMap.values().iterator().next();
            //设置店铺名称
            ordersHistoryVO.setShopName(anyValue.getShopName());

            // 组装商品配置 Map
            Map<Long, String> configDetailsMap = productDetailsMap.values().stream()
                    .flatMap(product -> product.getConfigurations().stream())
                    .collect(Collectors.toMap(ProductConfigurationDTO::getConfigId, ProductConfigurationDTO::getConfiguration));

            // 组装订单详情商品信息列表
            List<OrderDetailsProductInfoDTO> orderProductList = orderDetailsList.stream().map(orderDetail -> {
                OrderDetailsProductInfoDTO productInfoDTO = new OrderDetailsProductInfoDTO();
                BeanUtil.copyProperties(orderDetail, productInfoDTO);

                ProductDetailsDTO productDetailsDTO = productDetailsMap.get(orderDetail.getProductId());
                if (productDetailsDTO != null) {
                    productInfoDTO.setName(productDetailsDTO.getName());
                    productInfoDTO.setUrl(productDetailsDTO.getUrl());
                    productInfoDTO.setBrand(productDetailsDTO.getBrand());
                }

                // 设置商品配置详情
                productInfoDTO.setConfiguration(configDetailsMap.get(orderDetail.getConfigId()));

                return productInfoDTO;
            }).collect(Collectors.toList());

            // 设置订单详情的商品信息
            ordersHistoryVO.setOrderItemsList(orderProductList);

            ordersHistoryVOList.add(ordersHistoryVO);
        }
        PageDTO<OrdersHistoryVO> ordersHistoryVOPage = new PageDTO<>();
        ordersHistoryVOPage.setList(ordersHistoryVOList);
        ordersHistoryVOPage.setPages(ordersPage.getPages());
        ordersHistoryVOPage.setTotal(ordersPage.getTotal());
        return R.ok(ordersHistoryVOPage);
    }

    @Override
    @GlobalTransactional
    public R<String> cancelOrder(Long orderId) {
        log.info("取消订单, 订单ID: {}", orderId);
        if(orderId == null){
            log.warn("订单ID为空");
            return R.error("订单ID为空");
        }
        Long userId = UserContext.getUserId();
        if(userId == null){
            log.warn("用户ID为空");
            return R.error("用户未登录");
        }
        Orders orders = this.getById(orderId);
        if(orders == null){
            log.warn("订单不存在, 订单ID: {}", orderId);
            return R.error("订单不存在");
        }
        if(!orders.getUserId().equals(userId)){
            log.warn("用户ID与订单用户ID不匹配, 用户ID: {}, 订单ID: {}", userId, orderId);
            return R.error("订单不匹配");
        }
        if(orders.getStatus() != OrderStatus.PENDING_PAYMENT && orders.getStatus() != OrderStatus.PENDING){
            log.warn("订单状态不正确, 订单ID: {}, 状态: {}", orderId, orders.getStatus());
            return R.error("订单状态不正确，不能够取消");
        }
        OrderStatus oldStatus = orders.getStatus();
        orders.setStatus(OrderStatus.CANCELLED);
        boolean update = this.updateById(orders);
        if(!update){
            log.warn("取消订单失败, 订单ID: {}", orderId);
            return R.error("取消订单失败");
        }
        // 发送库存回退消息消息
        // 组成Map<Long,Integer> configIdAndQuantityMap的关系
        // 查询所有orderDetails
        List<OrderDetails> orderDetailsByOrderId = orderDetailsService.getOrderDetailsByOrderId(orderId);
        Map<Long,Integer> configIdAndQuantityMap = orderDetailsByOrderId.stream()
                .collect(Collectors.toMap(OrderDetails::getConfigId, OrderDetails::getQuantity));
        String stockMsg = JSONUtil.toJsonStr(configIdAndQuantityMap);
        rabbitMqHelper.sendMessage(ProductConfigurationMQConstant.PRODUCT_CONFIGURATION_EXCHANGE,
                ProductConfigurationMQConstant.PRODUCT_CONFIGURATION_UPDATE_ROUTING_KEY,stockMsg);
        log.info("发送库存回退消息成功, 订单ID: {}", orderId);
        log.info("Orderstatus:{}",orders.getStatus());
        // 如果用户是在支付状态下取消订单的
        if ( OrderStatus.PENDING.equals(oldStatus)) {
            // 余额需要回退给用户
            WalletAmountChangeDTO userWalletChange = new WalletAmountChangeDTO();
            userWalletChange.setId(orders.getUserId());
            userWalletChange.setChangeAmount(orders.getTotalAmount());
            userWalletChange.setWalletFlowType(WalletFlowType.REFUND);
            userWalletChange.setRelatedOrderId(orderId);
            String userPendingBalanceChangeMsg = JSONUtil.toJsonStr(userWalletChange);
            rabbitMqHelper.sendMessage(WallerMQConstant.WALLET_EXCHANGE,
                    WallerMQConstant.WALLET_UPDATE_PENDING_BALANCE_BY_USERID_ROUTING_KEY,userPendingBalanceChangeMsg);
            log.info("发送用户余额回退消息成功, 订单ID: {}", orderId);
            log.info("userWalletChange: {}", userWalletChange);
            // 商家的待处理金额需要扣减
            ShopsInfoVO shopsInfoVO = shopClient.getShopInfoById(orders.getShopId());
            WalletAmountChangeDTO merchantWalletChange = new WalletAmountChangeDTO();
            merchantWalletChange.setId(shopsInfoVO.getUserId());
            merchantWalletChange.setChangeAmount(orders.getTotalAmount().negate());
            merchantWalletChange.setWalletFlowType(WalletFlowType.REFUND);
            merchantWalletChange.setRelatedOrderId(orderId);
            String shopPendingBalanceChangeMsg = JSONUtil.toJsonStr(merchantWalletChange);
            rabbitMqHelper.sendMessage(WallerMQConstant.WALLET_EXCHANGE,
                    WallerMQConstant.WALLET_UPDATE_PENDING_BALANCE_BY_USERID_ROUTING_KEY,shopPendingBalanceChangeMsg);
            log.info("merchantWalletChange: {}", merchantWalletChange);
        }

        return R.ok("取消订单成功");
    }

    /**
     * 用户支付订单
     * @param orderId 订单ID
     * @return String 是否成功的信息
     * @author watergun
     */

    @Override
    @GlobalTransactional
    public R<String> payOrder(Long orderId) {
        log.info("支付订单, 订单ID: {}", orderId);
        if(orderId == null){
            log.warn("订单ID为空");
            return R.error("订单ID为空");
        }
        Long userId = UserContext.getUserId();
        if(userId == null){
            log.warn("用户ID为空");
            return R.error("用户未登录");
        }
        //获取订单的信息
        Orders orders = this.getById(orderId);
        if(orders == null){
            log.warn("订单不存在, 订单ID: {}", orderId);
            return R.error("订单不存在");
        }
        if(!orders.getUserId().equals(userId)){
            log.warn("用户ID与订单用户ID不匹配, 用户ID: {}, 订单ID: {}", userId, orderId);
            return R.error("订单不匹配");
        }
        //查看订单是否在待支付状态
        if(orders.getStatus() != OrderStatus.PENDING_PAYMENT){
            log.warn("订单状态不正确, 订单ID: {}, 状态: {}", orderId, orders.getStatus());
            return R.error("订单状态不正确，不能够支付");
        }
        //todo 查看用户是否使用了优惠券

        //openfeign调用waller-service来进行支付
        try{
            walletClient.userPay(orders.getTotalAmount(),orderId);
        }catch (Exception e){
            log.error("用户支付失败, 订单ID: {}, 用户ID: {}, 异常: {}", orderId, userId, e.getMessage());
            throw new RuntimeException("用户支付失败");
        }

        //商家的待确认金额要增加
        ShopsInfoVO shopsInfoVO = shopClient.getShopInfoById(orders.getShopId());
        WalletAmountChangeDTO walletAmountChangeDTO = new WalletAmountChangeDTO();
        walletAmountChangeDTO.setId(shopsInfoVO.getUserId());
        walletAmountChangeDTO.setChangeAmount(orders.getTotalAmount());
        walletAmountChangeDTO.setRelatedOrderId(orderId);
        walletAmountChangeDTO.setWalletFlowType(WalletFlowType.EXPENSE);
        walletClient.userWalletPendingAmountChange(walletAmountChangeDTO);
        log.info("商家待确认金额增加成功, 订单ID: {}, 用户ID: {}", orderId, userId);
        orders.setStatus(OrderStatus.PENDING);
        boolean update = this.updateById(orders);
        if (!update) {
            log.warn("支付订单失败, 订单ID: {}", orderId);
            throw new RuntimeException("支付订单失败");
        }
        return R.ok("支付订单成功");
    }

    /**
     * 用户删除订单
     * @param orderId 订单ID
     * @return String 是否成功的信息
     * @author watergun
     */

    @Override
    @Transactional
    public R<String> deleteOrder(Long orderId) {
        log.info("删除订单, 订单ID: {}", orderId);
        if(orderId == null){
            log.warn("订单ID为空");
            return R.error("订单ID为空");
        }
        Long userId = UserContext.getUserId();
        if(userId == null){
            log.warn("用户ID为空");
            return R.error("用户未登录");
        }
        //获取订单的信息
        Orders orders = this.getById(orderId);
        if(orders == null){
            log.warn("订单不存在, 订单ID: {}", orderId);
            return R.error("订单不存在");
        }
        if(!orders.getUserId().equals(userId)){
            log.warn("用户ID与订单用户ID不匹配, 用户ID: {}, 订单ID: {}", userId, orderId);
            return R.error("订单不匹配");
        }
        //查看订单是否为可以删除的状态
        //先检查这是否为申请了退货的订单
        if (orders.getReturnStatus().equals(ReturnStatus.NOT_REQUESTED)){
            //查看订单是否为可以删除的状态
            //只有取消的订单或者完成的订单可以删除
            if(!orders.getStatus().equals(OrderStatus.CANCELLED) && !orders.getStatus().equals( OrderStatus.RECEIVED)){
                log.warn("订单状态不正确, 订单ID: {}, 状态: {}", orderId, orders.getStatus());
                return R.error("订单状态不正确，不能够删除,否则会影响售后权益");
            }
        }else {
        //申请了退货的订单，只有退货完成的订单可以删除
            if(!orders.getReturnStatus().equals(ReturnStatus.RETURNED)){
                log.warn("订单状态不正确, 订单ID: {}, 状态: {}", orderId, orders.getStatus());
                return R.error("订单状态不正确，不能够删除,否则会影响售后权益");
            }
        }
        //先删除订单详情信息
        boolean b = orderDetailsService.deleteOrderDetailsByOrderId(orderId);
        if(!b){
            log.warn("删除订单详情失败, 订单ID: {}", orderId);
            return R.error("删除订单详情失败");
        }

        //可以开始删除订单
        boolean remove = this.removeById(orderId);
        if(!remove){
            log.warn("删除订单失败, 订单ID: {}", orderId);
            return R.error("删除订单失败");
        }
        return R.ok("删除订单成功");
    }

    /**
     * 用户申请退货
     * @param orderId 订单ID
     * @return R<String> 是否成功的信息
     * @author watergun
     */
    @Override
    public R<String> userApplyForReturn(Long orderId) {
        log.info("用户申请退货, 订单ID: {}", orderId);
        if(orderId == null){
            log.warn("订单ID为空");
            return R.error("订单ID为空");
        }
        Long userId = UserContext.getUserId();
        if(userId == null){
            log.warn("用户ID为空");
            return R.error("用户未登录");
        }
        //获取订单的信息
        Orders orders = this.getById(orderId);
        if(orders == null){
            log.warn("订单不存在, 订单ID: {}", orderId);
            return R.error("订单不存在");
        }
        if(!orders.getUserId().equals(userId)){
            log.warn("用户ID与订单用户ID不匹配, 用户ID: {}, 订单ID: {}", userId, orderId);
            return R.error("订单不匹配");
        }
        //查看订单是否为可以退货的状态
        if(!orders.getStatus().equals(OrderStatus.RECEIVED)){
            log.warn("订单状态不正确, 订单ID: {}, 状态: {}", orderId, orders.getStatus());
            return R.error("请先收货再申请退货");
        }
        //可以开始申请退货
        orders.setReturnStatus(ReturnStatus.REQUESTED);
        boolean update = this.updateById(orders);
        if (!update) {
            log.warn("申请退货失败, 订单ID: {}", orderId);
            return R.error("申请退货失败，请重试");
        }
        return R.ok("申请退货成功");
    }

    @Override
    public R<String> merchantAuditReturnApplication(Long orderId, ReturnStatus returnStatus) {
        log.info("商家审核退货申请, 订单ID: {}, 状态: {}", orderId, returnStatus);
        Long userId = UserContext.getUserId();
        String currentUserRole = UserContext.getUserRole();
        if(userId == null && !UserRole.MERCHANT.name().equals(currentUserRole)){
            log.warn("用户ID为空或者用户角色不是商家, 用户ID: {}, 用户角色: {}", userId, currentUserRole);
            return R.error("用户未登录或者用户角色不是商家");
        }
        if(orderId == null){
            log.warn("订单ID为空");
            return R.error("订单ID为空");
        }
        if(!ReturnStatus.APPROVED.equals(returnStatus)&&!ReturnStatus.REJECTED.equals(returnStatus)){
            log.warn("商家设置退货状态不正确, 状态: {}", returnStatus);
            return R.error("商家设置退货状态不正确");
        }

        //开始处理订单申请
        //获取该订单详情
        Orders orders = this.getById(orderId);

        //获得当前商家信息
        ShopsInfoVO shopsInfoVO = shopClient.getShopInfoById(orders.getShopId());

        //判断订单是否为该商家的订单
        if(!shopsInfoVO.getShopId().equals(userId)){
            log.warn("商家ID与订单商家ID不匹配, 商家ID: {}, 订单ID: {}", userId, orderId);
            return R.error("订单不匹配");
        }

        //判断该订单是否为申请退货的订单
        if(!orders.getReturnStatus().equals(ReturnStatus.REQUESTED)){
            log.warn("订单状态不正确, 订单ID: {}, 状态: {}", orderId, orders.getStatus());
            return R.error("订单已经处理过");
        }

        //改变订单状态
        orders.setReturnStatus(returnStatus);

        boolean update = this.updateById(orders);
        if (!update) {
            log.warn("商家审核退货申请失败, 订单ID: {}", orderId);
            return R.error("商家审核退货申请失败，请重试");
        }
        return R.ok("商家审核退货申请成功:"+returnStatus);

    }

    @Override
    public R<String> userConfirmReceipt(Long orderId) {
        log.info("用户确认收货, 订单ID: {}", orderId);
        Long userId = UserContext.getUserId();
        if(userId == null){
            log.warn("用户ID为空");
            return R.error("用户未登录");
        }
        if(orderId == null){
            log.warn("订单ID为空");
            return R.error("订单ID为空");
        }
        //开始处理订单申请
        //获取该订单详情
        Orders orders = this.getById(orderId);
        //判断订单是否为该用户的订单
        if(!orders.getUserId().equals(userId)){
            log.warn("用户ID与订单用户ID不匹配, 用户ID: {}, 订单ID: {}", userId, orderId);
            return R.error("订单不匹配");
        }
        //判断该订单是否为送达状态的订单
        if(!orders.getStatus().equals(OrderStatus.DELIVERED)){
            log.warn("订单状态不正确, 订单ID: {}, 状态: {}", orderId, orders.getStatus());
            return R.error("订单未送达");
        }
        //改变订单状态
        orders.setStatus(OrderStatus.RECEIVED);
        boolean update = this.updateById(orders);
        if (!update) {
            log.warn("用户确认收货失败, 订单ID: {}", orderId);
            return R.error("用户确认收货失败，请重试");
        }
        return R.ok("用户确认收货成功");
    }


    public void sendDeleteCartMessage(List<Long> configIds, Long userId) {
        if (CollectionUtils.isEmpty(configIds) || userId == null) {
            log.warn("sendDeleteCartMessage: 参数不合法, configIds={}, userId={}", configIds, userId);
            return;
        }

        // 构造 DTO
        CartMessageDTO cartMessageDTO = new CartMessageDTO();
        cartMessageDTO.setConfigIds(configIds);
        cartMessageDTO.setUserId(userId);
        cartMessageDTO.setOperation("DELETE CART_ITEMS");

        try {
            String cartMsg = JSONUtil.toJsonStr(cartMessageDTO);
            log.info("准备发送购物车删除消息: {}", cartMsg);

            rabbitMqHelper.sendMessage(
                    CartItemMQConstant.CART_ITEM_EXCHANGE,
                    CartItemMQConstant.CART_ITEM_DELETE_ROUTING_KEY,
                    cartMsg
            );

            log.info("购物车删除消息发送成功: {}", cartMsg);
        } catch (Exception e) {
            log.error("购物车删除消息发送失败, configIds={}, userId={}", configIds, userId, e);
        }
    }

}
