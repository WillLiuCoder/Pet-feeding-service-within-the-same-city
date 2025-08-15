package com.jinxiu.service.petadmin.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 服务类型实体类
 */
@Entity
@Table(name = "service_type")
@Data
public class ServiceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private Long serviceId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "duration")
    private Integer duration; // 预计服务时长(分钟)

    @Column(name = "category", length = 50)
    private String category; // 服务分类：喂养、遛狗、护理等

    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private ServiceStatus status = ServiceStatus.ENABLED;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "icon_url", length = 255)
    private String iconUrl;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    // 服务状态枚举
    public enum ServiceStatus {
        ENABLED("启用"),
        DISABLED("禁用");

        private final String description;

        ServiceStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
