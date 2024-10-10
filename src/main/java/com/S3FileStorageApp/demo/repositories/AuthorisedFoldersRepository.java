package com.S3FileStorageApp.demo.repositories;

import com.S3FileStorageApp.demo.domain.entities.AuthorisedFolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorisedFoldersRepository extends JpaRepository<AuthorisedFolderEntity,Long> {

    List<AuthorisedFolderEntity> findAllByUsername(String username);

    void deleteById(Long id);


}
