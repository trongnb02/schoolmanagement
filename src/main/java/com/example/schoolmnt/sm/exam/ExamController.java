package com.example.schoolmnt.sm.exam;

import com.example.schoolmnt.sm.classes.ClassService;
import com.example.schoolmnt.sm.classes.Classes;
import com.example.schoolmnt.sm.student.StudentService;
import com.example.schoolmnt.sm.teacher.Teacher;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller
public class ExamController {

    private ExamService examService;
    private ClassService classService;
    private StudentService studentService;

    public ExamController(ExamService examService, ClassService classService, StudentService studentService) {
        this.examService = examService;
        this.classService = classService;
        this.studentService = studentService;
    }

    @GetMapping("/exams")
    public String listExams(Model model) {
        return examFindPaginated(1, "id", "asc", null, model);
    }

    @GetMapping("/exam/new")
    public String examForm(Model model) {
        model.addAttribute("exam", new Exam());
        model.addAttribute("classes", classService.getAllClasses());
        model.addAttribute("students", studentService.getAllStudents());
        return "exam/examform";
    }

    @GetMapping("/exam/new/{classid}")
    public String examFormWithClassid(@PathVariable Long classid, Model model) {
        Classes classes = classService.getClassById(classid);
        model.addAttribute("exam", new Exam());
        model.addAttribute("classes", classes);
        model.addAttribute("students", classes.getStudents());
        return "exam/examform";
    }

    @PostMapping("/exam")
    public String saveExam(@ModelAttribute("exam")Exam exam) {
        examService.saveExam(exam);
        return "redirect:/exams";
    }

    @GetMapping("/exam/edit/{id}")
    public String editExam(@PathVariable Long id, Model model) {
        model.addAttribute("exam", examService.getExamById(id));
        model.addAttribute("classes", classService.getAllClasses());
        model.addAttribute("students", studentService.getAllStudents());
        return "exam/examform";
    }

    @PostMapping("/exam/edit/{id}")
    public String updateExam(@PathVariable Long id, @ModelAttribute("exam") Exam exam, Model model) {
        Exam newExam = examService.getExamById(id);
        newExam.setExamname(exam.getExamname());
        newExam.setWeight(exam.getWeight());
        newExam.setScore(exam.getScore());
        newExam.setExamdate(exam.getExamdate());
        newExam.setDuration(exam.getDuration());
        examService.updateExam(newExam);
        return "redirect:/exams";
    }

    @GetMapping("/exam/delete/{id}")
    public String deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return "redirect:/exams";
    }

    @GetMapping("/exam/page/{pageNo}")
    public String examFindPaginated(@PathVariable int pageNo,
                                     @RequestParam("sortField") String sortField,
                                     @RequestParam("sortDir") String sortDir,
                                    @RequestParam(value = "keyword", required = false) String keyword,
                                     Model model) {
        int pageSize = 5;
        Page<Exam> page = examService.findPaginated(pageNo, pageSize, sortField, sortDir, keyword);
        List<Exam> exams = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("keyword", keyword);

        model.addAttribute("exams", exams);
        model.addAttribute("class", new Classes());

        return "exam/exams";
    }

}
