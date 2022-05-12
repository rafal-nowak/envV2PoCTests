package com.example.locktest;

public class Fixtures {

    static Task testTask() {
        var task = new Task("Task name", "Task description", "Workspace URL", TaskStatus.SUBMITTED, 1L);
        return task;
    }

}
