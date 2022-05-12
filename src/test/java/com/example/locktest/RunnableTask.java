package com.example.locktest;

import java.util.concurrent.CountDownLatch;

public interface RunnableTask extends Runnable {

    void setCountDownLatch(CountDownLatch countDownLatch);

}
