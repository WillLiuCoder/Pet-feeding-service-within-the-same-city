/**
 * File: OrderAssignDTO
 * Author: will.liu
 * Date: 2025/8/14 13:44
 * Description:
 */
package com.jinxiu.service.petadmin.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 订单分配DTO
 */
@Data
public class OrderAssignDTO {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @NotNull(message = "服务人员ID不能为空")
    private Long caregiverId;

    private String assignReason;
    private String adminNotes;
}