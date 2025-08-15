/**
 * File: OrderManagementService
 * Author: will.liu
 * Date: 2025/8/14 13:47
 * Description:
 */
package com.jinxiu.service.petadmin.service;

import com.jinxiu.service.petadmin.dto.OrderAssignDTO;
import com.jinxiu.service.petadmin.dto.OrderDetailDTO;
import com.jinxiu.service.petadmin.dto.OrderQueryDTO;
import com.jinxiu.service.petadmin.entity.PetServiceOrder;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单管理服务接口
 */
public interface OrderManagementService {

    /**
     * 分页查询订单
     */
    Page<OrderDetailDTO> queryOrders(OrderQueryDTO queryDTO);

    /**
     * 获取订单详情
     */
    OrderDetailDTO getOrderDetail(Long orderId);

    /**
     * 分配订单给服务人员
     */
    void assignOrder(OrderAssignDTO assignDTO);

    /**
     * 取消订单
     */
    void cancelOrder(Long orderId, String reason, String adminNotes);

    /**
     * 处理退款
     */
    void processRefund(Long orderId, BigDecimal refundAmount, String refundReason);

    /**
     * 获取订单统计信息
     */
    Map<String, Object> getOrderStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取异常订单列表
     */
    List<OrderDetailDTO> getAbnormalOrders();

    /**
     * 导出订单数据
     */
    byte[] exportOrders(OrderQueryDTO queryDTO);

    /**
     * 获取实时订单状态统计
     */
    Map<String, Long> getRealTimeOrderStatusCount();
}