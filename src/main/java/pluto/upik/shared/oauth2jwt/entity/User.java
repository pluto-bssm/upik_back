package pluto.upik.shared.oauth2jwt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class User {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    private String role;

    private String username;

    private String name;

    private String email;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private double dollar;

    private double won;

    private long streakCount;

    @CreatedDate
    private LocalDateTime recentDate;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

    @Builder
    public User(String role, String username, String name, String email){
        this.role = role;
        this.username = username;
        this.name = name;
        this.email = email;
        this.createdAt = LocalDateTime.now();
        this.dollar = 0;
        this.won = 0;
        this.streakCount = 0;
        this.recentDate = LocalDateTime.now();
    }
}
