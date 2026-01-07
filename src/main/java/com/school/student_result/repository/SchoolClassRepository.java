package com.school.student_result.repository;

import com.school.student_result.entity.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    // DISTINCT class names
    @Query("SELECT DISTINCT sc.className FROM SchoolClass sc")
    List<String> findDistinctClassNames();

    // DISTINCT sections by class
    @Query("SELECT DISTINCT sc.section FROM SchoolClass sc WHERE sc.className = :className")
    List<String> findSectionsByClassName(@Param("className") String className);

    boolean existsByClassNameAndSection(String className, String section);
    boolean existsByClassNameAndSectionAndIdNot(
            String className,
            String section,
            Long id
    );

}
