package com.example.jasper.controller;

import com.example.jasper.dto.ReportRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JsonQLDataSource;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;

@RestController
@RequestMapping("/report")
public class ReportController {

    @PostMapping("/get")
    public ResponseEntity<InputStreamResource> getFile(@RequestBody ReportRequest request) throws Exception {
        ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String value = writer.writeValueAsString(request);
        JsonQLDataSource data = new JsonQLDataSource(new ByteArrayInputStream(value.getBytes()));
        File file = new File("test.pdf");
        file.createNewFile();

        try {
            File reportFile = new ClassPathResource("report/test.jrxml").getFile();
            JasperReport report = JasperCompileManager.compileReport(JRXmlLoader.load(reportFile));
            JasperPrint print = JasperFillManager.fillReport(report, new HashMap<>(), data);
            JasperExportManager.exportReportToPdfFile(print, file.getName());
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }

        InputStreamResource resource = new InputStreamResource(Files.newInputStream(file.toPath()));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
