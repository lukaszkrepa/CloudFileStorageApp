package com.S3FileStorageApp.demo.services;

import com.S3FileStorageApp.demo.domain.dto.UserDto;
import com.S3FileStorageApp.demo.domain.entities.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {

    /**
     * * Saves the User in the database
     * @param userDto Data transfer object with all the user data
     * @return Returns the Dto provided
     */
    UserDto save(UserDto userDto);


    /**
     * * Returns a user from the database with specific email
     * @param email Email of the user
     * @return Optional that holds the UserData if it exists and empty otherwise
     */
    Optional<UserDto> findOne(String email);


    /**
     * * Lists all the users
     * @return List of UserData of all users
     */
    List<UserDto> findAllUsers();


    /**
     * * Checks if the user exists
     * @param email Email of the user
     * @return Whether the user exists
     */
    boolean exists(String email);


    /**
     * * Deletes the user from the database
     * ! Warning This code will actually delete the user that you specify!
     * @param email The email of the user
     */
    void delete(String email);
}
