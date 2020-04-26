package com.academy.data.rest;


import com.academy.data.domains.APIResult;
import com.academy.data.domains.User;
import com.academy.data.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("users")
public class UsersRestController {

    private static final Logger logger = LoggerFactory.getLogger(UsersRestController.class);
    @Autowired
    private UserRepository userRepository;

    // -------------------Retrieve All Users---------------------------------------------

    @RequestMapping(value = "/user/", method = RequestMethod.GET)
    public ResponseEntity<List<User>> listAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
            // You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<List<User>>(users, HttpStatus.OK);
    }

    @RequestMapping(value = "/findByEmail/{email}", method = RequestMethod.GET)
    public ResponseEntity<User> findByEmail(@PathVariable("email") String email) {
        User user = userRepository.findByEmail(email);
        if (user ==null) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
            // You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    // -------------------Retrieve Single User------------------------------------------

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@PathVariable("id") Integer id) {
        logger.info("Fetching User with id {}", id);
       Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            logger.error("User with id {} not found.", id);
            return new ResponseEntity(new APIResult("User with id " + id
                    + " not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<User>(user.get(), HttpStatus.OK);
    }

    // -------------------Create a User-------------------------------------------

//    {
//        "username": "admin",
//            "email": "kk@gmail.com",
//            "password": "12345",
//            "active": true,
//            "usertype": 1,
//            "grade": ""
//    }
//
    @RequestMapping(value = "/user/", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
        logger.info("Creating User : {}", user);

        if (userRepository.findByEmail(user.getEmail())!=null) {
            logger.error("Unable to create. A User with email {} already exist {}", user.getEmail(), user.getUsername());
            return new ResponseEntity(new APIResult("Unable to create. A User with name " +
                    user.getEmail() + " already exist."),HttpStatus.CONFLICT);
        }
        try{

            userRepository.save(user);
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(ucBuilder.path("/api/user/{id}").buildAndExpand(user.getUserid()).toUri());
            return new ResponseEntity<String>(headers, HttpStatus.CREATED);
        }
        catch (Exception ex)
        {
            return new ResponseEntity(new APIResult(ex.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ------------------- Update a User ------------------------------------------------

    @RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)//PATCH  PATCH request is non-idempotent (like a POST request).
    public ResponseEntity<?> updateUser(@PathVariable("id") Integer id, @RequestBody User user) {
        logger.info("Updating User with id {}", id);

        User currentUser = userRepository.findById(id).orElse(null);

        if (currentUser == null) {
            logger.error("Unable to update. User with id {} not found.", id);
            return new ResponseEntity(new APIResult("Unable to upate. User with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        currentUser.setUsername(user.getUsername());
        //currentUser.setAge(user.getAge());
        //currentUser.setSalary(user.getSalary());

        userRepository.save(currentUser);
        return new ResponseEntity<User>(currentUser, HttpStatus.OK);
    }

    // ------------------- Delete a User-----------------------------------------

    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable("id") Integer id) {
        logger.info("Fetching & Deleting User with id {}", id);

        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            logger.error("Unable to delete. User with id {} not found.", id);
            return new ResponseEntity(new APIResult("Unable to delete. User with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(id);
        return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
    }

}
