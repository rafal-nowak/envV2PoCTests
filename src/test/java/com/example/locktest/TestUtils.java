package com.example.locktest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class TestUtils {

    static void execute(List<RunnableTask> tasks) throws InterruptedException {
        var countDownLatch = new CountDownLatch(tasks.size());
        var executor = Executors.newFixedThreadPool(tasks.size());
        tasks.forEach(task -> {
            task.setCountDownLatch(countDownLatch);
            executor.submit(task);
        });
        countDownLatch.await();
        System.out.println("Completed");
    }

}
