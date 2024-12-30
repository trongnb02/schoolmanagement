package com.example.schoolmnt.sm.teacher;

import com.example.schoolmnt.sm.classes.Classes;
import com.example.schoolmnt.sm.student.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherService {
    private TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public List<Teacher> getAllTeachers() {

        return teacherRepository.findAll();
    }

    public Teacher saveTeacher(Teacher teacher) {
        boolean exists = teacherRepository.findByEmail(teacher.getEmail()).isPresent();
        if (exists){
            throw new IllegalStateException("Email already exist!");
        }
        return teacherRepository.save(teacher);
    }

    public Teacher getTeacherById(long id) {

        return teacherRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Teacher not found by id"));
    }

    public Teacher updateTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public void deleteTeacher(Long id) {

        teacherRepository.deleteById(id);
    }

    public Page<Teacher> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection, String keyword) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo -1, pageSize, sort);
        if (keyword != null && !keyword.isEmpty()) {
            return teacherRepository.search(keyword, pageable);
        }
        return teacherRepository.findAll(pageable);
    }

    public Teacher getTeacherByEmail(String email) {
        return teacherRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Teacher not found by email"));
    }

}
