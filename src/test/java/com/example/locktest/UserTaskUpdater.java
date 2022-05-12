package com.example.locktest;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class UserTaskUpdater implements RunnableTask{

    private final UUID updaterId;
    private final Task task;
    private final long sleepBefore;
    private final long updateTime;

    private final TaskService taskService;
    private final TaskLockService taskLockService;

    private CountDownLatch countDownLatch;

    @Override
    public void run() {
        var threadName = Thread.currentThread().getName();

        try {
            System.out.println("### Task " + task.getId() + " will try to update thread " + threadName);
            TimeUnit.SECONDS.sleep(sleepBefore);
            System.out.println("### Task " + task.getId() + " tries to update thread " + threadName);

            Optional<LocalDateTime> optionalToken = taskLockService.trySetLock(updaterId, task.getId(), updateTime);
            if ( optionalToken.isPresent() ) {
                System.out.println("### Task " + task.getId() + " updates thread " + threadName);
                TimeUnit.SECONDS.sleep(updateTime);
                task.incrementVersion();
                taskService.update(task);
                taskLockService.removeLock(updaterId, task.getId());
                System.out.println("### Task " + task.getId() + " has been updated by thread " + threadName);
            } else {
                System.out.println("### Task " + task.getId() + " is locked and can not be updated");
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
