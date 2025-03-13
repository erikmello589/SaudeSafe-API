package com.faeterj.saudesafe.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.faeterj.saudesafe.model.Role;
import com.faeterj.saudesafe.model.User;
import com.faeterj.saudesafe.repository.RoleRepository;
import com.faeterj.saudesafe.repository.UserRepository;

import jakarta.transaction.Transactional;

@Configuration
public class AdminUserConfig implements CommandLineRunner {

    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public AdminUserConfig(RoleRepository roleRepository, UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String ... args) throws Exception
    {
        var roleAdmin = roleRepository.findByName(Role.Values.ADMIN.name());
        var userAdmin = userRepository.findByUsername("erikG");

        userAdmin.ifPresentOrElse(
            user -> { 
                System.out.println("Admin já existente no sistema");
            },
            () -> {
                var user = new User();
                user.setUsername("erikG");
                user.setEmail("erikmello589@gmail.com");
                user.setName("Erik");
                user.setLastName("Mello");
                user.setPassword(passwordEncoder.encode("Erik589@"));
                user.setRoles(Set.of(roleAdmin));
                userRepository.save(user);
            }
        );
    }

}
