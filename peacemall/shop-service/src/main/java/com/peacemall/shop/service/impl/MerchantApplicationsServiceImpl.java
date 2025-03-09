package com.peacemall.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peacemall.api.client.UserClient;
import com.peacemall.common.domain.R;
import com.peacemall.common.enums.UserRole;
import com.peacemall.common.utils.UserContext;
import com.peacemall.shop.domain.po.MerchantApplications;
import com.peacemall.shop.domain.vo.AdminCheckApplications;
import com.peacemall.shop.enums.ApplicationStatus;
import com.peacemall.shop.mapper.MerchantApplicationsMapper;
import com.peacemall.shop.service.MerchantApplicationService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MerchantApplicationsServiceImpl extends ServiceImpl<MerchantApplicationsMapper, MerchantApplications> implements MerchantApplicationService {

    private final UserClient userClient;

    @Override
    public R<String> userCreateMerchantApplication(MerchantApplications merchantApplications) {
        log.info("userCreateMerchantApplication method is called");
        Long userId = UserContext.getUserId();

        //查询用户是否登陆
        if (userId == null) {
            log.error("用户未登录");
            return R.error("用户未登录");
        }
        log.info("用户已登陆，userId:{}",userId);

        //查询用户是否已经申请过商家，如果申请过，则返回错误信息
        LambdaQueryWrapper<MerchantApplications> merchantApplicationsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        merchantApplicationsLambdaQueryWrapper.eq(MerchantApplications::getUserId, userId);
        MerchantApplications dbMerchantApplications = this.getOne(merchantApplicationsLambdaQueryWrapper);

        if (dbMerchantApplications != null) {
            log.error("用户已经申请过商家");
            return R.error("用户已经申请过商家");
        }
        log.info("用户未申请过商家，可以申请商家");

        //创建商家申请记录
        merchantApplications.setUserId(userId);
        merchantApplications.setStatus(ApplicationStatus.PENDING);
        boolean save = this.save(merchantApplications);

        if (!save) {
            log.error("创建商家申请记录失败");
            return R.error("创建商家申请记录失败");
        }

        log.info("创建商家申请记录成功");
        return R.ok("创建商家申请记录成功");
    }

    @Override
    public R<String> userCancelMerchantApplication(Long applicationId) {
        log.info("userCancelMerchantApplication method is called");
        Long userId = UserContext.getUserId();
        //查询用户是否登陆
        if (userId == null) {
            log.error("用户未登录");
            return R.error("用户未登录");
        }
        log.info("用户已登陆，userId:{}",userId);

        //查询当前的商家申请是否是该用户的
        MerchantApplications merchantApplications = this.getById(applicationId);

        if (merchantApplications == null) {
            log.error("商家申请不存在");
            return R.error("商家申请不存在");
        }
        if (!merchantApplications.getUserId().equals(userId)) {
            log.error("商家申请不属于该用户");
            return R.error("商家申请不属于该用户");
        }
        log.info("商家申请属于该用户");

        //如果当前申请状态不为PENDING,说明已经被处理过了
        if (!merchantApplications.getStatus().equals(ApplicationStatus.PENDING)) {
            log.error("商家申请已经被处理过了");
            return R.error("商家申请已经被处理过了,不能够被取消");
        }

        //现在可以取消申请
        merchantApplications.setStatus(ApplicationStatus.CANCELED);
        boolean update = this.updateById(merchantApplications);
        if (!update) {
            log.error("取消商家申请失败");
            return R.error("取消商家申请失败");
        }
        log.info("取消商家申请成功");
        return R.ok("取消商家申请成功");
    }

    @Override
    public R<MerchantApplications> userGetMerchantApplication() {
        log.info("userGetMerchantApplication method is called");
        Long userId = UserContext.getUserId();
        //查询用户是否登陆
        if (userId == null) {
            log.error("用户未登录");
            return R.error("用户未登录");
        }
        log.info("用户已登陆，userId:{}",userId);

        //根据当前登陆的userId来返回Application
        LambdaQueryWrapper<MerchantApplications> merchantApplicationsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        merchantApplicationsLambdaQueryWrapper.eq(MerchantApplications::getUserId, userId);
        MerchantApplications merchantApplications = this.getOne(merchantApplicationsLambdaQueryWrapper);
        if (merchantApplications == null) {
            log.error("用户未申请过商家");
            return R.error("用户未申请过商家");
        }
        log.info("用户已申请过商家");
        return R.ok(merchantApplications);
    }

    @Override
    public R<Page<MerchantApplications>> adminGetMerchantApplicationByStatus(int page, int pageSize, ApplicationStatus merchantApplicationStatus) {
        log.info("adminGetMerchantApplicationByStatus method is called");
        log.info("page:{},pageSize:{},merchantApplicationStatus:{}",page,pageSize,merchantApplicationStatus);
        Long userId =UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        log.info("userId:{},userRole:{}",userId,userRole);

        //判断当前用户是否登陆，角色是否为管理员
        if (userId ==null || !UserRole.ADMIN.name().equals(userRole)){
            log.error("用户未登录或者用户不是管理员");
            return R.error("用户未登录或者用户不是管理员");
        }
        log.info("用户已登录，且用户是管理员");

        //根据状态分页查询商家申请
        LambdaQueryWrapper<MerchantApplications> merchantApplicationsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        merchantApplicationsLambdaQueryWrapper.eq(MerchantApplications::getStatus, merchantApplicationStatus);
        Page<MerchantApplications> merchantApplicationsPage = new Page<>(page, pageSize);
        this.page(merchantApplicationsPage, merchantApplicationsLambdaQueryWrapper);
        log.info("查询商家申请成功");
        return R.ok(merchantApplicationsPage);
    }

    @Override
    @GlobalTransactional
    public R<String> adminCheckMerchantApplication(AdminCheckApplications adminCheckApplications) {
        log.info("adminCheckMerchantApplication method is called");
        log.info("applicationId:{},applicationStatus:{}",adminCheckApplications.getApplicationId()
                ,adminCheckApplications.getApplicationStatus());

        Long userId = UserContext.getUserId();
        String userRole = UserContext.getUserRole();
        log.info("userId:{},userRole:{}",userId,userRole);

        //判断当前用户是否登陆，角色是否为管理员
        if (userId ==null || !UserRole.ADMIN.name().equals(userRole)){
            log.error("用户未登录或者用户不是管理员");
            return R.error("用户未登录或者用户不是管理员");
        }
        log.info("用户已登录，且用户是管理员");

        ////根据applicationId查询商家申请
        MerchantApplications merchantApplications = this.getById(adminCheckApplications.getApplicationId());
        if (merchantApplications == null) {
            log.error("商家申请不存在");
            return R.error("商家申请不存在");
        }
        log.info("商家申请存在");

        //查看商家申请状态是否为PENDING
        if (!ApplicationStatus.PENDING.equals(merchantApplications.getStatus())) {
            log.error("商家申请状态不是PENDING");
            return R.error("商家申请状态不是PENDING，请刷新重试");
        }
        log.info("商家申请状态为PENDING");

        //如果管理员同意用户申请,要将用户身份变为MERCHANT
        if (ApplicationStatus.APPROVED.equals(adminCheckApplications.getApplicationStatus())){
            //将用户的身份变为商家
            try{
                userClient.adminChangeUserRole(adminCheckApplications.getUserId(), UserRole.MERCHANT);
            }catch (Exception e){
                log.error("用户身份修改失败");
                throw new RuntimeException("用户身份修改失败");
            }
        }

        //修改商家申请状态
        merchantApplications.setStatus(adminCheckApplications.getApplicationStatus());
        merchantApplications.setReason(adminCheckApplications.getReason());
        boolean updated = this.updateById(merchantApplications);
        if (!updated) {
            log.error("商家申请状态修改失败");
            return R.error("商家申请状态修改失败");
        }
        log.info("商家申请状态修改成功");
        return R.ok("商家申请状态修改成功");
    }
}
