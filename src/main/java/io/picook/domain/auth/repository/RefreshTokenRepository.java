package io.picook.domain.auth.repository;

import io.picook.domain.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenIdAndUser_Id(String tokenId, Long userId);
}
