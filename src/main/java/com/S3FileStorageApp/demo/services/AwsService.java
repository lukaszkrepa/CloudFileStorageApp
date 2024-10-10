package com.S3FileStorageApp.demo.services;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface AwsService {

    /**
     * * Return all objects and folders from the S3 bucket
     * @param path Path from which the objects will be listed
     * @return List of information about the S3Objects from the S3 bucket
     */
    List<S3ObjectSummary> listObjects(String path);


    /**
     * * Uploads a file to the S3 bucket
     * @param fullPath Path to where the file will be added
     * @param file File that will be added
     * @return Whether the file was successfully uploaded
     */
    boolean uploadFile(String fullPath, MultipartFile file) throws IOException;


    /**
     * * Creates a folder on the S3 Bucket
     * @param fullPath Path to where the folder will be created
     * @param name Name of the folder that will be created
     * @return Whether the creation was successful
     */
    boolean createFolder(String fullPath, String name);


    /**
     * * Deletes an object from S3 bucket
     * ! Warning This code will actually delete the object that you specify!
     * @param fullPath Path to the object that will be deleted on S3 bucket
     */
    void deleteFile(String fullPath);


    /**
     * * Deletes a folder and all its files
     * ! Warning This code will actually delete the folder and the files you specify!
     * @param fullPath Path to the folder that will be deleted;.
     */
    void deleteFolder(String fullPath);


    /**
     * * Downloads an object from S3 bucket
     * @param filePath Path to the object on S3 bucket
     * @return S3Object from S3 bucket
     */
    S3Object download(String filePath);

}
