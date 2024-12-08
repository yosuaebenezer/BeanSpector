package com.example.BeanSpector.controller;

import com.example.BeanSpector.model.User;
import com.example.BeanSpector.repository.UserRepository;
import com.example.BeanSpector.service.YoloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.CvType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private YoloService yoloService;

    @GetMapping("/")
    public String index() {
        return "index"; // Mengembalikan halaman index.html.
    }

    @PostMapping("/analyze")
    public String analyze(@RequestParam("name") String name,
                          @RequestParam("email") String email,
                          @RequestParam("image") MultipartFile image,
                          Model model) throws IOException {

        if (image.getSize() > 5 * 1024 * 1024) { // Batasi ukuran gambar maksimal 5MB
            model.addAttribute("error", "File terlalu besar! Maksimal ukuran file adalah 5MB.");
            return "index"; // Kembali ke halaman index jika ukuran file terlalu besar
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setImage(image.getBytes());

        // Mengonversi MultipartFile ke Mat untuk pemrosesan OpenCV
        Mat img = convertToMat(image); // Memanggil metode konversi

        // Memanggil metode deteksi YOLO
        List<Rect> detectedBoxes = yoloService.detectObjects(img);

        // Menyimpan informasi pengguna dan hasil analisis
        user.setAnalysisResult("Detected " + detectedBoxes.size() + " objects.");
        userRepository.save(user); // Menyimpan data pengguna ke database

        model.addAttribute("result", user.getAnalysisResult());
        return "result"; // Mengarahkan ke halaman hasil.
    }

    private Mat convertToMat(MultipartFile image) throws IOException {
        // Mengonversi MultipartFile ke BufferedImage
        InputStream inputStream = new ByteArrayInputStream(image.getBytes());
        BufferedImage bufferedImage = ImageIO.read(inputStream);

        // Mengonversi BufferedImage ke Mat
        Mat mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC3);

        // Menyalin data piksel dari BufferedImage ke Mat
        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                int rgb = bufferedImage.getRGB(x, y);
                mat.put(y, x, new byte[]{
                        (byte) ((rgb >> 16) & 0xff), // Merah
                        (byte) ((rgb >> 8) & 0xff),  // Hijau
                        (byte) (rgb & 0xff)           // Biru
                });
            }
        }

        return mat; // Mengembalikan objek Mat yang telah dikonversi
    }
}