package com.jinxiu.service.petadmin.repository;

import com.jinxiu.service.petadmin.entity.CaregiverInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 服务人员信息数据访问层
 */
@Repository
public interface CaregiverInfoRepository extends JpaRepository<CaregiverInfo, Long>, JpaSpecificationExecutor<CaregiverInfo> {
    
    /**
     * 根据身份证号查询
     */
    Optional<CaregiverInfo> findByIdCard(String idCard);
    
    /**
     * 根据手机号查询
     */
    Optional<CaregiverInfo> findByPhone(String phone);
    
    /**
     * 根据用户ID查询
     */
    Optional<CaregiverInfo> findByUserId(Long userId);
    
    /**
     * 根据状态查询
     */
    List<CaregiverInfo> findByStatus(CaregiverInfo.CaregiverStatus status);
    
    /**
     * 根据服务区域查询
     */
    @Query("SELECT c FROM CaregiverInfo c WHERE c.serviceZones LIKE %:zone%")
    List<CaregiverInfo> findByServiceZoneContaining(@Param("zone") String zone);
    
    /**
     * 根据评分范围查询
     */
    List<CaregiverInfo> findByAvgRatingBetween(BigDecimal minRating, BigDecimal maxRating);
    
    /**
     * 根据在线状态查询
     */
    List<CaregiverInfo> findByIsOnline(Boolean isOnline);
    
    /**
     * 根据忙碌状态查询
     */
    List<CaregiverInfo> findByIsBusy(Boolean isBusy);
    
    /**
     * 统计各状态的服务人员数量
     */
    @Query("SELECT c.status, COUNT(c) FROM CaregiverInfo c GROUP BY c.status")
    List<Object[]> countByStatus();
    
    /**
     * 统计平均评分
     */
    @Query("SELECT AVG(c.avgRating) FROM CaregiverInfo c WHERE c.status = 'APPROVED'")
    BigDecimal getAverageRating();

    /**
     * 根据在线状态统计数量
     */
    long countByIsOnline(Boolean isOnline);

    /**
     * 根据忙碌状态统计数量
     */
    long countByIsBusy(Boolean isBusy);
}