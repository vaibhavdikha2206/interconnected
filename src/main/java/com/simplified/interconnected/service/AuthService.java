package com.simplified.interconnected.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.simplified.interconnected.dto.AuthResponseDTO;
import com.simplified.interconnected.dto.GLoginDto;
import com.simplified.interconnected.models.Role;
import com.simplified.interconnected.models.UserEntity;
import com.simplified.interconnected.repository.RoleRepository;
import com.simplified.interconnected.repository.UserRepository;
import com.simplified.interconnected.security.JWTGenerator;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.UUID;

@Component
@Transactional
public class AuthService {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JWTGenerator jwtGenerator;

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public UserEntity registerUser(UserEntity user) {
        user.setEnabled(true);
        //user.setEmailVerified(false);
        Role roles = roleRepository.findByName("OAUTH2_USER").get();
        user.setRoles(Collections.singletonList(roles));
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        return user;

        //sendAccountVerificationMail(user);
    }

    private AuthResponseDTO saveGoogleToken(String username) {
        String jwtToken = jwtGenerator.generateToken(username);  // Create JWT for your application
        return new AuthResponseDTO(jwtToken);
    }

    private UserEntity getProfileDetailsGoogle(GLoginDto gLoginDto) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(gLoginDto.getClientId()))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

        // (Receive idTokenString by HTTPS POST)

        GoogleIdToken idToken = verifier.verify(gLoginDto.getCredential());
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("Google User ID: " + userId);

            // Get profile information from payload
            if(!payload.getEmailVerified()) {
                System.out.println("Email not verified for " + payload.getEmail());
            }
            UserEntity user = new UserEntity();
            user.setUsername(payload.getEmail());
            user.setEmail(payload.getEmail());
            user.setLocale((String) payload.get("locale"));
            user.setFirstName((String) payload.get("given_name"));
            user.setLastName((String) payload.get("family_name"));
            user.setPictureUrl((String) payload.get("picture"));
            user.setPassword(UUID.randomUUID().toString());

            return user;

        } else {
            System.out.println("Invalid ID token.");
        }
        return null;
    }

    public AuthResponseDTO processGoogleCred(GLoginDto gloginDto) throws GeneralSecurityException, IOException {
        UserEntity googleUser;
        try {
            googleUser = getProfileDetailsGoogle(gloginDto);
        } catch (Exception e) {
            System.out.println("Google Authentication Failed:" + e.getMessage());
            return null;
        }
        if (googleUser ==  null)
        {
            System.out.println("Google User not found");
            return null;
        }
        UserEntity user = userRepository.findByEmail(googleUser.getEmail());

        if(user == null) {
            user = registerUser(googleUser);
        }

        return saveGoogleToken(user.getUsername());
    }
}
