package com.example.schoolmnt.sm.student;

import com.example.schoolmnt.sm.teacher.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmail(String email);

    @Query("SELECT DISTINCT  s from Student s" +
            " JOIN s.classes c" +
            " WHERE CONCAT(s.id,'') LIKE %?1% OR s.fullname LIKE %?1% OR s.email LIKE %?1% OR c.classname LIKE %?1%")
    public Page<Student> search(String keyword, Pageable pageable);
}