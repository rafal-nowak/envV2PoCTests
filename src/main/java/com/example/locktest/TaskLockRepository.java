package com.example.locktest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

public interface TaskLockRepository extends JpaRepository<TaskLock, UUID> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<TaskLock> findByTaskId(UUID taskId);

}
