package com.sikeat.mailservice.Controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mail")
public class MailController {
    @GetMapping
    public String sendMail() {
        return "Mail Sent";
    }
}
