package com.academy.data.controller;

import com.academy.data.domains.Marks;
import com.academy.data.domains.Student;
import com.academy.data.dto.MarksReportDTO;
import com.academy.data.dto.StudentMarksDTO;
import com.academy.data.repository.MarksRepository;
import com.academy.data.repository.StudentRepository;
import com.academy.data.repository.UserRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.ui.jasperreports.JasperReportsUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class MarksController {

    //https://memorynotfound.com/spring-mvc-download-file-examples/

    private static final Logger log = LoggerFactory.getLogger(MarksController.class);
    // Path to the jrxml template
   // private final String invoice_template_path = "/static/jasper/rpt_employee.jrxml";
    private String invoice_template_path1 = "/static/jasper/rpt_arabic.jrxml";
    private String invoice_template_path = "/static/jasper/rtp_dbmarks.jrxml";

    private static final String APPLICATION_PDF = "application/pdf";
    private static final String path1="/opt/tomcat/academy-api/ROOT/WEB-INF/classes/ProfilePictureStore/";
    private static final String path="/Users/chadirahme/IdeaProjects/americanacademy/ProfilePictureStore/";
    private boolean withoutHeader=false;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MarksRepository marksRepository;
    @Autowired
    private StudentRepository studentRepository;

    @RequestMapping(value = "/studentMarksPDF", method = RequestMethod.POST, produces = APPLICATION_PDF)
    public @ResponseBody
    ResponseEntity<byte[]> studentMarksPDF(@RequestBody StudentMarksDTO studentMarksDTO) throws IOException {

        String name=generateStudentMark(studentMarksDTO);
        File file = new File(path + name);
        byte[] document = FileCopyUtils.copyToByteArray(file);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "pdf"));
        header.set("Content-Disposition", "inline; filename=" + file.getName());
        header.setContentLength(document.length);
        return ResponseEntity.ok().contentType(new MediaType("application", "pdf"))
                .contentLength(document.length)
                .body(document);
    }

    @RequestMapping(value = "/getReportWithoutHeader", method = RequestMethod.POST, produces = APPLICATION_PDF)
    public @ResponseBody
    ResponseEntity<byte[]> getReportWithoutHeader(@RequestBody StudentMarksDTO studentMarksDTO) throws IOException {

        withoutHeader=true;
        String name=generateStudentMark(studentMarksDTO);
        File file = new File(path + name);
        byte[] document = FileCopyUtils.copyToByteArray(file);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "pdf"));
        header.set("Content-Disposition", "inline; filename=" + file.getName());
        header.setContentLength(document.length);
        return ResponseEntity.ok().contentType(new MediaType("application", "pdf"))
                .contentLength(document.length)
                .body(document);
    }


    private String generateStudentMark(StudentMarksDTO studentMarksDTO) throws IOException {
        File pdfFile = File.createTempFile("marks", ".pdf",new File(path));

        try(FileOutputStream pos = new FileOutputStream(pdfFile))
        {
            // Load the invoice jrxml template.
            final JasperReport report = loadTemplate();

            // Create parameters map.
            final Map<String, Object> parameters = parameters(studentMarksDTO);

            // Create an empty datasource.
            final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(Collections.singletonList("marks"));
           // final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(fillMarks(studentMarksDTO));
            // Render the PDF file

            JasperReportsUtils.renderAsPdf(report, parameters, dataSource, pos);
            return pdfFile.getName();
        }
        catch (final Exception e)
        {
            log.error(String.format("An error occured during PDF creation: %s", e));
            return null;
        }
    }

    // Fill template order parametres
    private Map<String, Object> parameters(StudentMarksDTO studentMarksDTO) {
        final Map<String, Object> parameters = new HashMap<>();

       Student student= studentRepository.findById(studentMarksDTO.getStudentid()).orElse(null);
       if(student!=null)
       {
           List<Marks> lstMarks=marksRepository.findByStudentid(studentMarksDTO.getStudentid().intValue());
           parameters.put("studentName",  student.getStudentname());
           parameters.put("grade",  student.getGrade());
           parameters.put("section",  student.getSection());
           parameters.put("objStudent",student);
           JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(fillSubMarks(lstMarks));
           parameters.put("subMarks",dataSource);
           JRBeanCollectionDataSource dsMarks = new JRBeanCollectionDataSource(fillMarks(studentMarksDTO,lstMarks));
           parameters.put("dsMarks",dsMarks);
       }

        //  parameters.put("logo", getClass().getResourceAsStream(logo_path));
        // parameters.put("REPORT_LOCALE", locale);

        return parameters;
    }

    //https://www.javaquery.com/2015/11/how-to-fill-jasper-table-using.html

    // Load invoice jrxml template
    private JasperReport loadTemplate() throws JRException {
        //String path = getServletContext().getRealPath("/jrxml/employeesList.jrxml");
        log.info(String.format("Invoice template path : %s", invoice_template_path));
        if(withoutHeader)
            invoice_template_path="/static/jasper/rpt_marksnohead.jrxml";

        final InputStream reportInputStream = getClass().getResourceAsStream(invoice_template_path);
        final JasperDesign jasperDesign = JRXmlLoader.load(reportInputStream);
        return JasperCompileManager.compileReport(jasperDesign);
    }

    private  List<MarksReportDTO> fillMarks(StudentMarksDTO studentMarksDTO,List<Marks> lstMarks) {

        List<MarksReportDTO> lst = new ArrayList<MarksReportDTO>();
        Random r = new Random();
        int low = 40;
        int high = 100;
        int totalMarks=0;
       // int result = r.nextInt(high-low) + low;
        List<String> lstSubjects=new ArrayList<>();
        lstSubjects.add("English Language");
        lstSubjects.add("Algebra 1");
        lstSubjects.add("Geometry");
        lstSubjects.add("Science");
        //lstSubjects.add("Chemistry");
        //lstSubjects.add("Physics");
        lstSubjects.add("World Cultures & Geo");
        //lstSubjects.add("World History");
        lstSubjects.add("Qatar History");
        lstSubjects.add("Arabic Language");
        lstSubjects.add("ICT");
        //lstSubjects.add("Average 100%");
        for (String subject : lstSubjects)
        {
            MarksReportDTO item = new MarksReportDTO();
            item.setSubject(subject);
            item.setMaxMark(100);
            item.setPassMark(50);
            Marks marks =lstMarks.stream().filter(x -> subject.equals(x.getSubject()))
                .findAny()
                .orElse(null);
            if(marks!=null) {
               // item.setSemester1(r.nextInt(high - low) + low);
                //item.setSemester2(r.nextInt(high - low) + low);
                //item.setFinalGrade(r.nextInt(high - low) + low);
                item.setSemester1((int)marks.getFinalmark());
                item.setSemester2(0);
                item.setFinalGrade(0);
                item.setSubjectGrad(getMarksLetter(item.getSemester1()));
            }
            else
            {
                item.setSemester1(0);
                item.setSemester2(0);
                item.setFinalGrade(0);
                item.setSubjectGrad("NG");
            }
            totalMarks+=item.getSemester1();
            lst.add(item);
        }
        //add Avg grade
        MarksReportDTO item = new MarksReportDTO();
        item.setSubject("Average 100%");
        item.setMaxMark(100);
        item.setPassMark(50);
        item.setSemester1(totalMarks/7);
        item.setSemester2(0);
        item.setFinalGrade(0);
        item.setSubjectGrad(getMarksLetter(item.getSemester1()));
        lst.add(item);

        return lst;
    }
    private  List<MarksReportDTO> fillSubMarks(List<Marks> lstMarks) {
        Random r = new Random();
        int low = 40;
        int high = 100;
        List<String> lstSubjects=new ArrayList<>();
        lstSubjects.add("Islamic Religion");
        lstSubjects.add("PE");
        List<MarksReportDTO> lst = new ArrayList<MarksReportDTO>();
        for (String subject : lstSubjects)
        {
            MarksReportDTO item = new MarksReportDTO();
            item.setSubject(subject);
            item.setMaxMark(100);
            item.setPassMark(50);
            Marks marks =lstMarks.stream().filter(x -> subject.equals(x.getSubject()))
                    .findAny()
                    .orElse(null);
            if(marks!=null) {
                // item.setSemester1(r.nextInt(high - low) + low);
                //item.setSemester2(r.nextInt(high - low) + low);
                //item.setFinalGrade(r.nextInt(high - low) + low);
                item.setSemester1((int)marks.getFinalmark());
                item.setSemester2(0);
                item.setFinalGrade(0);
                item.setSubjectGrad(getMarksLetter(item.getSemester1()));
            }
            else
            {
                item.setSemester1(0);
                item.setSemester2(0);
                item.setFinalGrade(0);
                item.setSubjectGrad("NG");
            }

//            item.setSemester1(r.nextInt(high-low) + low);
//            item.setSemester2(r.nextInt(high-low) + low);
//            item.setFinalGrade(r.nextInt(high-low) + low);
//            item.setSubjectGrad("A");
            lst.add(item);
        }

        return lst;
    }

    @RequestMapping(value = "/studentMarksPDF1", method = RequestMethod.POST, produces = APPLICATION_PDF)
    public @ResponseBody
    ResponseEntity<byte[]> studentDBMarksPDF(@RequestBody StudentMarksDTO studentMarksDTO) throws IOException {

        String name=generateMarksData(studentMarksDTO);
        File file = new File(path + name);
        byte[] document = FileCopyUtils.copyToByteArray(file);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "pdf"));
        header.set("Content-Disposition", "inline; filename=" + file.getName());
        header.setContentLength(document.length);
        return ResponseEntity.ok().contentType(new MediaType("application", "pdf"))
                .contentLength(document.length)
                .body(document);
    }

    @Autowired
    private JdbcTemplate jtm;

    private String generateMarksData(StudentMarksDTO studentMarksDTO) throws IOException {
        //File pdfFile = File.createTempFile("marks", ".pdf",new File(path));
        try
        {
            // Load the invoice jrxml template. rpt_testsub.jasper
            final JasperReport report = loadTemplate();
            //String sql = "SELECT * FROM marks where studentid=" + studentMarksDTO.getStudentid();
            //List<Marks> cars = jtm.query(sql, new BeanPropertyRowMapper(Marks.class));

            final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(marksRepository.findByStudentid(studentMarksDTO.getStudentid().intValue()));//report()

            InputStream reportInputStream = getClass().getResourceAsStream("/static/jasper/rpt_testsub.jrxml");
            JasperDesign jasperDesign = JRXmlLoader.load(reportInputStream);
            JasperReport subReport = JasperCompileManager.compileReport(jasperDesign);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("subReport",subReport);

            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, dataSource);
            JasperExportManager.exportReportToPdfFile(jasperPrint, path+"marks.pdf");

            // Create parameters map.
            //final Map<String, Object> parameters = parameters(studentMarksDTO);
           // Map<String, Object> parameters = new HashMap<>();

            // Create an empty datasource.




           // parameters.put("marks",report());

           // final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(report());
            // Render the PDF file



            //JasperReportsUtils.renderAsPdf(jasperPrint, parameters, dataSource, pos);
            return "marks.pdf";//pdfFile.getName();
        }
        catch (final Exception e)
        {
            log.error(String.format("An error occured during PDF creation: %s", e));
            return null;
        }
    }

    public List<Map<String, Object>> report() {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (Marks marks : marksRepository.findAll()) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("id", marks.getMarkid());
            item.put("studentname", marks.getStudentname());
            item.put("subject", marks.getSubject());
            item.put("finalmark", marks.getFinalmark());
            result.add(item);
        }
        return result;
    }

    public static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x < upper;
    }
    public String getMarksLetter(int finalMarks) {
        if (isBetween(finalMarks, 97, 100)) {
            return "A+";
        }
        if (isBetween(finalMarks, 93, 97)) {
            return "A";
        }
        if (isBetween(finalMarks, 90, 93)) {
            return "A-";
        }
        if (isBetween(finalMarks, 87, 90)) {
            return "B+";
        }
        if (isBetween(finalMarks, 83, 87)) {
            return "B";
        }
        if (isBetween(finalMarks, 80, 83)) {
            return "B-";
        }
        if (isBetween(finalMarks, 77, 80)) {
            return "C+";
        }
        if (isBetween(finalMarks, 73, 77)) {
            return "C";
        }
        if (isBetween(finalMarks, 70, 73)) {
            return "C-";
        }
        if (isBetween(finalMarks, 67, 70)) {
            return "D+";
        }
        if (isBetween(finalMarks, 63, 67)) {
            return "D";
        }
        if (isBetween(finalMarks, 50, 63)) {
            return "D-";
        }
        if (isBetween(finalMarks, 0, 50)) {
            return "BA";
        }

        return "NG";
    }

    @RequestMapping(value = "/studentNewMarksPDF", method = RequestMethod.POST, produces = APPLICATION_PDF)
    public @ResponseBody
    ResponseEntity<byte[]> studentNewMarksPDF(@RequestBody StudentMarksDTO studentMarksDTO) throws IOException {

        String name=generateNewStudentMark(studentMarksDTO);
        File file = new File(path + name);
        byte[] document = FileCopyUtils.copyToByteArray(file);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "pdf"));
        header.set("Content-Disposition", "inline; filename=" + file.getName());
        header.setContentLength(document.length);
        return ResponseEntity.ok().contentType(new MediaType("application", "pdf"))
                .contentLength(document.length)
                .body(document);
    }
    private String generateNewStudentMark(StudentMarksDTO studentMarksDTO) throws IOException {
        File pdfFile = File.createTempFile("marks", ".pdf",new File(path));
        try(FileOutputStream pos = new FileOutputStream(pdfFile))
        {
            // Load the invoice jrxml template.
            final JasperReport report = loadNewTemplate();
            Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/academy","root","123456");
            JasperPrint jasperPrint = JasperFillManager.fillReport(report,null,con);

            JasperExportManager.exportReportToPdfFile(jasperPrint, path+"marks.pdf");

            // Create parameters map.
            final Map<String, Object> parameters = parametersNew(studentMarksDTO);

            // Create an empty datasource.
            final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(Collections.singletonList("marks"));
            // final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(fillMarks(studentMarksDTO));
            // Render the PDF file

            JasperReportsUtils.renderAsPdf(report, parameters, dataSource, pos);
            return pdfFile.getName();
        }
        catch (final Exception e)
        {
            log.error(String.format("An error occured during PDF creation: %s", e));
            return null;
        }
    }
    private JasperReport loadNewTemplate() throws JRException {
        invoice_template_path="/static/jasper/rpt_grade8.jrxml";
        final InputStream reportInputStream = getClass().getResourceAsStream(invoice_template_path);
        final JasperDesign jasperDesign = JRXmlLoader.load(reportInputStream);
        JRDesignQuery jrDesignQuery=new JRDesignQuery();
        jrDesignQuery.setText("select s.studentid, s.studentname,s.rollnumber,  s.dayabsent,\n" +
                "m.academicyear,m.semester,s.grade,s.section,m.subject,m.finalmark\n" +
                "from marks m\n" +
                "inner join student s on m.studentid=s.studentid\n" +
                "inner join gradesubject g on g.subjectname=m.subject\n"+
                "where s.grade='Grade8' and s.section='A' and g.subjectlevel=0 \n" +
                "order by s.studentname,g.subjectorder");
       // jasperDesign.setQuery(jrDesignQuery);
        return JasperCompileManager.compileReport(jasperDesign);
    }
    private Map<String, Object> parametersNew(StudentMarksDTO studentMarksDTO) {
        final Map<String, Object> parameters = new HashMap<>();

        Student student= studentRepository.findById(studentMarksDTO.getStudentid()).orElse(null);
        if(student!=null)
        {
            String sql = "SELECT * FROM marks where grade='Grade8' and section='ABB' ";//where studentid=" + studentMarksDTO.getStudentid();
            List<Marks> cars = jtm.query(sql, new BeanPropertyRowMapper(Marks.class));


           // List<Marks> lstMarks=marksRepository.findByStudentid(studentMarksDTO.getStudentid().intValue());
            //parameters.put("studentName",  student.getStudentname());
           // parameters.put("grade",  student.getGrade());
           // parameters.put("section",  student.getSection());
           // parameters.put("objStudent",student);
            //JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(fillSubMarks(lstMarks));
            //parameters.put("subMarks",dataSource);
            JRBeanCollectionDataSource dsMarks = new JRBeanCollectionDataSource(cars);
            parameters.put("datasource",dsMarks);
        }

        //  parameters.put("logo", getClass().getResourceAsStream(logo_path));
        // parameters.put("REPORT_LOCALE", locale);

        return parameters;
    }
}
