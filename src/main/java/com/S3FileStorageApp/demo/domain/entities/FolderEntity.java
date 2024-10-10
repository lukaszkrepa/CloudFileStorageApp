package com.S3FileStorageApp.demo.domain.entities;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FolderEntity {
    private String path;
    private String name;
    private String date;
}