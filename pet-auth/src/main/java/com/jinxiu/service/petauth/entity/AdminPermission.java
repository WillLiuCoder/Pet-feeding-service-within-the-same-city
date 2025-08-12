package com.jinxiu.service.petauth.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 管理员权限实体类
 */
@Entity
@Table(name = "admin_permission")
@Data
public class AdminPermission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Long permissionId;
    
    @Column(name = "permission_name", nullable = false, length = 100)
    private String permissionName;
    
    @Column(name = "permission_code", unique = true, nullable = false, length = 100)
    private String permissionCode;
    
    @Column(name = "permission_type", length = 20)
    @Enumerated(EnumType.STRING)
    private PermissionType permissionType;
    
    @Column(name = "resource_path", length = 200)
    private String resourcePath;
    
    @Column(name = "description", length = 200)
    private String description;
    
    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private PermissionStatus status = PermissionStatus.ACTIVE;
    
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    // 权限类型枚举
    public enum PermissionType {
        MENU("菜单"),
        BUTTON("按钮"),
        API("接口");
        
        private final String description;
        
        PermissionType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 权限状态枚举
    public enum PermissionStatus {
        ACTIVE("启用"),
        INACTIVE("禁用");
        
        private final String description;
        
        PermissionStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
