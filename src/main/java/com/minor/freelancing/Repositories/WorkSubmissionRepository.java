package com.minor.freelancing.Repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.minor.freelancing.Entities.WorkSubmission;

@Repository
public interface WorkSubmissionRepository extends JpaRepository<WorkSubmission, Long> {

}
