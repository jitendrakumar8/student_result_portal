package com.school.student_result.repository;

import com.school.student_result.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    boolean existsByClassNameAndSectionAndSubjectName(
            String className,
            String section,
            String subjectName
    );

    boolean existsByClassNameAndSectionAndSubjectNameAndIdNot(
            String className,
            String section,
            String subjectName,
            Long id
    );
    // 3️⃣ Subjects by class + section
    @Query("""
        SELECT DISTINCT s.subjectName
        from Subject s 
        where s.className = :className and s.section = :section
    """)
    List<String> findSubjectsByClassAndSection(String className, String section);
    List<Subject> findByClassNameAndSection(String className, String section);
}
