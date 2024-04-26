package com.ua.clear.model;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
public class User {

    private Long id;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Past
    private LocalDate birthDate;

    private String address;

    @Size(min = 10, max = 15)
    private String phoneNumber;


}

