package com.school.student_result.service;

import com.school.student_result.entity.Marks;
import com.school.student_result.entity.Student;
import com.school.student_result.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public Student getStudentByRoll(String roll) {
        return repository.findByRollNumber(roll);
    }


    public int calculateTotal(List<Marks> marksList) {
        return marksList.stream()
                .mapToInt(Marks::getMarksObtained)

                .sum();
    }

    public double calculatePercentage(int total, int subjectCount) {
        return (double) total / subjectCount;
    }




    public String getDivision(double percentage) {
        if (percentage >= 60) return "First";
        else if (percentage >= 45) return "Second";
        else return "Fail";
    }
}
