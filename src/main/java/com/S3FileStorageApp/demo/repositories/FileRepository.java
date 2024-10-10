package com.S3FileStorageApp.demo.repositories;

import com.S3FileStorageApp.demo.domain.entities.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, String> {
    Optional<FileEntity> findByPath(String path);

    boolean existsByPath(String path);
    @Modifying
    @Transactional
    @Query("UPDATE FileEntity f SET f.isPublic = :isPublic WHERE f.path = :path")
    void updateIsPublicByPath(String path, boolean isPublic);

}
