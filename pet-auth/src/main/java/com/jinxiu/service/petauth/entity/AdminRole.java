package com.jinxiu.service.petauth.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 管理员角色实体类
 */
@Entity
@Table(name = "admin_role")
@Data
public class AdminRole {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;
    
    @Column(name = "role_name", unique = true, nullable = false, length = 50)
    private String roleName;
    
    @Column(name = "role_code", unique = true, nullable = false, length = 50)
    private String roleCode;
    
    @Column(name = "description", length = 200)
    private String description;
    
    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private RoleStatus status = RoleStatus.ACTIVE;
    
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "admin_role_permission",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<AdminPermission> permissions;
    
    // 角色状态枚举
    public enum RoleStatus {
        ACTIVE("启用"),
        INACTIVE("禁用");
        
        private final String description;
        
        RoleStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
         
