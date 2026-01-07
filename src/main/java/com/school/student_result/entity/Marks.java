package com.school.student_result.entity;

import jakarta.persistence.*;


@Entity
@Table(
        name = "marks",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "class_name",
                                "subject_name",
                                "exam_type",
                                "student_id"
                        }
                )
        }
)
public class Marks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_name")
    private String className;

    private String section;   // ✅ ADD THIS


    @Column(name = "subject_name")
    private String subjectName;

    @Column(name = "exam_type")
    private String examType;

    @Column(name = "student_id")
    private Long studentId;

    private String studentName;
    private String rollNumber;

    @Column(name = "marks_obtained", nullable = false)
    private Integer marksObtained;

    @Column(nullable = false)
    private Boolean deleted = false;

    // getters & setters
    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }


    // 👉 getters & setters


    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Integer getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(Integer marksObtained) {
        this.marksObtained = marksObtained;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }


}
