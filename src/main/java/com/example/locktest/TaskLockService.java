package com.example.locktest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskLockService {

    private final TaskLockRepository taskLockRepository;

    @Transactional
    public Optional<LocalDateTime> trySetLock(UUID userId, UUID taskId, long seconds) {
        Optional<TaskLock> foundOptional = taskLockRepository.findByTaskId(taskId);
        if (foundOptional.isEmpty()) {
            LocalDateTime timestamp = LocalDateTime.now().plusSeconds(seconds);
            TaskLock taskLock = new TaskLock(taskId, userId, timestamp);
            taskLockRepository.save(taskLock);
            return Optional.of(timestamp);
        }

        var found = foundOptional.get();
        if (found.isLockedBy(userId)) {
            return Optional.of(found.getTimestamp());
        }

        return Optional.empty();
    }

    @Transactional
    public void removeLock(UUID userId, UUID taskId) {
        Optional<TaskLock> foundOptional = taskLockRepository.findByTaskId(taskId);
        if (foundOptional.isPresent()) {
            var found = foundOptional.get();
            if (found.isLockedBy(userId)) {
                taskLockRepository.deleteById(taskId);
            }
        }
    }

    @Transactional
    public void removeObsoleteLocks() {
        LocalDateTime now = LocalDateTime.now();
        Set<TaskLock> obsoleteLocks = taskLockRepository
                .findAll()
                .stream().filter(task -> task.getTimestamp().isBefore(now))
                .collect(Collectors.toSet());

        obsoleteLocks.forEach(taskLock -> taskLockRepository.deleteById(taskLock.getTaskId()));
    }

    @Transactional
    public boolean isTaskLocked(UUID taskId) {
        Optional<TaskLock> foundOptional = taskLockRepository.findByTaskId(taskId);
        return foundOptional.isPresent();
    }

}
