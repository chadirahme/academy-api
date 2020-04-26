package com.academy.data.rest;

import com.academy.data.domains.Assignment;
import com.academy.data.repository.AssignmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AssignmentRestController {

    private static final Logger logger = LoggerFactory.getLogger(AssignmentRestController.class);
    @Autowired
    private AssignmentRepository assignmentRepository;

    @GetMapping("/assignments")
    List<Assignment> all() {
        return assignmentRepository.findAll();
    }

    @GetMapping("/assignments1/{id}")
    Assignment one(@PathVariable Long id) throws ResourseNotFoundException{
        try
        {
            throw new ResourseNotFoundException("demo");
            //return assignmentRepository.findById(id)
            //        .orElseThrow(() -> new ResourseNotFoundException(String.format("assignments {} not found ",id)));
        }
        catch (ResourseNotFoundException ex)
        {
            String msg=ex.getMessage();
            return null;
        }

    }

    @GetMapping("/assignments/{id}")
    Assignment oneadvice(@PathVariable Long id) {
        if (assignmentRepository.findById(id).isPresent()) {
            return assignmentRepository.findById(id).get();
        }
        return assignmentRepository.findById(id).get();
        //throw new Exception("demo not found");
    }

}
