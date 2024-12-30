package com.example.schoolmnt.sm.post;

import com.example.schoolmnt.sm.exam.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post,Long> {
    @Query("SELECT p from Post p" +
            " LEFT JOIN Teacher t ON p.author.id = t.id" +
            " WHERE CONCAT(p.id,'') LIKE %?1% OR p.title LIKE %?1% OR p.content LIKE %?1% or p.author.fullname LIKE %?1%")
    public Page<Post> search(String keyword, Pageable pageable);
}
