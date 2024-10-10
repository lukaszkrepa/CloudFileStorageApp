package com.S3FileStorageApp.demo.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class FileEntity {
    @Id
    private String path;

    private String name;

    private long size;

    private String date;

    private boolean isPublic;

    public String converted_size(){
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("B");
        arrayList.add("KB");
        arrayList.add("MB");
        arrayList.add("GB");
        arrayList.add("TB");

        float size_copy = (float) size;
        int index = 0;
        while (size_copy / 1024 >= 1){
            size_copy /= 1024;
            index += 1;
        }
        if (index > arrayList.size() -1) {return "error";}
        else{return String.format("%.2f", size_copy) + arrayList.get(index);}
    }
}
