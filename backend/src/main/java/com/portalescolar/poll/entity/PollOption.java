package com.portalescolar.poll.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "poll_options")
public class PollOption {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @Column(name = "text", nullable = false, length = 300)
    private String text;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Builder.Default
    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL)
    private List<PollVote> votes = new ArrayList<>();

    public int totalVotes() {
        return this.votes.size();
    }

    public double percentage(int total) {
        if (total == 0) return 0.0;
        return (this.votes.size() * 100.0) / total;
    }
}
