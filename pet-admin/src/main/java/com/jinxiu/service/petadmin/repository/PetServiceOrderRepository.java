/**
 * File: OrderRepository
 * Author: will.liu
 * Date: 2025/8/14 13:45
 * Description:
 */
package com.jinxiu.service.petadmin.repository;

import com.jinxiu.service.petadmin.entity.PetServiceOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 宠物服务订单Repository接口
 */
@Repository
public interface PetServiceOrderRepository extends JpaRepository<PetServiceOrder, Long> {

    /**
     * 根据状态查询订单
     */
    List<PetServiceOrder> findByStatus(PetServiceOrder.OrderStatus status);

    /**
     * 根据服务人员ID查询订单
     */
    List<PetServiceOrder> findByCaregiverId(Long caregiverId);

    /**
     * 根据用户ID查询订单
     */
    List<PetServiceOrder> findByUserId(Long userId);

    /**
     * 根据时间范围查询订单
     */
    List<PetServiceOrder> findByCreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据状态和时间范围查询订单
     */
    List<PetServiceOrder> findByStatusAndCreateTimeBetween(
            PetServiceOrder.OrderStatus status,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    /**
     * 统计指定状态的订单数量
     */
    long countByStatus(PetServiceOrder.OrderStatus status);

    /**
     * 统计指定时间范围内的订单数量
     */
    long countByCreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 复杂条件查询订单（分页）
     */
    @Query("SELECT o FROM PetServiceOrder o WHERE " +
            "(:orderNo IS NULL OR o.orderId = :orderNo) AND " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:startTime IS NULL OR o.createTime >= :startTime) AND " +
            "(:endTime IS NULL OR o.createTime <= :endTime)")
    Page<PetServiceOrder> findOrdersByConditions(
            @Param("orderNo") Long orderNo,
            @Param("status") PetServiceOrder.OrderStatus status,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable
    );
}
