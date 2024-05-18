package ch.zhaw.deeplearningjava.footwear;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.nio.file.Paths;


@RestController
public class ClassificationController {

    private Inference inference = new Inference();
    Map<Integer, String> idToLabelMap = new HashMap<>();

    public ClassificationController() {
        // CSV-Daten laden und in Map speichern
        try (Reader reader = new FileReader("./flower-labels/labels.csv");
                CSVParser csvParser = new CSVParser(reader,
                        CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            for (CSVRecord record : csvParser) {
                idToLabelMap.put(Integer.valueOf(record.get(0)), record.get(1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/ping")
    public String ping() {
        return "Classification app is up and running!";
    }

    @PostMapping(path = "/analyze")
    public String predict(@RequestParam("image") MultipartFile image) throws Exception {
        String jsonResponse = inference.predict(image.getBytes()).toJson();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonResponse);

        ArrayNode resultsArray = mapper.createArrayNode();

        String basePath = System.getProperty("user.dir");

        System.out.println(basePath);

        if (rootNode.isArray()) {
            for (JsonNode node : rootNode) {

                ObjectNode resultNode = mapper.createObjectNode();

                Integer classId = node.get("className").asInt();
                
                Double probability = node.get("probability").asDouble();
                probability = round(probability * 100, 2);
                String probabilityText = probability.toString() + "%";

                String label = idToLabelMap.get(classId);

                String imagePath = "/flower-images/" + classId + "/image.jpg";
                

                resultNode.put("className", classId.toString());
                resultNode.put("label", label);
                resultNode.put("probability", probabilityText);
                resultNode.put("imagePath", imagePath);
                
                resultsArray.add(resultNode);

            }
        }
        return mapper.writeValueAsString(resultsArray);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
    
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}