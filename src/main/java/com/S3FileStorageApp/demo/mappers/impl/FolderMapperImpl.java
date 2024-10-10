package com.S3FileStorageApp.demo.mappers.impl;

import com.S3FileStorageApp.demo.domain.entities.FileEntity;
import com.S3FileStorageApp.demo.domain.entities.FolderEntity;
import com.S3FileStorageApp.demo.mappers.Mapper;
import com.amazonaws.services.codecommit.model.Folder;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class FolderMapperImpl implements Mapper<FolderEntity, S3ObjectSummary> {

    private ModelMapper modelMapper;

    public FolderMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
    @Override
    public S3ObjectSummary mapTo(FolderEntity folderEntity) {
        return modelMapper.map(folderEntity, S3ObjectSummary.class);
    }

    @Override
    public FolderEntity mapFrom(S3ObjectSummary s3ObjectSummary) {
        String temp = s3ObjectSummary.getKey().substring(0,s3ObjectSummary.getKey().length()-1);
        return FolderEntity.builder()
                .path(s3ObjectSummary.getKey())
                .name(temp.substring(temp.lastIndexOf("/")+1))
                .date(String.valueOf(s3ObjectSummary.getLastModified()))
                .build();
    }
}
