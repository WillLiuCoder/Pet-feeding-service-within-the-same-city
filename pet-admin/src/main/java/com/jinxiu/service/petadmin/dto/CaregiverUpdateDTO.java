package com.jinxiu.service.petadmin.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 更新服务人员DTO
 */
@Data
public class CaregiverUpdateDTO {
    
    @NotNull(message = "服务人员ID不能为空")
    private Long caregiverId;
    
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String realName;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    private String avatarUrl;
    
    private List<String> serviceZones;
    
    private List<String> certifications;
    
    @Size(max = 500, message = "个人介绍不能超过500个字符")
    private String introduction;
    
    @DecimalMin(value = "0.01", message = "时薪必须大于0")
    @DecimalMax(value = "9999.99", message = "时薪不能超过9999.99")
    private BigDecimal hourlyRate;
}