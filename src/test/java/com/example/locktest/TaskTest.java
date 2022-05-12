package com.example.locktest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskTest {

    @Autowired
    TaskService taskService;

    @Autowired
    TaskLockService taskLockService;

    @Test
    void should_be_possible_to_set_lock_on_task(){
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        taskLockService.trySetLock(userId, taskId, 2);

        assertTrue(taskLockService.isTaskLocked(taskId));
    }

    @Test
    void task_being_locked_should_not_be_executed() throws InterruptedException {
        Task task = Fixtures.testTask();
        taskService.save(task);

        UserTaskLocker userTaskLocker = new UserTaskLocker(UUID.randomUUID(), task, 1, 3, taskService, taskLockService);
        UserTaskExecutor userTaskExecutor = new UserTaskExecutor(UUID.randomUUID(), task, 2, 5, taskService, taskLockService);

        TestUtils.execute(List.of(userTaskLocker, userTaskExecutor));

        assertEquals(TaskStatus.SUBMITTED, task.getStatus());
    }

    @Test
    void task_being_executed_should_not_be_locked() throws InterruptedException {
        Task task = Fixtures.testTask();
        taskService.save(task);

        UserTaskLocker userTaskLocker = new UserTaskLocker(UUID.randomUUID(), task, 2, 5, taskService, taskLockService);
        UserTaskExecutor userTaskExecutor = new UserTaskExecutor(UUID.randomUUID(), task, 1, 3, taskService, taskLockService);

        TestUtils.execute(List.of(userTaskLocker, userTaskExecutor));

        assertEquals(TaskStatus.COMPLETED, task.getStatus());
    }

    @Test
    void task_being_updated_should_not_be_executed() throws InterruptedException {
        Task task = Fixtures.testTask();
        taskService.save(task);

        UserTaskUpdater userTaskUpdater = new UserTaskUpdater(UUID.randomUUID(), task, 1, 2, taskService, taskLockService);
        UserTaskExecutor userTaskExecutor = new UserTaskExecutor(UUID.randomUUID(), task, 2, 3, taskService, taskLockService);

        TestUtils.execute(List.of(userTaskUpdater, userTaskExecutor));

        assertEquals(TaskStatus.SUBMITTED, task.getStatus());
        assertEquals(2L, task.getVersion());
    }

    @Test
    void task_being_executed_should_not_be_updated() throws InterruptedException {
        Task task = Fixtures.testTask();
        taskService.save(task);

        UserTaskUpdater userTaskUpdater = new UserTaskUpdater(UUID.randomUUID(), task, 2, 5, taskService, taskLockService);
        UserTaskExecutor userTaskExecutor = new UserTaskExecutor(UUID.randomUUID(), task, 1, 3, taskService, taskLockService);

        TestUtils.execute(List.of(userTaskUpdater, userTaskExecutor));

        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertEquals(1L, task.getVersion());
    }

}