package com.academy.data.domains;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="empname")
    private String employeeName;

    @Column(name="empno")
    private String employeeNumber;

    @Column(name="username")
    private String userName;
    @Column(name="password")
    private String password;

    //private int port;

    public Employee(Long id,String employeeName, String empNumber) {
        this.id = id;
        this.employeeName = employeeName;
        this.employeeNumber = empNumber;
    }
}

