package com.faeterj.tcc.service;

import com.faeterj.tcc.dto.CreateUserDTO;
import com.faeterj.tcc.model.Paciente;
import com.faeterj.tcc.model.Role;
import com.faeterj.tcc.model.User;
import com.faeterj.tcc.repository.PacienteRepository;
import com.faeterj.tcc.repository.RoleRepository;
import com.faeterj.tcc.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PacienteRepository pacienteRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, 
                       PacienteRepository pacienteRepository, 
                       RoleRepository roleRepository, 
                       BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.pacienteRepository = pacienteRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerNewUser(CreateUserDTO createUserDTO) {
        var basicRole = roleRepository.findByName(Role.Values.BASIC.name());
        if (basicRole == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Role básica não encontrada");
        }

        Optional<User> userFromUsernameDB = userRepository.findByUsername(createUserDTO.username());
        Optional<User> userFromEmailDB = userRepository.findByEmail(createUserDTO.email());

        if (userFromUsernameDB.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nome de usuário já existe.");
        }
        if (userFromEmailDB.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já existe.");
        }

        var user = new User();
        user.setUsername(createUserDTO.username());
        user.setEmail(createUserDTO.email());
        user.setName(createUserDTO.name());
        user.setLastName(createUserDTO.lastName());
        user.setPassword(passwordEncoder.encode(createUserDTO.password()));
        user.setRoles(Set.of(basicRole));

        User savedUser = userRepository.save(user);

        var paciente = new Paciente();
        paciente.setUser(savedUser);
        paciente.setNomePaciente(createUserDTO.name());
        paciente.setSobrenomePaciente(createUserDTO.lastName());
        pacienteRepository.save(paciente);
    }

    public List<User> listAllUsers() {
        return userRepository.findAll();
    }
}
