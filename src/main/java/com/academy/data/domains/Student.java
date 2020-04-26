package com.academy.data.domains;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "student")
@Getter
@Setter
@NoArgsConstructor
public class Student {

    @Id
    @Column(name="studentid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentid;

    @Column(name="rollnumber")
    private String rollnumber;

    @Column(name="studentname")
    private String studentname;

    @Column(name="grade")
    private String grade;

    @Column(name="section")
    private String section;
}
