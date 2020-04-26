package com.academy.data.controller;

import com.academy.data.config.GenerateFileName;
import com.academy.data.domains.*;
import com.academy.data.repository.AssignmentRepository;
import com.academy.data.repository.EmployeeRepository;
import com.academy.data.repository.UserRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleHtmlReportConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.jasperreports.JasperReportsUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.*;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.ModelAndView;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class EmployeeController {

    //https://www.stackextend.com/java/generate-pdf-document-using-jasperreports-and-spring-boot/

    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);
    @Value("${upload.path}")
    private String path;

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    FileService fileservice;
    //http://localhost:8000/employee/id/1

    @GetMapping("/employee/id/{id}")
    public Employee retrieveExchangeValue
    (@PathVariable long id){
        Employee employee = repository.findById(id);
        return employee;
    }

    @GetMapping("/employee")
    public List<Employee> getAll(){
        String x=path;
       return repository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST, value="/login/employee")
    @ResponseBody
    Employee registerStudent(@RequestBody Employee employee) {
       return repository.findByUserNameAndPassword(employee.getUserName(),employee.getPassword());
    }

    @RequestMapping(method = RequestMethod.POST, value="/login/user")
    @ResponseBody
    User loginUser(@RequestBody User user) {
        return userRepository.findByEmailAndPassword(user.getUsername(),user.getPassword());
    }

    @RequestMapping(method = RequestMethod.POST, value="/user/changePassword")
    @ResponseBody
    User changePasswordUser(@RequestBody User user) {
        User oldUser= userRepository.findById(user.getUserid()).orElse(null);
        if(oldUser==null)
        {
            return null;
        }
        else
        {
            oldUser.setPassword(user.getPassword());
            userRepository.save(oldUser);
            return oldUser;
        }
    }

    // TODO: 11/28/18 change directory name
   // @CrossOrigin(origins = "http://localhost:4200") // Call  from Local Angualar
    @PostMapping("/profile/uploadpicture")
    @ResponseBody
    public ResponseEntity<APIResult> handleFileUpload(@RequestParam("year") String year, @RequestParam("grade") String grade,
                                                      @RequestParam("section") String section,@RequestParam("subject") String subject,
                                                      @RequestParam("filetype") String filetype,@RequestParam("teacherid") int teacherid,
                                                      @RequestParam("description") String description,
                                                      @RequestParam("file") MultipartFile file) {
        APIResult apiResult=new APIResult();
        String message = "";
        try {
            String filePath= fileservice.store(file);
            if(!filePath.equals("-1"))
            {
                Assignment assignment=new Assignment();
                assignment.setAcademicyear(year);
                assignment.setGrade(grade);
                assignment.setSection(section);
                assignment.setSubject(subject);
                assignment.setFiletype(filetype);
                assignment.setDescription(description);
                assignment.setTeacherid(teacherid);

                assignment.setFilename( GenerateFileName.getNewFileName());//file.getOriginalFilename());
                assignment.setFilepath(filePath);
                assignmentRepository.save(assignment);

                message = "You successfully uploaded " + file.getOriginalFilename() + "!";
                apiResult.setMessage(message);
                return ResponseEntity.status(HttpStatus.OK).body(apiResult);
            }
            else
            {
                message = "Fail to upload Profile Picture" + file.getOriginalFilename() + "!";
                apiResult.setMessage(message);
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(apiResult);
            }
        } catch (Exception e) {
            message = "Fail to upload Profile Picture" + file.getOriginalFilename() + "!";
            apiResult.setMessage(message);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(apiResult);
        }
    }

    // TODO: 11/28/18 if admin show all files
    @GetMapping("/getTeacherAssignment")
    public List<Assignment> getTeacherAssignment(@RequestParam("teacherid") int teacherid){
        if(teacherid>0)
        return assignmentRepository.findByTeacherid(teacherid);
        else
            return assignmentRepository.findAll();
    }

    @GetMapping("/getGradeAssignment")
    public List<Assignment> getGradeAssignment(@RequestParam("grade") String grade){
            return assignmentRepository.findByGrade(grade);
    }


    // TODO: 11/28/18 pass fileid instead of filename
    // TODO: 11/28/18 add progress bar when downloading
    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = fileservice.loadFile(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @RequestMapping(method = RequestMethod.DELETE,value = "/deleteAssignment/id/{id}")
    @ResponseBody
    public int deleteAssignment(@PathVariable long id)
    {
        try {
            assignmentRepository.deleteById(id);
            return 1;
        }catch (Exception ex)
        {
            return -1;
        }
    }

    @RequestMapping(value = "/foo", method = RequestMethod.OPTIONS)
    public ResponseEntity<String> options(HttpServletResponse response) {
        log.info("OPTIONS /foo called");
        response.setHeader("Allowsss", "HEAD,GET,PUT,OPTIONS");
        response.setStatus(300);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value="/employee/reports",method=RequestMethod.GET)
    public ModelAndView getReport()
    {
        List<Employee> lst = repository.findAll();

        JRDataSource ds = new JRBeanCollectionDataSource(lst,false);
        Map<String,Object> parameterMap = new HashMap<String,Object>();
        //parameterMap.put("objRequestOrder",requestOrder);
        // parameterMap.put("supplierName", supplierName);
        //parameterMap.put("media_number_assignment_id", 999);
        parameterMap.put("datasource", ds);
        parameterMap.put("format", "pdf"); //xlsx

        ModelAndView modelAndView = new ModelAndView("rpt_employee", parameterMap);
        return modelAndView;

        //JasperPrint jasperPrint = getObjectPdf("reports/test.jrxml", new HashMap<String, Object>());
    }


    @RequestMapping("/")
    public ModelAndView index() throws IOException {
        generateInvoiceFor();
        ModelAndView modelAndView = new ModelAndView("rpt_employee");
        return modelAndView;
        //return "index.html";
    }

    // Path to the jrxml template
    private final String invoice_template_path = "/static/jasper/rpt_employee.jrxml";

    public String generateInvoiceFor() throws IOException {

        // /Users/chadirahme/IdeaProjects/americanacademy/ProfilePictureStore

        String path="/Users/chadirahme/IdeaProjects/americanacademy/ProfilePictureStore";
        File pdfFile = File.createTempFile("my-invoice", ".pdf",new File(path));


        try(FileOutputStream pos = new FileOutputStream(pdfFile))
        {
            // Load the invoice jrxml template.
            final JasperReport report = loadTemplate();

            // Create parameters map.
            final Map<String, Object> parameters = parameters();

//            List<String> list = new ArrayList<>();
//            Collections.addAll(list,"Invoice","asasa","Invoice");
//            List<String> list1 = Collections.singletonList(list.toString());
//            System.out.println(list1);

            // Create an empty datasource.
            final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(Collections.singletonList("Invoice"));

            // Render the PDF file
            JasperReportsUtils.renderAsPdf(report, parameters, dataSource, pos);
            //JasperReportsUtils.renderAsHtml();
            //JasperExportManager.exportReportToPdfStream(report, pos);
            return pdfFile.getName();

        }
        catch (final Exception e)
        {
            log.error(String.format("An error occured during PDF creation: %s", e));
            return null;
        }
    }

    @RequestMapping(value = "helloReport1", method = RequestMethod.GET)
    @ResponseBody
    public void getRpt1(HttpServletResponse response) throws JRException, IOException {
        final InputStream reportInputStream = getClass().getResourceAsStream(invoice_template_path);

       // InputStream jasperStream = this.getClass().getResourceAsStream(invoice_template_path);
        Map<String,Object> params = new HashMap<>();
        JasperReport jasperReport =loadTemplate(); //(JasperReport) JRLoader.loadObject(reportInputStream);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());

        response.setContentType("application/x-pdf");
        response.setHeader("Content-disposition", "inline; filename=helloWorldReport.pdf");

        final OutputStream outStream = response.getOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);

//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "test.pdf" + "\"")
//                .body(fileservice.loadFile(filename));
    }

    @RequestMapping(value = "pdfpage", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Resource> openinpage()
    {
        try {
           String name= generateInvoiceFor();
            String path="/Users/chadirahme/IdeaProjects/americanacademy/ProfilePictureStore/";
           // File pdfFile = File.createTempFile("my-invoice", ".pdf",new File(path));

            name="GRADE7.pdf";
                    return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "test.pdf" + "\"")
                .body(fileservice.loadFile(path +name ));


        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.ok(null);
        }
    }
    private static final String APPLICATION_PDF = "application/pdf";
    @RequestMapping(value = "/b", method = RequestMethod.GET, produces = APPLICATION_PDF)
    public @ResponseBody ResponseEntity<byte[]> downloadB() throws IOException {
        String path="/Users/chadirahme/IdeaProjects/americanacademy/ProfilePictureStore/";
        String name=generateInvoiceFor(); //"GRADE7.pdf";

        File file = new File(path + name);
        byte[] document = FileCopyUtils.copyToByteArray(file);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "pdf"));
        header.set("Content-Disposition", "inline; filename=" + file.getName());
        header.setContentLength(document.length);
        return ResponseEntity.ok().contentType(new MediaType("application", "pdf"))
                .contentLength(document.length)
                .body(document);
      //  return new ResponseEntity<byte[]>(document, header);
    }

    private void generateReportHtml(JasperPrint jasperPrint, HttpServletRequest req, HttpServletResponse resp) throws IOException, JRException {
        HtmlExporter exporter=new HtmlExporter();
        List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
        jasperPrintList.add(jasperPrint);
        exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrintList));
        exporter.setExporterOutput( new SimpleHtmlExporterOutput(resp.getWriter()));
        SimpleHtmlReportConfiguration configuration =new SimpleHtmlReportConfiguration();
        exporter.setConfiguration(configuration);
        exporter.exportReport();

    }
    // Fill template order parametres
    private Map<String, Object> parameters() {
        final Map<String, Object> parameters = new HashMap<>();

      //  parameters.put("logo", getClass().getResourceAsStream(logo_path));
        parameters.put("studentName",  "shadi rahme");
       // parameters.put("REPORT_LOCALE", locale);

        return parameters;
    }

    // Load invoice jrxml template
    private JasperReport loadTemplate() throws JRException {

        //String path = getServletContext().getRealPath("/jrxml/employeesList.jrxml");
        log.info(String.format("Invoice template path : %s", invoice_template_path));

        final InputStream reportInputStream = getClass().getResourceAsStream(invoice_template_path);
        final JasperDesign jasperDesign = JRXmlLoader.load(reportInputStream);

        return JasperCompileManager.compileReport(jasperDesign);
    }
}
