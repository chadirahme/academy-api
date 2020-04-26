package com.academy.data.domains;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "marks")
@Getter
@Setter
@NoArgsConstructor
public class Marks {

    @Id
    @Column(name="markid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long markid;

    @Column(name="academicyear")
    private String academicyear;

    @Column(name="semester")
    private int semester;

    @Column(name="grade")
    private String grade;

    @Column(name="section")
    private String section;

    @Column(name="teacherid")
    private int teacherid;

    @Column(name="teachername")
    private String teachername;

    @Column(name="studentid")
    private int studentid;

    @Column(name="studentname")
    private String studentname;

    @Column(name="subject")
    private String subject;

    @Column(name="cw")
    private float cw;

    @Column(name="hw")
    private float hw;
    @Column(name="project")
    private float project;
    @Column(name="quiz1")
    private float quiz1;
    @Column(name="quiz2")
    private float quiz2;
    @Column(name="quiz3")
    private float quiz3;
    @Column(name="avgquiz")
    private float avgquiz;
    @Column(name="test1")
    private float test1;
    @Column(name="finalexam")
    private float finalexam;
    @Column(name="finalmark")
    private float finalmark;

    @Column(name="createdby")
    private int createdby;
    @Column(name="updatedby")
    private int updatedby;

    @Column(name="createdtime")
    private Date createdtime;

    @Column(name="updatedtime")
    private Date updatedtime;
}
