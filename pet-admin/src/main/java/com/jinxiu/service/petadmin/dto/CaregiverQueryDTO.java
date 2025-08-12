package com.jinxiu.service.petadmin.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 服务人员查询DTO
 */
@Data
public class CaregiverQueryDTO {
    
    private String realName;
    
    private String phone;
    
    private String serviceZone;
    
    private String status;
    
    private BigDecimal minRating;
    
    private BigDecimal maxRating;
    
    private Boolean isOnline;
    
    private Boolean isBusy;
    
    private Integer page = 1;
    
    private Integer size = 10;
}