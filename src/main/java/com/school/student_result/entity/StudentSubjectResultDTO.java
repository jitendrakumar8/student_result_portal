package com.school.student_result.entity;

public class StudentSubjectResultDTO {
    private String subjectName;
    private Integer maxMarks;
    private Integer halfYearlyMarks;
    private Integer annualMarks;

    public StudentSubjectResultDTO(
            String subjectName,
            Integer maxMarks,
            Integer halfYearlyMarks,
            Integer annualMarks
    ) {
        this.subjectName = subjectName;
        this.maxMarks = maxMarks;
        this.halfYearlyMarks = halfYearlyMarks;
        this.annualMarks = annualMarks;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public Integer getMaxMarks() {
        return maxMarks;
    }

    public Integer getHalfYearlyMarks() {
        return halfYearlyMarks;
    }

    public Integer getAnnualMarks() {
        return annualMarks;
    }
}
