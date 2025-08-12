package com.jinxiu.service.petauth.service.impl;

import com.jinxiu.service.petauth.dto.AdminLoginDTO;
import com.jinxiu.service.petauth.dto.AdminRegisterDTO;
import com.jinxiu.service.petauth.dto.AdminUserInfoDTO;
import com.jinxiu.service.petauth.entity.AdminUser;
import com.jinxiu.service.petauth.repository.AdminUserRepository;
import com.jinxiu.service.petauth.service.AuthService;
import com.jinxiu.service.petauth.util.JwtUtil;
import com.jinxiu.service.petauth.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {
    
    private final AdminUserRepository adminUserRepository;
    private final JwtUtil jwtUtil;
    private final PasswordUtil passwordUtil;
    private final StringRedisTemplate redisTemplate;
    
    @Override
    public Map<String, Object> login(AdminLoginDTO loginDTO) {
        log.info("管理员登录: {}", loginDTO.getUsername());
        
        // 验证用户名和密码
        AdminUser adminUser = adminUserRepository.findByUsernameAndStatus(
                loginDTO.getUsername(), 
                AdminUser.AdminStatus.ACTIVE
        ).orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        
        if (!passwordUtil.matches(loginDTO.getPassword(), adminUser.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 更新最后登录时间和IP
        adminUser.setLastLoginTime(LocalDateTime.now());
        adminUser.setLastLoginIp(getClientIp());
        adminUserRepository.save(adminUser);
        
        // 生成token
        String token = jwtUtil.generateToken(
                adminUser.getUsername(), 
                adminUser.getAdminId(), 
                "ADMIN"
        );
        
        // 生成refresh token
        String refreshToken = UUID.randomUUID().toString();
        
        // 将token存储到Redis，设置过期时间
        String tokenKey = "token:" + adminUser.getAdminId();
        redisTemplate.opsForValue().set(tokenKey, token, 24, TimeUnit.HOURS);
        
        // 将refresh token存储到Redis
        String refreshTokenKey = "refresh_token:" + adminUser.getAdminId();
        redisTemplate.opsForValue().set(refreshTokenKey, refreshToken, 7, TimeUnit.DAYS);
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("refreshToken", refreshToken);
        result.put("expiresIn", 24 * 60 * 60); // 24小时
        result.put("userInfo", convertToDTO(adminUser));
        
        return result;
    }
    
    @Override
    public AdminUserInfoDTO register(AdminRegisterDTO registerDTO) {
        log.info("管理员注册: {}", registerDTO.getUsername());
        
        // 验证密码确认
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }
        
        // 检查用户名是否已存在
        if (adminUserRepository.existsByUsername(registerDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (StringUtils.hasText(registerDTO.getEmail()) && 
            adminUserRepository.existsByEmail(registerDTO.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 检查手机号是否已存在
        if (StringUtils.hasText(registerDTO.getPhone()) && 
            adminUserRepository.existsByPhone(registerDTO.getPhone())) {
            throw new RuntimeException("手机号已存在");
        }
        
        // 创建管理员用户
        AdminUser adminUser = new AdminUser();
        adminUser.setUsername(registerDTO.getUsername());
        adminUser.setPassword(passwordUtil.encodePassword(registerDTO.getPassword()));
        adminUser.setRealName(registerDTO.getRealName());
        adminUser.setEmail(registerDTO.getEmail());
        adminUser.setPhone(registerDTO.getPhone());
        adminUser.setAvatarUrl(registerDTO.getAvatarUrl());
        adminUser.setStatus(AdminUser.AdminStatus.ACTIVE);
        adminUser.setCreateTime(LocalDateTime.now());
        adminUser.setUpdateTime(LocalDateTime.now());
        
        AdminUser savedUser = adminUserRepository.save(adminUser);
        
        return convertToDTO(savedUser);
    }
    
    @Override
    public Map<String, Object> refreshToken(String refreshToken) {
        log.info("刷新token");
        
        // 从Redis中查找refresh token对应的用户ID
        String userId = null;
        for (String key : redisTemplate.keys("refresh_token:*")) {
            String value = redisTemplate.opsForValue().get(key);
            if (refreshToken.equals(value)) {
                userId = key.replace("refresh_token:", "");
                break;
            }
        }
        
        if (userId == null) {
            throw new RuntimeException("refresh token无效");
        }
        
        // 获取用户信息
        AdminUser adminUser = adminUserRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 生成新的token
        String newToken = jwtUtil.generateToken(
                adminUser.getUsername(), 
                adminUser.getAdminId(), 
                "ADMIN"
        );
        
        // 更新Redis中的token
        String tokenKey = "token:" + adminUser.getAdminId();
        redisTemplate.opsForValue().set(tokenKey, newToken, 24, TimeUnit.HOURS);
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", newToken);
        result.put("expiresIn", 24 * 60 * 60);
        
        return result;
    }
    
    @Override
    public void logout(String token) {
        log.info("用户登出");
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId != null) {
            // 从Redis中删除token
            String tokenKey = "token:" + userId;
            redisTemplate.delete(tokenKey);
            
            // 从Redis中删除refresh token
            String refreshTokenKey = "refresh_token:" + userId;
            redisTemplate.delete(refreshTokenKey);
        }
    }
    
    @Override
    public AdminUserInfoDTO getCurrentUserInfo(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            throw new RuntimeException("token无效");
        }
        
        AdminUser adminUser = adminUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        return convertToDTO(adminUser);
    }
    
    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("修改密码: {}", userId);
        
        AdminUser adminUser = adminUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 验证旧密码
        if (!passwordUtil.matches(oldPassword, adminUser.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }
        
        // 检查新密码强度
        if (!passwordUtil.isPasswordStrong(newPassword)) {
            throw new RuntimeException("新密码强度不够，必须包含字母、数字和特殊字符，长度至少8位");
        }
        
        // 更新密码
        adminUser.setPassword(passwordUtil.encodePassword(newPassword));
        adminUser.setUpdateTime(LocalDateTime.now());
        adminUserRepository.save(adminUser);
        
        // 登出所有设备
        logoutByUserId(userId);
    }
    
    @Override
    public String resetPassword(Long userId) {
        log.info("重置密码: {}", userId);
        
        AdminUser adminUser = adminUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 生成随机密码
        String newPassword = passwordUtil.generateRandomPassword(12);
        
        // 更新密码
        adminUser.setPassword(passwordUtil.encodePassword(newPassword));
        adminUser.setUpdateTime(LocalDateTime.now());
        adminUserRepository.save(adminUser);
        
        // 登出所有设备
        logoutByUserId(userId);
        
        return newPassword;
    }
    
    /**
     * 转换实体为DTO
     */
    private AdminUserInfoDTO convertToDTO(AdminUser adminUser) {
        AdminUserInfoDTO dto = new AdminUserInfoDTO();
        dto.setAdminId(adminUser.getAdminId());
        dto.setUsername(adminUser.getUsername());
        dto.setRealName(adminUser.getRealName());
        dto.setEmail(adminUser.getEmail());
        dto.setPhone(adminUser.getPhone());
        dto.setAvatarUrl(adminUser.getAvatarUrl());
        dto.setStatus(adminUser.getStatus().name());
        dto.setLastLoginTime(adminUser.getLastLoginTime());
        dto.setLastLoginIp(adminUser.getLastLoginIp());
        dto.setCreateTime(adminUser.getCreateTime());
        
        // TODO: 设置角色和权限信息
        
        return dto;
    }
    
    /**
     * 根据用户ID登出所有设备
     */
    private void logoutByUserId(Long userId) {
        String tokenKey = "token:" + userId;
        String refreshTokenKey = "refresh_token:" + userId;
        redisTemplate.delete(tokenKey, refreshTokenKey);
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIp() {
        // TODO: 实现获取客户端IP的逻辑
        return "127.0.0.1";
    }
}
