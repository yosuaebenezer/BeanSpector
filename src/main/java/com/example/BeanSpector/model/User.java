package com.example.BeanSpector.model;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Lob;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name; // Tipe data String valid untuk kolom
    private String email; // Tipe data String valid untuk kolom

    @Lob // Large object for storing images in db.
    private byte[] image; // Store image as byte array

    private String analysisResult; // Tipe data String valid untuk kolom
}