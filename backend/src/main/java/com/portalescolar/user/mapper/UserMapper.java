package com.portalescolar.user.mapper;

import com.portalescolar.user.dto.UserRequestDto;
import com.portalescolar.user.dto.UserResponseDto;
import com.portalescolar.user.dto.UserUpdateRequestDto;
import com.portalescolar.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target= "password", ignore = true)
    User toEntity(UserRequestDto dto);


    UserResponseDto toResponseDTO(User entity);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(UserUpdateRequestDto dto, @MappingTarget User entity);
}
