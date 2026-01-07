package com.school.student_result.entity;

public class StudentRankDTO {

    private Long studentId;
    private String studentName;
    private Integer totalMarks;
    private Integer rank;

    public StudentRankDTO(Long studentId, String studentName, Integer totalMarks) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.totalMarks = totalMarks;
    }

    // getters & setters
    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public Integer getTotalMarks() {
        return totalMarks;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
