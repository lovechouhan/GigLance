package com.minor.freelancing.Services;

import com.minor.freelancing.Entities.Task;
import com.minor.freelancing.Entities.WorkSubmission;
import com.minor.freelancing.Entities.Freelancer;
import com.minor.freelancing.Repositories.WorkSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkSubmissionService {

    @Autowired
    private WorkSubmissionRepository workSubmissionRepository;

    public WorkSubmission createSubmission(Task task, String message, String githubLink, Freelancer freelancer) {
        WorkSubmission submission = new WorkSubmission();
        submission.setTask(task);
        submission.setFreelancer(freelancer);
        submission.setMessage(message);
        submission.setGithubLink(githubLink);

        return workSubmissionRepository.save(submission);
    }

    public WorkSubmission getSubmittedWork(Long submissionId) {
        return workSubmissionRepository.findById(submissionId).orElse(null);
    }

    public void updateSubmission(WorkSubmission submission) {
        workSubmissionRepository.save(submission);
    }

}
