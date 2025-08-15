/**
 * File: OrderManagerController
 * Author: will.liu
 * Date: 2025/8/14 13:48
 * Description:
 */

package com.jinxiu.service.petadmin.controller;

import com.jinxiu.service.petadmin.dto.OrderAssignDTO;
import com.jinxiu.service.petadmin.dto.OrderDetailDTO;
import com.jinxiu.service.petadmin.dto.OrderQueryDTO;
import com.jinxiu.service.petadmin.service.OrderManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderManagementController {

    private final OrderManagementService orderManagementService;

    /**
     * 分页查询订单
     */
    @GetMapping
    public ResponseEntity<Page<OrderDetailDTO>> queryOrders(OrderQueryDTO queryDTO) {
        try {
            Page<OrderDetailDTO> orders = orderManagementService.queryOrders(queryDTO);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("查询订单失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailDTO> getOrderDetail(@PathVariable Long orderId) {
        try {
            OrderDetailDTO orderDetail = orderManagementService.getOrderDetail(orderId);
            return ResponseEntity.ok(orderDetail);
        } catch (Exception e) {
            log.error("获取订单详情失败: {}", orderId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 分配订单
     */
    @PostMapping("/assign")
    public ResponseEntity<Void> assignOrder(@Valid @RequestBody OrderAssignDTO assignDTO) {
        try {
            orderManagementService.assignOrder(assignDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("分配订单失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 取消订单
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long orderId,
            @RequestParam String reason,
            @RequestParam(required = false) String adminNotes) {
        try {
            orderManagementService.cancelOrder(orderId, reason, adminNotes);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("取消订单失败: {}", orderId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 处理退款
     */
    @PostMapping("/{orderId}/refund")
    public ResponseEntity<Void> processRefund(
            @PathVariable Long orderId,
            @RequestParam BigDecimal refundAmount,
            @RequestParam String refundReason) {
        try {
            orderManagementService.processRefund(orderId, refundAmount, refundReason);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("处理退款失败: {}", orderId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取订单统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getOrderStatistics(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        try {
            LocalDateTime start = startTime != null ? LocalDateTime.parse(startTime) : LocalDateTime.now().minusDays(30);
            LocalDateTime end = endTime != null ? LocalDateTime.parse(endTime) : LocalDateTime.now();

            Map<String, Object> statistics = orderManagementService.getOrderStatistics(start, end);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            log.error("获取订单统计信息失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取异常订单列表
     */
    @GetMapping("/abnormal")
    public ResponseEntity<List<OrderDetailDTO>> getAbnormalOrders() {
        try {
            List<OrderDetailDTO> abnormalOrders = orderManagementService.getAbnormalOrders();
            return ResponseEntity.ok(abnormalOrders);
        } catch (Exception e) {
            log.error("获取异常订单失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 导出订单数据
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportOrders(OrderQueryDTO queryDTO) {
        try {
            byte[] data = orderManagementService.exportOrders(queryDTO);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=orders.xlsx")
                    .body(data);
        } catch (Exception e) {
            log.error("导出订单失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取实时订单状态统计
     */
    @GetMapping("/realtime-statistics")
    public ResponseEntity<Map<String, Long>> getRealTimeOrderStatusCount() {
        try {
            Map<String, Long> statusCount = orderManagementService.getRealTimeOrderStatusCount();
            return ResponseEntity.ok(statusCount);
        } catch (Exception e) {
            log.error("获取实时订单状态统计失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}