package com.campus.portal.service;

import com.campus.portal.dto.AuthDTO;
import com.campus.portal.entity.*;
import com.campus.portal.exception.BadRequestException;
import com.campus.portal.repository.*;
import com.campus.portal.security.JwtUtils;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authManager, UserRepository userRepository,
                       RoleRepository roleRepository, DepartmentRepository departmentRepository,
                       JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthDTO.JwtResponse login(AuthDTO.LoginRequest request) {
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        String token = jwtUtils.generateToken(auth);
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new BadRequestException("User not found"));
        return new AuthDTO.JwtResponse(
            token, user.getId(), user.getUsername(), user.getEmail(),
            user.getFullName(), user.getRole().getName().name(),
            user.getDepartment() != null ? user.getDepartment().getId() : null
        );
    }

    public String register(AuthDTO.RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new BadRequestException("Username is already taken");
        if (userRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("Email is already registered");

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setActive(true);

        RoleName roleName = RoleName.ROLE_STUDENT;
        if (request.getRole() != null) {
            try { roleName = RoleName.valueOf(request.getRole()); } catch (Exception ignored) {}
        }
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new BadRequestException("Role not found"));
        user.setRole(role);

        if (request.getDepartmentId() != null) {
            departmentRepository.findById(request.getDepartmentId())
                .ifPresent(user::setDepartment);
        }

        userRepository.save(user);
        return "User registered successfully";
    }

    public String forgotPassword(AuthDTO.ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BadRequestException("User with this email not found"));

        String token = java.util.UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(java.time.LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        // In a real app, send email here. For demo, we just return the token or log it.
        System.out.println("🔑 Password reset token for " + user.getEmail() + ": " + token);
        return "Reset token generated successfully. Please check your email (simulated).";
    }

    public String resetPassword(AuthDTO.ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getToken())
            .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));

        if (user.getResetTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
            throw new BadRequestException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        return "Password has been reset successfully";
    }
}
