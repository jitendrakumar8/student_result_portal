package com.school.student_result.repository;

import com.school.student_result.entity.Marks;
import com.school.student_result.entity.StudentSubjectResultDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MarksRepository extends JpaRepository<Marks, Long> {

    List<Marks> findByStudentIdAndExamType(Long studentId, String examType);
    boolean existsByClassNameAndSectionAndSubjectNameAndExamTypeAndStudentId(
            String className,
            String section,
            String subjectName,
            String examType,
            Long studentId
    );

    List<Marks> findByClassNameAndSubjectNameAndExamType(
            String className,
            String subjectName,
            String examType
    );



    // show only active marks
    List<Marks> findByClassNameAndSubjectNameAndExamTypeAndDeletedFalse(
            String className,
            String subjectName,
            String examType
    );

    // show deleted (for restore page)
    List<Marks> findByDeletedTrue();




    long countByDeletedTrue();

    long countByDeletedFalse(); // (optional, future use)
    // for score card
    List<Marks> findByStudentIdAndExamTypeAndDeletedFalse(
            Long studentId,
            String examType
    );

    @Query("""
    SELECT m.studentId, m.studentName, SUM(m.marksObtained)
    FROM Marks m
    WHERE m.className = :className
      AND m.section = :section
      AND m.examType = :examType
      AND m.deleted = false
    GROUP BY m.studentId, m.studentName
    ORDER BY SUM(m.marksObtained) DESC
""")
List<Object[]> findTotalMarksForRanking(
        @Param("className") String className,
        @Param("section") String section,
        @Param("examType") String examType
);

    List<Marks> findByStudentIdAndClassNameAndSectionAndExamTypeAndDeletedFalse(
            Long studentId,
            String className,
            String section,
            String   examType
    );




    @Query("""
SELECT new com.school.student_result.entity.StudentSubjectResultDTO(
    s.subjectName,
    s.maxMarks,
    MAX(CASE WHEN m.examType = 'HALF_YEARLY' THEN m.marksObtained END),
    MAX(CASE WHEN m.examType = 'ANNUAL' THEN m.marksObtained END)
)
FROM Subject s
LEFT JOIN Marks m
    ON s.subjectName = m.subjectName
   AND s.className = m.className
   AND s.section = m.section
   AND m.studentId = :studentId
   AND m.deleted = false
WHERE s.className = :className
  AND s.section = :section
GROUP BY s.subjectName, s.maxMarks
ORDER BY s.subjectName
""")
    List<StudentSubjectResultDTO> getStudentCombinedResult(
            @Param("studentId") Long studentId,
            @Param("className") String className,
            @Param("section") String section
    );


    List<Marks> findByClassNameAndSection(String className, String section);





}
