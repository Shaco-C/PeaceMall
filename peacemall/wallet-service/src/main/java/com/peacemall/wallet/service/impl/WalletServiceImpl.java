package com.peacemall.wallet.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.common.constant.FlowLogsMQConstants;
import com.peacemall.common.domain.R;
import com.peacemall.common.domain.dto.FlowLogsDTO;
import com.peacemall.common.domain.dto.WalletAmountChangeDTO;
import com.peacemall.common.enums.UserRole;
import com.peacemall.common.enums.WalletFlowType;
import com.peacemall.common.exception.BadRequestException;
import com.peacemall.common.exception.UnauthorizedException;
import com.peacemall.common.utils.RabbitMqHelper;
import com.peacemall.common.utils.UserContext;
import com.peacemall.wallet.domain.po.Wallet;
import com.peacemall.common.domain.vo.WalletVO;
import com.peacemall.wallet.mapper.WalletMapper;
import com.peacemall.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author watergun
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WalletServiceImpl extends ServiceImpl<WalletMapper, Wallet> implements WalletService {

    private final RabbitMqHelper rabbitMqHelper;

    //创建用户钱包（用户注册时一起执行）
    @Override
    public void createWalletWhenRegister(Long userId) {
        log.info("createWalletWhenRegister method is called");
        log.info("userId:{}", userId);
        if (userId == null) {
            log.error("用户参数为空");
            throw new BadRequestException("用户参数为空");
        }
        log.info("开始构建钱包信息");

        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setAvailableBalance(BigDecimal.valueOf(0));
        wallet.setTotalBalance(BigDecimal.valueOf(0));
        wallet.setPendingBalance(BigDecimal.valueOf(0));

        boolean save = this.save(wallet);
        if (!save) {
            log.error("创建钱包失败");
            throw  new RuntimeException("创建钱包失败");
        }
        log.info("创建钱包成功");
    }

    //用户查询自己的钱包信息
    @Override
    public R<WalletVO> userGetSelfWalletInfo() {
        log.info("serGetSelfWalletInfo method is called");
        Long userId = UserContext.getUserId();
        if (userId == null) {
            log.error("用户未登录");
            return R.error("用户未登录");
        }

        log.info("userId:{}", userId);

        LambdaQueryWrapper<Wallet> walletLambdaQueryWrapper = new LambdaQueryWrapper<>();
        walletLambdaQueryWrapper.eq(Wallet::getUserId, userId);

        Wallet wallet = this.getOne(walletLambdaQueryWrapper);
        if (wallet == null) {
            log.error("用户钱包不存在");
            return R.error("用户钱包不存在，系统错误");
        }

        WalletVO walletVO = new WalletVO();
        BeanUtil.copyProperties(wallet, walletVO);
        log.info("wallet:{}", walletVO);
        return R.ok(walletVO);
    }


    //管理员通过用户Id查询钱包
    @Override
    public R<WalletVO> adminGetWalletInfoByUserId(Long userId) {
        log.info("adminGetWalletInfoByUserId method is called");
        log.info("userId:{}", userId);
        Long userIdFromToken = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        if (userIdFromToken == null || !UserRole.ADMIN.name().equals(userRole)) {
            log.error("用户未登录或用户不是管理员");
            return R.error("用户未登录或用户权限不足");
        }
        LambdaQueryWrapper<Wallet> walletLambdaQueryWrapper = new LambdaQueryWrapper<>();
        walletLambdaQueryWrapper.eq(Wallet::getUserId, userId);
        Wallet wallet = this.getOne(walletLambdaQueryWrapper);
        if (wallet == null) {
            log.error("用户钱包不存在");
            return R.error("用户钱包不存在，系统错误");
        }
        WalletVO walletVO = new WalletVO();
        BeanUtil.copyProperties(wallet, walletVO);
        log.info("user:{} wallet info is:{}", userId, walletVO);
        return R.ok(walletVO);
    }

    //用户充值钱包
    @Override
    public R<String> userRechargeWallet(BigDecimal amount) {
        log.info("userRechargeWallet method is called");
        if ((amount == null) || (amount.compareTo(BigDecimal.valueOf(0))<=0)) {
            log.error("用户充值金额不合法");
            return R.error("充值金额不合法");
        }
        Long userId = UserContext.getUserId();
        if (userId == null) {
            log.error("用户未登录");
            return R.error("用户未登录");
        }
        log.info("userId:{}", userId);
        LambdaQueryWrapper<Wallet> walletLambdaQueryWrapper = new LambdaQueryWrapper<>();
        walletLambdaQueryWrapper.eq(Wallet::getUserId, userId);
        Wallet wallet = this.getOne(walletLambdaQueryWrapper);
        if (wallet == null) {
            log.error("用户钱包不存在");
            return R.error("用户钱包不存在，系统错误");
        }
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(amount));
        wallet.setTotalBalance(wallet.getTotalBalance().add(amount));
        boolean update = this.updateById(wallet);
        if (!update) {
            log.error("用户充值失败");
            return R.error("用户充值失败");
        }

        //添加流水日志
        FlowLogsDTO flowLogsDTO = new FlowLogsDTO();
        flowLogsDTO.setWalletId(wallet.getWalletId());
        flowLogsDTO.setFlowType(WalletFlowType.RECHARGE);
        flowLogsDTO.setBalanceChange(amount);
        flowLogsDTO.setUserId(userId);
        flowLogsDTO.setBalanceAfter(wallet.getTotalBalance());
        try{
            rabbitMqHelper.sendMessage(FlowLogsMQConstants.FLOW_LOGS_EXCHANGE_NAME,
                    FlowLogsMQConstants.FLOW_LOGS_ROUTING_KEY,flowLogsDTO);
        }catch (Exception e){
            log.error("发送消息失败,失败的流水日志信息为:{}",flowLogsDTO);
        }

        return R.ok("用户充值成功");
    }

    //用户支付
    @Override
    @Transactional
    public R<String> userPay(BigDecimal amount) {
        log.info("userPay method is called");
        if ((amount == null) || (amount.compareTo(BigDecimal.valueOf(0))<=0)) {
            log.error("用户支付金额不合法");
            throw new RuntimeException("用户支付金额不合法");
        }
        Long userId = UserContext.getUserId();
        if (userId == null) {
            log.error("用户未登录");
            throw new RuntimeException("用户未登录");
        }
        log.info("userId:{}", userId);
        LambdaQueryWrapper<Wallet> walletLambdaQueryWrapper = new LambdaQueryWrapper<>();
        walletLambdaQueryWrapper.eq(Wallet::getUserId, userId);
        Wallet wallet = this.getOne(walletLambdaQueryWrapper);
        if (wallet == null) {
            log.error("用户钱包不存在");
            throw new RuntimeException("用户钱包不存在，系统错误");
        }
        if (wallet.getAvailableBalance().compareTo(amount) < 0) {
            log.error("用户余额不足");
            throw new RuntimeException("用户余额不足");
        }
        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(amount));
        wallet.setTotalBalance(wallet.getAvailableBalance().add(wallet.getPendingBalance()));
        boolean update = this.updateById(wallet);
        if (!update) {
            log.error("用户支付失败");
            throw new RuntimeException("用户支付失败");
        }

        //添加用户流水日志
        //todo 需要修改
        FlowLogsDTO flowLogsDTO = new FlowLogsDTO();
        flowLogsDTO.setWalletId(wallet.getWalletId());
        flowLogsDTO.setFlowType(WalletFlowType.EXPENSE);
        flowLogsDTO.setBalanceChange(amount.negate());
        flowLogsDTO.setUserId(userId);
        flowLogsDTO.setBalanceAfter(wallet.getTotalBalance());
        try{
            rabbitMqHelper.sendMessage(FlowLogsMQConstants.FLOW_LOGS_EXCHANGE_NAME,
                    FlowLogsMQConstants.FLOW_LOGS_ROUTING_KEY,flowLogsDTO);
        }catch (Exception e){
            log.error("发送消息失败,失败的流水日志信息为:{}",flowLogsDTO);
        }
        log.info("发送支付流水信息成功:{}",flowLogsDTO);

        return R.ok("用户支付成功");
    }

    //管理员删除用户的钱包
    @Override
    @Transactional
    public void adminDeleteWallet(List<Long> userId) {
        log.info("adminDeleteWallet method is called");
        if (userId == null) {
            log.error("用户id不合法");
            throw new BadRequestException("用户id不合法");
        }
        Long userIdFromToken = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        log.info("userIdFromToken:{},userRole:{}",userIdFromToken,userRole);
        if (userIdFromToken == null || !UserRole.ADMIN.name().equals(userRole)) {
            log.error("用户未登录或用户不是管理员");
            throw new UnauthorizedException("用户未登录或用户不是管理员");
        }
        log.info("权限正常");

        LambdaQueryWrapper<Wallet> walletLambdaQueryWrapper = new LambdaQueryWrapper<>();
        walletLambdaQueryWrapper.in(Wallet::getUserId, userId);
        List<Long> walletList = Optional.ofNullable(this.list(walletLambdaQueryWrapper))
                .orElse(Collections.emptyList()) // 避免 null
                .stream()
                .map(Wallet::getWalletId)
                .collect(Collectors.toList());
        log.info("walletList:{}",walletList);

        if (walletList.isEmpty()) {
            log.error("用户钱包不存在");
            throw new BadRequestException("用户钱包不存在");
        }

        log.info("用户钱包存在");
        boolean remove = this.removeByIds(walletList);
        if (!remove) {
            log.error("用户钱包删除失败");
            throw new RuntimeException("用户钱包删除失败");
        }
        log.info("用户钱包删除成功");
    }

    @Override
    @Transactional
    public void userWalletPendingAmountChange(WalletAmountChangeDTO walletAmountChangeDTO) {
        log.info("userWalletAmountChange called with DTO: {}", walletAmountChangeDTO);

        if (walletAmountChangeDTO == null || walletAmountChangeDTO.getId() == null || walletAmountChangeDTO.getChangeAmount() == null) {
            log.error("参数错误: {}", walletAmountChangeDTO);
            throw new BadRequestException("参数错误");
        }

        Long userId = walletAmountChangeDTO.getId();
        BigDecimal amountChange = walletAmountChangeDTO.getChangeAmount();

        LambdaQueryWrapper<Wallet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Wallet::getUserId, userId);
        Wallet wallet = this.getOne(queryWrapper);
        if (wallet == null) {
            log.error("用户钱包不存在, userId: {}", userId);
            throw new BadRequestException("用户钱包不存在");
        }

        BigDecimal availableBalance = wallet.getAvailableBalance();
        BigDecimal pendingBalance = wallet.getPendingBalance().add(amountChange);
        wallet.setPendingBalance(pendingBalance);
        wallet.setTotalBalance(availableBalance.add(pendingBalance));

        boolean update = this.updateById(wallet);
        if (!update) {
            log.error("用户钱包更新失败, userId: {}", userId);
            throw new RuntimeException("用户钱包更新失败");
        }

        log.info("用户钱包更新成功, userId: {}, pendingBalance: {}, totalBalance: {}",
                userId, wallet.getPendingBalance(), wallet.getTotalBalance());

        // 发送流水消息
        FlowLogsDTO flowLogsDTO = new FlowLogsDTO(
                wallet.getWalletId(),
                userId,
                walletAmountChangeDTO.getRelatedOrderId(),
                walletAmountChangeDTO.getWalletFlowType(),
                walletAmountChangeDTO.getChangeAmount(),
                wallet.getTotalBalance()
        );
        rabbitMqHelper.sendMessage(FlowLogsMQConstants.FLOW_LOGS_EXCHANGE_NAME,
                FlowLogsMQConstants.FLOW_LOGS_ROUTING_KEY,flowLogsDTO);
        log.info("发送流水消息成功, flowLogsDTO: {}", flowLogsDTO);
    }

}
