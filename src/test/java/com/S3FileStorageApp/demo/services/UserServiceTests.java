package com.S3FileStorageApp.demo.services;

import com.S3FileStorageApp.demo.domain.dto.UserDto;
import com.S3FileStorageApp.demo.domain.entities.UserEntity;
import com.S3FileStorageApp.demo.mappers.impl.UserMapperImpl;
import com.S3FileStorageApp.demo.repositories.UserRepository;
import com.S3FileStorageApp.demo.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest // Load the full Spring Boot application context
@ActiveProfiles("test") // Use the 'test' profile for testing (application-test.properties)
@TestPropertySource(properties = {"BUCKET_NAME=test-bucket"})
@Transactional
public class UserServiceTests {

    @Autowired
    private UserRepository userRepository;  // Actual repository, no mocking

    @Autowired
    private UserServiceImpl userService;    // Actual service, no mocking

    @Autowired
    private PasswordEncoder passwordEncoder;  // Actual password encoder

    @Autowired
    private UserMapperImpl userMapper;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();  // Ensure the database is clean before each test
    }

    @Test
    public void saveUser() {

        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .name("TestUser")
                .username("TestUsername")
                .email("testemail@gmail.com")
                .password("password")
                .build();
        UserDto userDto = userMapper.mapTo(userEntity);
        // Save UserDto using actual userService
        userService.save(userDto);

        // Fetch the saved user from the database
        Optional<UserEntity> newUser = userRepository.findByEmail("testemail@gmail.com");

        // Assertions to check if data is saved in DB
        assertTrue(newUser.isPresent());
        assertEquals(newUser.get().getName(), userDto.getName());
    }

    @Test
    public void findOne() {
        // Prepare and save a UserEntity directly into the database
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .name("TestUser")
                .username("TestUsername")
                .email("test@example.com")
                .password(passwordEncoder.encode("password"))
                .build();
        userService.save(userMapper.mapTo(userEntity));

        // Call the service method to find a user by email
        Optional<UserDto> foundUser = userService.findOne("test@example.com");

        // Assertions to check if the correct user is found
        assertTrue(foundUser.isPresent());
        assertEquals("TestUser", foundUser.get().getName());
    }

    @Test
    public void findAllUsers() {
        // Prepare and save two users into the database
        UserEntity user1 = UserEntity.builder()
                .id(1L)
                .name("TestUser1")
                .username("TestUsername1")
                .password(passwordEncoder.encode("password"))
                .email("testemail3@gmail.com")
                .build();
        UserEntity user2 = UserEntity.builder()
                .id(2L)
                .name("TestUser2")
                .username("TestUsername2")
                .email("testemail4@gmail.com")
                .password(passwordEncoder.encode("password"))
                .build();
        UserDto userDto1 = userMapper.mapTo(user1);
        UserDto userDto2 = userMapper.mapTo(user2);

        userService.save(userDto1);
        userService.save(userDto2);

        // Fetch all users using the service
        List<UserDto> users = userService.findAllUsers();

        // Assertions to verify that the correct number of users are found
        assertEquals(2, users.size());
        assertEquals("TestUser1", users.get(0).getName());
        assertEquals("TestUser2", users.get(1).getName());
    }

    @Test
    public void exists() {
        // Prepare and save a user into the database
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .name("TestUser")
                .username("TestUsername")
                .email("test@example.com")
                .password(passwordEncoder.encode("password"))
                .build();

        UserDto userDto = userMapper.mapTo(userEntity);
        userService.save(userDto);

        // Check if the user exists by email
        boolean userExists = userService.exists("test@example.com");

        // Assertions to check if the user exists
        assertTrue(userExists);
    }

    @Test
    public void delete() {

        // Prepare and save a user into the database
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .name("TestUser")
                .username("TestUsername")
                .email("testemail10@gmail.com")
                .password(passwordEncoder.encode("password"))
                .build();
        userService.save(userMapper.mapTo(userEntity));

        // Call the service method to delete the user by ID
        userService.delete("testemail10@gmail.com");

        // Verify that the user is no longer in the database
        Optional<UserDto> deletedUser = userService.findOne("testemail10@gmail.com");
        assertTrue(deletedUser.isEmpty());
    }
}
