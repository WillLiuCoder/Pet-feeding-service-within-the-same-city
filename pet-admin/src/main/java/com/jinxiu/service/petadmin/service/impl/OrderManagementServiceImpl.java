/**
 * File: OrderManagementServiceImpl
 * Author: will.liu
 * Date: 2025/8/14 13:47
 * Description:
 */
package com.jinxiu.service.petadmin.service.impl;

import com.jinxiu.service.petadmin.dto.OrderAssignDTO;
import com.jinxiu.service.petadmin.dto.OrderDetailDTO;
import com.jinxiu.service.petadmin.dto.OrderQueryDTO;
import com.jinxiu.service.petadmin.entity.PetServiceOrder;
import com.jinxiu.service.petadmin.repository.PetServiceOrderRepository;
import com.jinxiu.service.petadmin.service.OrderManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单管理服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderManagementServiceImpl implements OrderManagementService {

    private final PetServiceOrderRepository orderRepository;

    @Override
    public Page<OrderDetailDTO> queryOrders(OrderQueryDTO queryDTO) {
        Pageable pageable = PageRequest.of(queryDTO.getPageNum() - 1, queryDTO.getPageSize());

        // 构建查询条件
        PetServiceOrder.OrderStatus status = null;
        if (queryDTO.getOrderStatus() != null && !queryDTO.getOrderStatus().isEmpty()) {
            try {
                status = PetServiceOrder.OrderStatus.valueOf(queryDTO.getOrderStatus());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid order status: {}", queryDTO.getOrderStatus());
            }
        }

        // 执行查询
        Page<PetServiceOrder> orderPage = orderRepository.findOrdersByConditions(
                queryDTO.getOrderNo() != null ? Long.valueOf(queryDTO.getOrderNo()) : null,
                status,
                queryDTO.getStartTime(),
                queryDTO.getEndTime(),
                pageable
        );

        // 转换为DTO
        return orderPage.map(this::convertToOrderDetailDTO);
    }

    @Override
    public OrderDetailDTO getOrderDetail(Long orderId) {
        PetServiceOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在: " + orderId));
        return convertToOrderDetailDTO(order);
    }

    @Override
    @Transactional
    public void assignOrder(OrderAssignDTO assignDTO) {
        PetServiceOrder order = orderRepository.findById(assignDTO.getOrderId())
                .orElseThrow(() -> new RuntimeException("订单不存在: " + assignDTO.getOrderId()));

        // 检查订单状态
        if (order.getStatus() != PetServiceOrder.OrderStatus.PAID) {
            throw new RuntimeException("订单状态不允许分配: " + order.getStatus());
        }

        // 更新订单状态
        order.setCaregiverId(assignDTO.getCaregiverId());
        order.setStatus(PetServiceOrder.OrderStatus.ASSIGNED);
        order.setAssignedTime(LocalDateTime.now());
        order.setAdminNotes(assignDTO.getAdminNotes());

        orderRepository.save(order);
        log.info("订单 {} 已分配给服务人员 {}", assignDTO.getOrderId(), assignDTO.getCaregiverId());
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, String reason, String adminNotes) {
        PetServiceOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在: " + orderId));

        // 检查订单状态
        if (order.getStatus() == PetServiceOrder.OrderStatus.COMPLETED ||
                order.getStatus() == PetServiceOrder.OrderStatus.CANCELLED) {
            throw new RuntimeException("订单状态不允许取消: " + order.getStatus());
        }

        // 更新订单状态
        order.setStatus(PetServiceOrder.OrderStatus.CANCELLED);
        order.setCancelReason(reason);
        order.setAdminNotes(adminNotes);

        orderRepository.save(order);
        log.info("订单 {} 已取消，原因: {}", orderId, reason);
    }

    @Override
    @Transactional
    public void processRefund(Long orderId, BigDecimal refundAmount, String refundReason) {
        PetServiceOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在: " + orderId));

        // 检查订单状态
        if (order.getStatus() != PetServiceOrder.OrderStatus.PAID &&
                order.getStatus() != PetServiceOrder.OrderStatus.ASSIGNED) {
            throw new RuntimeException("订单状态不允许退款: " + order.getStatus());
        }

        // 更新订单状态
        order.setStatus(PetServiceOrder.OrderStatus.REFUNDED);
        order.setRefundAmount(refundAmount);
        order.setRefundReason(refundReason);
        order.setRefundTime(LocalDateTime.now());

        orderRepository.save(order);
        log.info("订单 {} 已退款，金额: {}", orderId, refundAmount);
    }

    @Override
    public Map<String, Object> getOrderStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> statistics = new HashMap<>();

        // 订单总数
        long totalOrders = orderRepository.countByCreateTimeBetween(startTime, endTime);
        statistics.put("totalOrders", totalOrders);

        // 各状态订单数量
        Map<String, Long> statusCount = new HashMap<>();
        for (PetServiceOrder.OrderStatus status : PetServiceOrder.OrderStatus.values()) {
            long count = orderRepository.countByStatus(status);
            statusCount.put(status.name(), count);
        }
        statistics.put("statusCount", statusCount);

        // 收入统计
        List<PetServiceOrder> completedOrders = orderRepository.findByStatusAndCreateTimeBetween(
                PetServiceOrder.OrderStatus.COMPLETED, startTime, endTime);
        BigDecimal totalRevenue = completedOrders.stream()
                .map(PetServiceOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.put("totalRevenue", totalRevenue);

        // 退款统计
        List<PetServiceOrder> refundedOrders = orderRepository.findByStatusAndCreateTimeBetween(
                PetServiceOrder.OrderStatus.REFUNDED, startTime, endTime);
        BigDecimal totalRefund = refundedOrders.stream()
                .map(PetServiceOrder::getRefundAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.put("totalRefund", totalRefund);

        return statistics;
    }

    @Override
    public List<OrderDetailDTO> getAbnormalOrders() {
        // 获取异常订单（超时未分配、长时间未完成等）
        LocalDateTime threshold = LocalDateTime.now().minusHours(2); // 2小时未分配的订单

        List<PetServiceOrder> abnormalOrders = orderRepository.findByStatusAndCreateTimeBetween(
                PetServiceOrder.OrderStatus.PAID,
                LocalDateTime.now().minusDays(1),
                threshold
        );

        return abnormalOrders.stream()
                .map(this::convertToOrderDetailDTO)
                .collect(Collectors.toList());
    }

    @Override
    public byte[] exportOrders(OrderQueryDTO queryDTO) {
        // 实现订单导出功能
        // 这里可以集成POI或其他Excel库来生成Excel文件
        log.info("导出订单数据，查询条件: {}", queryDTO);
        return new byte[0]; // 临时返回空数组
    }

    @Override
    public Map<String, Long> getRealTimeOrderStatusCount() {
        Map<String, Long> statusCount = new HashMap<>();

        for (PetServiceOrder.OrderStatus status : PetServiceOrder.OrderStatus.values()) {
            long count = orderRepository.countByStatus(status);
            statusCount.put(status.name(), count);
        }

        return statusCount;
    }

    /**
     * 将订单实体转换为DTO
     */
    private OrderDetailDTO convertToOrderDetailDTO(PetServiceOrder order) {
        OrderDetailDTO dto = new OrderDetailDTO();
        BeanUtils.copyProperties(order, dto);

        // 这里需要根据实际业务需求填充客户信息、宠物信息、服务信息等
        // 可以通过关联查询或调用其他服务来获取

        return dto;
    }
}