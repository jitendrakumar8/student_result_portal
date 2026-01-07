package com.school.student_result.repository;

import com.school.student_result.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Student findByRollNumber(String rollNumber);

    boolean existsByRollNumber(String rollNumber);
    List<Student> findByClassName(String className);
    List<Student> findByClassNameAndSection(String className, String section);


    List<Student> findByClassNameAndSectionAndIsDeletedFalse(String className, String section);

    Optional<Student> findByIdAndIsDeletedFalse(Long id);


    List<Student> findByIsDeletedTrue();

    Optional<Student> findByIdAndIsDeletedTrue(Long id);




}
