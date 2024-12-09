package com.example.BeanSpector.repository;

import com.example.BeanSpector.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Tambahkan metode query jika diperlukan
}