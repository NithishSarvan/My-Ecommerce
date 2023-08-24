package com.shopme.admin.user;

import com.shopme.common.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {

    Optional<User> findByEmail(String email);


    @Modifying
    @Transactional
    @Query("UPDATE users u SET u.enabled = ?2 WHERE u.id = ?1")
    void updateUserEnableStatus(int id, boolean enabled);

    //@Query("SELECT  u FROM users u WHERE u.firstName LIKE %?1% OR u.lastName LIKE %?1%" + "OR u.email LIKE %?1%")
    @Query("SELECT  u FROM users u WHERE CONCAT (u.id ,' ', u.firstName,' ' , u.lastName,' ',  u.email) LIKE %?1%")
    Page<User> findAll(String keyword, final Pageable pageable);

}
