package com.S3FileStorageApp.demo.controllers;


import com.S3FileStorageApp.demo.domain.entities.AuthorisedFolderEntity;
import com.S3FileStorageApp.demo.domain.entities.FileEntity;
import com.S3FileStorageApp.demo.domain.entities.FolderEntity;
import com.S3FileStorageApp.demo.domain.entities.UserEntity;
import com.S3FileStorageApp.demo.mappers.impl.FileMapperImpl;
import com.S3FileStorageApp.demo.mappers.impl.FolderMapperImpl;
import com.S3FileStorageApp.demo.services.impl.AuthorisedFoldersServiceImpl;
import com.S3FileStorageApp.demo.services.impl.AwsServiceImpl;
import com.S3FileStorageApp.demo.services.impl.FileServiceImpl;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class FileController {

    private AwsServiceImpl awsService;
    private FileMapperImpl fileMapper;
    private FolderMapperImpl folderMapper;

    private FileServiceImpl fileService;

    private AuthorisedFoldersServiceImpl authorisedFoldersService;
    public FileController(AwsServiceImpl awsService, FileMapperImpl fileMapper, FolderMapperImpl folderMapper, AuthorisedFoldersServiceImpl authorisedFoldersService, FileServiceImpl fileService){
        this.awsService = awsService;
        this.fileMapper = fileMapper;
        this.folderMapper = folderMapper;
        this.authorisedFoldersService = authorisedFoldersService;
        this.fileService = fileService;
    }
    @GetMapping(value = "/profile/{username}/**")
    public String listFiles(@PathVariable String username, HttpServletRequest request, Model model) {
        // remove profile prefix
        String fullPath = request.getRequestURI().substring(9);
        if (!fullPath.endsWith("/")){fullPath += "/";}

        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String finalFullPath = fullPath;

        // Check if user can access this folder
        List<AuthorisedFolderEntity> authorisedFolderEntities = authorisedFoldersService.findAllByUsername(username).stream()
                .filter(x -> finalFullPath.startsWith(x.getFolderKey()))
                .filter(x -> authentication.getName().equals(x.getAuthorised_user()))
                .toList();

        if (!(authentication.getName().equals(username)) && authorisedFolderEntities.isEmpty()){return "access-denied";}

        // Get all objects and group them by a file or directory
        List<S3ObjectSummary> s3ObjectSummaryList = awsService.listObjects(fullPath);
        List<FileEntity> s3Files = s3ObjectSummaryList.stream()
                        .filter(i -> !i.getKey().endsWith("/"))
                        .map(i -> fileMapper.mapFrom(i))
                        .toList();
        List<FolderEntity> s3Directories = s3ObjectSummaryList.stream()
                        .filter(i -> i.getKey().endsWith("/"))
                        .map(i -> folderMapper.mapFrom(i))
                        .toList();
        List<String> list = authorisedFoldersService.findAllByUsername(username).stream().map(AuthorisedFolderEntity::getAuthorised_user).toList();

        // Add attributes to files.html
        model.addAttribute("files", s3Files);
        model.addAttribute("directories", s3Directories);
        model.addAttribute("currentUrl", request.getRequestURI());
        model.addAttribute("urlwithoutprofile", fullPath);
        model.addAttribute("isowner", authentication.getName().equals(username));
        model.addAttribute("authorisedUsers",list);
        return "files";
    }
    @GetMapping(value = "/download/{username}/**")
    public ResponseEntity<?> download(@PathVariable String username, HttpServletRequest request, Model model) {
        // Check if user has access to this file
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String fullPath = request.getRequestURI().substring(9).replaceFirst("/", "");

        List<AuthorisedFolderEntity> authorisedFolderEntities = authorisedFoldersService.findAllByUsername(username).stream()
                .filter(x -> fullPath.startsWith(x.getFolderKey()))
                .filter(x -> authentication.getName().equals(x.getAuthorised_user()))
                .toList();

        Optional<FileEntity> fileEntity = fileService.findOne(fullPath.replaceAll("%20", " "));
        if (!(authentication.getName().equals(username)) && !fileEntity.map(FileEntity :: isPublic).orElse(false) && authorisedFolderEntities.isEmpty()){return new ResponseEntity<>("Error downloading the file", HttpStatus.FORBIDDEN);}
        
        // Download the file
        S3Object s3Object = awsService.download(fullPath.replaceAll("%20", " "));
        try (S3ObjectInputStream inputStream = s3Object.getObjectContent()) {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            StringBuilder stringBuilder = new StringBuilder(s3Object.getKey());
            headers.setContentDispositionFormData("attachment", stringBuilder.substring(stringBuilder.lastIndexOf("/")+1));
            headers.setContentLength(bytes.length);
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Error downloading the file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @PostMapping(value = "/profile/{username}/**")
    public String handlePostRequest(@PathVariable String username, HttpServletRequest request,
                                    @RequestParam(value = "file", required = false) MultipartFile file,
                                    @RequestParam(value = "name", required = false) String name,
                                    @RequestParam(value = "otherusername", required = false) String otherusername,
                                    @RequestParam(value = "otherusername2", required = false) String otherusername2,
                                    @RequestParam(value = "filename", required = false) String filename, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getName().equals(username))){return "access-denied";}

        // Remove the profile prefix
        String fullPath = request.getRequestURI().substring(9);
        if (!fullPath.endsWith("/")) { fullPath += "/"; }

        if (file != null && !file.isEmpty()) {
            // Handle file upload
            boolean result = awsService.uploadFile(fullPath + file.getOriginalFilename(), file);
            FileEntity fileEntity = FileEntity.builder()
                    .size(file.getSize())
                    .path(fullPath + file.getOriginalFilename())
                    .isPublic(false)
                    .name(file.getOriginalFilename())
                    .build();
            fileService.save(fileEntity);
        } else if (name != null && !name.isEmpty()) {
            // Handle folder creation
            awsService.createFolder(fullPath, name);
        } else if (otherusername != null){
            // Handle folder access
            List<AuthorisedFolderEntity> list = authorisedFoldersService.findAllByUsername(username).stream().filter(x -> x.getAuthorised_user().equals(otherusername)).toList();
            if(list.isEmpty()){
                AuthorisedFolderEntity authorisedFolderEntity = AuthorisedFolderEntity.builder()
                        .username(username)
                        .authorised_user(otherusername)
                        .folderKey(fullPath)
                        .build();
                authorisedFoldersService.save(authorisedFolderEntity);
            }

        } else if (otherusername2 != null) {
            // Handle folder access
            List<AuthorisedFolderEntity> list = authorisedFoldersService.findAllByUsername(username).stream().filter(x -> x.getAuthorised_user().equals(otherusername2)).toList();
            if (!list.isEmpty()) {
                authorisedFoldersService.delete(list.get(0).getId());
            }
        } else if (filename != null) {
            // Handle changing file from public to private and vice versa
            Optional<FileEntity> fileEntity = fileService.findOne(filename);
            fileEntity.ifPresent(entity -> fileService.updateIsPublic(filename, !entity.isPublic()));
        }

        return "redirect:" + request.getContextPath() + request.getRequestURI();
    }

    @PostMapping(value = "/delete/{username}/**")
    public String deleteFile(@PathVariable String username, HttpServletRequest request) {

        // Check if user can delete a file
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getName().equals(username))){return "access-denied";}


        if (request.getRequestURI().endsWith("/")){ // delete a folder
            awsService.deleteFolder(request.getRequestURI().substring(8));
            return "redirect:/profile/" + request.getRequestURI().substring(8);
        }
        else { // delete a file
            String fullPath = request.getRequestURI().substring(7).replaceFirst("/", ""); // Remove the "/delete" prefix
            awsService.deleteFile(fullPath.replaceAll("%20", " "));
            fileService.delete(fullPath.replaceAll("%20", " "));
            return "redirect:/profile/" + request.getRequestURI().substring(8,request.getRequestURI().lastIndexOf("/"));
        }
    }

}
