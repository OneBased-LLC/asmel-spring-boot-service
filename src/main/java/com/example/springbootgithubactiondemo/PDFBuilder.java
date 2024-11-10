package com.example.springbootgithubactiondemo;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;


@Service
public class PDFBuilder {


    private ResourceLoader resourceLoader;

    public PDFBuilder(@Qualifier("webApplicationContext") ResourceLoader resourceLoader) { this.resourceLoader = resourceLoader; }


    public ByteArrayOutputStream generatePdf(Map<String, String> placeholders) throws IOException
    {
        Resource resource = resourceLoader.getResource("classpath:answersdoc.html");

        // Read the HTML template as a string
        InputStream inputStream = resource.getInputStream();
        String htmlContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        // Replace placeholders with actual values
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            htmlContent = htmlContent.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();

        // Convert the updated HTML content to PDF
        HtmlConverter.convertToPdf(htmlContent,  pdfOutputStream);

        return pdfOutputStream;
    }
}
