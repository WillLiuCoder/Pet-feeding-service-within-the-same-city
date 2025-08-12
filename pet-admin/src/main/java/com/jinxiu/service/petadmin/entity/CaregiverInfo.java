package com.jinxiu.service.petadmin.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 服务人员信息实体类
 */
@Entity
@Table(name = "caregiver_info")
@Data
public class CaregiverInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "caregiver_id")
    private Long caregiverId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "real_name", nullable = false, length = 50)
    private String realName;

    @Column(name = "id_card", unique = true, nullable = false, length = 20)
    private String idCard;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "service_zones", columnDefinition = "JSON")
    private String serviceZones; // JSON格式存储服务区域

    @Column(name = "certifications", columnDefinition = "JSON")
    private String certifications; // JSON格式存储证书信息

    @Column(name = "avg_rating", precision = 3, scale = 2, columnDefinition = "DECIMAL(3,2) DEFAULT 5.00")
    private BigDecimal avgRating = new BigDecimal("5.00");

    @Column(name = "total_orders", columnDefinition = "INT DEFAULT 0")
    private Integer totalOrders = 0;

    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private CaregiverStatus status = CaregiverStatus.PENDING;

    @Column(name = "is_online", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isOnline = false;

    @Column(name = "is_busy", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isBusy = false;

    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;

    @Column(name = "hourly_rate", precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    // 服务人员状态枚举
    public enum CaregiverStatus {
        PENDING("审核中"),
        APPROVED("已认证"),
        REJECTED("已拒绝"),
        DISABLED("已禁用");

        private final String description;

        CaregiverStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
