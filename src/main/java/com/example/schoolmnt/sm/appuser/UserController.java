package com.example.schoolmnt.sm.appuser;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@AllArgsConstructor
public class UserController {

    private final AppUserService appUserService;

    @GetMapping("/user/info")
    public String info(@AuthenticationPrincipal UserDetails userDetails, Model model){

        model.addAttribute("user", (AppUser)appUserService.loadUserByUsername(userDetails.getUsername()));
        return "user/info";
    }

    @PostMapping("/user/changepassword")
    public String changePassword(@ModelAttribute ChangePasswordRequest changePasswordRequest,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model){
        String msg = appUserService.changePassword(changePasswordRequest, userDetails);
        model.addAttribute("user", (AppUser)appUserService.loadUserByUsername(userDetails.getUsername()));
        model.addAttribute("msg", msg);
        return "user/info";
    }

}
