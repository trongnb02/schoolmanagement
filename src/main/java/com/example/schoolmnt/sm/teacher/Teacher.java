package com.example.schoolmnt.sm.teacher;

import com.example.schoolmnt.sm.classes.Classes;
import com.example.schoolmnt.sm.post.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="teacher")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Teacher {
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

    @OneToMany(orphanRemoval = false, cascade = CascadeType.ALL) //mappedBy = "teacher"
    @JoinColumn(name="teacher_id")
    private List<Classes> classesList = new ArrayList<>();

    @Column(name="createaccount", nullable = false)
    private boolean createaccount = false;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name="author_id")
    private List<Post> postList = new ArrayList<>();

    public Teacher(String fullname, Date birthdate, String gender, String email, boolean createaccount) {
        this.fullname = fullname;
        this.birthdate = birthdate;
        this.gender = gender;
        this.email = email;
        this.createaccount = createaccount;
    }
    public boolean getCreateaccount() {
        return this.createaccount;
    }

    public void removeClass(Classes classes) {
        this.classesList.remove(classes);
        classes.setTeacher(null);
    }
}
