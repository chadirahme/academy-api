package com.academy.data.domains;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;

@Entity
//@Table(catalog = "test")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column(name = "user_id")
    private int userid;
    @NotEmpty(message = "*Please provide a username")
   // @Column(name = "user_name")
    private String username;
    //@org.hibernate.validator.constraints.Email(message = "*Please provide a valid Email")
   // @NotEmpty(message = "*Please provide an email")
    private String email;
    @Length(min = 5, message = "*Your password must have at least 5 characters")
    @NotEmpty(message = "*Please provide your password")
//    @Transient
    private String password;
    //    @NotEmpty(message = "*Please provide your first name")
//    private String firstName;
//    @NotEmpty(message = "*Please provide your last name")
//    private String lastName;
    private boolean active;
    private int usertype;
//    @ManyToMany(cascade = CascadeType.ALL)
//    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
//    private Set<Role> roles;

    @Column(name="grade")
    private String grade;
}
