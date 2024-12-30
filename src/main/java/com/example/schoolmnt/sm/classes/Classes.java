package com.example.schoolmnt.sm.classes;

import com.example.schoolmnt.sm.exam.Exam;
import com.example.schoolmnt.sm.post.Post;
import com.example.schoolmnt.sm.student.Student;
import com.example.schoolmnt.sm.teacher.Teacher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.*;

@Entity
@Table(name="class")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Classes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String classname;

    @Column(nullable = false)
    private String subjectname;

    @Column(nullable = false)
    private int credits;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Column(name="startdate", nullable = false)
    private Date startdate;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Column(name="enddate", nullable = false)
    private Date enddate;


    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name="student_class",
            joinColumns = @JoinColumn(name="class_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name="student_id", referencedColumnName = "id")
    )
    private Set<Student> students = new HashSet<>();

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL) //mappedBy = "classes"
    @JoinColumn(name="classes_id")
    private List<Exam> examList = new ArrayList<>();;

    @ManyToOne
    @JoinColumn(name="teacher_id")
    private Teacher teacher;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name="post_class",
            joinColumns = @JoinColumn(name="class_id"),
            inverseJoinColumns = @JoinColumn(name="post_id")
    )
    private Set<Post> posts = new HashSet<>();

    public Classes(String classname, String subjectname, int credits, Date startdate, Date enddate) {
        this.classname = classname;
        this.subjectname = subjectname;
        this.credits = credits;
        this.startdate = startdate;
        this.enddate = enddate;
    }

    @Override
    public String toString() {
        return classname;
    }

}
