package com.academy.data.rest;


import com.academy.data.domains.Marks;
import com.academy.data.domains.Student;
import com.academy.data.dto.StudentMarksDTO;
import com.academy.data.repository.MarksRepository;
import com.academy.data.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class MarksRestController {

    private static final Logger logger = LoggerFactory.getLogger(AssignmentRestController.class);
    @Autowired
    private MarksRepository marksRepository;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/marks")
    List<Marks> all() {
        return marksRepository.findAll();
    }

    @GetMapping("/marks/getStudentGradeAndSection")
    List<Student> getStudentGradeAndSection(@RequestParam("grade") String grade,
                                            @RequestParam("section") String section) {
        return studentRepository.findByGradeAndSection(grade,section);
    }

    @PostMapping("/marks/getStudentMarks")
    List<StudentMarksDTO> getStudentMarks(@RequestBody StudentMarksDTO studentMarksDTO)
    {

        List<StudentMarksDTO> lstMarks=new ArrayList<StudentMarksDTO>();
        List<Marks>  lstOldMarks= marksRepository.findByAcademicyearAndSemesterAndGradeAndSectionAndTeacheridAndSubject(
                studentMarksDTO.getAcademicyear(),studentMarksDTO.getSemester(),studentMarksDTO.getGrade(),studentMarksDTO.getSection(),
                studentMarksDTO.getTeacherid(),studentMarksDTO.getSubject());

        List<Student> lstStudent=studentRepository.findByGradeAndSection(studentMarksDTO.getGrade(),studentMarksDTO.getSection());

        for(Student student:lstStudent){
            StudentMarksDTO obj=new StudentMarksDTO();
            obj.setStudentid(student.getStudentid());
            obj.setStudentname(student.getStudentname());

            Marks oldMark = lstOldMarks.stream()
                    .filter(line -> student.getStudentid() == (line.getStudentid()))
                    .findFirst().orElse(null);
            if(oldMark!=null)
            {
                obj.setCw(oldMark.getCw());
                obj.setHw(oldMark.getHw());
                obj.setProject(oldMark.getProject());
                obj.setQuiz1(oldMark.getQuiz1());
                obj.setQuiz2(oldMark.getQuiz2());
                obj.setQuiz3(oldMark.getQuiz3());
                obj.setAvgquiz(oldMark.getAvgquiz());
                obj.setTest1(oldMark.getTest1());
                obj.setFinalexam(oldMark.getFinalexam());
                obj.setFinalmark(oldMark.getFinalmark());

            }

            lstMarks.add(obj);
        }

        return lstMarks;
    }


    @RequestMapping(method = RequestMethod.POST, value="/marks/save")
    @ResponseBody
    int saveMarks(@RequestBody List<Marks> lstMarks) {
        if(lstMarks!=null && lstMarks.size()>0)
        {
            Marks tmp=lstMarks.get(0);
            List<Marks>  lstOldMarks= marksRepository.findByAcademicyearAndSemesterAndGradeAndSectionAndTeacheridAndSubject(
                    tmp.getAcademicyear(),tmp.getSemester(),tmp.getGrade(),tmp.getSection(),tmp.getTeacherid(),tmp.getSubject());

            if(lstOldMarks!=null) {
                for (Marks marks : lstMarks) {
                    Marks student = lstOldMarks.stream()
                            .filter(line -> marks.getStudentid() == (line.getStudentid()))
                            .findFirst().orElse(null);
                    if (student == null) {
                        marksRepository.save(marks);
                    }
                    else {
                        BeanUtils.copyProperties(marks, student,"markid","createdtime");//(newObject, oldObject)
                        marksRepository.save(student);
                    }
                }
            }

        }

        return 1;
    }

}
