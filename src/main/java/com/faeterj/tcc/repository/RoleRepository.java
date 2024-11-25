package com.faeterj.tcc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.faeterj.tcc.model.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

    
}