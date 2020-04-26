package com.academy.data.repository;


import com.academy.data.domains.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "employee", path = "employee")
public interface EmployeeRepository extends JpaRepository<Employee,Long>{

    Employee findById(long id);
    Employee findByUserNameAndPassword(String userName, String password);
}