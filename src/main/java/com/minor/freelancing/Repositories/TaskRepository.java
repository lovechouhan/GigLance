package com.minor.freelancing.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.minor.freelancing.Entities.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{

    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId")
    public List<Task> findByProjectId(Long projectId);

}
