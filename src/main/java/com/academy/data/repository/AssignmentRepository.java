package com.academy.data.repository;


import com.academy.data.domains.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "assignment", path = "assignment")
public interface AssignmentRepository extends JpaRepository<Assignment,Long> {

    List<Assignment> findByTeacherid(int teacherid);
    List<Assignment> findByGrade(String grade);
}
