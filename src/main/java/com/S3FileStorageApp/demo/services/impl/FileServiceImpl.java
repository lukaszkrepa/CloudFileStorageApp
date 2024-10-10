package com.S3FileStorageApp.demo.services.impl;

import com.S3FileStorageApp.demo.domain.entities.FileEntity;
import com.S3FileStorageApp.demo.repositories.FileRepository;
import com.S3FileStorageApp.demo.services.FileService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;
    public FileServiceImpl(FileRepository fileRepository){
        this.fileRepository = fileRepository;
    }
    @Override
    public FileEntity save(FileEntity fileEntity) {
        return fileRepository.save(fileEntity);
    }

    @Override
    public Optional<FileEntity> findOne(String path) {
        return fileRepository.findByPath(path);
    }


    @Override
    public boolean isPublic(String path) {
        Optional<FileEntity> entity = findOne(path);
        return entity.map(FileEntity::isPublic).orElse(false);
    }

    @Override
    public void delete(String path) {
        fileRepository.deleteById(path);
    }

    @Override
    public void updateIsPublic(String path, boolean isPublic){
        fileRepository.updateIsPublicByPath(path,isPublic);
    }
}
