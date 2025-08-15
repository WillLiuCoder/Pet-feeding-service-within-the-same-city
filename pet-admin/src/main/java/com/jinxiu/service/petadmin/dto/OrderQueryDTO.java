package com.jinxiu.service.petadmin.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 订单查询DTO
 */
@Data
public class OrderQueryDTO {
    
    private String orderNo;
    private String customerName;
    private String customerPhone;
    private String caregiverName;
    private String serviceType;
    private String orderStatus;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    private String address;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}

