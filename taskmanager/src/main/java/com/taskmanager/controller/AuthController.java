package com.taskmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.taskmanager.config.JwtUtil;
import com.taskmanager.model.User;
import com.taskmanager.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserRepository repo;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user){
        User existing = repo.findByUsername(user.getUsername());
        if (existing != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists");
        }
        return ResponseEntity.ok(repo.save(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user){

        User existing = repo.findByUsername(user.getUsername());

        if(existing != null && existing.getPassword().equals(user.getPassword())){
            return ResponseEntity.ok(JwtUtil.generateToken(user.getUsername()));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
    }
}