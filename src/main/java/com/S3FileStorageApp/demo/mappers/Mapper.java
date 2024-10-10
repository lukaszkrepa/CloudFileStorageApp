package com.S3FileStorageApp.demo.mappers;

public interface Mapper<A,B> {


    B mapTo(A a);

    A mapFrom(B b);
}