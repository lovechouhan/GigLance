package com.minor.freelancing.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.minor.freelancing.Entities.Client;
import com.minor.freelancing.Entities.Projects;

@Repository
public interface ProjectRepository extends JpaRepository<Projects, Long> {
    @Query("select p from Projects p where lower(p.title) like lower(concat('%',:q,'%')) or lower(p.description) like lower(concat('%',:q,'%'))")
    List<Projects> search(@Param("q") String q);

    // Deprecated: use findAllProjectsByClientId which queries by p.client.id
    List<Projects> findByClientId(Long clientId);

    
    @Query("select p from Projects p where p.client.id = :clientId")
    List<Projects> findAllProjectsByClientId(Long clientId);

    @Query("select p from Projects p where p.status = 'OPEN'")
    List<Projects> findByStatus(String string);

    @Query("select p.client from Projects p where p.id = :projectId")
    Client findClientByProjectId(Long projectId);

    @Query("select p from Projects p where p.client.id = :id and p.status = 'IN_PROGRESS'")
    List<Projects> findActiveProjectsByClientId(Long id);

    @Query("select count(p) from Projects p where p.freelancer.id = :id and p.status = :accepted")
    public int countByFreelancerIdAndStatus(Long id, String accepted);

    @Query("select p from Projects p where p.freelancer.id = :id and p.status = 'IN_PROGRESS'")
    public List<Projects> findActiveProjectsByFreelancerncerId(Long id);

    @Query("select p from Projects p where p.freelancer.id = :id and p.status = 'COMPLETED'")
    public List<Projects> getCompletedProjectsByFreelancerId(Long id);

    @Query("select p from Projects p where p.client.id = :id and p.status = 'OPEN'")
    List<Projects> findAllProjectsByClientIdAndStatus(Long id);

    @Query("select count(p) from Projects p where p.client.id = :id and p.status = 'IN_PROGRESS'")
    public Long countActiveProjectsByClientId(Long id);
}
