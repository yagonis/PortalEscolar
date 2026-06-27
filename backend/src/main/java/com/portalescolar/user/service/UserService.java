package com.portalescolar.user.service;


import com.portalescolar.shared.exception.BusinessRuleException;
import com.portalescolar.shared.exception.ResourceNotFoundException;
import com.portalescolar.user.dto.UserPasswordUpdateDto;
import com.portalescolar.user.dto.UserRequestDto;
import com.portalescolar.user.dto.UserResponseDto;
import com.portalescolar.user.dto.UserUpdateRequestDto;
import com.portalescolar.user.entity.Role;
import com.portalescolar.user.entity.User;
import com.portalescolar.user.mapper.UserMapper;
import com.portalescolar.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;
    @Transactional
    public UserResponseDto save(UserRequestDto dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new BusinessRuleException("E-mail já cadastrado.");
        }

        Role role;
        try {
            role = Role.valueOf(dto.role().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Role inválida: " + dto.role());
        }

        // Se tentar criar um ADMIN, só outro ADMIN pode
        if (role == Role.ADMIN) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User requester = (User) auth.getPrincipal();
            if (!requester.isAdmin()) {
                throw new BusinessRuleException("Apenas administradores podem criar outros administradores.");
            }
        }

        User user = mapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(role);
        user.setActive(true);

        return mapper.toResponseDTO(userRepository.save(user));
    }
    @Transactional(readOnly= true)
    public Page<UserResponseDto> findAll(Pageable pageable, String role) {
        if (role != null) {
            Role roleEnum;
            try {
                roleEnum = Role.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessRuleException("Role inválida: " + role);
            }
            return userRepository.findAllByRole(roleEnum, pageable)
                    .map(mapper::toResponseDTO);
        }
        return userRepository.findAll(pageable)
                .map(mapper::toResponseDTO);
    }
    @Transactional(readOnly= true)
    public UserResponseDto findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        return mapper.toResponseDTO(user);
    }

    public UserResponseDto update(UUID id, UserUpdateRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        if (!user.getEmail().equals(dto.email()) && userRepository.existsByEmail(dto.email())) {
            throw new BusinessRuleException("E-mail já está em uso por outro usuário.");
        }

        mapper.updateEntityFromDTO(dto, user);
        return mapper.toResponseDTO(userRepository.save(user));
    }
    @Transactional
    public void updatePassword(UUID id, UserPasswordUpdateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new BusinessRuleException("Senha atual incorreta.");
        }

        if (!dto.newPassword().equals(dto.passwordConfirmation())) {
            throw new BusinessRuleException("Nova senha e confirmação não conferem.");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }
    //soft dele because user can be reactivated
    @Transactional
    public UserResponseDto toggleActive(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        user.setActive(!user.getActive()); // inverte o estado atual
        return mapper.toResponseDTO(userRepository.save(user));
    }
    //Unused method because toggleActive is enough
    @Transactional
    public void deactivate(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        user.setActive(false); // soft delete
        userRepository.save(user);
    }

    @Transactional(readOnly= true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    }


}
