package com.school.student_result.controller;
import com.school.student_result.entity.*;
import com.school.student_result.repository.*;
import com.school.student_result.service.RankService;
import com.school.student_result.service.ReportCardPdfService;
import com.school.student_result.service.StudentResultService;
import com.school.student_result.service.StudentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;


import java.util.ArrayList;
import java.util.List;



@Controller

public class AdminController {

    private final AdminRepository adminRepo;
    private final StudentRepository studentRepo;

    private final SchoolClassRepository classRepo;

    private final SubjectRepository subjectRepo;
    private final MarksRepository marksRepo;
    private final RankService rankService;
    private final ReportCardPdfService pdfService;
    private final StudentResultService studentResultService;



    public AdminController(AdminRepository adminRepo,
                           StudentRepository studentRepo,
                           SchoolClassRepository classRepo,
                           SubjectRepository subjectRepo,MarksRepository marksRepo,RankService rankService,ReportCardPdfService pdfService ,StudentResultService studentResultService   ) {

        this.adminRepo = adminRepo;
        this.studentRepo = studentRepo;
        this.classRepo = classRepo;
        this.subjectRepo = subjectRepo;
        this.marksRepo = marksRepo;
        this.rankService =rankService;
        this.pdfService = pdfService;
        this.studentResultService =studentResultService;

    }



    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {

        long deletedMarksCount = marksRepo.countByDeletedTrue();

        model.addAttribute("deletedMarksCount", deletedMarksCount);

        return "admin-dashboard";
    }



    // 👉 HOME PAGE = LOGIN PAGE
    @GetMapping("/")
    public String homePage() {
        return "login";
    }

    // 👉 LOGIN PROCESS
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        Admin admin = adminRepo.findByUsername(username);

        if (admin != null && admin.getPassword().equals(password)) {
            session.setAttribute("loggedAdmin", admin.getUsername());
            return "redirect:/admin/dashboard";
        }

        redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Invalid Username or Password"
        );
        return "redirect:/";
    }


    // add students
    /*@GetMapping("/admin/add-student")
    public String addStudentPage(Model model) {

        model.addAttribute("classes", classRepo.findAll());

        return "add-student";
    }*/
    @GetMapping("/admin/add-student")
    public String addStudentPage(Model model) {

        // distinct class names only
        model.addAttribute("classList", classRepo.findDistinctClassNames());

        return "add-student";
    }



    @PostMapping("/admin/add-student")
    public String saveStudent(Student student,
                              RedirectAttributes redirectAttributes) {

        // Duplicate check
        if (studentRepo.existsByRollNumber(student.getRollNumber())) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Student with Roll No " + student.getRollNumber() + " already exists!"
            );
            return "redirect:/admin/add-student";
        }

        // Save student
        studentRepo.save(student);

        // Success message
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Student added successfully!"
        );

        // ✅ Redirect BACK to same page
        return "redirect:/admin/add-student";
    }


    @GetMapping("/admin/edit-student/{id}")
    public String editStudentForm(@PathVariable Long id, Model model) {

        Student student = studentRepo.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new RuntimeException("Student not found"));


        model.addAttribute("student", student);
        model.addAttribute("classList", classRepo.findDistinctClassNames());

        return "edit-student";
    }

    @PostMapping("/admin/update-student")
    public String updateStudent(Student student) {

        studentRepo.save(student);

        return "redirect:/admin/view-students";
    }



    // for class add
    @GetMapping("/admin/add-class")
    public String addClassPage() {
        return "add-class";
    }

    @PostMapping("/admin/add-class")
    public String saveClass(
            @RequestParam String className,
            @RequestParam String section,
            RedirectAttributes redirectAttributes
    ) {

        if (section == null || section.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Section is mandatory!"
            );
            return "redirect:/admin/add-class";
        }

        if (classRepo.existsByClassNameAndSection(className, section)) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Class with this section already exists!"
            );
            return "redirect:/admin/add-class";
        }

        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setClassName(className);
        schoolClass.setSection(section);

        classRepo.save(schoolClass);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Class added successfully!"
        );

        return "redirect:/admin/classes";
    }


    // edit classes
    @GetMapping("/admin/edit-class/{id}")
    public String editClass(@PathVariable Long id, Model model) {

        SchoolClass schoolClass = classRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid class id"));

        model.addAttribute("schoolClass", schoolClass);
        return "edit-class";
    }

    // update class
    @PostMapping("/admin/update-class")
    public String updateClass(
            @RequestParam Long id,
            @RequestParam String className,
            @RequestParam String section,
            RedirectAttributes redirectAttributes) {

        SchoolClass existing = classRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid class id"));

        // check duplicate (excluding current record)
        boolean exists = classRepo
                .existsByClassNameAndSectionAndIdNot(className, section, id);

        if (exists) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Class with this section already exists!"
            );
            return "redirect:/admin/edit-class/" + id;
        }

        existing.setClassName(className);
        existing.setSection(section);

        classRepo.save(existing);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Class updated successfully!"
        );

        return "redirect:/admin/classes";
    }



    // show all classes in one table
    @GetMapping("/admin/classes")
    public String viewAllClasses(Model model) {

        model.addAttribute("classes", classRepo.findAll());

        return "view-classes";
    }



    //  add subjects  for every class
    @GetMapping("/admin/add-subject")
    public String addSubjectPage(Model model) {
        model.addAttribute("classList", classRepo.findDistinctClassNames());
        return "add-subject";
    }


    @PostMapping("/admin/add-subject")
    public String saveSubject(
            @RequestParam String className,
            @RequestParam String section,
            @RequestParam String subjectName,
            @RequestParam Integer maxMarks,
            RedirectAttributes redirectAttributes
    ) {

        boolean exists = subjectRepo
                .existsByClassNameAndSectionAndSubjectName(className, section, subjectName);

        if (exists) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "❌ Subject already exists for this class & section"
            );
            return "redirect:/admin/add-subject";
        }

        Subject subject = new Subject();
        subject.setClassName(className);
        subject.setSection(section);
        subject.setSubjectName(subjectName);
        subject.setMaxMarks(maxMarks);

        subjectRepo.save(subject);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "✅ Subject added successfully"
        );

        return "redirect:/admin/add-subject";
    }




    // show all subject  table

    @GetMapping("/admin/subjects")
    public String viewSubjects(Model model) {

        model.addAttribute("subjects", subjectRepo.findAll());

        return "view-subjects";
    }

    // delete subject list
    @GetMapping("/admin/delete-subject/{id}")
    public String deleteSubject(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {

        subjectRepo.deleteById(id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Subject deleted successfully!"
        );

        return "redirect:/admin/subjects";
    }
    // edit subjects
    @GetMapping("/admin/edit-subject/{id}")
    public String editSubjectPage(@PathVariable Long id, Model model) {

        Subject subject = subjectRepo.findById(id).orElse(null);

        model.addAttribute("subject", subject);
        model.addAttribute("classes", classRepo.findAll());

        return "edit-subject";
    }
    // update subjects
    @PostMapping("/admin/update-subject")
    public String updateSubject(
            @RequestParam Long id,
            @RequestParam String className,
            @RequestParam String section,
            @RequestParam String subjectName,
            @RequestParam Integer maxMarks,
            RedirectAttributes redirectAttributes
    ) {

        boolean exists = subjectRepo
                .existsByClassNameAndSectionAndSubjectNameAndIdNot(
                        className, section, subjectName, id
                );

        if (exists) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "❌ Subject already exists for this class & section"
            );
            return "redirect:/admin/edit-subject/" + id;
        }

        Subject subject = subjectRepo.findById(id).orElseThrow();
        subject.setClassName(className);
        subject.setSection(section);
        subject.setSubjectName(subjectName);
        subject.setMaxMarks(maxMarks);

        subjectRepo.save(subject);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "✅ Subject updated successfully"
        );

        return "redirect:/admin/subjects";
    }


    // add marks
    @GetMapping("/admin/add-marks")
    public String addMarksPage(Model model) {

        model.addAttribute("classes", classRepo.findAll());
        model.addAttribute("students", studentRepo.findAll());
        model.addAttribute("subjects", subjectRepo.findAll());

        return "add-marks";
    }


     // save marks
     @PostMapping("/admin/save-marks")
     public String saveMarks(
             @RequestParam String className,
             @RequestParam String examType,
             @RequestParam Long studentId,
             @RequestParam String subjectName,
             @RequestParam Integer marks,
             RedirectAttributes redirectAttributes
     ) {

         Student student = studentRepo.findById(studentId).orElse(null);

         Marks m = new Marks();
         m.setClassName(className);
         m.setExamType(examType);
         m.setStudentId(studentId);
         m.setStudentName(student.getName());
         m.setRollNumber(student.getRollNumber());
         m.setSubjectName(subjectName);
         m.setMarksObtained(marks);

         marksRepo.save(m);

         redirectAttributes.addFlashAttribute(
                 "successMessage",
                 "Marks added successfully!"
         );

         return "redirect:/admin/add-marks";
     }

    // add bulk marks
    /*(@GetMapping("/admin/add-marks-bulk")
    public String addMarksBulk(Model model) {

        model.addAttribute("classes", classRepo.findAll());
        model.addAttribute("subjects", subjectRepo.findAll());

        return "add-marks-bulk";
    }*/

    @GetMapping("/admin/add-marks-bulk")
    public String addMarksBulk(Model model) {

        model.addAttribute(
                "classList",
                classRepo.findDistinctClassNames() // 🔥 ONLY DISTINCT
        );

        return "add-marks-bulk";
    }


    // show all students by class
    @PostMapping("/admin/load-students")
    public String loadStudents(
            @RequestParam String className,
            @RequestParam String section,
            @RequestParam String subjectName,
            @RequestParam String examType,
            Model model) {

        model.addAttribute(
                "students",
                studentRepo.findByClassNameAndSection(className, section)
        );

        model.addAttribute("className", className);
        model.addAttribute("section", section);
        model.addAttribute("subjectName", subjectName);
        model.addAttribute("examType", examType);

        return "add-marks-bulk";
    }



    // save makrks off students of purticular class
     @PostMapping("/admin/save-all-marks")
     public String saveAllMarks(@RequestParam String className,
                                @RequestParam String section,
                                @RequestParam String subjectName,
                                @RequestParam String examType,
                                @RequestParam List<Long> studentId,
                                @RequestParam List<Integer> marks,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

         String adminUser = (String) session.getAttribute("loggedAdmin");

         for (int i = 0; i < studentId.size(); i++) {

             boolean exists = marksRepo
                     .existsByClassNameAndSectionAndSubjectNameAndExamTypeAndStudentId(
                             className,section, subjectName, examType, studentId.get(i)
                     );

             if (exists) continue;

             Student s = studentRepo.findById(studentId.get(i)).orElse(null);
             if (s == null) continue;

             Marks m = new Marks();
             m.setClassName(className);
             m.setSection(section);
             m.setSubjectName(subjectName);
             m.setExamType(examType);
             m.setStudentId(s.getId());
             m.setStudentName(s.getName());
             m.setRollNumber(s.getRollNumber());
             m.setMarksObtained(marks.get(i));

             // 🔐 AUDIT


             marksRepo.save(m);
         }

         redirectAttributes.addFlashAttribute(
                 "successMessage",
                 "All marks saved successfully!"
         );

         return "redirect:/admin/add-marks-bulk";
     }





    // view marks
    /* @GetMapping("/admin/view-marks")
     public String viewMarksPage(Model model) {

         model.addAttribute("classes", classRepo.findAll());
         model.addAttribute("subjects", subjectRepo.findAll());

         return "view-marks";
     }*/
    @GetMapping("/admin/view-marks")
    public String viewMarksPage(Model model) {

        // DISTINCT class list only
        model.addAttribute("classList", classRepo.findDistinctClassNames());

        return "view-marks";
    }


    // load marks
      @PostMapping("/admin/load-marks")
      public String loadMarks(@RequestParam String className,
                              @RequestParam String subjectName,
                              @RequestParam String examType,
                              Model model) {

          List<Marks> marksList =
                  marksRepo.findByClassNameAndSubjectNameAndExamTypeAndDeletedFalse(
                          className, subjectName, examType
                  );

          model.addAttribute("marksList", marksList);
          model.addAttribute("className", className);
          model.addAttribute("subjectName", subjectName);
          model.addAttribute("examType", examType);

          model.addAttribute("classes", classRepo.findAll());
          model.addAttribute("subjects", subjectRepo.findAll());

          return "view-marks";
      }



     // edit enterd marks
    @GetMapping("/admin/edit-marks/{id}")
    public String editMarksPage(@PathVariable Long id, Model model) {

        Marks marks = marksRepo.findById(id).orElse(null);

        model.addAttribute("marks", marks);

        return "edit-marks";
    }

     // update enterd marks
     @PostMapping("/admin/update-marks")
     public String updateMarks(@RequestParam Long id,
                               @RequestParam Integer marksObtained,
                               RedirectAttributes redirectAttributes) {

         Marks m = marksRepo.findById(id).orElse(null);

         if (m != null) {
             m.setMarksObtained(marksObtained);

             // 🔐 AUDIT


             marksRepo.save(m);
         }

         redirectAttributes.addFlashAttribute(
                 "successMessage",
                 "Marks updated successfully!"
         );

         return "redirect:/admin/view-marks";
     }


    // delete entered marks
    @PostMapping("/admin/delete-marks/{id}")
    public String softDeleteMarks(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        Marks m = marksRepo.findById(id).orElse(null);
        if (m != null) {
            m.setDeleted(true);
            marksRepo.save(m);
        }

        redirectAttributes.addFlashAttribute(
                "successMessage", "Marks deleted successfully");

        return "redirect:/admin/view-marks";
    }



    // restore deleted marks
    @GetMapping("/admin/deleted-marks")
    public String viewDeletedMarks(Model model) {

        model.addAttribute("marksList", marksRepo.findByDeletedTrue());
        return "deleted-marks";
    }

    // restore action
    @GetMapping("/admin/restore-marks/{id}")
    public String restoreMarks(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {

        Marks m = marksRepo.findById(id).orElse(null);

        if (m != null) {
            m.setDeleted(false);
            marksRepo.save(m);
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Marks restored successfully"
        );

        return "redirect:/admin/deleted-marks";
    }



   //
   /* @GetMapping("/admin/sections-by-class")
   @ResponseBody
   public List<String> getSectionsByClass(@RequestParam String className) {
       return classRepo.findSectionsByClassName(className);
   }*/

    // 1️⃣ get sections by class
    @GetMapping("/admin/sections-by-class")
    @ResponseBody
    public List<String> getSections(@RequestParam String className) {
        return classRepo.findSectionsByClassName(className);
    }

    // 2️⃣ get subjects by class + section
    @GetMapping("/admin/subjects-by-class-section")
    @ResponseBody
    public List<String> getSubjects(
            @RequestParam String className,
            @RequestParam String section) {

        return subjectRepo.findSubjectsByClassAndSection(className, section);
    }

    @GetMapping("/admin/student-report")
    public String studentReportPage(Model model) {
        model.addAttribute("classList", classRepo.findDistinctClassNames());
        return "student-report";
    }


    @PostMapping("/admin/student-report")
    public String generateStudentReport(
            @RequestParam Long studentId,
            @RequestParam String className,
            @RequestParam String section,
            @RequestParam String examType,
            Model model) {

        Student student = studentRepo.findById(studentId).orElseThrow();
        List<Marks> marks = marksRepo
                .findByStudentIdAndClassNameAndSectionAndExamTypeAndDeletedFalse(
                        studentId, className, section, examType);

        int rank = rankService.getStudentRank(
                studentId, className, section, examType);



        List<Marks> marksList =
                marksRepo.findByStudentIdAndExamTypeAndDeletedFalse(
                        studentId, examType
                );

        int total = marksList.stream()
                .mapToInt(Marks::getMarksObtained)
                .sum();
        int totalMarks = marks.stream()
                .mapToInt(Marks::getMarksObtained)
                .sum();
        int maxTotal = marksList.size() * 100;
        double percentage = maxTotal == 0 ? 0 : (total * 100.0) / maxTotal;

        String grade;
        if (percentage >= 75) grade = "A";
        else if (percentage >= 60) grade = "B";
        else if (percentage >= 45) grade = "C";
        else grade = "D";

        model.addAttribute("student", student);
        model.addAttribute("marksList", marksList);
        model.addAttribute("total", total);
        model.addAttribute("percentage", percentage);
        model.addAttribute("grade", grade);
        model.addAttribute("examType", examType);
        model.addAttribute("marks", marks);
        model.addAttribute("totalMarks", totalMarks);
        model.addAttribute("rank", rank);


        return "student-report";
    }

    @GetMapping("/admin/students-by-class-section")
    @ResponseBody
    public List<Student> getStudents(
            @RequestParam String className,
            @RequestParam String section) {

        return studentRepo.findByClassNameAndSection(className, section);
    }

    @GetMapping("/admin/class-rank")
    public String showRankPage(Model model) {

        model.addAttribute("classList",
                classRepo.findDistinctClassNames());

        return "class-rank";
    }

    @PostMapping("/admin/class-rank")
    public String calculateRank(
            @RequestParam String className,
            @RequestParam String section,
            @RequestParam String examType,
            Model model) {

        List<StudentRankDTO> ranks =
                rankService.calculateClassRank(className, section, examType);

        model.addAttribute("ranks", ranks);
        model.addAttribute("className", className);
        model.addAttribute("section", section);
        model.addAttribute("examType", examType);

        return "class-rank";
    }

    @GetMapping("/admin/student-report/pdf")
    public ResponseEntity<byte[]> downloadReportCardPdf(
            @RequestParam Long studentId,
            @RequestParam String className,
            @RequestParam String section,
            @RequestParam String examType) {

        Student student = studentRepo.findById(studentId).orElseThrow();

        List<Marks> marks = marksRepo
                .findByStudentIdAndClassNameAndSectionAndExamTypeAndDeletedFalse(
                        studentId,
                        className,
                        section,
                        String.valueOf(examType));

        int rank = rankService.getStudentRank(
                studentId,
                className,
                section,
                String.valueOf(examType));

        byte[] pdf = pdfService.generatePdf(
                student,
                className,
                section,
                examType,
                marks,
                rank);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=report-card.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/admin/combine-student-report")
    public String studentReportPageCombine(Model model) {
        model.addAttribute("classList", classRepo.findDistinctClassNames());
        return "combine-student-report";
    }

    @PostMapping("/admin/combine-student-report")
    public String generateStudentReport(
            @RequestParam Long studentId,
            @RequestParam String className,
            @RequestParam String section,
            Model model) {

        Student student = studentRepo.findById(studentId).orElseThrow();

        List<StudentSubjectResultDTO> resultList =
                marksRepo.getStudentCombinedResult(
                        studentId,
                        className,
                        section
                );

        int totalObtained = resultList.stream()
                .mapToInt(r ->
                        (r.getHalfYearlyMarks() != null ? r.getHalfYearlyMarks() : 0) +
                                (r.getAnnualMarks() != null ? r.getAnnualMarks() : 0)
                )
                .sum();

        int totalMax = resultList.stream()
                .mapToInt(r -> r.getMaxMarks() * 2)
                .sum();

        double percentage = totalMax == 0 ? 0 :
                (totalObtained * 100.0) / totalMax;
        int rank = studentResultService.getClassRank(studentId, className, section);

        String grade;
        if (percentage >= 75) grade = "A";
        else if (percentage >= 60) grade = "B";
        else if (percentage >= 45) grade = "C";
        else grade = "D";

        model.addAttribute("student", student);
        model.addAttribute("resultList", resultList);
        model.addAttribute("rank", rank);
        model.addAttribute("totalMarks", totalObtained);
        model.addAttribute("percentage", percentage);
        model.addAttribute("grade", grade);

        return "combine-student-report";
    }


    // 🔹 View Students Page
    // 1️⃣ OPEN PAGE
    @GetMapping("/view-students")
    public String viewStudentsPage(Model model) {
        model.addAttribute("classList", classRepo.findDistinctClassNames());
        return "view-students";
    }


    // 🔹 Load Students by Class & Section
    /*@PostMapping("/admin/view-students")
    public String loadStudentsByClassSection(
            @RequestParam String className,
            @RequestParam String section,
            Model model) {

        model.addAttribute("classList", classRepo.findDistinctClassNames());

        model.addAttribute(
                "students",
                studentRepo.findByClassNameAndSection(className, section)
        );

        model.addAttribute("selectedClass", className);
        model.addAttribute("selectedSection", section);

        return "view-students";
    }*/



        // 1️⃣ OPEN VIEW STUDENTS PAGE
        @GetMapping("admin/view-students")
        public String viewStudentsPagen(Model model) {
            model.addAttribute("classList", classRepo.findDistinctClassNames());
            return "view-students";
        }

        // 2️⃣ LOAD SECTIONS (GET ONLY)
        @GetMapping("admin/get-sections")
        @ResponseBody
        public List<String> getSectionsn(@RequestParam String className) {
            return classRepo.findSectionsByClassName(className);
        }

        // 3️⃣ LOAD STUDENTS (GET ONLY)
        @GetMapping("/admin/get-students")
        @ResponseBody
        public List<Student> getStudent(
                @RequestParam String className,
                @RequestParam String section) {

            return studentRepo.findByClassNameAndSectionAndIsDeletedFalse(className, section);
        }

    @PostMapping("/admin/delete-student/{id}")
    @ResponseBody
    public String deleteStudent(@PathVariable Long id) {

        Student student = studentRepo.findById(id).orElseThrow();
        student.setDeleted(true);   // 👈 SOFT DELETE
        studentRepo.save(student);

        return "success";
    }



    @GetMapping("/admin/deleted-students")
    public String viewDeletedStudents(Model model) {

        List<Student> deletedStudents =
                studentRepo.findByIsDeletedTrue();

        model.addAttribute("students", deletedStudents);

        return "deleted-students";
    }

    @GetMapping("/admin/restore-student/{id}")
    public String restoreStudent(@PathVariable Long id) {

        Student student = studentRepo
                .findByIdAndIsDeletedTrue(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.setDeleted(false);   // ♻️ RESTORE
        studentRepo.save(student);

        return "redirect:/admin/deleted-students";
    }


    @GetMapping("/admin/student-profile/{id}")
    public String studentProfile(@PathVariable Long id, Model model) {

        Student student = studentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        model.addAttribute("student", student);

        return "student-profile";
    }




























}
