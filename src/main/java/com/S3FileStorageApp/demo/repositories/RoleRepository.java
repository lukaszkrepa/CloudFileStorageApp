package com.S3FileStorageApp.demo.repositories;

import com.S3FileStorageApp.demo.domain.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    /**
     * * Returns a role from the database with specific name
     * @param name Name of the role
     * @return Role entity with the specified name
     */
    RoleEntity findByName(String name);
}