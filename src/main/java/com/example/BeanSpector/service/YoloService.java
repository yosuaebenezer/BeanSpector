package com.example.BeanSpector.service;

import org.opencv.core.*;
import org.opencv.dnn.Net;
import org.opencv.dnn.Dnn;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class YoloService {
    private final Net net; // Menjadikan net sebagai final
    private final double confidenceThreshold = 0.7; // Ambang batas kepercayaan default, dijadikan final

    public YoloService() {
        // Memuat library OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Membaca model YOLO dari file konfigurasi dan bobot
        this.net = Dnn.readNetFromDarknet("src/main/resources/custom_yolov4.cfg", "src/main/resources/custom_yolov4.weights");
    }

    public List<Rect> detectObjects(Mat image) {
        // Membuat blob dari gambar yang diberikan
        Mat blob = Dnn.blobFromImage(image, 1 / 255.0, new Size(640, 640), new Scalar(0, 0, 0), true, false);
        net.setInput(blob); // Mengatur input untuk model

        // Menyimpan output dari deteksi
        List<Mat> outputs = new ArrayList<>();
        net.forward(outputs, net.getUnconnectedOutLayersNames()); // Melakukan forward pass

        List<Rect> boxes = new ArrayList<>(); // Menyimpan kotak deteksi

        // Memproses output untuk mendapatkan kotak deteksi
        for (Mat output : outputs) {
            for (int i = 0; i < output.rows(); i++) {
                double confidence = output.get(i, 4)[0]; // Mengambil nilai kepercayaan

                // Memeriksa apakah kepercayaan lebih tinggi dari ambang batas
                if (confidence > confidenceThreshold) {
                    int x = (int) (output.get(i, 0)[0] * image.cols());
                    int y = (int) (output.get(i, 1)[0] * image.rows());
                    int width = (int) (output.get(i, 2)[0] * image.cols());
                    int height = (int) (output.get(i, 3)[0] * image.rows());

                    // Menambahkan kotak deteksi ke daftar
                    boxes.add(new Rect(x - width / 2, y - height / 2, width, height));
                }
            }
        }

        return boxes; // Mengembalikan daftar kotak deteksi
    }
}