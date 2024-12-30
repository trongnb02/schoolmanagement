package com.example.schoolmnt.sm.exam;

import com.example.schoolmnt.sm.classes.Classes;
import com.example.schoolmnt.sm.student.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name="exam")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String examname;

    private double weight;

    private double score;

    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm")
    private Date examdate;

    private double duration;

    @ManyToOne
    @JoinColumn(name="classes_id")
    private Classes classes;

    @ManyToOne
    @JoinColumn(name="student_id")
    private Student student;

    public Exam(String examname, double weight, double score, Date examdate, double duration, Classes classes, Student student) {
        this.examname = examname;
        this.weight = weight;
        this.score = score;
        this.examdate = examdate;
        this.duration = duration;
        this.classes = classes;
        this.student = student;
    }
}
