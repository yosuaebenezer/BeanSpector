package com.example.BeanSpector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class BeanSpectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeanSpectorApplication.class, args);

		// Membuat instance RestTemplate
		RestTemplate restTemplate = new RestTemplate();

		// Akses endpoint /api/hello dari Flask
		String flaskUrl = "http://localhost:5000/api/hello";
		ResponseEntity<String> response = restTemplate.getForEntity(flaskUrl, String.class);
		System.out.println("Response from Flask API: " + response.getBody());

		// Akses endpoint /api/sum dari Flask
		String sumUrl = "http://localhost:5000/api/sum";
		String jsonPayload = "{\"num1\": 10, \"num2\": 20}";

		// Mengirim data ke Flask dan mendapatkan hasilnya
		ResponseEntity<String> sumResponse = restTemplate.postForEntity(sumUrl, jsonPayload, String.class);
		System.out.println("Response from Flask /api/sum: " + sumResponse.getBody());
	}
}