package com.jinxiu.service.petauth.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 管理员用户信息DTO
 */
@Data
public class AdminUserInfoDTO {
    
    private Long adminId;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String avatarUrl;
    private String status;
    private LocalDateTime lastLoginTime;
    private String lastLoginIp;
    private LocalDateTime createTime;
    private Set<String> roles;
    private Set<String> permissions;
}
