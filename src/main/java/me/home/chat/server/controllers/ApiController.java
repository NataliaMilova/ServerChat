package me.home.chat.server.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
//@RequestMapping(value = "/api/v1")
@RequestMapping(value = "/ws/socket")
public class ApiController {
    private static final Logger log = LoggerFactory.getLogger(ApiController.class);
    private static final String DOCS_FILE_PATH = "api_docs.html";
    private static final String DOCS_DIRECTORY = "classpath:docs";

    @GetMapping("/docs")
    public String getDocs(HttpSession session) {
        log.info("sid: {}: Request for documentation - {}", session.getId(), "GET: " + session.getServletContext().getContextPath() + "/api/v1/docs");
        try {
            Path filePath = ResourceUtils.getFile(DOCS_DIRECTORY + "/" + DOCS_FILE_PATH).toPath();
            List<String> lines = Files.readAllLines(filePath);
            StringBuilder sb = new StringBuilder();
            lines.forEach(sb::append);
            return sb.toString();
        } catch (IOException e) {
            log.error("sid: {}: Fail to read file with api documentation: {}.", session.getId(), e);
        }
        return "";
    }
}
