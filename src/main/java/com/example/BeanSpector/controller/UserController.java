package com.example.BeanSpector.controller;

import com.example.BeanSpector.model.User;
import com.example.BeanSpector.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String index() {
        return "index"; // Return the index.html page.
    }

    @PostMapping("/analyze")
    public String analyze(@RequestParam("name") String name,
                          @RequestParam("email") String email,
                          @RequestParam("image") MultipartFile image,
                          Model model) throws IOException {

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setImage(image.getBytes());

        // Perform analysis on the image (placeholder for analysis logic)
        String result = "Bagus"; // Example result based on analysis logic.
        user.setAnalysisResult(result);

        userRepository.save(user); // Save user data to database.

        model.addAttribute("result", result);
        return "result"; // Redirect to result page.
    }
}