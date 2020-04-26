package com.academy.data.dto;

import lombok.Getter;
import lombok.Setter;
import java.sql.Date;

@Getter
@Setter
public class StudentMarksDTO {

    private Long markid;
    private String academicyear;
    private int semester;
    private String grade;
    private String section;
    private int teacherid;
    private String teachername;
    private Long studentid;
    private String studentname;
    private String subject;

    private float cw;
    private float hw;

    private float project;

    private float quiz1;

    private float quiz2;

    private float quiz3;

    private float avgquiz;

    private float test1;

    private float finalexam;
    private float finalmark;

    private int createdby;
    private int updatedby;
    private Date createdtime;
    private Date updatedtime;
}
