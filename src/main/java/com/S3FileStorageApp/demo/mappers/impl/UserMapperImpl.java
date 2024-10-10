package com.S3FileStorageApp.demo.mappers.impl;

import com.S3FileStorageApp.demo.domain.dto.UserDto;
import com.S3FileStorageApp.demo.domain.entities.UserEntity;
import com.S3FileStorageApp.demo.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements Mapper<UserEntity,UserDto> {
    private ModelMapper modelMapper;

    public UserMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto mapTo(UserEntity authorEntity) {
        return modelMapper.map(authorEntity, UserDto.class);
    }

    @Override
    public UserEntity mapFrom(UserDto userDto) { return modelMapper.map(userDto,UserEntity.class);}

}
