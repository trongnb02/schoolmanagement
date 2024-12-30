package com.example.schoolmnt.sm.exam;

import com.example.schoolmnt.sm.classes.Classes;
import com.example.schoolmnt.sm.teacher.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamService {

    private ExamRepository examRepository;

    public ExamService(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    public List<Exam> getAllExams() {

        return examRepository.findAll();
    }

    public Exam saveExam(Exam exam) {

        return examRepository.save(exam);
    }

    public Exam getExamById(long id) {

        return examRepository.findById(id).get();
    }

    public Exam updateExam(Exam exam) {
        return examRepository.save(exam);
    }

    public void deleteExam(Long id) {

        examRepository.deleteById(id);
    }

    public Page<Exam> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection, String keyword) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo -1, pageSize, sort);
        if (keyword != null && !keyword.isEmpty()) {
            return examRepository.search(keyword, pageable);
        }
        return examRepository.findAll(pageable);
    }

}
