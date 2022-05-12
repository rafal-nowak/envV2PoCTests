package com.example.locktest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskTest {

    @Autowired
    TaskRepository taskRepository;

    @Test
    void first_test() {

    }

    @Test
    void saved_entity_should_have_id() {
        var task = Fixtures.testTask();
        System.out.println("##########");
        UUID taskIdBeforePersist = task.getId();
        System.out.println("task id before save: " + taskIdBeforePersist);
        Task savedTask = taskRepository.save(task);
        assertNotEquals(taskIdBeforePersist, savedTask.getId());
    }

}