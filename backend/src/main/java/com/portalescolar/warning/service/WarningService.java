package com.portalescolar.warning.service;

import com.portalescolar.shared.exception.BusinessRuleException;
import com.portalescolar.shared.exception.ResourceNotFoundException;
import com.portalescolar.warning.dto.WarningRequestDto;
import com.portalescolar.warning.dto.WarningResponseDto;
import com.portalescolar.warning.entity.Priority;
import com.portalescolar.warning.entity.Warning;
import com.portalescolar.warning.mapper.WarningMapper;
import com.portalescolar.warning.repository.WarningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@RequiredArgsConstructor
@Service
public class WarningService {
    private final WarningRepository warningRepository;
    private final WarningMapper warningMapper;

    @Transactional
    public WarningResponseDto save(WarningRequestDto dto) {
        Priority priority;
        try {
            priority = Priority.valueOf(dto.priority().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Prioridade inválida: " + dto.priority());
        }

        Warning warning = warningMapper.toEntity(dto);
        warning.setPriority(priority);
        warning.setActive(true);

        return warningMapper.toResponseDto(warningRepository.save(warning));
    }

    @Transactional(readOnly = true)
    public Page<WarningResponseDto> findAll(Pageable pageable, String priority) {
        if (priority != null) {
            Priority priorityEnum;
            try {
                priorityEnum = Priority.valueOf(priority.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessRuleException("Prioridade inválida: " + priority);
            }
            return warningRepository.findAllByActiveTrueAndPriority(priorityEnum, pageable)
                    .map(warningMapper::toResponseDto);
        }
        return warningRepository.findAllByActiveTrue(pageable)
                .map(warningMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public WarningResponseDto findById(UUID id) {
        Warning warning = warningRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aviso não encontrado."));
        return warningMapper.toResponseDto(warning);
    }

    @Transactional
    public WarningResponseDto update(UUID id, WarningRequestDto dto) {
        Warning warning = warningRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aviso não encontrado."));

        if (!warning.getActive()) {
            throw new BusinessRuleException("Não é possível editar um aviso arquivado.");
        }

        Priority priority;
        try {
            priority = Priority.valueOf(dto.priority().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Prioridade inválida: " + dto.priority());
        }

        warningMapper.updateEntityFromDto(dto, warning);
        warning.setPriority(priority);

        return warningMapper.toResponseDto(warningRepository.save(warning));
    }

    @Transactional
    public WarningResponseDto togglePin(UUID id) {
        Warning warning = warningRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aviso não encontrado."));

        if (!warning.getActive()) {
            throw new BusinessRuleException("Não é possível fixar um aviso arquivado.");
        }

        if (warning.getPinned()) {
            warning.unpin();
        } else {
            warning.pin();
        }

        return warningMapper.toResponseDto(warningRepository.save(warning));
    }

    @Transactional
    public WarningResponseDto archive(UUID id) {
        Warning warning = warningRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aviso não encontrado."));

        if (!warning.getActive()) {
           warning.unarchive();
        }else {
            warning.archive();
        }

        return warningMapper.toResponseDto(warningRepository.save(warning));
    }
    //hard delete
    @Transactional
    public void delete(UUID id) {
        Warning warning = warningRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aviso não encontrado."));

        if (warning.getActive()) {
            throw new BusinessRuleException("Arquive o aviso antes de excluí-lo.");
        }

        warningRepository.delete(warning);
    }

}
