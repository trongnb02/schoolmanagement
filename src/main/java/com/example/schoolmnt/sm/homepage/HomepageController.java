package com.example.schoolmnt.sm.homepage;

import com.example.schoolmnt.sm.registration.RegistrationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomepageController {

    @GetMapping("/")
    public String homepage() {
        return "index";
    }

}
