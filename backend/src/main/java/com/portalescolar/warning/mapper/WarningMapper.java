package com.portalescolar.warning.mapper;

import com.portalescolar.warning.dto.WarningRequestDto;
import com.portalescolar.warning.dto.WarningResponseDto;
import com.portalescolar.warning.entity.Warning;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WarningMapper {
    @Mapping(target = "id", ignore = true)         // Gerado pelo banco (UUID)
    @Mapping(target = "createdAt", ignore = true)  // Gerado pelo Hibernate (@CreationTimestamp)
    @Mapping(target = "active", ignore = true)     // Protegido pelo @Builder.Default (true)
    @Mapping(target = "pinned", defaultValue = "false") // Garante o default caso o DTO venha sem esse campo
    Warning toEntity(WarningRequestDto dto);

    // 2. Retorno (GET/POST/PUT): Entidade -> ResponseDto
    // O MapStruct converte Priority (Enum) para String e LocalDateTime para LocalDate automaticamente
    WarningResponseDto toResponseDto(Warning entity);

    // 3. Atualização (PUT): RequestDto -> Entidade Existente
    // Atualiza apenas os dados permitidos em um aviso já existente no banco
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntityFromDto(WarningRequestDto dto, @MappingTarget Warning entity);
}
