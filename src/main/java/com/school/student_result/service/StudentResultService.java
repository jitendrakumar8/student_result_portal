package com.school.student_result.service;

import com.school.student_result.entity.Marks;
import com.school.student_result.repository.MarksRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudentResultService {

    private final MarksRepository marksRepository;

    public StudentResultService(MarksRepository marksRepository) {
        this.marksRepository = marksRepository;
    }

    // 🔹 Get Class Rank
    public int getClassRank(Long studentId, String className, String section) {

        List<Marks> allMarks =
                marksRepository.findByClassNameAndSection(className, section);

        // studentId -> totalMarks
        Map<Long, Integer> studentTotalMap = new HashMap<>();

        for (Marks m : allMarks) {
            Long id = m.getStudentId();
            studentTotalMap.put(
                    id,
                    studentTotalMap.getOrDefault(id, 0) + m.getMarksObtained()
            );
        }

        // Sort totals DESC
        List<Map.Entry<Long, Integer>> sortedList =
                new ArrayList<>(studentTotalMap.entrySet());

        sortedList.sort((a, b) -> b.getValue() - a.getValue());

        // Assign rank
        int rank = 1;
        for (Map.Entry<Long, Integer> entry : sortedList) {
            if (entry.getKey().equals(studentId)) {
                return rank;
            }
            rank++;
        }

        return rank;
    }
}
