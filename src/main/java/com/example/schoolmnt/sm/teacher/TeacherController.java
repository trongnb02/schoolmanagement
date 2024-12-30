package com.example.schoolmnt.sm.teacher;

import com.example.schoolmnt.sm.appuser.AppUserService;
import com.example.schoolmnt.sm.classes.ClassService;
import com.example.schoolmnt.sm.classes.Classes;
import com.example.schoolmnt.sm.registration.RegistrationRequest;
import com.example.schoolmnt.sm.registration.RegistrationService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

import static com.example.schoolmnt.sm.appuser.Role.TEACHER;


@Controller
public class TeacherController {

    private final AppUserService appUserService;
    private TeacherService teacherService;
    private ClassService classService;
    private RegistrationService registrationService;

    public TeacherController(TeacherService teacherService, ClassService classService, RegistrationService registrationService, AppUserService appUserService) {
        this.teacherService = teacherService;
        this.classService = classService;
        this.registrationService = registrationService;
        this.appUserService = appUserService;
    }

    @GetMapping("/teacher")
    public String listTeachers(Model model) {
        return teacherFindPaginated(1, "id", "asc", null, model);
    }

    @GetMapping("/teacher/new")
    public String teacherForm(Model model) {
        Teacher teacher = new Teacher();
        model.addAttribute("teacher", teacher);
        model.addAttribute("classes", classService.getAllClasses());
        return "teacher/teacherform";
    }

    @PostMapping("/teacher")
    public String saveTeacher(@ModelAttribute("teacher")Teacher teacher, RedirectAttributes redirectAttributes) {
        String message = null;
        if (teacher.getCreateaccount()){
            message = registrationService.register(
                    RegistrationRequest.builder()
                            .fullname(teacher.getFullname())
                            .username(registrationService.generateUsername(teacher.getFullname()))
                            .email(teacher.getEmail())
                            .password(registrationService.generatePassword(20))
                            .role(TEACHER)
                            .build()
                    );
            if (message.equals("ok")){
                teacherService.saveTeacher(teacher);
                message = "Create account successfully. Please check " + teacher.getEmail() + " and confirm.";
            }

        }else{
            teacherService.saveTeacher(teacher);
            message = "Add new teacher successfully.";
        }
        redirectAttributes.addFlashAttribute("msg", message);
        return "redirect:/teacher";
    }

    @GetMapping("/teacher/edit/{id}")
    public String editTeacher(@PathVariable Long id, Model model) {
        model.addAttribute("teacher", teacherService.getTeacherById(id));
        model.addAttribute("classes", classService.getAllClasses());
        return "teacher/teacherform";
    }

    @PostMapping("/teacher/edit/{id}")
    public String updateTeacher(@PathVariable Long id, @ModelAttribute("teacher") Teacher teacher, Model model) {
        Teacher newTeacher = teacherService.getTeacherById(id);
        newTeacher.setFullname(teacher.getFullname());
        newTeacher.setGender(teacher.getGender());
        newTeacher.setEmail(newTeacher.getEmail());
        newTeacher.setBirthdate(teacher.getBirthdate());
        List<Classes> classesList = newTeacher.getClassesList();
        if (classesList != null && !classesList.isEmpty()) {
            for (Classes clas : new ArrayList<>(classesList)) {
                newTeacher.removeClass(clas);
            }
        }
        newTeacher.setClassesList(teacher.getClassesList());
        teacherService.updateTeacher(newTeacher);
        return "redirect:/teacher";
    }

    @GetMapping("/teacher/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        Teacher teacher = teacherService.getTeacherById(id);
        if (teacher.getCreateaccount()){
            appUserService.deleteAppuserByEmail(teacher.getEmail());
        }
        if (teacher.getClassesList() != null && !teacher.getClassesList().isEmpty()) {
            for (Classes clas : new ArrayList<>(teacher.getClassesList())) {
                teacher.removeClass(clas);
            }
        }
        teacherService.deleteTeacher(id);
        return "redirect:/teacher";
    }

    @GetMapping("/teacher/page/{pageNo}")
    public String teacherFindPaginated(@PathVariable int pageNo,
                                     @RequestParam("sortField") String sortField,
                                     @RequestParam("sortDir") String sortDir,
                                     @RequestParam(value = "keyword", required = false) String keyword,
                                     Model model) {
        int pageSize = 5;
        Page<Teacher> page = teacherService.findPaginated(pageNo, pageSize, sortField, sortDir, keyword);
        List<Teacher> teachers = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);

        model.addAttribute("teachers", teachers);

        return "teacher/teachers";
    }
}
