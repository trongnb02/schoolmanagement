package com.example.schoolmnt.sm.registration.token;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    public void saveConfirmationToken(ConfirmationToken token){
        confirmationTokenRepository.save(token);
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public int setConfirmedAt(String token) {
        return confirmationTokenRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }

    public void deleteConfirmationToken(String token) {
        if (confirmationTokenRepository.findByToken(token).isPresent()){
            ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token).get();
            confirmationTokenRepository.delete(confirmationToken);
        }
    }

    @Scheduled(fixedRate = 150000)  // 2.5 phút
    public void deleteExpiredTokens() {
        List<ConfirmationToken> tokens = confirmationTokenRepository.findAllByConfirmedAtIsNullAndExpiresAtBefore(LocalDateTime.now());
        if (tokens.size() > 0){
            confirmationTokenRepository.deleteAll(tokens);
        }
    }

}
