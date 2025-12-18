package com.minor.freelancing.Repositories;

import com.minor.freelancing.Entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Client findByEmail(String email);

    // Find client by associated user id
    Client findByUser_Id(Long id);

}
