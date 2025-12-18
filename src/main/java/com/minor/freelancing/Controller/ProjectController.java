package com.minor.freelancing.Controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.minor.freelancing.Entities.Client;
import com.minor.freelancing.Entities.Projects;
import com.minor.freelancing.Services.ClientServices;

import com.minor.freelancing.Services.ProjectService;
import com.minor.freelancing.Services.UserService;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ClientServices clientServices;
    private final ProjectService projectService;
    private final UserService userService;
   

    public ProjectController(ProjectService projectService, UserService userService, ClientServices clientServices) {
        this.projectService = projectService;
        this.userService = userService;
        this.clientServices = clientServices;
      

    }

    @GetMapping("activeprojects/{id}")
    public String getActiveProjects(@PathVariable Long id, org.springframework.ui.Model model) {
        Client client = clientServices.findById(id);
        List<Projects> project = projectService.getActiveProjectsByClient(client);

        if (project != null && !project.isEmpty()) {
            System.out.println("Active projects count: " + project.size());
        } else {
            System.out.println("No active projects found.");
        }
        // Add projects to model using the attribute name expected by the template
        model.addAttribute("project", project);
        // Build a map of projectId -> freelancer to help the view render freelancer
        // details
        java.util.Map<Long, com.minor.freelancing.Entities.Freelancer> freelancers = new java.util.HashMap<>();
        if (project != null) {
            for (Projects p : project) {
                if (p.getFreelancer() != null) {
                    freelancers.put(p.getId(), p.getFreelancer());
                    System.out.println("Project " + p.getId() + " has freelancer: " + p.getFreelancer().getName());
                } else {
                    System.out.println("Project " + p.getId() + " has no freelancer assigned.");
                }
            }
        }
        model.addAttribute("freelancers", freelancers);
        return "client/activeprojects";
    }

    @GetMapping("/viewdetails/{id}")
    public String viewProjectDetails(@PathVariable Long id, org.springframework.ui.Model model) {
        Projects project = projectService.findById(id);
        model.addAttribute("project", project);
        return "Projects/Details";
    }

//     @GetMapping("/live")
// public String liveProjects(Model model) {
//     model.addAttribute("projects",
//         externalProjectService.fetchLiveProjects());
//     return "live-projects";
// }


}
