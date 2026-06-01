package com.recrutement.auth.service;

import com.recrutement.auth.config.JwtUtils;
import com.recrutement.auth.dto.AuthRequest;
import com.recrutement.auth.dto.AuthResponse;
import com.recrutement.auth.dto.RegisterRequest;
import com.recrutement.auth.dto.UserDto;
import com.recrutement.auth.model.Role;
import com.recrutement.auth.model.User;
import com.recrutement.auth.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @PostConstruct
    public void initDefaultUsers() {
        if (userRepository.count() == 0) {
            // Responsable RH (Admin)
            User rh = User.builder()
                    .username("rh")
                    .email("rh@recrutement.com")
                    .password(passwordEncoder.encode("rh123"))
                    .fullName("Responsable RH")
                    .roles(Set.of(Role.RESPONSABLE_RH))
                    .build();
            userRepository.save(rh);

            // Recruteur
            User recruteur = User.builder()
                    .username("recruteur")
                    .email("recruteur@recrutement.com")
                    .password(passwordEncoder.encode("recruteur123"))
                    .fullName("Recruteur Principal")
                    .roles(Set.of(Role.RECRUTEUR))
                    .build();
            userRepository.save(recruteur);

            // Responsable Département
            User dept = User.builder()
                    .username("dept")
                    .email("dept@recrutement.com")
                    .password(passwordEncoder.encode("dept123"))
                    .fullName("Responsable Dép IT")
                    .roles(Set.of(Role.RESPONSABLE_DEPT))
                    .build();
            userRepository.save(dept);

            // Candidat
            User candidat = User.builder()
                    .username("candidat")
                    .email("candidat@email.com")
                    .password(passwordEncoder.encode("candidat123"))
                    .fullName("John Doe")
                    .roles(Set.of(Role.CANDIDAT))
                    .build();
            userRepository.save(candidat);

            User admin = User.builder()
                    .username("admin")
                    .email("admin@recrutement.com")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Administrateur")
                    .roles(Set.of(Role.ADMIN, Role.RESPONSABLE_RH))
                    .build();
            userRepository.save(admin);
        }
    }

    public AuthResponse login(AuthRequest request) {
        String login = request.getUsername() == null ? "" : request.getUsername().trim();
        User user = userRepository.findByUsername(login)
                .or(() -> userRepository.findByEmail(login))
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        String token = jwtUtils.generateToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .build();
    }

    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Nom d'utilisateur déjà pris");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà associé à un compte");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(Role.CANDIDAT);

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .roles(roles)
                .build();

        User savedUser = userRepository.save(user);

        return mapToDto(savedUser);
    }

    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return mapToDto(user);
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .build();
    }
}
