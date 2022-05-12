package com.example.locktest;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
public class Task {

    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String description;
    private String workspaceUrl;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    private Long version;

    public Task(String name, String description, String workspaceUrl, TaskStatus status, Long version) {
        this.name = name;
        this.description = description;
        this.workspaceUrl = workspaceUrl;
        this.status = status;
        this.version = version;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Long incrementVersion(){
        version += 1;
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }

}
