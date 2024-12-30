package com.example.schoolmnt.sm.classes;

import com.example.schoolmnt.sm.CSV.CSVService;
import com.example.schoolmnt.sm.appuser.AppUser;
import com.example.schoolmnt.sm.post.Post;
import com.example.schoolmnt.sm.post.PostService;
import com.example.schoolmnt.sm.student.Student;
import com.example.schoolmnt.sm.student.StudentService;
import com.example.schoolmnt.sm.teacher.Teacher;
import com.example.schoolmnt.sm.teacher.TeacherService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Controller
public class ClassController {

    private ClassService classService;
    private TeacherService teacherService;
    private StudentService studentService;
    private PostService postService;
    private CSVService csvService;

    public ClassController(ClassService classService, TeacherService teacherService, StudentService studentService, PostService postService, CSVService csvService) {
        this.classService = classService;
        this.teacherService = teacherService;
        this.studentService = studentService;
        this.postService = postService;
        this.csvService = csvService;
    }

    @GetMapping("/classes")
    public String listClasses(Model model) {
        return classFindPaginated(1, "id", "asc", null,model);
    }

    @GetMapping("/class/new")
    public String classForm(Model model) {
        Classes clas = new Classes();
        model.addAttribute("class", clas);
        model.addAttribute("teachers", teacherService.getAllTeachers());
        model.addAttribute("students", studentService.getAllStudents());
        return "class/classform";
    }

    @PostMapping("/class")
    public String saveClass(@ModelAttribute("class")Classes classes) {
        classService.saveClass(classes);
        return "redirect:/classes";
    }

    @GetMapping("/class/edit/{id}")
    public String editClass(@PathVariable Long id, Model model) {
        model.addAttribute("class", classService.getClassById(id));
        model.addAttribute("teachers", teacherService.getAllTeachers());
        model.addAttribute("students", studentService.getAllStudents());
        return "class/classform";
    }

    @PostMapping("/class/edit/{id}")
    public String updateClass(@PathVariable Long id, @ModelAttribute("class")Classes classes, Model model) {
        Classes newClasses = classService.getClassById(id);
        newClasses.setClassname(classes.getClassname());
        newClasses.setSubjectname(classes.getSubjectname());
        newClasses.setCredits(classes.getCredits());
        newClasses.setStartdate(classes.getStartdate());
        newClasses.setEnddate(classes.getEnddate());
        newClasses.setTeacher(classes.getTeacher());
        classService.updateClass(newClasses);
        return "redirect:/classes";
    }

    @GetMapping("/class/delete/{id}")
    public String deleteclass(@PathVariable Long id) {
        classService.deleteClass(id);
        return "redirect:/classes";
    }

    @GetMapping("/class/updatestudents/{id}")
    public String studentsShow(@PathVariable Long id, Model model) {
        model.addAttribute("class", classService.getClassById(id));
        model.addAttribute("students", classService.getClassById(id).getStudents());
        return "class/studentlist";
    }

    @PostMapping("/class/updatestudents/{classid}/delete/{studentid}")
    public String updatestudentlist(@PathVariable Long classid, @PathVariable Long studentid, RedirectAttributes redirectAttributes) {
        Student removeStudent = studentService.getStudentById(studentid);
        Classes classes = classService.getClassById(classid);
        classes.getStudents().remove(removeStudent);
        classService.updateClass(classes);
        redirectAttributes.addFlashAttribute("msg","Remove student from this class successfully");
        return "redirect:/class/updatestudents/{classid}";
    }

    @GetMapping("/class/updatestudents/{id}/new")
    public String addStudentToList(@PathVariable Long id, Model model) {
        Classes classes = classService.getClassById(id);
        Set<Student> studentList = new HashSet<Student>();
        for (Student student: studentService.getAllStudents()) {
            if (!classes.getStudents().contains(student)) {
                studentList.add(student);
            }
        }
        model.addAttribute("class", classes);
        model.addAttribute("students", studentList);
        return "class/studentlistadd";
    }

    @PostMapping("/class/updatestudents/{classid}/add/{studentid}")
    public String addStudent(@PathVariable Long classid, @PathVariable Long studentid, RedirectAttributes redirectAttributes) {
        Student studentAdd = studentService.getStudentById(studentid);
        Classes classes = classService.getClassById(classid);
        classes.getStudents().add(studentAdd);
        classService.updateClass(classes);
        redirectAttributes.addFlashAttribute("msg","Add student to this class successfully");
        return "redirect:/class/updatestudents/{classid}/new";
    }

    @GetMapping("/class/{classid}/exams")
    public String examsShow(@PathVariable Long classid, Model model) {
        model.addAttribute("class", classService.getClassById(classid));
        model.addAttribute("exams", classService.getClassById(classid).getExamList());
        return "exam/exams";
    }

    @GetMapping("/class/{classid}/exams/generatecsv")
    public void generatecsv(@PathVariable Long classid, Model model, HttpServletResponse response) throws IOException {
        Set<Student> studentList = classService.getClassById(classid).getStudents();
        response.setContentType("text/csv");
        response.addHeader("Content-Disposition", "attachment; filename=\"exams-" + classService.getClassById(classid).getClassname() + ".csv\"");
        csvService.csvGenerate(studentList,response.getWriter(), classid);
    }

    @PostMapping("/class/{classid}/exams/upload")
    public String importExamsFromCSV(@RequestParam("file") MultipartFile file,@PathVariable Long classid, RedirectAttributes redirectAttributes){
        String message = "";
        if (csvService.hasCSVFormat(file)) {
            try {
                csvService.save(file);

                message = "Uploaded the file successfully: " + file.getOriginalFilename();
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!" + e.getMessage();
            }
        }else {
            message = "No CSV file found!";
        }
        redirectAttributes.addFlashAttribute("msg", message);
        return "redirect:/class/{classid}/exams";
    }

    @GetMapping("/class/page/{pageNo}")
    public String classFindPaginated(@PathVariable int pageNo,
                                     @RequestParam("sortField") String sortField,
                                     @RequestParam("sortDir") String sortDir,
                                     @RequestParam(value = "keyword", required = false) String keyword,
                                     Model model) {
        int pageSize = 5;
        Page<Classes> page = classService.findPaginated(pageNo, pageSize, sortField, sortDir, keyword);
        List<Classes> classes = page.getContent();
        List<Classes> modifiableClassList = new ArrayList<>(classes);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))) {
            AppUser user = (AppUser) auth.getPrincipal();
            Student student = studentService.getStudentByEmail(user.getEmail());
            modifiableClassList.removeIf(clas -> !clas.getStudents().contains(student));
        }
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"))) {
            AppUser user = (AppUser) auth.getPrincipal();
            Teacher teacher = teacherService.getTeacherByEmail(user.getEmail());
            modifiableClassList.removeIf(clas -> !clas.getTeacher().equals(teacher));
        }

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);

        model.addAttribute("classes", modifiableClassList);

        return "class/classes";
    }

    @GetMapping("/class/updateposts/{classid}")
    public String postsShow(@PathVariable Long classid, Model model) {
        model.addAttribute("class", classService.getClassById(classid));
        model.addAttribute("posts", classService.getClassById(classid).getPosts());
        return "class/postlist";
    }

    @GetMapping("/class/updateposts/{classid}/new")
    public String addPostToList(@PathVariable Long classid, Model model) {
        Classes classes = classService.getClassById(classid);
        Set<Post> postList = new HashSet<Post>();
        for (Post post: postService.getAllPosts()) {
            if (!classes.getPosts().contains(post)) {
                postList.add(post);
            }
        }
        model.addAttribute("class", classes);
        model.addAttribute("posts", postList);
        return "class/postlistadd";
    }

    @PostMapping("/class/updateposts/{classid}/add/{postid}")
    public String addPost(@PathVariable Long classid, @PathVariable Long postid, RedirectAttributes redirectAttributes) {
        Post post = postService.getPostById(postid);
        Classes classes = classService.getClassById(classid);
        classes.getPosts().add(post);
        classService.updateClass(classes);
        redirectAttributes.addFlashAttribute("msg","Add new post to this class successfully");
        return "redirect:/class/updateposts/{classid}/new";
    }

    @PostMapping("/class/updateposts/{classid}/delete/{postid}")
    public String updatepostlist(@PathVariable Long classid, @PathVariable Long postid, RedirectAttributes redirectAttributes) {
        Post removePost = postService.getPostById(postid);
        Classes classes = classService.getClassById(classid);
        classes.getPosts().remove(removePost);
        classService.updateClass(classes);
        redirectAttributes.addFlashAttribute("msg","Deleted the post from this class successfully");
        return "redirect:/class/updateposts/{classid}";
    }

    @GetMapping("/class/view/{classid}")
    public String view(@PathVariable Long classid, Model model) {
        Classes classes = classService.getClassById(classid);
        model.addAttribute("class", classes);
        model.addAttribute("posts", classes.getPosts());
        return "class/view";
    }

}
