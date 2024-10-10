package com.S3FileStorageApp.demo.services;

import com.S3FileStorageApp.demo.domain.entities.AuthorisedFolderEntity;

import java.util.List;

public interface AuthorisedFoldersService {
    public void save(AuthorisedFolderEntity authorisedFolderEntity);

    public List<AuthorisedFolderEntity> findAllByUsername(String username);

    public void delete(Long id);

}
