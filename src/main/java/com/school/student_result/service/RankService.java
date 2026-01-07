package com.school.student_result.service;

import com.school.student_result.entity.Student;
import com.school.student_result.entity.StudentRankDTO;
import com.school.student_result.repository.MarksRepository;
import com.school.student_result.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RankService {

    @Autowired
    private MarksRepository marksRepository;
    public RankService(MarksRepository marksRepository) {
        this.marksRepository = marksRepository;
    }

    public int getStudentRank(
            Long studentId,
            String className,
            String section,
            String examType) {

        List<Object[]> result =
                marksRepository.findTotalMarksForRanking(
                        className, section, examType);

        int rank = 1;

        for (Object[] row : result) {
            Long currentStudentId = (Long) row[0];

            if (currentStudentId.equals(studentId)) {
                return rank;
            }
            rank++;
        }

        return 0; // student not found
    }

    public List<StudentRankDTO> calculateClassRank(
            String className,
            String section,
            String examType) {

        List<Object[]> rows =
                marksRepository.findTotalMarksForRanking(className, section, examType);

        List<StudentRankDTO> result = new ArrayList<>();

        int rank = 0;
        int previousMarks = -1;
        int sameRankCount = 0;

        for (Object[] row : rows) {

            Long studentId = (Long) row[0];
            String studentName = (String) row[1];
            Integer totalMarks = ((Number) row[2]).intValue();

            if (totalMarks != previousMarks) {
                rank = rank + 1 + sameRankCount;
                sameRankCount = 0;
            } else {
                sameRankCount++;
            }

            StudentRankDTO dto =
                    new StudentRankDTO(studentId, studentName, totalMarks);
            dto.setRank(rank);

            result.add(dto);

            previousMarks = totalMarks;
        }

        return result;
    }
}


