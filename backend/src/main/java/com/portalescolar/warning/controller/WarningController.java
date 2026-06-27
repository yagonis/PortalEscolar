package com.portalescolar.warning.controller;
import com.portalescolar.warning.dto.WarningRequestDto;
import com.portalescolar.warning.dto.WarningResponseDto;
import com.portalescolar.warning.service.WarningService;
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
@RequestMapping("/api/warnings")
@RequiredArgsConstructor
public class WarningController {
    private final WarningService warningService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<WarningResponseDto>> findAll(
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Boolean includeInactive,
            Pageable pageable) {
        return ResponseEntity.ok(warningService.findAll(pageable, priority));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WarningResponseDto> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(warningService.findById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WarningResponseDto> save(@RequestBody @Valid WarningRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(warningService.save(dto));
    }

    @PutMapping(value="/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WarningResponseDto> update(
            @PathVariable UUID id,
            @RequestBody @Valid WarningRequestDto dto) {
        return ResponseEntity.ok(warningService.update(id, dto));
    }

    @PatchMapping("/{id}/pin")
    public ResponseEntity<WarningResponseDto> togglePin(@PathVariable UUID id) {
        return ResponseEntity.ok(warningService.togglePin(id));
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<WarningResponseDto> archive(@PathVariable UUID id) {
        return ResponseEntity.ok(warningService.archive(id));
    }

    @DeleteMapping(value = "/{id}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        warningService.delete(id);
        return ResponseEntity.noContent().build();
    }


}