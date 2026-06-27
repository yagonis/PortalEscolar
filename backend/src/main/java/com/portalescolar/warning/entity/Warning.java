package com.portalescolar.warning.entity;

import com.portalescolar.user.entity.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "warnings")
public class Warning {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name= "title", nullable = false, length = 150)
    private String title;

    @Column(name="content",columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @Column(name = "pinned", nullable = false)
    private Boolean pinned = false;

    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;


    public void pin() {
        this.pinned = true;
    }
    public void unpin() {
        this.pinned = false;
    }
    public void archive() {
        this.active = false;
    }
    public void unarchive() {
        this.active = true;
    }
    public boolean isUrgent() {
        return priority == Priority.HIGH;
    }

}
