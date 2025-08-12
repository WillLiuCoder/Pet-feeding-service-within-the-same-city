package com.jinxiu.service.petadmin.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 创建服务人员DTO
 */
@Data
public class CaregiverCreateDTO {
    
    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String realName;
    
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", 
             message = "身份证号格式不正确")
    private String idCard;
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotBlank(message = "头像不能为空")
    private String avatarUrl;
    
    @NotEmpty(message = "服务区域不能为空")
    private List<String> serviceZones;
    
    private List<String> certifications;
    
    @Size(max = 500, message = "个人介绍不能超过500个字符")
    private String introduction;
    
    @DecimalMin(value = "0.01", message = "时薪必须大于0")
    @DecimalMax(value = "9999.99", message = "时薪不能超过9999.99")
    private BigDecimal hourlyRate;
}
