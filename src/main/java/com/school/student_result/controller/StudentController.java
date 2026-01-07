package com.school.student_result.controller;

import com.school.student_result.entity.Marks;
import com.school.student_result.entity.Student;
import com.school.student_result.repository.MarksRepository;
import com.school.student_result.repository.StudentRepository;
import com.school.student_result.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class StudentController {

    private final StudentService service;
    private final MarksRepository marksRepo;
    private final StudentRepository studentRepo;

    // ✅ Constructor Injection (REQUIRED)
    public StudentController(StudentService service,
                             MarksRepository marksRepo,
                             StudentRepository studentRepo) {
        this.service = service;
        this.marksRepo = marksRepo;
        this.studentRepo = studentRepo;
    }

    @PostMapping("/result")
    public String showResult(@RequestParam String roll,
                             @RequestParam String examType,
                             Model model) {

        // 1️⃣ find student
        Student student = studentRepo.findByRollNumber(roll);

        if (student == null) {
            model.addAttribute("errorMessage", "Student not found!");
            return "search";
        }

        // 2️⃣ find marks
        List<Marks> marks =
                marksRepo.findByStudentIdAndExamType(student.getId(), examType);

        if (marks.isEmpty()) {
            model.addAttribute("errorMessage", "No marks found!");
            return "search";
        }

        // 3️⃣ calculate result
        int total = service.calculateTotal(marks);
        double percentage =
                service.calculatePercentage(total, marks.size());

        // 4️⃣ send to UI
        model.addAttribute("student", student);
        model.addAttribute("marks", marks);
        model.addAttribute("total", total);
        model.addAttribute("percentage", percentage);
        model.addAttribute("division",
                service.getDivision(percentage));

        return "result";
    }
}
