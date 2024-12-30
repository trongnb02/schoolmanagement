package com.example.schoolmnt.sm.post;

import com.example.schoolmnt.sm.classes.Classes;
import com.example.schoolmnt.sm.teacher.Teacher;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="post")
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="content", nullable = false)
    private String content;

    @Column(name="lastmodified", nullable=false)
    private LocalDateTime lastmodified;

    @ManyToOne
    @JoinColumn(name="author_id")
    private Teacher author;

    @ManyToMany(mappedBy = "posts")
    private Set<Classes> classes = new HashSet<>();

    public Post(String title, String content, LocalDateTime lastmodified, Teacher author) {
        this.title = title;
        this.content = content;
        this.lastmodified = lastmodified;
        this.author = author;
    }

    @PreRemove
    private void removeClassAssociations() {
        for (Classes c : this.classes){
            c.getPosts().remove(this);
        }
    }
}
