package com.example.springbootgithubactiondemo.controller;

import com.example.springbootgithubactiondemo.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.ai.bedrock.anthropic.BedrockAnthropicChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.example.springbootgithubactiondemo.Utils.*;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

@RestController
@RequestMapping("/api/v1/")
public class HomeController {

    @Autowired
    private final MongoClient mongoClient;

    @Autowired
    public HomeController(MongoClient mongoClient, BedrockAnthropicChatModel chatModel, SubmissionRepository submissionRepository) {
        this.mongoClient = mongoClient;
        this.chatModel = chatModel;
        this.submissionsRepository = submissionRepository;
    }

    @Autowired
    private DatabaseConfig databaseProperties;

    @Qualifier("webApplicationContext")
    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private SubmissionRepository submissionsRepository;

    private final BedrockAnthropicChatModel chatModel;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm a");


    @GetMapping("home")
    public String index(){
        return "OneBased Backend Service home endpoint running as expected.";
    }

    @Operation(
            summary = "Submit data",
            description = "Tests submitting to the database from MongoDB"
    )
    @PostMapping("/submit")
    public String submitData(@RequestBody String data) {
        System.out.println("=> Connection successful: " + preFlightChecks(mongoClient));
        System.out.println("=> Print list of databases:");
        List<Document> databases = mongoClient.listDatabases().into(new ArrayList<>());
        databases.forEach(db -> System.out.println(db.toJson()));
        return "Received data: " + data;
    }

    @Operation(
            summary = "Get User Submissions",
            description = "Retrieves User Submissions"
    )
    @GetMapping("/submissions")
    public ResponseEntity<List<Document>> getUserSubmissions(@RequestParam String userId) {
        MongoDatabase submissionsDB = mongoClient.getDatabase("sophie");
        MongoCollection<Document> collection = submissionsDB.getCollection("submissions");

        List<Document> results = collection.find(eq("userId", userId )).into(new ArrayList<>());
        return ResponseEntity.ok(results);
    }

    @Operation(
            summary = "Login to ASMEL",
            description = "Logs into the ASMEL Database"
    )
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginParams loginParams) {
        MongoDatabase userDB = mongoClient.getDatabase("asmel");
        MongoCollection<Document> collection = userDB.getCollection("users");

        Document user = collection.find(and(eq("facility", loginParams.getFacility()), eq("cdcnumber", loginParams.getCdcNumber()))).first();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            UserDTO userResponse = new UserDTO(user.get("_id").toString(), user.getString("name"));
            return ResponseEntity.ok(userResponse);
        }
    }


    public class UserDTO {
        private String id;
        private String name;

        public UserDTO(String id, String name) {
            this.id = id;
            this.name = name;
        }

        // Getters and setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Operation(
            summary = "Submit Questions",
            description = "Submits Questions to ASMEL AI"
    )
    @PostMapping("/questions")
    public ResponseEntity<AIDTO> submit(@RequestBody QuestionParams questionParams) throws IOException {
        MongoDatabase asmelDB = mongoClient.getDatabase("asmel");
        MongoCollection<Document> collection = asmelDB.getCollection("submissions");

        String questionAnswerStub = """
                <li><strong>{{question}}</strong></li>
                  <p><span style="color: #0000ff;"><strong>{{answer}}</strong></span></p>
                """;

        StringBuilder sb = new StringBuilder();

        PDFBuilder pdfBuilder = new PDFBuilder(resourceLoader);

        UUID documentID = UUID.randomUUID();

        LocalDateTime currentDateTime = LocalDateTime.now();

        if (questionParams.name == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            List<String> responses = new ArrayList<String>();
            String responseAI = "";
            for (int i = 0; i < questionParams.getQuestions().length; i++) {
                responseAI = (chatModel.call("Answer this question about " + questionParams.getBookName() + ", " + questionParams.getQuestions()[i]));
                sb.append(questionAnswerStub.replace("{{question}}", questionParams.getQuestions()[i]).replace("{{answer}}", responseAI));
            }

            ByteArrayOutputStream outputStream = pdfBuilder.generatePdf( Map.of(
                    "name", questionParams.getName(),
                    "answers", sb.toString(),
                    "bookName", questionParams.getBookName(),
                    "timestamp", currentDateTime.format(formatter),
                    "submissionID", documentID.toString()
            ));

            String uploadedURL = s3Service.uploadPDF(outputStream, documentID.toString() + ".pdf");

            submissionsRepository.save(new Submission(documentID, currentDateTime, questionParams.getUserId(), uploadedURL, questionParams.getBookName()));

            return ResponseEntity.ok(new AIDTO(documentID.toString(), responses, questionParams.name, uploadedURL));
        }
    }

    public class AIDTO {
        private String id;
        private List<String> responses;
        private String name;
        private String url;

        public AIDTO(String id, List<String> responses, String name, String url) {
            this.id = id;
            this.responses = responses;
            this.name = name;
            this.url = url;
        }

        // Getters and setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<String> getResponses() { return responses;}

        public void setResponses(List<String> responses) {
            this.responses = responses;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }

}
