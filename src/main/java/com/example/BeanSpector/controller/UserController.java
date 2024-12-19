package com.example.BeanSpector.controller;

import com.example.BeanSpector.model.User;
import com.example.BeanSpector.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/analyze")
    public String analyze(@RequestParam("name") String name,
                          @RequestParam("email") String email,
                          @RequestParam("image") MultipartFile image,
                          Model model) throws IOException {
        if (image.getSize() > 5 * 1024 * 1024) {
            model.addAttribute("error", "File terlalu besar! Maksimal ukuran file adalah 5MB.");
            return "index";
        }

        byte[] imageBytes = image.getBytes();
        String flaskUrl = "http://127.0.0.1:5050/api/analyze";

        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename();
            }
        });

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(flaskUrl, body, String.class);
            model.addAttribute("result", response.getBody());
        } catch (Exception e) {
            model.addAttribute("error", "Gagal terhubung ke Flask API: " + e.getMessage());
            return "index";
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setImage(image.getOriginalFilename());
        userRepository.save(user);

        return "result";
    }
}
