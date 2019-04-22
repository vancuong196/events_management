package com.example.clay.event_manager.models;

public class Salary {
    private String salaryId;
    private String eventId;
    private String employeeId;
    private int salary;

    public Salary() {
    }

    public Salary(String salaryId, String eventId, String employeeId, int salary) {
        this.salaryId = salaryId;
        this.eventId = eventId;
        this.employeeId = employeeId;
        this.salary = salary;
    }

    public String getSalaryId() {
        return salaryId;
    }

    public void setSalaryId(String salaryId) {
        this.salaryId = salaryId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }
}
