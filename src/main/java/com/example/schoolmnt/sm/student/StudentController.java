package com.example.schoolmnt.sm.student;

import com.example.schoolmnt.sm.appuser.AppUserService;
import com.example.schoolmnt.sm.classes.Classes;
import com.example.schoolmnt.sm.registration.RegistrationRequest;
import com.example.schoolmnt.sm.registration.RegistrationService;
import com.example.schoolmnt.sm.teacher.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.example.schoolmnt.sm.appuser.Role.STUDENT;
import static com.example.schoolmnt.sm.appuser.Role.TEACHER;

@Controller
public class StudentController {
    private StudentService studentService;
    private RegistrationService registrationService;
    private AppUserService appUserService;

    public StudentController(StudentService studentService, RegistrationService registrationService, AppUserService appUserService) {
        this.studentService = studentService;
        this.registrationService = registrationService;
        this.appUserService = appUserService;
    }

    @GetMapping("/students")
    public String listStudents(Model model) {
        return studentFindPaginated(1, "id", "asc", null, model);
    }

    @GetMapping("/students/new")
    public String studentForm(Model model) {
        Student student = new Student();
        model.addAttribute("student", student);
        return "student/studentform";
    }

    @PostMapping("/students")
    public String saveStudent(@ModelAttribute("student")Student student, RedirectAttributes redirectAttributes) {
        String message = null;
        if (student.getCreateaccount()){
            message = registrationService.register(
                    RegistrationRequest.builder()
                            .fullname(student.getFullname())
                            .username(registrationService.generateUsername(student.getFullname()))
                            .email(student.getEmail())
                            .password(registrationService.generatePassword(20))
                            .role(STUDENT)
                            .build()
                    );
            if (message.equals("ok")){
                studentService.saveStudent(student);
                message = "Create account successfully. Please check " + student.getEmail() + " and confirm.";
            }
        }else{
            studentService.saveStudent(student);
            message = "Add new student successfully.";
        }
        //model.addAttribute("msg", message);
        redirectAttributes.addFlashAttribute("msg", message);
        return "redirect:/students";
    }

    @GetMapping("/students/edit/{id}")
    public String editStudent(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        return "student/studentform";
    }

    @PostMapping("/students/edit/{id}")
    public String updateStudent(@PathVariable Long id, @ModelAttribute("student")Student student, Model model) {
        Student newStudent = studentService.getStudentById(id);
        newStudent.setFullname(student.getFullname());
        newStudent.setGender(student.getGender());
        newStudent.setEmail(student.getEmail());
        newStudent.setBirthdate(student.getBirthdate());
        studentService.updateStudent(newStudent);
        return "redirect:/students";
    }

    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        if (student.getCreateaccount()){
            appUserService.deleteAppuserByEmail(student.getEmail());
        }
        studentService.deleteStudent(id);
        return "redirect:/students";
    }

    @GetMapping("/students/page/{pageNo}")
    public String studentFindPaginated(@PathVariable int pageNo,
                                     @RequestParam("sortField") String sortField,
                                     @RequestParam("sortDir") String sortDir,
                                       @RequestParam(value = "keyword", required = false) String keyword,
                                     Model model) {
        int pageSize = 5;
        Page<Student> page = studentService.findPaginated(pageNo, pageSize, sortField, sortDir, keyword);
        List<Student> students = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);

        model.addAttribute("students", students);

        return "student/students";
    }

}
