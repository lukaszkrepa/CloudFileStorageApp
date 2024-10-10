package com.S3FileStorageApp.demo.services.impl;

import com.S3FileStorageApp.demo.domain.dto.UserDto;
import com.S3FileStorageApp.demo.domain.entities.RoleEntity;
import com.S3FileStorageApp.demo.domain.entities.UserEntity;
import com.S3FileStorageApp.demo.mappers.impl.UserMapperImpl;
import com.S3FileStorageApp.demo.repositories.RoleRepository;
import com.S3FileStorageApp.demo.repositories.UserRepository;
import com.S3FileStorageApp.demo.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private UserMapperImpl userMapperImpl;
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserMapperImpl userMapperImpl){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapperImpl = userMapperImpl;

    }

    @Override
    public UserDto save(UserDto userDto) {
        // Creates a role for the user
        RoleEntity roleEntity = roleRepository.findByName("USER");
        if(roleEntity == null){
            roleEntity = checkRoleExist();
        }

        // Creates a new userEntity from the Dto
        UserEntity userEntity = UserEntity.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .roles(Arrays.asList(roleEntity))
                .build();

        // Saves the user in the database
        userRepository.save(userEntity);
        return userDto;
    }

    @Override
    public Optional<UserDto> findOne(String email) {
        // Finds the user in the database
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);

        // Maps the Entity to Dto and return it
        return userEntity.map(entity -> userMapperImpl.mapTo(entity));
    }

    @Override
    public List<UserDto> findAllUsers() {
        // Finds all the users and converts them from Entity to Dto and return them
        return userRepository.findAll().stream().map(userEntity -> userMapperImpl.mapTo(userEntity)).collect(Collectors.toList());
    }

    @Override
    public boolean exists(String email) {return userRepository.existsByEmail(email);} // Checks whether the user exists in the database

    @Override
    public void delete(String email) {
        userRepository.deleteByEmail(email);
    } // Removes the user from the database

    private RoleEntity checkRoleExist(){
        // Checks whether the role exists in the database
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("USER");
        return roleRepository.save(roleEntity);
    }
}
