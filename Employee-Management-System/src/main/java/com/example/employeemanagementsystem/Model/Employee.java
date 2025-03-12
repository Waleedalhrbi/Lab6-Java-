package com.example.employeemanagementsystem.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Employee {


    @NotEmpty(message = "You must enter an ID")
    @Size(min = 3, message = "The length of ID must be more than 2")
    private String ID;

    @NotEmpty(message = "You must enter a name")
    @Size(min = 5, message = "The length of name must be more than 4")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Please enter only characters")
    private String name;

    @NotEmpty(message = "Please enter an email")
    @Email
    private String email;

    @NotEmpty(message = "Please enter a number")
    @Pattern(regexp = "^05\\d{8}$", message = "The phone number must start with 05 and contain 10 digit")
    private String phoneNumber;

    @NotNull(message = "Please enter an age")
    @Positive(message = "Age must be a positive number")
    @Min(value = 26, message = "Your age must be more than 25")
    private int Age;

    @NotEmpty(message = "Please enter your position")
    @Pattern(regexp = "supervisor|coordinator", message = "The value must be either supervisor or coordinator ")
    private String position;

    @NotEmpty(message = "Please enter a value")
    @AssertFalse
    private boolean onLeave = false;

    @PastOrPresent(message = "The date must be in the present or past")
    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate hireDate;

    @NotNull(message = "Please enter a value")
    @Positive(message = "Must be a positive number")
    @Min(value = 0)
    private int AnnualLeave;
}
