package com.example.BeanSpector.controller;

import com.example.BeanSpector.model.User;
import com.example.BeanSpector.repository.UserRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
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
        return "index";
    }

    @PostMapping("/analyze")
    public String analyze(@RequestParam("name") String name,
                          @RequestParam("email") String email,
                          @RequestParam("image") MultipartFile image,
                          Model model) throws IOException {

        // Validate file size
        if (image.getSize() > 5 * 1024 * 1024) {
            model.addAttribute("error", "File terlalu besar! Maksimal ukuran file adalah 5MB.");
            return "index";
        }

        // Create and save user information
        User user = createUser(name, email, image);

        // Send the image to Flask API for object detection
        byte[] imageBytes = image.getBytes();
        String flaskUrl = "http://localhost:5000/api/detect";

        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new ByteArrayResource(imageBytes));

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body);
        ResponseEntity<String> response = restTemplate.postForEntity(flaskUrl, request, String.class);

        // Process response from Flask
        String result = response.getBody();
        model.addAttribute("result", result);

        // Save the user with the analysis result in the database
        user.setAnalysisResult(result);
        userRepository.save(user);

        return "result"; // Redirect to results page
    }

    private User createUser(String name, String email, MultipartFile image) throws IOException {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setImage(image.getBytes());
        return user;
    }
}