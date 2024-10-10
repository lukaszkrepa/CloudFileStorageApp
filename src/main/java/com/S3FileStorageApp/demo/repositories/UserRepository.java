package com.S3FileStorageApp.demo.repositories;
import com.S3FileStorageApp.demo.domain.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
    /**
     * * Returns a user from the database with specific email
     * @param email Email of the user
     * @return Optional that holds the UserData if it exists and empty otherwise
     */
    Optional<UserEntity> findByEmail(String email);


    /**
     * * Checks if the user exists
     * @param email Email of the user
     * @return Whether the user exists
     */
    boolean existsByEmail(String email);

    /**
     * Returns a user from the database with a specific username
     * @param username Username of the user
     * @return UserEntity of the user from the database
     */
    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> deleteByEmail(String email);
}
