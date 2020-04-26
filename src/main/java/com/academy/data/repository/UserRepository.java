package com.academy.data.repository;

import com.academy.data.domains.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "user", collectionResourceRel = "user")
public interface UserRepository extends JpaRepository<User, Integer>{//,QuerydslPredicateExecutor<User> {

    User findByUsername(String username);
    User findByEmail(String email);
    User findByUsernameAndPassword(String username,String password);
    User findByEmailAndPassword(String email,String password);
}