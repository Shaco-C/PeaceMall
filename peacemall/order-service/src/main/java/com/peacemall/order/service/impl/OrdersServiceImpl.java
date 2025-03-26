package com.peacemall.order.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.api.client.ProductClient;
import com.peacemall.common.constant.OrderListenerMQConstant;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.*;
import com.peacemall.common.utils.RabbitMqHelper;
import com.peacemall.common.utils.UserContext;
import com.peacemall.order.domain.po.OrderDetails;
import com.peacemall.order.domain.po.Orders;
import com.peacemall.order.enums.OrderStatus;
import com.peacemall.order.enums.ReturnStatus;
import com.peacemall.order.mapper.OrdersMapper;
import com.peacemall.order.service.OrderDetailsService;
import com.peacemall.order.service.OrdersService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    private final OrderDetailsService orderDetailsService;

    private final ProductClient productClient;

    private final RabbitMqHelper rabbitMqHelper;



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
                    20000
            );
        }

        //todo 通过RabbitMQ发送消息,将商品从购物车中删除

        log.info("【库存扣减】开始: {}", configIdAndStockChange);
        //调用openfeign同步扣减库存
        productClient.updateProductConfigurationsQuantity(configIdAndStockChange);


        return R.ok("创建订单成功");
    }


}
