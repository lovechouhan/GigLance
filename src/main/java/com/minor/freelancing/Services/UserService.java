package com.minor.freelancing.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.minor.freelancing.DTO.ClientRegistrationDto;
import com.minor.freelancing.DTO.FreelancerRegistrationDto;
import com.minor.freelancing.Entities.Client;
import com.minor.freelancing.Entities.Freelancer;
import com.minor.freelancing.Entities.User;
import com.minor.freelancing.Helper.Roles;
import com.minor.freelancing.Repositories.ClientRepository;
import com.minor.freelancing.Repositories.FreelancerRepository;
import com.minor.freelancing.Repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FreelancerRepository freelancerRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserService(UserRepository userRepository, FreelancerRepository freelancerRepository,
            ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.freelancerRepository = freelancerRepository;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    

    public Freelancer registerFreelancer(FreelancerRegistrationDto dto) {
        User existing = userRepository.findByEmail(dto.getEmail());
        if (existing != null) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setEnabled(true);
        user.setRole(Roles.FREELANCER);
       User savedUser = userRepository.save(user);


        Freelancer f = new Freelancer();
        f.setEmail(dto.getEmail());
        f.setId(savedUser.getId());
        f.setPassword(passwordEncoder.encode(dto.getPassword()));
        f.setName(dto.getName());
        f.setPhone(dto.getPhone());
        f.setDesignation(dto.getDesignation());
        f.setBio(dto.getBio());
       
        if (dto.getSkills() != null && !dto.getSkills().isBlank()) {
            String[] parts = dto.getSkills().split(",");
            java.util.List<String> skills = new java.util.ArrayList<>();
            for (String s : parts) {
                if (!s.trim().isEmpty())
                    skills.add(s.trim());
            }
            f.setSkills(skills);
        }

        Freelancer saved = freelancerRepository.save(f);
        
      
        
      
        return saved;
    }

    public Client registerClient(ClientRegistrationDto dto) {
        User existing = userRepository.findByEmail(dto.getEmail());
        if (existing != null) {
            throw new IllegalArgumentException("Email already in use");
        }

         User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setEnabled(true);
        user.setRole(Roles.CLIENT);
       

        User savedUser = userRepository.save(user);


        Client c = new Client();
        c.setId(savedUser.getId());
        c.setEmail(dto.getEmail());
        c.setPassword(passwordEncoder.encode(dto.getPassword()));
        c.setName(dto.getName());
        c.setPhone(dto.getPhone());
        c.setCompanyName(dto.getCompanyName());
        c.setWebsite(dto.getWebsite());
        c.setAddress(dto.getAddress());
        
       
        
        return clientRepository.save(c);

    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Freelancer loadFreelancerByEmail(String email) {
        Freelancer freelancer = freelancerRepository.findByEmail(email);
        if (freelancer == null) {
            throw new IllegalArgumentException("Freelancer not found with email: " + email);
        }
        return freelancer;
    }

    public Client loadClientByEmail(String email) {
        Client client = clientRepository.findByEmail(email);
        if (client == null) {
            throw new IllegalArgumentException("Client not found with email: " + email);
        }
        return client;
    }

    public List<User> getAllFreelancers() {
       return userRepository.findByRole(Roles.FREELANCER);
    }

    public List<User> getAllClients() {
       return userRepository.findByRole(Roles.CLIENT);
    }
}
