package com.example.schoolmnt.sm.CSV;

import com.example.schoolmnt.sm.classes.ClassRepository;
import com.example.schoolmnt.sm.classes.ClassService;
import com.example.schoolmnt.sm.exam.Exam;
import com.example.schoolmnt.sm.exam.ExamRepository;
import com.example.schoolmnt.sm.student.Student;
import com.example.schoolmnt.sm.student.StudentRepository;
import com.example.schoolmnt.sm.student.StudentService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;


@Service
public class CSVService {
    @Autowired
    ClassService classService;
    @Autowired
    StudentService studentService;
    @Autowired
    ExamRepository examRepository;

    public boolean hasCSVFormat(MultipartFile file) {
        String TYPE = "text/csv";
        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }

    public List<Exam> csvToExams(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

            List<Exam> exams = new ArrayList<Exam>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH);

            for (CSVRecord csvRecord : csvRecords) {
                Exam newexam = new Exam(
                        csvRecord.get("Examname"),
                        Double.parseDouble(csvRecord.get("Weight")),
                        Double.parseDouble(csvRecord.get("Score")),
                        formatter.parse(csvRecord.get("Examdate")),
                        Double.parseDouble(csvRecord.get("Duration")),
                        classService.getClassById(Long.parseLong(csvRecord.get("ClassId"))),
                        studentService.getStudentByEmail(csvRecord.get("StudentEmail"))
                );

                exams.add(newexam);
            }

            return exams;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        } catch (ParseException e) {
            throw new RuntimeException("Parse examdate failed: " + e.getMessage());
        }
    }

    public void save(MultipartFile file) {
        try {
            List<Exam> exams = csvToExams(file.getInputStream());
            examRepository.saveAll(exams);
        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }

    public void csvGenerate(Set<Student> students, Writer writer, Long classid){
        try {

            CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
            printer.printRecord("Examname","Weight","Examdate","Duration","ClassId","StudentEmail","Student's Fullname","Gender","Score");
            for (Student student : students) {
                printer.printRecord("",0,"2024-12-13T14:30",0,classid,student.getEmail(),student.getFullname(),student.getGender(),0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
