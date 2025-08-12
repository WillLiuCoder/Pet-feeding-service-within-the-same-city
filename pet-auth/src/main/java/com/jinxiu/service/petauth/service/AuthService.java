package com.jinxiu.service.petauth.service;

import com.jinxiu.service.petauth.dto.AdminLoginDTO;
import com.jinxiu.service.petauth.dto.AdminRegisterDTO;
import com.jinxiu.service.petauth.dto.AdminUserInfoDTO;

import java.util.Map;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 管理员登录
     */
    Map<String, Object> login(AdminLoginDTO loginDTO);
    
    /**
     * 管理员注册
     */
    AdminUserInfoDTO register(AdminRegisterDTO registerDTO);
    
    /**
     * 刷新token
     */
    Map<String, Object> refreshToken(String refreshToken);
    
    /**
     * 登出
     */
    void logout(String token);
    
    /**
     * 获取当前用户信息
     */
    AdminUserInfoDTO getCurrentUserInfo(String token);
    
    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 重置密码
     */
    String resetPassword(Long userId);
}
