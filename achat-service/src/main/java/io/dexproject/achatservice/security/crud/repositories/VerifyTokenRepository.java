package io.dexproject.achatservice.security.crud.repositories;

import io.dexproject.achatservice.security.crud.entities.UserAccount;
import io.dexproject.achatservice.security.crud.entities.VerifyToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VerifyTokenRepository extends JpaRepository<VerifyToken, Long> {
    @Query("SELECT DISTINCT v FROM VerifyToken v WHERE v.token = :token")
    VerifyToken findByToken(String token);

    @Query("SELECT DISTINCT v FROM VerifyToken v WHERE v.user = :user")
    VerifyToken findByUser(UserAccount user);
}
