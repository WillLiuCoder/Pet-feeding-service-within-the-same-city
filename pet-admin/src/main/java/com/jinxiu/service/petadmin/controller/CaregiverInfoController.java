package com.jinxiu.service.petadmin.controller;

import com.jinxiu.service.petadmin.dto.CaregiverCreateDTO;
import com.jinxiu.service.petadmin.dto.CaregiverQueryDTO;
import com.jinxiu.service.petadmin.dto.CaregiverUpdateDTO;
import com.jinxiu.service.petadmin.entity.CaregiverInfo;
import com.jinxiu.service.petadmin.service.CaregiverInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 服务人员管理控制器
 */
@RestController
@RequestMapping("/api/caregivers")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CaregiverInfoController {
    
    private final CaregiverInfoService caregiverInfoService;
    
    /**
     * 创建服务人员
     */
    @PostMapping
    public ResponseEntity<CaregiverInfo> createCaregiver(@Validated @RequestBody CaregiverCreateDTO createDTO) {
        log.info("创建服务人员请求: {}", createDTO);
        
        try {
            CaregiverInfo caregiverInfo = caregiverInfoService.createCaregiver(createDTO);
            return ResponseEntity.ok(caregiverInfo);
        } catch (Exception e) {
            log.error("创建服务人员失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 更新服务人员信息
     */
    @PutMapping
    public ResponseEntity<CaregiverInfo> updateCaregiver(@Validated @RequestBody CaregiverUpdateDTO updateDTO) {
        log.info("更新服务人员请求: {}", updateDTO);
        
        try {
            CaregiverInfo caregiverInfo = caregiverInfoService.updateCaregiver(updateDTO);
            return ResponseEntity.ok(caregiverInfo);
        } catch (Exception e) {
            log.error("更新服务人员失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 根据ID查询服务人员
     */
    @GetMapping("/{caregiverId}")
    public ResponseEntity<CaregiverInfo> getCaregiverById(@PathVariable Long caregiverId) {
        log.info("查询服务人员: {}", caregiverId);
        
        try {
            CaregiverInfo caregiverInfo = caregiverInfoService.getCaregiverById(caregiverId);
            return ResponseEntity.ok(caregiverInfo);
        } catch (Exception e) {
            log.error("查询服务人员失败", e);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 分页查询服务人员
     */
    @GetMapping("/page")
    public ResponseEntity<Page<CaregiverInfo>> getCaregiversByPage(CaregiverQueryDTO queryDTO) {
        log.info("分页查询服务人员: {}", queryDTO);
        
        try {
            Page<CaregiverInfo> page = caregiverInfoService.getCaregiversByPage(queryDTO);
            return ResponseEntity.ok(page);
        } catch (Exception e) {
            log.error("分页查询服务人员失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 根据条件查询服务人员列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<CaregiverInfo>> getCaregiversByCondition(CaregiverQueryDTO queryDTO) {
        log.info("条件查询服务人员: {}", queryDTO);
        
        try {
            List<CaregiverInfo> caregivers = caregiverInfoService.getCaregiversByCondition(queryDTO);
            return ResponseEntity.ok(caregivers);
        } catch (Exception e) {
            log.error("条件查询服务人员失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 删除服务人员
     */
    @DeleteMapping("/{caregiverId}")
    public ResponseEntity<Void> deleteCaregiver(@PathVariable Long caregiverId) {
        log.info("删除服务人员: {}", caregiverId);
        
        try {
            caregiverInfoService.deleteCaregiver(caregiverId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("删除服务人员失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 审核服务人员
     */
    @PostMapping("/{caregiverId}/review")
    public ResponseEntity<CaregiverInfo> reviewCaregiver(
            @PathVariable Long caregiverId,
            @RequestParam CaregiverInfo.CaregiverStatus status,
            @RequestParam(required = false) String reviewNote) {
        log.info("审核服务人员: {}, 状态: {}, 备注: {}", caregiverId, status, reviewNote);
        
        try {
            CaregiverInfo caregiverInfo = caregiverInfoService.reviewCaregiver(caregiverId, status, reviewNote);
            return ResponseEntity.ok(caregiverInfo);
        } catch (Exception e) {
            log.error("审核服务人员失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 更新服务人员状态
     */
    @PutMapping("/{caregiverId}/status")
    public ResponseEntity<CaregiverInfo> updateCaregiverStatus(
            @PathVariable Long caregiverId,
            @RequestParam CaregiverInfo.CaregiverStatus status) {
        log.info("更新服务人员状态: {}, 状态: {}", caregiverId, status);
        
        try {
            CaregiverInfo caregiverInfo = caregiverInfoService.updateCaregiverStatus(caregiverId, status);
            return ResponseEntity.ok(caregiverInfo);
        } catch (Exception e) {
            log.error("更新服务人员状态失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 更新在线状态
     */
    @PutMapping("/{caregiverId}/online")
    public ResponseEntity<CaregiverInfo> updateOnlineStatus(
            @PathVariable Long caregiverId,
            @RequestParam Boolean isOnline) {
        log.info("更新在线状态: {}, 在线: {}", caregiverId, isOnline);
        
        try {
            CaregiverInfo caregiverInfo = caregiverInfoService.updateOnlineStatus(caregiverId, isOnline);
            return ResponseEntity.ok(caregiverInfo);
        } catch (Exception e) {
            log.error("更新在线状态失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 更新忙碌状态
     */
    @PutMapping("/{caregiverId}/busy")
    public ResponseEntity<CaregiverInfo> updateBusyStatus(
            @PathVariable Long caregiverId,
            @RequestParam Boolean isBusy) {
        log.info("更新忙碌状态: {}, 忙碌: {}", caregiverId, isBusy);
        
        try {
            CaregiverInfo caregiverInfo = caregiverInfoService.updateBusyStatus(caregiverId, isBusy);
            return ResponseEntity.ok(caregiverInfo);
        } catch (Exception e) {
            log.error("更新忙碌状态失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取服务人员统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getCaregiverStatistics() {
        log.info("获取服务人员统计信息");
        
        try {
            Map<String, Object> statistics = caregiverInfoService.getCaregiverStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            log.error("获取统计信息失败", e);
            return ResponseEntity.badRequest().build();
        }
    }
}