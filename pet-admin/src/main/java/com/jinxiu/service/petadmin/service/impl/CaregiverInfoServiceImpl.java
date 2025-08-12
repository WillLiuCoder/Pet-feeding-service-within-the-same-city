package com.jinxiu.service.petadmin.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinxiu.service.petadmin.dto.CaregiverCreateDTO;
import com.jinxiu.service.petadmin.dto.CaregiverQueryDTO;
import com.jinxiu.service.petadmin.dto.CaregiverUpdateDTO;
import com.jinxiu.service.petadmin.entity.CaregiverInfo;
import com.jinxiu.service.petadmin.repository.CaregiverInfoRepository;
import com.jinxiu.service.petadmin.service.CaregiverInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 服务人员信息服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CaregiverInfoServiceImpl implements CaregiverInfoService {
    
    private final CaregiverInfoRepository caregiverInfoRepository;
    private final ObjectMapper objectMapper;
    
    @Override
    public CaregiverInfo createCaregiver(CaregiverCreateDTO createDTO) {
        log.info("创建服务人员: {}", createDTO.getRealName());
        
        // 检查身份证号是否已存在
        if (caregiverInfoRepository.findByIdCard(createDTO.getIdCard()).isPresent()) {
            throw new RuntimeException("身份证号已存在");
        }
        
        // 检查手机号是否已存在
        if (caregiverInfoRepository.findByPhone(createDTO.getPhone()).isPresent()) {
            throw new RuntimeException("手机号已存在");
        }
        
        CaregiverInfo caregiverInfo = new CaregiverInfo();
        caregiverInfo.setRealName(createDTO.getRealName());
        caregiverInfo.setIdCard(createDTO.getIdCard());
        caregiverInfo.setPhone(createDTO.getPhone());
        caregiverInfo.setEmail(createDTO.getEmail());
        caregiverInfo.setAvatarUrl(createDTO.getAvatarUrl());
        caregiverInfo.setIntroduction(createDTO.getIntroduction());
        caregiverInfo.setHourlyRate(createDTO.getHourlyRate());
        
        // 转换服务区域为JSON
        try {
            caregiverInfo.setServiceZones(objectMapper.writeValueAsString(createDTO.getServiceZones()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("服务区域格式转换失败", e);
        }
        
        // 转换证书信息为JSON
        if (createDTO.getCertifications() != null && !createDTO.getCertifications().isEmpty()) {
            try {
                caregiverInfo.setCertifications(objectMapper.writeValueAsString(createDTO.getCertifications()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("证书信息格式转换失败", e);
            }
        }
        
        caregiverInfo.setStatus(CaregiverInfo.CaregiverStatus.PENDING);
        caregiverInfo.setCreateTime(LocalDateTime.now());
        caregiverInfo.setUpdateTime(LocalDateTime.now());
        
        return caregiverInfoRepository.save(caregiverInfo);
    }
    
    @Override
    public CaregiverInfo updateCaregiver(CaregiverUpdateDTO updateDTO) {
        log.info("更新服务人员信息: {}", updateDTO.getCaregiverId());
        
        CaregiverInfo caregiverInfo = getCaregiverById(updateDTO.getCaregiverId());
        
        if (StringUtils.hasText(updateDTO.getRealName())) {
            caregiverInfo.setRealName(updateDTO.getRealName());
        }
        
        if (StringUtils.hasText(updateDTO.getPhone())) {
            // 检查手机号是否被其他服务人员使用
            Optional<CaregiverInfo> existingCaregiver = caregiverInfoRepository.findByPhone(updateDTO.getPhone());
            if (existingCaregiver.isPresent() && !existingCaregiver.get().getCaregiverId().equals(updateDTO.getCaregiverId())) {
                throw new RuntimeException("手机号已被其他服务人员使用");
            }
            caregiverInfo.setPhone(updateDTO.getPhone());
        }
        
        if (StringUtils.hasText(updateDTO.getEmail())) {
            caregiverInfo.setEmail(updateDTO.getEmail());
        }
        
        if (StringUtils.hasText(updateDTO.getAvatarUrl())) {
            caregiverInfo.setAvatarUrl(updateDTO.getAvatarUrl());
        }
        
        if (StringUtils.hasText(updateDTO.getIntroduction())) {
            caregiverInfo.setIntroduction(updateDTO.getIntroduction());
        }
        
        if (updateDTO.getHourlyRate() != null) {
            caregiverInfo.setHourlyRate(updateDTO.getHourlyRate());
        }
        
        // 更新服务区域
        if (updateDTO.getServiceZones() != null) {
            try {
                caregiverInfo.setServiceZones(objectMapper.writeValueAsString(updateDTO.getServiceZones()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("服务区域格式转换失败", e);
            }
        }
        
        // 更新证书信息
        if (updateDTO.getCertifications() != null) {
            try {
                caregiverInfo.setCertifications(objectMapper.writeValueAsString(updateDTO.getCertifications()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("证书信息格式转换失败", e);
            }
        }
        
        caregiverInfo.setUpdateTime(LocalDateTime.now());
        
        return caregiverInfoRepository.save(caregiverInfo);
    }
    
    @Override
    public CaregiverInfo getCaregiverById(Long caregiverId) {
        return caregiverInfoRepository.findById(caregiverId)
                .orElseThrow(() -> new RuntimeException("服务人员不存在"));
    }
    
    @Override
    public Page<CaregiverInfo> getCaregiversByPage(CaregiverQueryDTO queryDTO) {
        log.info("分页查询服务人员: {}", queryDTO);
        
        Pageable pageable = PageRequest.of(
                queryDTO.getPage() - 1,
                queryDTO.getSize(),
                Sort.by(Sort.Direction.DESC, "createTime")
        );
        
        Specification<CaregiverInfo> spec = buildSpecification(queryDTO);
        
        return caregiverInfoRepository.findAll(spec, pageable);
    }
    
    @Override
    public void deleteCaregiver(Long caregiverId) {
        log.info("删除服务人员: {}", caregiverId);
        
        CaregiverInfo caregiverInfo = getCaregiverById(caregiverId);
        
        // 检查是否可以删除（比如没有进行中的订单）
        // TODO: 添加业务逻辑检查
        
        caregiverInfoRepository.delete(caregiverInfo);
    }
    
    @Override
    public CaregiverInfo reviewCaregiver(Long caregiverId, CaregiverInfo.CaregiverStatus status, String reviewNote) {
        log.info("审核服务人员: {}, 状态: {}", caregiverId, status);
        
        CaregiverInfo caregiverInfo = getCaregiverById(caregiverId);
        caregiverInfo.setStatus(status);
        caregiverInfo.setUpdateTime(LocalDateTime.now());
        
        // TODO: 可以添加审核记录表
        
        return caregiverInfoRepository.save(caregiverInfo);
    }
    
    @Override
    public CaregiverInfo updateCaregiverStatus(Long caregiverId, CaregiverInfo.CaregiverStatus status) {
        log.info("更新服务人员状态: {}, 状态: {}", caregiverId, status);
        
        CaregiverInfo caregiverInfo = getCaregiverById(caregiverId);
        caregiverInfo.setStatus(status);
        caregiverInfo.setUpdateTime(LocalDateTime.now());
        
        return caregiverInfoRepository.save(caregiverInfo);
    }
    
    @Override
    public CaregiverInfo updateOnlineStatus(Long caregiverId, Boolean isOnline) {
        log.info("更新服务人员在线状态: {}, 在线: {}", caregiverId, isOnline);
        
        CaregiverInfo caregiverInfo = getCaregiverById(caregiverId);
        caregiverInfo.setIsOnline(isOnline);
        caregiverInfo.setUpdateTime(LocalDateTime.now());
        
        return caregiverInfoRepository.save(caregiverInfo);
    }
    
    @Override
    public CaregiverInfo updateBusyStatus(Long caregiverId, Boolean isBusy) {
        log.info("更新服务人员忙碌状态: {}, 忙碌: {}", caregiverId, isBusy);
        
        CaregiverInfo caregiverInfo = getCaregiverById(caregiverId);
        caregiverInfo.setIsBusy(isBusy);
        caregiverInfo.setUpdateTime(LocalDateTime.now());
        
        return caregiverInfoRepository.save(caregiverInfo);
    }
    
    @Override
    public Map<String, Object> getCaregiverStatistics() {
        log.info("获取服务人员统计信息");
        
        Map<String, Object> statistics = new HashMap<>();
        
        // 总数量
        long totalCount = caregiverInfoRepository.count();
        statistics.put("totalCount", totalCount);
        
        // 各状态数量
        List<Object[]> statusCounts = caregiverInfoRepository.countByStatus();
        Map<String, Long> statusMap = new HashMap<>();
        for (Object[] statusCount : statusCounts) {
            statusMap.put(statusCount[0].toString(), (Long) statusCount[1]);
        }
        statistics.put("statusCounts", statusMap);
        
        // 平均评分
        BigDecimal avgRating = caregiverInfoRepository.getAverageRating();
        statistics.put("averageRating", avgRating != null ? avgRating : BigDecimal.ZERO);
        
        // 在线数量
        long onlineCount = caregiverInfoRepository.countByIsOnline(true);
        statistics.put("onlineCount", onlineCount);
        
        // 忙碌数量
        long busyCount = caregiverInfoRepository.countByIsBusy(true);
        statistics.put("busyCount", busyCount);
        
        return statistics;
    }
    
    @Override
    public List<CaregiverInfo> getCaregiversByCondition(CaregiverQueryDTO queryDTO) {
        log.info("根据条件查询服务人员: {}", queryDTO);
        
        Specification<CaregiverInfo> spec = buildSpecification(queryDTO);
        
        return caregiverInfoRepository.findAll(spec);
    }
    
    /**
     * 构建查询条件
     */
    private Specification<CaregiverInfo> buildSpecification(CaregiverQueryDTO queryDTO) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (StringUtils.hasText(queryDTO.getRealName())) {
                predicates.add(criteriaBuilder.like(root.get("realName"), "%" + queryDTO.getRealName() + "%"));
            }
            
            if (StringUtils.hasText(queryDTO.getPhone())) {
                predicates.add(criteriaBuilder.like(root.get("phone"), "%" + queryDTO.getPhone() + "%"));
            }
            
            if (StringUtils.hasText(queryDTO.getServiceZone())) {
                predicates.add(criteriaBuilder.like(root.get("serviceZones"), "%" + queryDTO.getServiceZone() + "%"));
            }
            
            if (StringUtils.hasText(queryDTO.getStatus())) {
                try {
                    CaregiverInfo.CaregiverStatus status = CaregiverInfo.CaregiverStatus.valueOf(queryDTO.getStatus());
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                } catch (IllegalArgumentException e) {
                    log.warn("无效的状态值: {}", queryDTO.getStatus());
                }
            }
            
            if (queryDTO.getMinRating() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("avgRating"), queryDTO.getMinRating()));
            }
            
            if (queryDTO.getMaxRating() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("avgRating"), queryDTO.getMaxRating()));
            }
            
            if (queryDTO.getIsOnline() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isOnline"), queryDTO.getIsOnline()));
            }
            
            if (queryDTO.getIsBusy() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isBusy"), queryDTO.getIsBusy()));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}