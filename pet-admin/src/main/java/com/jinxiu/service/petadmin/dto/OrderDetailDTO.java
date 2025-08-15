/**
 * File: OrderDetailDTO
 * Author: will.liu
 * Date: 2025/8/14 13:44
 * Description:
 */
package com.jinxiu.service.petadmin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单详情DTO
 */
@Data
public class OrderDetailDTO {

    private Long orderId;
    private String orderNo;
    private String customerName;
    private String customerPhone;
    private String petName;
    private String petType;
    private String serviceName;
    private String caregiverName;
    private String caregiverPhone;
    private String address;
    private LocalDateTime scheduledTime;
    private String specialNotes;
    private BigDecimal totalAmount;
    private String orderStatus;
    private LocalDateTime paymentTime;
    private LocalDateTime assignedTime;
    private LocalDateTime serviceStartTime;
    private LocalDateTime serviceEndTime;
    private String cancelReason;
    private BigDecimal refundAmount;
    private String refundReason;
    private LocalDateTime refundTime;
    private Integer customerRating;
    private String customerComment;
    private String adminNotes;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}