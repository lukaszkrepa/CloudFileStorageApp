package com.S3FileStorageApp.demo.services;

import com.S3FileStorageApp.demo.domain.dto.UserDto;
import com.S3FileStorageApp.demo.domain.entities.FileEntity;

import java.util.List;
import java.util.Optional;

// *! fix comments
public interface FileService {
    /**
     * * Saves the File in the database
     * @param userDto Data transfer object with all the user data
     * @return Returns the Dto provided
     */
    FileEntity save(FileEntity userDto);


    /**
     * * Returns a user from the database with specific email
     * @param path Email of the user
     * @return Optional that holds the UserData if it exists and empty otherwise
     */
    Optional<FileEntity> findOne(String path);


    /**
     *
     */
    boolean isPublic(String path);

    /**
     * * Deletes the user from the database
     * ! Warning This code will actually delete the user that you specify!
     * @param path The id of the user
     */
    void delete(String path);

    void updateIsPublic(String path, boolean isPublic);
}
