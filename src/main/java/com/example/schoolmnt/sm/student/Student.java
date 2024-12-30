package com.example.schoolmnt.sm.student;

import com.example.schoolmnt.sm.classes.Classes;
import com.example.schoolmnt.sm.exam.Exam;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="student")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="fullname", nullable = false)
    private String fullname;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Column(name="birthdate", nullable = false)
    private Date birthdate;

    @Column(name="gender", nullable = false)
    private String gender;

    @Column(name="email" , nullable = false, unique = true)
    private String email;

    @ManyToMany(mappedBy = "students")
    private Set<Classes> classes = new HashSet<>();

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL) //mappedBy = "student"
    @JoinColumn(name="student_id")
    private List<Exam> examList;

    @Column(name="createaccount", nullable = false)
    private boolean createaccount = false;

    public Student(String fullname, Date birthdate, String gender, String email) {
        this.fullname = fullname;
        this.birthdate = birthdate;
        this.gender = gender;
        this.email = email;
    }

    public boolean getCreateaccount() {
        return this.createaccount;
    }

    @PreRemove
    private void removeClassAssociations() {
        for (Classes c : this.classes){
            c.getStudents().remove(this);
        }
    }
}
