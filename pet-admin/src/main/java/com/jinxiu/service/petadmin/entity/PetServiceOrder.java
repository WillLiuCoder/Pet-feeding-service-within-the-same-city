package com.jinxiu.service.petadmin.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 宠物服务订单实体类
 */
@Entity
@Table(name = "pet_service_order")
@Data
public class PetServiceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "pet_id", nullable = false)
    private Long petId;

    @Column(name = "caregiver_id")
    private Long caregiverId;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "scheduled_time", nullable = false)
    private LocalDateTime scheduledTime;

    @Column(name = "special_notes", columnDefinition = "TEXT")
    private String specialNotes;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING_PAYMENT;

    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    @Column(name = "assigned_time")
    private LocalDateTime assignedTime;

    @Column(name = "service_start_time")
    private LocalDateTime serviceStartTime;

    @Column(name = "service_end_time")
    private LocalDateTime serviceEndTime;

    @Column(name = "cancel_reason", length = 200)
    private String cancelReason;

    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "refund_reason", length = 200)
    private String refundReason;

    @Column(name = "refund_time")
    private LocalDateTime refundTime;

    @Column(name = "customer_rating")
    private Integer customerRating;

    @Column(name = "customer_comment", columnDefinition = "TEXT")
    private String customerComment;

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    // 订单状态枚举
    public enum OrderStatus {
        PENDING_PAYMENT("待支付"),
        PAID("已支付"),
        ASSIGNED("已派单"),
        IN_SERVICE("服务中"),
        COMPLETED("已完成"),
        CANCELLED("已取消"),
        REFUNDED("已退款");

        private final String description;

        OrderStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
