package com.S3FileStorageApp.demo.services.impl;

import com.S3FileStorageApp.demo.services.AwsService;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class AwsServiceImpl implements AwsService {
    private final AmazonS3 s3;
    private final String bucketName;

    public AwsServiceImpl(AmazonS3 s3, @Value("${BUCKET_NAME}") String bucketName){
        this.s3 = s3;
        this.bucketName = bucketName;
    }


    @Override
    public List<S3ObjectSummary> listObjects(String path) {
        // Checks if the bucketName is provided
        if (bucketName == null || bucketName.isEmpty()) {
            throw new IllegalArgumentException("BUCKET_NAME is not set or is empty");
        }

        // Creates a new ListObjectsV2Request with bucketName and path as a prefix
        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(path);

        // Gets all objects with from the bucket with the prefix
        ListObjectsV2Result result = s3.listObjectsV2(req);

        // Converts the Result to list of objects
        List<S3ObjectSummary> objectSummaries = result.getObjectSummaries();

        int maxSlashCount = StringUtils.countMatches(path,"/"); // Counts the number of "/" in the path provided

        // Filters all the objects and removes the objects that go deeper than the provided path
        return objectSummaries.stream()
                .filter(s3ObjectSummary -> {
                    if (s3ObjectSummary.getKey().endsWith("/")) {
                        if (StringUtils.countMatches(s3ObjectSummary.getKey(), "/") > maxSlashCount + 1) {
                            return false;
                        }
                        return StringUtils.countMatches(s3ObjectSummary.getKey(), "/") != maxSlashCount ;
                    } else {
                        return !(StringUtils.countMatches(s3ObjectSummary.getKey(), "/") > maxSlashCount);
                    }
                })
                .toList();
    }


    @Override
    public boolean uploadFile(String key, MultipartFile file){
        // Start multipart upload
        InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucketName, key);
        InitiateMultipartUploadResult initiateMultipartUploadResult = s3.initiateMultipartUpload(initiateMultipartUploadRequest);
        String uploadId = initiateMultipartUploadResult.getUploadId();


        // Split uploading files into parts which allows uploading biger files
        long partSize = 5 * 1024 * 1024; // 5 MB part size
        List<UploadPartResult> completedParts = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            byte[] buffer = new byte[(int) partSize];
            int bytesRead;
            int partNumber = 1;

            while ((bytesRead = inputStream.read(buffer)) > 0) {
                UploadPartRequest uploadPartRequest = new UploadPartRequest()
                        .withBucketName(bucketName)
                        .withKey(key)
                        .withUploadId(uploadId)
                        .withPartNumber(partNumber)
                        .withInputStream(new ByteArrayInputStream(buffer, 0, bytesRead))
                        .withPartSize(bytesRead);

                UploadPartResult uploadPartResult = s3.uploadPart(uploadPartRequest);
                completedParts.add(uploadPartResult);

                partNumber++;
            }

            CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest()
                    .withBucketName(bucketName)
                    .withKey(key)
                    .withUploadId(uploadId)
                    .withPartETags(completedParts);

            s3.completeMultipartUpload(completeMultipartUploadRequest);
            return true;
        } catch (SdkClientException | IOException e) {
            s3.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, key, uploadId));
            return false;   
        }
    }


    @Override
    public boolean createFolder(String fullPath, String name) {
        // Removes a "/" from the fullPath if it starts with "/"
        if (fullPath.startsWith("/")){fullPath = fullPath.replaceFirst("/","");}

        // Creates a new ObjectMetadata object and sets it length to 0
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);

        // Creates an empty InputStream
        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

        // Creates a PutObjectRequest with the correct bucketName, key, InputStream and metadata.
        PutObjectRequest request = new PutObjectRequest(bucketName, fullPath + name + "/", emptyContent, metadata);

        // Puts the object on S3
        s3.putObject(request);
        return true;
    }


    @Override
    public void deleteFile(String fullPath) {
        // Removes a "/" from the fullPath if it starts with "/"
        if (fullPath.startsWith("/")){fullPath = fullPath.replaceFirst("/","");}
        s3.deleteObject(bucketName,fullPath);
    }

    @Override
    public void deleteFolder(String fullPath){
        ObjectListing objectList = s3.listObjects(bucketName, fullPath );
        List<S3ObjectSummary> objectSummeryList =  objectList.getObjectSummaries();
        // Create a list of keys to be deleted
        String[] listOfKeys = new String[ objectSummeryList.size() ];
        int count = 0;
        for( S3ObjectSummary summery : objectSummeryList ) {
            //Populate the list with keys
            listOfKeys[count++] = summery.getKey();
        }
        //Delete all objects within the folder
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest( bucketName ).withKeys(listOfKeys);
        this.s3.deleteObjects(deleteObjectsRequest);
    }


    @Override
    public S3Object download(String filePath) {
        try{
            // Downloads the object from S3 Bucket.
            return s3.getObject(bucketName, filePath);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            return null;
        }
    }
}
