package com.academy.data.repository;


import com.academy.data.domains.Marks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "marks", path = "marks")
public interface MarksRepository extends JpaRepository<Marks,Long> {

    List<Marks> findByAcademicyearAndSemesterAndGradeAndSectionAndTeacheridAndSubject(String academicyear,int semester,String grade,String section,int teacherid,String subject);
    List<Marks> findByStudentid(int studentid);

}
