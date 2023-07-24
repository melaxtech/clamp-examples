package edu.uth.clamp.support.api;

import com.google.gson.*;
import edu.uth.clamp.nlp.structure.Document;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.XMLSerializer;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DemoApi {
    private static String base_url = "http://localhost:8080";

    //First command line parameter is xmi or json for which api endpoint to call
    // Second parameter is optional URL for pipeline, defaults to http://localhost:8080 (if parameter doesn't begin with http it is assumed to be text to send to pipeline
    //If other command line parameters are present then they are all concatenated together and sent to the pipeline
    public static void main(String[] argv) throws IOException {
        boolean customBaseUrl = false;

        if (argv.length == 0 || (!argv[0].equalsIgnoreCase("xmi") && !argv[0].equalsIgnoreCase("json"))) {
            System.out.println("First parameter should be xmi or json for api endpoint format");
        }
        if (argv.length > 1 && argv[1].startsWith("http")) {
            base_url = argv[1];
            customBaseUrl = true;
        }
        // String data = "Taking ibuprofen for Monoplegia of left lower extremity affecting nondominant side";
        String data = """
                ASSESSMENT: # Chronic kidney disease stage 4 due to type 2 diabetes mellitus : FU with nephro, # Chronic kidney disease stage 4 : as above # Mechanical low back pain : medrol dose pak/ check xray of LS spine PLAN: Plan printed and provided to patient: FU 1 month PROVIDED: Patient Education (8/28/2019)
                """;
        if (argv.length > 2 || (argv.length == 2 && !argv[1].startsWith("http"))) {
            data = String.join(" ", argv).substring(argv[0].length() + 1);
            if (customBaseUrl) {
                data = data.substring(argv[1].length() + 1);
            }
        }
        HttpURLConnection connection;
        if (argv[0].equalsIgnoreCase("xmi")) {
            connection = createConnection(argv[0], data);
        } else {
            connection = createConnection(argv[0], data);
        }
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            reportError(connection, responseCode);
            return;
        }
        StringBuilder responseBody = new StringBuilder();
        String line;
        try (InputStreamReader read = new InputStreamReader(connection.getInputStream());
             BufferedReader reader = new BufferedReader(read)) {
            while ((line = reader.readLine()) != null) {
                responseBody.append(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading input stream from Normalize response\n" + e.toString());
            return;
        }
        if (argv[0].equalsIgnoreCase("xmi")) {
            try (PrintWriter pw = new PrintWriter("demoTest.xmi")) {
                pw.write(responseBody.toString());
            }
        } else {
            processJson(responseBody.toString());
        }
    }

    private static HttpURLConnection createConnection(String type, String data) throws IOException {
        String urlParameters = "query=" + data;
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        URL url;
        if (type.equalsIgnoreCase("xmi")) {
            url = new URL(base_url + "/pipeline/xmi");
        } else {
            url = new URL(base_url + "/pipeline/json");
        }
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
        conn.setUseCaches(false);
        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(postData);
        }
        return conn;
    }

    //Gather error status and print it
    private static void reportError(HttpURLConnection connection, int responseCode) {
        StringBuilder responseBody = new StringBuilder();
        String line;
        try (InputStreamReader read = new InputStreamReader(connection.getErrorStream());
             BufferedReader readerError = new BufferedReader(read)) {
            while ((line = readerError.readLine()) != null) {
                responseBody.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server returned status " + responseCode + " " + responseBody);
    }

    //Find entities in index
    private static List<String> findKeysWithSubstring(JsonObject jsonObject, String substring) {
        List<String> keyPaths = new ArrayList<>();
        findKeysWithSubstringHelper(jsonObject, "", substring, keyPaths);
        return keyPaths;
    }

    private static void findKeysWithSubstringHelper(JsonObject jsonObject, String currentPath, String substring, List<String> keyPaths) {
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            String newPath = currentPath.isEmpty() ? key : currentPath + "." + key;
            if (key.contains(substring)) {
                keyPaths.add(newPath);
            }
            if (value.isJsonObject()) {
                findKeysWithSubstringHelper(value.getAsJsonObject(), newPath, substring, keyPaths);
            }
        }
    }

    //Process string returned as JSon and print the Entities and Relations
    private static void processJson(String response) {
        JsonObject responseJson = JsonParser.parseString(response).getAsJsonObject();
        JsonObject note_json = responseJson.getAsJsonObject("indexes");
        String content = responseJson.getAsJsonPrimitive("content").getAsString();
        List<String> keyPaths = findKeysWithSubstring(note_json, "_Entity_");

        for (String keyPath : keyPaths) {//print each found item
            String[] keys = keyPath.split("\\.");
            JsonObject currentObject = note_json;
            for (int i = 0; i < keys.length - 1; i++) {
                currentObject = currentObject.getAsJsonObject(keys[i]);
            }

            JsonObject entityObject = currentObject.getAsJsonObject(keys[keys.length - 1]);
            JsonArray umlsConcepts = entityObject.getAsJsonArray("umlsConcepts");
            StringBuilder umlsOutput = new StringBuilder("\t");
            if (umlsConcepts != null) {
                for (JsonElement concept : umlsConcepts) {
                    String tui = concept.getAsJsonObject().getAsJsonPrimitive("tui").getAsString();
                    if (tui.length() > 0) {
                        umlsOutput.append("TUI: ").append(tui).append(" ");
                    }
                    String code = concept.getAsJsonObject().getAsJsonPrimitive("code").getAsString();
                    if (code.length() > 0) {
                        umlsOutput.append("code: ").append(code).append(" ");
                    }
                    String preferredText = concept.getAsJsonObject().getAsJsonPrimitive("preferredText").getAsString();
                    if (preferredText.length() > 0) {
                        umlsOutput.append("preferredText: ").append(preferredText).append(" ");
                    }
                }
            }
            String basicInfo = getBasicInfo(entityObject, content);
            if (entityObject.getAsJsonPrimitive("type").getAsString().equals("Entity")) {
                System.out.println("Entity: " + basicInfo);
            } else {
                JsonObject fromEnt, toEnt;
                fromEnt = entityObject.getAsJsonObject("fromEnt");
                toEnt = entityObject.getAsJsonObject("toEnt");
                System.out.println("Relation: " + basicInfo);
                System.out.println("\tFrom: " + getBasicInfo(fromEnt, content));
                System.out.println("\t  To: " + getBasicInfo(toEnt, content));
            }
            if (umlsOutput.length() > 1) {
                System.out.println(umlsOutput);
            }
            JsonObject attrs = entityObject.getAsJsonObject("attrs");//extra data
            if (attrs != null) {
                for (String key : attrs.keySet()) {
                    System.out.println("\t attribute: " + key + " mapped to " + attrs.getAsJsonPrimitive(key).getAsString());
                }
            }
        }
    }

    private static String getBasicInfo(JsonObject entityObject, String content) {
        int begin = entityObject.getAsJsonPrimitive("begin").getAsInt();
        int end = entityObject.getAsJsonPrimitive("end").getAsInt();
        String semantic = entityObject.getAsJsonPrimitive("semantic").getAsString();
        String coveredText = content.substring(begin, end);
        return begin + " - " + end + " of type " + semantic + " [[" + coveredText + "]]";
    }
}
