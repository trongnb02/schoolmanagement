package com.example.schoolmnt.sm.classes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ClassRepository extends JpaRepository<Classes,Long> {

    @Query("SELECT c from Classes c" +
            " LEFT JOIN Teacher t" +
            " ON c.teacher.id = t.id" +
            " WHERE CONCAT(c.id,'') LIKE %?1% OR c.classname LIKE %?1% OR c.subjectname LIKE %?1% OR t.fullname LIKE %?1%")
    public Page<Classes> search(String keyword, Pageable pageable);
}
