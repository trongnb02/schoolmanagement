package com.example.schoolmnt.sm.exam;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    @Query("SELECT e from Exam e" +
            " JOIN Classes c on e.classes.id = c.id" +
            " JOIN Student s on e.student.id = s.id" +
            " WHERE CONCAT(e.id,'') LIKE %?1% OR e.examname LIKE %?1% OR CONCAT(e.weight,'') LIKE %?1% OR CONCAT(e.score,'') LIKE %?1%" +
            " OR CONCAT(e.duration,'') LIKE %?1% OR c.classname LIKE %?1% OR s.fullname LIKE %?1%")
    public Page<Exam> search(String keyword, Pageable pageable);
}
