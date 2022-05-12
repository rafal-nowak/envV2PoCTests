package com.example.locktest;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class UserTaskLocker implements RunnableTask{

    private final UUID lockerId;
    private final Task task;
    private final long sleepBefore;
    private final long lockTime;

    private final TaskService taskService;
    private final TaskLockService taskLockService;

    private CountDownLatch countDownLatch;

    @Override
    public void run() {
        var threadName = Thread.currentThread().getName();

        try {
            System.out.println("### Task " + task.getId() + " will try to lock thread " + threadName);
            TimeUnit.SECONDS.sleep(sleepBefore);
            System.out.println("### Task " + task.getId() + " tries to lock thread " + threadName);


            Optional<LocalDateTime> optionalToken = taskLockService.trySetLock(lockerId, task.getId(), lockTime);
            if ( optionalToken.isPresent() ) {
                System.out.println("### Task " + task.getId() + " locked by " + threadName);
                TimeUnit.SECONDS.sleep(lockTime);
                taskLockService.removeLock(lockerId, task.getId());
                System.out.println("### Task " + task.getId() + " unlocked by " + threadName);
            } else {
                System.out.println("### Task " + task.getId() + " can not be locked by " + threadName);
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
