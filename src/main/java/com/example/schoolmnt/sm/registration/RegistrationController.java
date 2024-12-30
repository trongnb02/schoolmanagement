package com.example.schoolmnt.sm.registration;

import com.example.schoolmnt.sm.student.Student;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @GetMapping("/register")
    public String regiterForm(Model model) {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        model.addAttribute("user", registrationRequest);
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegistrationRequest request, Model model) {
        String message = registrationService.register(request);
        if  (message != "ok") {
            model.addAttribute("message", message);
        } else {
            model.addAttribute("success", true);
        }
        model.addAttribute("user", new RegistrationRequest());
        return "register";
    }

    @ResponseBody
    @GetMapping("/registration/confirm")
    public String confirm(@RequestParam("token") String token) {
        return  registrationService.confirmToken(token);
    }
}
