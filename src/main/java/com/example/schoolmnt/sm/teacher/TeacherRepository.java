package com.example.schoolmnt.sm.teacher;

import com.example.schoolmnt.sm.appuser.AppUser;
import com.example.schoolmnt.sm.classes.Classes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByEmail(String email);

    @Query("SELECT t from Teacher t" +
            " JOIN t.classesList c" +
            " WHERE CONCAT(t.id,'') LIKE %?1% OR t.fullname LIKE %?1% OR t.email LIKE %?1% OR c.classname LIKE %?1%")
    public Page<Teacher> search(String keyword, Pageable pageable);


}
