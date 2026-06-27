package com.portalescolar.poll.controller;
import com.portalescolar.poll.dto.*;
import com.portalescolar.poll.service.PollService;
import com.portalescolar.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/polls", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PollController {
    private final PollService pollService;

    @GetMapping
    public ResponseEntity<Page<PollResponseDto>> findAll(
            @RequestParam(required = false) String status,
            Pageable pageable) {
        return ResponseEntity.ok(pollService.findAll(pageable, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PollResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(pollService.findById(id));
    }

    @GetMapping("/{id}/result")
    public ResponseEntity<PollResultResponseDto> getResult(@PathVariable UUID id) {
        return ResponseEntity.ok(pollService.getResult(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PollResponseDto> save(@RequestBody @Valid PollRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pollService.save(dto));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PollResponseDto> update(
            @PathVariable UUID id,
            @RequestBody @Valid PollRequestDto dto) {
        return ResponseEntity.ok(pollService.update(id, dto));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<PollResponseDto> close(@PathVariable UUID id) {
        return ResponseEntity.ok(pollService.close(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<PollResponseDto> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(pollService.cancel(id));
    }

    @PostMapping(value = "/{id}/vote", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> vote(
            @PathVariable UUID id,
            @RequestBody @Valid PollVoteRequestDto dto,
            @AuthenticationPrincipal User user) {
        pollService.vote(id, dto, user.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        pollService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
