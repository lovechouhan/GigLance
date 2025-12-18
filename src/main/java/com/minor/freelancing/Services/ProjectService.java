package com.minor.freelancing.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minor.freelancing.Entities.Client;
import com.minor.freelancing.Entities.Freelancer;
import com.minor.freelancing.Entities.Projects;
import com.minor.freelancing.Entities.Proposal;
import com.minor.freelancing.Helper.ProjectStatus;
import com.minor.freelancing.Repositories.FreelancerRepository;
import com.minor.freelancing.Repositories.ProjectRepository;
import com.minor.freelancing.Repositories.ProposalRepository;

import org.springframework.transaction.annotation.Transactional;

import com.minor.freelancing.Entities.Task;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Autowired
    private FreelancerRepository freelancerRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Autowired
    private ProposalRepository proposalRepository;

    public Projects getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    public void updateProjectStatus(Projects project, Freelancer freelancer) {
        if (project != null) {
            project.setStatus(ProjectStatus.IN_PROGRESS);
            project.setFreelancer(freelancer);
            projectRepository.save(project);
        }
    }

    public Projects create(Projects p) {
        return projectRepository.save(p);
    }

    public List<Projects> listAll() {
        return projectRepository.findAll();
    }

    public List<Projects> search(String q) {
        if (q == null || q.trim().isEmpty())
            return listAll();
        return projectRepository.search(q.trim());
    }

    public List<Projects> listByClient(Long clientId) {
        return projectRepository.findAllProjectsByClientId(clientId);
    }

    public Projects findById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deleteById(Long id) {
        // remove dependent proposals first to avoid FK constraint violations
        try {
            java.util.List<Proposal> proposals = proposalRepository.findByProjectId(id);
            if (proposals != null && !proposals.isEmpty()) {
                proposalRepository.deleteAll(proposals);
            }
        } catch (Exception e) {
            // log and continue - if deletion of proposals fails, rethrow to rollback
            throw e;
        }

        projectRepository.deleteById(id);
    }

    public List<Projects> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Projects> recommendBySkills(java.util.List<String> skills) {
        if (skills == null || skills.isEmpty())
            return listAll();

        // Map projectId -> score
        java.util.Map<Long, Integer> score = new java.util.HashMap<>();
        java.util.Map<Long, Projects> byId = new java.util.HashMap<>();

        for (String skill : skills) {
            if (skill == null || skill.trim().isEmpty())
                continue;
            List<Projects> found = projectRepository.search(skill.trim());
            for (Projects p : found) {
                byId.put(p.getId(), p);
                score.put(p.getId(), score.getOrDefault(p.getId(), 0) + 1);
            }
        }

        // Build a list sorted by score desc
        java.util.List<Projects> result = new java.util.ArrayList<>(byId.values());
        result.sort((a, b) -> Integer.compare(score.getOrDefault(b.getId(), 0), score.getOrDefault(a.getId(), 0)));
        return result;
    }

    public List<Projects> getAllProjectsOPEN() {

        return projectRepository.findByStatus("OPEN");
    }

    public List<Projects> getAllProjectsByFreelancerId(Long freelancerId) {

        Freelancer freelancer = freelancerRepository.findById(freelancerId).orElse(null);
        if (freelancer != null) {
            return freelancerRepository.getProjectsByFreelancerId(freelancer.getId());
        } else {
            return java.util.Collections.emptyList();
        }

    }

    public List<Projects> getActiveProjectsByClient(Client client) {
        return projectRepository.findActiveProjectsByClientId(client.getId());
    }

    public List<Proposal> getProposalByFreelancerId(Long id) {

        throw new UnsupportedOperationException("Unimplemented method 'getProposalByFreelancerId'");
    }

    public Freelancer findFreelancerByProjectId(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Projects> getAllProjectsByClientId(Long id) {
        return projectRepository.findAllProjectsByClientId(id);
    }

    public int countActiveProjectsByFreelancerId(Long id) {
        return projectRepository.countByFreelancerIdAndStatus(id, ProjectStatus.IN_PROGRESS);
    }

    public List<Projects> getActiveProjectsByFreelancerId(Long id) {
        return projectRepository.findActiveProjectsByFreelancerncerId(id);
    }

    public List<Projects> getCompletedProjectsByFreelancerId(Long id) {
        return projectRepository.getCompletedProjectsByFreelancerId(id);
    }

    public void markProjectAsAssigned(Projects existingProject, Freelancer freelancer) {
        existingProject.setStatus(ProjectStatus.IN_PROGRESS);
        existingProject.setFreelancer(freelancer);
        projectRepository.save(existingProject);
    }

    public List<Task> getTasksByProjectId(Long projectId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Projects update(Projects project) {
        return projectRepository.save(project);
    }
}
