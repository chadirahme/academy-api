package com.academy.data.controller;


import com.academy.data.config.GenerateFileName;
import com.academy.data.domains.FileUploadModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

@Service
public class FileService extends GenerateFileName {

    @Value("${upload.path}")
    private String path;

    //private final Path rootLocation = Paths.get(path);

    public String store(MultipartFile file) {
        String filePath="";

        try {
            //String ext= getFileExtension(file.getOriginalFilename());
            String  uniqueFileName=getUniqueFileName(path,"" ,file.getOriginalFilename());
            Path rootLocation = Paths.get(path);
            System.out.println(file.getOriginalFilename());
            System.out.println(rootLocation.toUri());
            //filePath=rootLocation.resolve(file.getOriginalFilename()).toString();
            filePath=rootLocation.resolve(uniqueFileName).toString();

            Files.copy(file.getInputStream(), rootLocation.resolve(uniqueFileName),
                   StandardCopyOption.REPLACE_EXISTING);


            return filePath;
        } catch (Exception e) {
            //throw new RuntimeException("FAIL!");
            return "-1";
        }
    }

    public Resource loadFile(String filename) {
        try {
            Path rootLocation = Paths.get(path);
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("FAIL!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("FAIL!");
        }
    }

    @Override
    public void sound() {

    }
}
