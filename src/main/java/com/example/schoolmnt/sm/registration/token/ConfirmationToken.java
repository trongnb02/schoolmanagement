package com.example.schoolmnt.sm.registration.token;

import com.example.schoolmnt.sm.appuser.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="confirmation_token")
public class ConfirmationToken {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE
    )
    private Long id;

    @Column(name="token", nullable=false)
    private String token;

    @Column(name="createdAt", nullable=false)
    private LocalDateTime createdAt;

    @Column(name="expiresAt", nullable=false)
    private LocalDateTime expiresAt;

    @Column(name="confirmedAt")
    private LocalDateTime confirmedAt;

    @ManyToOne
    @JoinColumn(name="_user_id",nullable = false)
    private AppUser appUser;

    public ConfirmationToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt, AppUser appUser) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.appUser = appUser;
    }
}
