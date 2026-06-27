package com.portalescolar.news.mapper;

import com.portalescolar.news.dto.NewsRequestDto;
import com.portalescolar.news.dto.NewsResponseDto;
import com.portalescolar.news.entity.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NewsMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "newsStatus", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    News toEntity(NewsRequestDto dto);

    @Mapping(source = "newsStatus", target = "status")
    NewsResponseDto toResponseDto(News entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "newsStatus", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(NewsRequestDto dto, @MappingTarget News entity);

}
