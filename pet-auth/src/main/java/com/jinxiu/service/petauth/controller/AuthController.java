package com.jinxiu.service.petauth.controller;

import com.jinxiu.service.petauth.dto.AdminLoginDTO;
import com.jinxiu.service.petauth.dto.AdminRegisterDTO;
import com.jinxiu.service.petauth.dto.AdminUserInfoDTO;
import com.jinxiu.service.petauth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Validated @RequestBody AdminLoginDTO loginDTO) {
        log.info("管理员登录请求: {}", loginDTO.getUsername());
        
        try {
            Map<String, Object> result = authService.login(loginDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("登录失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 管理员注册
     */
    @PostMapping("/register")
    public ResponseEntity<AdminUserInfoDTO> register(@Validated @RequestBody AdminRegisterDTO registerDTO) {
        log.info("管理员注册请求: {}", registerDTO.getUsername());
        
        try {
            AdminUserInfoDTO result = authService.register(registerDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("注册失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 刷新token
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestParam String refreshToken) {
        log.info("刷新token请求");
        
        try {
            Map<String, Object> result = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("刷新token失败", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * 登出
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        log.info("登出请求");
        
        try {
            // 去掉Bearer前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            authService.logout(token);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("登出失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/userinfo")
    public ResponseEntity<AdminUserInfoDTO> getUserInfo(@RequestHeader("Authorization") String token) {
        log.info("获取用户信息请求");
        
        try {
            // 去掉Bearer前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            AdminUserInfoDTO userInfo = authService.getCurrentUserInfo(token);
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        log.info("修改密码请求");
        
        try {
            // 去掉Bearer前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // 从token中获取用户ID
            // TODO: 实现从token获取用户ID的逻辑
            
            // authService.changePassword(userId, oldPassword, newPassword);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("修改密码失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
