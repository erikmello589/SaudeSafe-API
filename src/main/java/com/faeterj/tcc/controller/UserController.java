package com.faeterj.tcc.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.faeterj.tcc.dto.CreateUserDTO;
import com.faeterj.tcc.model.Paciente;
import com.faeterj.tcc.model.Role;
import com.faeterj.tcc.model.User;
import com.faeterj.tcc.repository.PacienteRepository;
import com.faeterj.tcc.repository.RoleRepository;
import com.faeterj.tcc.repository.UserRepository;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
public class UserController {

    private final UserRepository userRepository;

    private final PacienteRepository pacienteRepository;

    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder;


    public UserController(UserRepository userRepository, PacienteRepository pacienteRepository,
            RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.pacienteRepository = pacienteRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<Void> newUser (@RequestBody CreateUserDTO createUserDTO) 
    {

        var basicRole = roleRepository.findByName(Role.Values.BASIC.name());

        var userFromUsernameDB = userRepository.findByUsername(createUserDTO.username());
        var userFromEmailDB = userRepository.findByEmail(createUserDTO.email());
        if (userFromUsernameDB.isPresent() || userFromEmailDB.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
            //TODO: Necessário refinar esse retorno informando se é o email ou o userName que já existe
        }

        var user = new User();
        user.setUsername(createUserDTO.username());
        user.setEmail(createUserDTO.email());
        user.setName(createUserDTO.name());
        user.setLastName(createUserDTO.lastName());
        user.setPassword(passwordEncoder.encode(createUserDTO.password()));
        user.setRoles(Set.of(basicRole));

        // Salva o usuário e armazena o retorno na variável userSaved
        User userSaved = userRepository.save(user);

        var paciente = new Paciente();
        paciente.setUser(userSaved);  // Define o user do paciente como o usuário recém-criado
        paciente.setNomePaciente(createUserDTO.name());
        paciente.setSobrenomePaciente(createUserDTO.lastName());
        pacienteRepository.save(paciente);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<User>> listaUsuarios () 
    {
        var users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
    
    

}
