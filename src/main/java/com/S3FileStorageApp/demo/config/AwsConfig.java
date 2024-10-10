package com.S3FileStorageApp.demo.config;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    @Value("${AWS_REGION:us-east-1}") // Default to us-east-1 if AWS_REGION is not set
    private String region;

    @Bean
    public AmazonS3 amazonS3() { // create a new aws client
        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.EU_WEST_1)
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
    }
}
