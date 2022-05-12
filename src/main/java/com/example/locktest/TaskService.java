package com.example.locktest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public Task save(Task task){
        return taskRepository.save(task);
    }

    public Task update(Task task) {
        return taskRepository.save(task);
    }
}
