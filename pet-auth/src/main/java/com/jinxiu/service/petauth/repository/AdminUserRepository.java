package com.jinxiu.service.petauth.repository;

import com.jinxiu.service.petauth.entity.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 管理员用户数据访问层
 */
@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    
    /**
     * 根据用户名查询
     */
    Optional<AdminUser> findByUsername(String username);
    
    /**
     * 根据邮箱查询
     */
    Optional<AdminUser> findByEmail(String email);
    
    /**
     * 根据手机号查询
     */
    Optional<AdminUser> findByPhone(String phone);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(String phone);
    
    /**
     * 根据用户名和状态查询
     */
    @Query("SELECT u FROM AdminUser u WHERE u.username = :username AND u.status = :status")
    Optional<AdminUser> findByUsernameAndStatus(@Param("username") String username, @Param("status") AdminUser.AdminStatus status);
}
