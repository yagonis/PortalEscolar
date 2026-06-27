package com.portalescolar.news.controller;

import com.portalescolar.news.dto.NewsRequestDto;
import com.portalescolar.news.dto.NewsResponseDto;
import com.portalescolar.news.service.NewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/news")
public class NewsController {

    private final NewsService newsService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<NewsResponseDto>> findAll(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "false") boolean isAdmin,
            Pageable pageable) {
        return ResponseEntity.ok(newsService.findAll(pageable, status, isAdmin));
    }

    @GetMapping(value="/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NewsResponseDto> findById(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean isAdmin) {
        return ResponseEntity.ok(newsService.findById(id, isAdmin));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NewsResponseDto> save(@RequestBody @Valid NewsRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(newsService.save(dto));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NewsResponseDto> update(
            @PathVariable UUID id,
            @RequestBody @Valid NewsRequestDto dto) {
        return ResponseEntity.ok(newsService.update(id, dto));
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<NewsResponseDto> publish(@PathVariable UUID id) {
        return ResponseEntity.ok(newsService.publish(id));
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<NewsResponseDto> archive(@PathVariable UUID id) {
        return ResponseEntity.ok(newsService.archive(id));
    }

    @PatchMapping("/{id}/draft")
    public ResponseEntity<NewsResponseDto> backToDraft(@PathVariable UUID id) {
        return ResponseEntity.ok(newsService.backToDraft(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        newsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}