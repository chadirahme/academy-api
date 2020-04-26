package com.academy.data.domains;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class FileUploadModel {

    private String year;
    private String grade;
    private String fileclass;
    private String subject;
    private String filetype;
    //private MultipartFile file;

//    data: File;
//    year ?: string;
//    grade: string;
//    class: string;
//    subject: string;
//    filetype: string;
}
