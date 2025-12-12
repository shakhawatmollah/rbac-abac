package com.shakhawat.rbacabac.repository;

import com.shakhawat.rbacabac.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByEmployeeId(Long employeeId);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.employee.id = :employeeId")
    void deleteByEmployeeId(Long employeeId);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :now")
    void deleteExpiredTokens(LocalDateTime now);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.employee.id = :employeeId")
    void revokeAllByEmployeeId(Long employeeId);

    boolean existsByTokenAndRevokedFalse(String token);
}
