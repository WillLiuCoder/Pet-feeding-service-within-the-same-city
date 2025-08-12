package com.jinxiu.service.petadmin.service;

import com.jinxiu.service.petadmin.dto.CaregiverCreateDTO;
import com.jinxiu.service.petadmin.dto.CaregiverQueryDTO;
import com.jinxiu.service.petadmin.dto.CaregiverUpdateDTO;
import com.jinxiu.service.petadmin.entity.CaregiverInfo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * 服务人员信息服务接口
 */
public interface CaregiverInfoService {
    
    /**
     * 创建服务人员
     */
    CaregiverInfo createCaregiver(CaregiverCreateDTO createDTO);
    
    /**
     * 更新服务人员信息
     */
    CaregiverInfo updateCaregiver(CaregiverUpdateDTO updateDTO);
    
    /**
     * 根据ID查询服务人员
     */
    CaregiverInfo getCaregiverById(Long caregiverId);
    
    /**
     * 分页查询服务人员
     */
    Page<CaregiverInfo> getCaregiversByPage(CaregiverQueryDTO queryDTO);
    
    /**
     * 删除服务人员
     */
    void deleteCaregiver(Long caregiverId);
    
    /**
     * 审核服务人员
     */
    CaregiverInfo reviewCaregiver(Long caregiverId, CaregiverInfo.CaregiverStatus status, String reviewNote);
    
    /**
     * 更新服务人员状态
     */
    CaregiverInfo updateCaregiverStatus(Long caregiverId, CaregiverInfo.CaregiverStatus status);
    
    /**
     * 更新在线状态
     */
    CaregiverInfo updateOnlineStatus(Long caregiverId, Boolean isOnline);
    
    /**
     * 更新忙碌状态
     */
    CaregiverInfo updateBusyStatus(Long caregiverId, Boolean isBusy);
    
    /**
     * 获取服务人员统计信息
     */
    Map<String, Object> getCaregiverStatistics();
    
    /**
     * 根据条件查询服务人员列表
     */
    List<CaregiverInfo> getCaregiversByCondition(CaregiverQueryDTO queryDTO);
}