package com.example.locktest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
public class TaskLock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true)
    private UUID taskId;

    private UUID userId;

    LocalDateTime timestamp;

    public TaskLock(UUID taskId, UUID userId, LocalDateTime timestamp) {
        this.taskId = taskId;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public boolean isLockedBy(UUID id) {
        return id == userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskLock taskLock = (TaskLock) o;

        return taskId != null ? taskId.equals(taskLock.taskId) : taskLock.taskId == null;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
