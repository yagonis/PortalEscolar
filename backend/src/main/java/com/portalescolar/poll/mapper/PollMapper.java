package com.portalescolar.poll.mapper;
import com.portalescolar.poll.dto.*;
import com.portalescolar.poll.entity.Poll;
import com.portalescolar.poll.entity.PollOption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PollMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "options", ignore = true)
    Poll toEntity(PollRequestDto dto);

    @Mapping(source = "status", target = "status")
    PollResponseDto toResponseDto(Poll entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "poll", ignore = true)
    @Mapping(target = "votes", ignore = true)
    @Mapping(source = "text", target = "text")
    @Mapping(source = "displayOrder", target = "displayOrder")
    PollOption toOptionEntity(PollOptionRequestDto dto);

    PollOptionResponseDto toOptionResponseDto(PollOption entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "options", ignore = true)
    void updateEntityFromDto(PollRequestDto dto, @MappingTarget Poll entity);
}
