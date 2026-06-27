package com.portalescolar.poll.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "polls")
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "question", nullable = false, length = 500)
    private String question;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PollStatus status;

    @Column(name = "opens_at", nullable = false)
    private LocalDateTime opensAt;

    @Column(name = "closes_at", nullable = false)
    private LocalDateTime closesAt;

    @Builder.Default
    @Column(name = "allow_multiple_votes", nullable = false)
    private Boolean allowMultipleVotes = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PollOption> options = new ArrayList<>();

    public void close() {
        this.status = PollStatus.CLOSED;
    }

    public void cancel() {
        this.status = PollStatus.CANCELLED;
    }

    public boolean isOpen() {
        return this.status == PollStatus.OPEN
                && LocalDateTime.now().isBefore(this.closesAt);
    }

    public boolean isExpired() {
        return this.status == PollStatus.OPEN
                && LocalDateTime.now().isAfter(this.closesAt);
    }

    public boolean hasVotes() {
        return this.options.stream()
                .anyMatch(o -> !o.getVotes().isEmpty());
    }

    public int totalVotes() {
        return this.options.stream()
                .mapToInt(o -> o.getVotes().size())
                .sum();
    }

}
