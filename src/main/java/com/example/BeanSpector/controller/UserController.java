package com.example.BeanSpector.controller;

import com.example.BeanSpector.model.User;
import com.example.BeanSpector.repository.UserRepository;
import com.example.BeanSpector.service.YoloService;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final YoloService yoloService;

    public UserController(UserRepository userRepository, YoloService yoloService) {
        this.userRepository = userRepository;
        this.yoloService = yoloService;
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

        // Convert MultipartFile to Mat for OpenCV processing
        Mat img = convertToMat(image);

        // Detect objects using YOLO service
        List<Rect> detectedBoxes = yoloService.detectObjects(img);

        // Set analysis result and save user to the database
        user.setAnalysisResult("Detected " + detectedBoxes.size() + " objects.");
        userRepository.save(user);

        model.addAttribute("result", user.getAnalysisResult());
        return "result"; // Redirect to results page
    }

    private User createUser(String name, String email, MultipartFile image) throws IOException {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setImage(image.getBytes());
        return user;
    }

    private Mat convertToMat(MultipartFile image) throws IOException {
        // Convert MultipartFile to BufferedImage
        InputStream inputStream = new ByteArrayInputStream(image.getBytes());
        BufferedImage bufferedImage = ImageIO.read(inputStream);

        // Create a Mat object from BufferedImage
        Mat mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC3);

        // Copy pixel data from BufferedImage to Mat
        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                int rgb = bufferedImage.getRGB(x, y);
                mat.put(y, x, new byte[]{
                        (byte) ((rgb >> 16) & 0xff), // Red
                        (byte) ((rgb >> 8) & 0xff),  // Green
                        (byte) (rgb & 0xff)           // Blue
                });
            }
        }

        return mat; // Return the converted Mat object
    }
}