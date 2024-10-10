package com.S3FileStorageApp.demo.services.impl;

import com.S3FileStorageApp.demo.domain.entities.AuthorisedFolderEntity;
import com.S3FileStorageApp.demo.repositories.AuthorisedFoldersRepository;
import com.S3FileStorageApp.demo.services.AuthorisedFoldersService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorisedFoldersServiceImpl implements AuthorisedFoldersService {

    private AuthorisedFoldersRepository authorisedFoldersRepository;

    AuthorisedFoldersServiceImpl(AuthorisedFoldersRepository authorisedFoldersRepository){
        this.authorisedFoldersRepository = authorisedFoldersRepository;
    }

    public void save(AuthorisedFolderEntity authorisedFolderEntity){authorisedFoldersRepository.save(authorisedFolderEntity);}

    public List<AuthorisedFolderEntity> findAllByUsername(String username){return authorisedFoldersRepository.findAllByUsername(username);}

    @Override
    public void delete(Long id) {
        authorisedFoldersRepository.deleteById(id);
    }

}
