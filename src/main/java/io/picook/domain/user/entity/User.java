package io.picook.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_provider_provider_user_id",
                        columnNames = {"provider", "provider_user_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SocialProvider provider;

    @Column(name = "provider_user_id", nullable = false, length = 64)
    private String providerUserId;

    @Column(length = 255)
    private String email;

    @Column(length = 100)
    private String nickname;

    @Column(name = "profile_image", length = 500)
    private String profileImage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    private User(SocialProvider provider, String providerUserId, String email, String nickname, String profileImage) {
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public static User createKakaoUser(String providerUserId, String email, String nickname, String profileImage) {
        return new User(SocialProvider.KAKAO, providerUserId, email, nickname, profileImage);
    }

    public void updateProfile(String email, String nickname, String profileImage) {
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
