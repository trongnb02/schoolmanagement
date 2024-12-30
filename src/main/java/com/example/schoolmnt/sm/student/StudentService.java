package com.example.schoolmnt.sm.student;

import com.example.schoolmnt.sm.classes.Classes;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StudentService {
    List<Student> getAllStudents();
    Student saveStudent(Student student);
    Student getStudentById(Long id);
    Student updateStudent(Student student);
    void deleteStudent(Long id);
    Page<Student> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection, String keyword);
    Student getStudentByEmail(String email);
}
