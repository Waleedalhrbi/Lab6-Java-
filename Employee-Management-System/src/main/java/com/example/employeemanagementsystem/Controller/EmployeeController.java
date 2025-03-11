package com.example.employeemanagementsystem.Controller;

import com.example.employeemanagementsystem.Api.ApiResponse;
import com.example.employeemanagementsystem.Model.Employee;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    ArrayList<Employee> employees = new ArrayList<>();

    @GetMapping("/get")
    public ArrayList<Employee> getEmployee(){
        return employees;
    }

    @PostMapping("/add")
    public ResponseEntity addEmployee(@RequestBody @Valid Employee employee, Errors errors){
        if (errors.hasErrors()){
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }

        employees.add(employee);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Employee added successfully"));
    }

    @PutMapping("/update/{ID}")
    public ResponseEntity updateEmployee(@PathVariable String ID, @RequestBody @Valid Employee employee, Errors errors){
        if (errors.hasErrors()){
            String message = errors.getFieldError().getDefaultMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        for (Employee employee1 : employees){
            if (employee1.getID().equalsIgnoreCase(ID)){
                employees.set(employees.indexOf(employee1), employee);
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Employee updated successfully"));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Employee not found"));
    }

    @DeleteMapping("/delete/{ID}")
    public ResponseEntity deleteEmployee(@PathVariable String ID){
        for (Employee employee1 : employees){
            if (employee1.getID().equalsIgnoreCase(ID)){
                employees.remove(employee1);
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Employee deleted successfully"));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("The employee not found"));
    }

    @GetMapping("/search-by-position/{position}")
    public ResponseEntity searchByPosition(@PathVariable String position){

        ArrayList<Employee> supervisor = new ArrayList<>();
        ArrayList<Employee> coordinator = new ArrayList<>();
        for (Employee employee1 : employees){
            if (employee1.getPosition().equalsIgnoreCase("coordinator")){
                coordinator.add(employee1);
            } else if (employee1.getPosition().equalsIgnoreCase("supervisor")) {
                supervisor.add(employee1);
            }
        }


        if (position.equalsIgnoreCase("coordinator")){
            return ResponseEntity.status(HttpStatus.OK).body("Employee found: "+ coordinator);
        } else if (position.equalsIgnoreCase("supervisor")) {
            return ResponseEntity.status(HttpStatus.OK).body("Employee found: "+ supervisor);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Employee not found"));
    }


    @GetMapping("/search-by-age/{min}/{max}")
    public ResponseEntity getRangeAge(@PathVariable int min, @PathVariable int max){
        if (min < 0 || max < 0 || min > max) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Invalid age range"));
        }

        ArrayList<Employee> employeesWithAgeRang = new ArrayList<>();

        for (Employee employee : employees){
            if (employee.getAge() >= min && employee.getAge() <= max){
                employeesWithAgeRang.add(employee);
            }
        }
        if (!employeesWithAgeRang.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(employeesWithAgeRang);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("No employees found in this range"));
        }
    }

    @PostMapping("/apply-leave/{ID}")
    public ResponseEntity applyForLeave(@PathVariable String ID){
        for (Employee employee : employees){
            if (employee.getID().equalsIgnoreCase(ID)){
                if (employee.isOnLeave()){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Employee is on leave"));
                }
                if (employee.getAnnualLeave() <= 0){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("No annual leave avlibal"));
                }

                employee.setOnLeave(true);
                employee.setAnnualLeave(employee.getAnnualLeave() - 1);
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Leave applied successfully"));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Employee not found"));
    }

    @GetMapping("/employees-no-leave")
    public ResponseEntity getEmployeesWithNoLeave() {
        ArrayList<Employee> noLeaveEmployees = new ArrayList<>();

        for (Employee employee : employees) {
            if (employee.getAnnualLeave() == 0) {
                noLeaveEmployees.add(employee);
            }
        }

        if (!noLeaveEmployees.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(noLeaveEmployees);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("All employees have avlibal leave"));
        }
    }
    @PutMapping("/promote/{ID}/{requesterID}")
    public ResponseEntity promoteEmployee(@PathVariable String ID, @PathVariable String requesterID){
        Employee requester = null;
        Employee employeeToPromote = null;

        for (Employee employee : employees){
            if (employee.getID().equalsIgnoreCase(requesterID) && employee.getPosition().equalsIgnoreCase("supervisor")){
                requester = employee;
            }
            if (employee.getID().equalsIgnoreCase(ID)){
                employeeToPromote = employee;
            }
        }

        if (requester == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Requester must be a supervisor"));
        }

        if (employeeToPromote == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Employee not found"));
        }
        if (employeeToPromote.getPosition().equalsIgnoreCase("supervisor")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Employee is already supervisor"));
        }

        if (employeeToPromote.getAge() < 30){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Employee age must be more than 30 years"));
        }

        if (employeeToPromote.isOnLeave()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Employee is currently on leave"));
        }

        employeeToPromote.setPosition("supervisor");
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Employee promoted successfully"));
    }

}
