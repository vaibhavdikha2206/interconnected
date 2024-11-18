package com.simplified.interconnected.controllers;

import com.simplified.interconnected.dto.AuthResponseDTO;
import com.simplified.interconnected.dto.GLoginDto;
import com.simplified.interconnected.dto.LoginDto;
import com.simplified.interconnected.dto.RegisterDto;
import com.simplified.interconnected.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDto loginDto){
        return new ResponseEntity<>(authService.loginWithPassword(loginDto.getUsername(), loginDto.getPassword()), HttpStatus.OK);
    }

    @PostMapping("googleLogin")
    public ResponseEntity<AuthResponseDTO> googleLogin(@RequestBody GLoginDto gloginDto) throws GeneralSecurityException, IOException {
        return new ResponseEntity<>(authService.processGoogleCred(gloginDto), HttpStatus.OK);
    }

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
//        if (userRepository.existsByUsername(registerDto.getUsername())) {
//            return new ResponseEntity<>("Username is taken!", HttpStatus.BAD_REQUEST);
//        }
//
//        UserEntity user = new UserEntity();
//        user.setUsername(registerDto.getUsername());
//        user.setPassword(passwordEncoder.encode((registerDto.getPassword())));
//
//        Role roles = roleRepository.findByName("USER").get();
//        user.setRoles(Collections.singletonList(roles));
//
//        userRepository.save(user);

        return new ResponseEntity<>("User registration denied!", HttpStatus.NOT_IMPLEMENTED);
    }
}

