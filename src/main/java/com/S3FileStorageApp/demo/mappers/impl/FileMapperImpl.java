package com.S3FileStorageApp.demo.mappers.impl;

import com.S3FileStorageApp.demo.domain.entities.FileEntity;
import com.S3FileStorageApp.demo.mappers.Mapper;
import com.S3FileStorageApp.demo.services.FileService;
import com.S3FileStorageApp.demo.services.impl.FileServiceImpl;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class FileMapperImpl implements Mapper<FileEntity, S3ObjectSummary> {

    private ModelMapper modelMapper;

    private FileService fileService;

    public FileMapperImpl(ModelMapper modelMapper, FileService fileService) {
        this.modelMapper = modelMapper;
        this.fileService = fileService;
    }

    @Override
    public S3ObjectSummary mapTo(FileEntity fileEntity) {
        return modelMapper.map(fileEntity, S3ObjectSummary.class);
    }

    @Override
    public FileEntity mapFrom(S3ObjectSummary s3ObjectSummary) {
        StringBuilder stringBuilder = new StringBuilder(s3ObjectSummary.getKey());
        return FileEntity.builder()
                .name(stringBuilder.substring(stringBuilder.lastIndexOf("/")+1))
                .path(s3ObjectSummary.getKey())
                .size(s3ObjectSummary.getSize())
                .date(String.valueOf(s3ObjectSummary.getLastModified()))
                .isPublic(fileService.findOne(s3ObjectSummary.getKey()).map(FileEntity::isPublic).orElse(false))
                .build();
    }
}
