package com.school.student_result.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.school.student_result.entity.Marks;
import com.school.student_result.entity.Student;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ReportCardPdfService {

    public byte[] generatePdf(
            Student student,
            String className,
            String section,
            String examType,
            List<Marks> marks,
            int rank) {

        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);

            document.open();

            // 🔹 TITLE
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Student Report Card", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("\n"));

            // 🔹 STUDENT INFO
            document.add(new Paragraph("Name: " + student.getName()));
            document.add(new Paragraph("Roll No: " + student.getRollNumber()));
            document.add(new Paragraph("Class: " + className + " " + section));
            document.add(new Paragraph("Exam: " + examType));

            document.add(new Paragraph("\n"));

            // 🔹 TABLE
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            table.addCell("Subject");
            table.addCell("Marks");

            int total = 0;

            for (Marks m : marks) {
                table.addCell(m.getSubjectName());
                table.addCell(String.valueOf(m.getMarksObtained()));
                total += m.getMarksObtained();
            }

            document.add(table);

            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Total Marks: " + total));
            document.add(new Paragraph("Class Rank: " + rank));

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
