package com.example.locktest;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class UserTaskExecutor implements RunnableTask {

    private final UUID executorId;
    private final Task task;
    private final long sleepBefore;
    private final long workTime;

    private final TaskService taskService;
    private final TaskLockService taskLockService;

    private CountDownLatch countDownLatch;

    @Override
    public void run() {
        var threadName = Thread.currentThread().getName();

        try {
            System.out.println("### Task " + task.getId() + "  will try to execute thread " + threadName);
            TimeUnit.SECONDS.sleep(sleepBefore);
            System.out.println("### Task " + task.getId() + " tries to execute thread " + threadName);

            Optional<LocalDateTime> optionalToken = taskLockService.trySetLock(executorId, task.getId(), workTime);
            if ( optionalToken.isPresent() ) {
                System.out.println("### Task " + task.getId() + " executes thread " + threadName);
                task.setStatus(TaskStatus.STARTED);
                taskService.update(task);
                TimeUnit.SECONDS.sleep(workTime);
                task.setStatus(TaskStatus.COMPLETED);
                taskService.update(task);
                taskLockService.removeLock(executorId, task.getId());
                System.out.println("### Task " + task.getId() + " has been executed by thread " + threadName);
            } else {
                System.out.println("### Task " + task.getId() + " is locked and can not be executed");
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }

    }

    @Override
    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }
}
