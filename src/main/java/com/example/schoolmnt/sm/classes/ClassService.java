package com.example.schoolmnt.sm.classes;

import com.example.schoolmnt.sm.student.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassService {
    private ClassRepository classRepository;

    public ClassService(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    public List<Classes> getAllClasses() {
        return classRepository.findAll();
    }

    public Classes saveClass(Classes classes) {

        return classRepository.save(classes);
    }

    public Classes getClassById(long id) {

        return classRepository.findById(id).get();
    }

    public void updateClass(Classes classes) {
        classRepository.save(classes);
    }

    public void deleteClass(Long id) {
        classRepository.deleteById(id);
    }

    public Page<Classes> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection, String keyword) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo -1, pageSize, sort);
        if (keyword != null && !keyword.isEmpty()) {
            return classRepository.search(keyword, pageable);
        }
        return classRepository.findAll(pageable);
    }

}
