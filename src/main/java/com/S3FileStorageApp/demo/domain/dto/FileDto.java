package com.S3FileStorageApp.demo.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {
    @NotEmpty
    private String path;

    @NotEmpty
    private String name;

    @NotEmpty
    private long size;

    @NotEmpty
    private String date;

    @NotEmpty
    private boolean isPublic;
}
