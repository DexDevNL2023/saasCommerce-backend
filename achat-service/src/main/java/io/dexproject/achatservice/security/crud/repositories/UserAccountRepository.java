package io.dexproject.achatservice.security.crud.repositories;

import io.dexproject.achatservice.security.crud.entities.UserAccount;
import io.dexproject.achatservice.security.crud.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    @Query("SELECT DISTINCT u FROM UserAccount u WHERE u.email = :emailOrPhone OR u.phone = :emailOrPhone")
    Optional<UserAccount> findByEmailOrPhone(String emailOrPhone);

    @Query("SELECT DISTINCT u FROM UserAccount u WHERE u.resetPasswordToken = :token")
    UserAccount findByResetPasswordToken(String token);

    @Query("SELECT CASE WHEN COUNT(u.id) > 0 THEN TRUE ELSE FALSE END FROM UserAccount u WHERE u.email = :emailOrPhone OR u.phone = :emailOrPhone")
    Boolean existsByEmailOrPhone(String emailOrPhone);

    @Query("SELECT DISTINCT u FROM UserAccount u WHERE u.lastName LIKE %?1% OR u.firstName LIKE %?1%")
    List<UserAccount> search(@Param("namesearch") String namesearch);

    @Query("SELECT DISTINCT u FROM UserAccount u JOIN u.roles r WHERE r.libelle = :name")
    List<UserAccount> findAllByRolename(RoleName name);
}
