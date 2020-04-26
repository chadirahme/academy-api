package com.academy.data.domains;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "assignment")
@Getter
@Setter
@NoArgsConstructor
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="teacherid")
    private int teacherid;

    @Column(name="academicyear")
    private String academicyear;

    @Column(name="grade")
    private String grade;


    @Column(name="section")
    private String section;

    @Column(name="subject")
    private String subject;

    @Column(name="filetype")
    private String filetype;

    @Column(name="filename")
    private String filename;

    @Column(name="filepath")
    private String filepath;

    @Column(name="description")
    private String description;

    @Column(name="uploadtime")
    private Date uploadtime;

    @Column(name="active")
    private int active;

}
