package io.picook.domain.user.repository;

import io.picook.domain.user.entity.SocialProvider;
import io.picook.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByProviderAndProviderUserId(SocialProvider provider, String providerUserId);
}
