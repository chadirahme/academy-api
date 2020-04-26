package com.academy.data.repository;

import com.academy.data.domains.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "student", path = "student")
public interface StudentRepository extends JpaRepository<Student,Long> {

    List<Student> findByGradeAndSection(String grade ,String section);
}
