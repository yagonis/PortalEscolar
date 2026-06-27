package com.portalescolar.poll.service;
import com.portalescolar.poll.dto.*;
import com.portalescolar.poll.entity.*;
import com.portalescolar.poll.mapper.PollMapper;
import com.portalescolar.poll.repository.*;
import com.portalescolar.shared.exception.BusinessRuleException;
import com.portalescolar.shared.exception.ResourceNotFoundException;
import com.portalescolar.user.entity.User;
import com.portalescolar.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PollService {
    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final PollVoteRepository pollVoteRepository;
    private final PollMapper pollMapper;
    private final UserRepository userRepository;

    @Transactional
    public PollResponseDto save(PollRequestDto dto) {
        if (!dto.closesAt().isAfter(dto.opensAt())) {
            throw new BusinessRuleException("A data de encerramento deve ser após a data de abertura.");
        }

        Poll poll = pollMapper.toEntity(dto);
        poll.setStatus(PollStatus.OPEN);

        // salva o poll primeiro para ter o ID
        Poll saved = pollRepository.save(poll);

        // cria e vincula as opções
        List<PollOption> options = dto.options().stream()
                .map(optDto -> {
                    PollOption option = pollMapper.toOptionEntity(optDto);
                    option.setPoll(saved);
                    return option;
                })
                .toList();

        pollOptionRepository.saveAll(options);
        saved.setOptions(options);

        return pollMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<PollResponseDto> findAll(Pageable pageable, String status) {
        if (status != null) {
            PollStatus statusEnum;
            try {
                statusEnum = PollStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessRuleException("Status inválido: " + status);
            }
            return pollRepository.findAllByStatus(statusEnum, pageable)
                    .map(pollMapper::toResponseDto);
        }
        return pollRepository.findAll(pageable)
                .map(pollMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public PollResponseDto findById(UUID id) {
        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enquete não encontrada."));
        return pollMapper.toResponseDto(poll);
    }

    @Transactional
    public PollResponseDto update(UUID id, PollRequestDto dto) {
        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enquete não encontrada."));

        if (poll.hasVotes()) {
            throw new BusinessRuleException("Não é possível editar uma enquete que já possui votos.");
        }

        if (!dto.closesAt().isAfter(dto.opensAt())) {
            throw new BusinessRuleException("A data de encerramento deve ser após a data de abertura.");
        }

        pollMapper.updateEntityFromDto(dto, poll);

        // recria as opções
        pollOptionRepository.deleteAll(poll.getOptions());

        List<PollOption> options = dto.options().stream()
                .map(optDto -> {
                    PollOption option = pollMapper.toOptionEntity(optDto);
                    option.setPoll(poll);
                    return option;
                })
                .toList();

        pollOptionRepository.saveAll(options);
        poll.setOptions(options);

        return pollMapper.toResponseDto(pollRepository.save(poll));
    }

    @Transactional
    public PollResponseDto close(UUID id) {
        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enquete não encontrada."));

        if (poll.getStatus() == PollStatus.CLOSED) {
            throw new BusinessRuleException("Enquete já está encerrada.");
        }

        poll.close();
        return pollMapper.toResponseDto(pollRepository.save(poll));
    }

    @Transactional
    public PollResponseDto cancel(UUID id) {
        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enquete não encontrada."));

        if (poll.getStatus() == PollStatus.CANCELLED) {
            throw new BusinessRuleException("Enquete já está cancelada.");
        }

        poll.cancel();
        return pollMapper.toResponseDto(pollRepository.save(poll));
    }

    @Transactional
    public void vote(UUID pollId, PollVoteRequestDto dto, UUID userId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("Enquete não encontrada."));

        if (!poll.isOpen()) {
            throw new BusinessRuleException("Enquete não está aberta para votação.");
        }

        if (!poll.getAllowMultipleVotes() && pollVoteRepository.existsByPollIdAndUserId(pollId, userId)) {
            throw new BusinessRuleException("Você já votou nesta enquete.");
        }

        PollOption option = pollOptionRepository.findByIdAndPollId(dto.optionId(), pollId)
                .orElseThrow(() -> new BusinessRuleException("Opção não pertence a esta enquete."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        PollVote vote = PollVote.builder()
                .poll(poll)
                .option(option)
                .user(user)
                .build();

        pollVoteRepository.save(vote);
    }

    @Transactional(readOnly = true)
    public PollResultResponseDto getResult(UUID id) {
        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enquete não encontrada."));

        int total = poll.totalVotes();

        List<PollOptionResultDto> optionResults = poll.getOptions().stream()
                .map(option -> new PollOptionResultDto(
                        option.getId(),
                        option.getText(),
                        option.getDisplayOrder(),
                        option.totalVotes(),
                        option.percentage(total)
                ))
                .toList();

        return new PollResultResponseDto(
                poll.getId(),
                poll.getQuestion(),
                poll.getStatus().name(),
                total,
                poll.getStatus() == PollStatus.CLOSED,
                optionResults
        );
    }

    @Transactional
    public void delete(UUID id) {
        Poll poll = pollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enquete não encontrada."));

        if (poll.hasVotes()) {
            throw new BusinessRuleException("Não é possível excluir uma enquete que já possui votos. Cancele-a.");
        }

        pollRepository.delete(poll);
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void closeExpiredPolls() {
        List<Poll> expired = pollRepository.findAllByStatusAndClosesAtBefore(
                PollStatus.OPEN, LocalDateTime.now()
        );
        expired.forEach(Poll::close);
        pollRepository.saveAll(expired);
    }
}
