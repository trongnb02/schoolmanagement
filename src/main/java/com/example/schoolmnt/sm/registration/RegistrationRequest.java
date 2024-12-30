package com.example.schoolmnt.sm.registration;

import com.example.schoolmnt.sm.appuser.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationRequest {
    private String fullname;
    private String username;
    private String email;
    private String password;
    private Role role;
}
