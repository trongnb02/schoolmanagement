package com.example.schoolmnt.sm.appuser;

import com.example.schoolmnt.sm.registration.token.ConfirmationToken;
import com.example.schoolmnt.sm.registration.token.ConfirmationTokenService;
import com.example.schoolmnt.sm.student.StudentRepository;
import com.example.schoolmnt.sm.teacher.TeacherRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "Username %s not found";
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, username)));
    }

    public String signUpUser(AppUser appUser) {
        String encodedPassword = passwordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );

        appUserRepository.save(appUser);

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        return token;
    }

    public int enableAppUser(String email) {
        return appUserRepository.enableAppUser(email);
    }

    public String checkExistence(String email, String username) {
        boolean emailExists = appUserRepository.findByEmail(email).isPresent()
                || teacherRepository.findByEmail(username).isPresent()
                || studentRepository.findByEmail(username).isPresent();
        boolean usernameExists = appUserRepository.findByUsername(username).isPresent();
        String message="ok";
        if (emailExists) {
            message = "Email already exists";
            return message;
        }

        if (usernameExists) {
            message = "Username already exists";
            return message;
        }
        return message;
    }

    public String changePassword(ChangePasswordRequest changePasswordRequest, UserDetails userDetails) {
        AppUser user = (AppUser) userDetails;
        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            return "Wrong password";
        }
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmationPassword())) {
            return "Password are not the same";
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        appUserRepository.save(user);
        return "Password changed";
    }

    public void deleteAppuserByEmail(String email) {
        AppUser appUser = appUserRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email not found!"));
        appUserRepository.deleteById(appUser.getId());
    }

    @Scheduled(fixedRate = 1800000)  // 30 phút
    public void deleteAppUser() {
        List<AppUser> usersToDelete = appUserRepository.findAllByEnabledAndNoConfirmationToken(0);
        if (!usersToDelete.isEmpty()) {
            appUserRepository.deleteAll(usersToDelete);
        }
    }

}
