package io.dexproject.achatservice.generic.security.crud.repositories;

import io.dexproject.achatservice.generic.security.crud.entities.UserAccount;
import io.dexproject.achatservice.generic.security.crud.entities.enums.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    @Query("SELECT DISTINCT u FROM UserAccount u WHERE u.email = :email OR u.phone = :phone")
    Optional<UserAccount> findByEmailOrPhone(String email, String phone);

    @Query("SELECT DISTINCT u FROM UserAccount u WHERE u.resetPasswordToken = :token")
    UserAccount findByResetPasswordToken(String token);

    @Query("SELECT CASE WHEN COUNT(u.id) > 0 THEN TRUE ELSE FALSE END FROM UserAccount u WHERE u.email = :email OR u.phone = :phone")
    Boolean existsByEmailOrPhone(String email, String phone);

    @Query("SELECT DISTINCT COUNT(u.id) from UserAccount u WHERE u.role = :name")
    Long countByRolename(RoleName name);

    @Query("SELECT DISTINCT u FROM UserAccount u WHERE u.lastName LIKE %?1% OR u.firstName LIKE %?1%")
    Page<UserAccount> findUserContainDisplaynameAndRolename(@Param("namesearch") String namesearch, Pageable pageable);

    @Query("SELECT DISTINCT u FROM UserAccount u WHERE u.role = :name")
    Page<UserAccount> findByRolename(RoleName name, Pageable pageable);
}
