package edu.uth.clamp.support.api;

import com.google.gson.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DemoApi {
    private static final String BASE_URL = "https://ar69m6s9y4.execute-api.us-east-2.amazonaws.com/dev/pipelineproxy/codaclinicalpipeline";

    public static void main(String[] argv) throws IOException {
        String data = "Monoplegia of left lower extremity affecting nondominant side";

        HttpURLConnection connection = createConnection();

        JsonObject messageJson = new JsonObject();
        messageJson.addProperty("content", data);
        byte[] requestBody = messageJson.toString().getBytes(); // form.getBytes(StandardCharsets.UTF_8);
        connection.setRequestProperty("Content-Length", String.valueOf(requestBody.length));
        connection.getOutputStream().write(requestBody);

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
        JsonObject result = JsonParser.parseString(responseBody.toString()).getAsJsonObject();
        processJson(result.getAsJsonPrimitive("output").getAsString());
    }

    private static HttpURLConnection createConnection() throws IOException {
        URL url = new URL(BASE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        return connection;
    }

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

            if ((key.contains("_Entity_") || key.equalsIgnoreCase("fromEnt") || key.equalsIgnoreCase("toEnt"))/* && !key.contains("_Relation_")*/) {
                keyPaths.add(newPath);
            }

            if (value.isJsonObject()) {
                findKeysWithSubstringHelper(value.getAsJsonObject(), newPath, substring, keyPaths);
            }
        }
    }

    private static void processJson(String jsonInput) {
        Gson gson = new Gson();
        JsonObject note_json = gson.fromJson(jsonInput, JsonObject.class);

        List<String> keyPaths = findKeysWithSubstring(note_json, "_Entity_");

        for (String keyPath : keyPaths) {
            String[] keys = keyPath.split("\\.");
            JsonObject currentObject = note_json;
            for (int i = 0; i < keys.length - 1; i++) {
                currentObject = currentObject.getAsJsonObject(keys[i]);
            }

            JsonObject entityObject = currentObject.getAsJsonObject(keys[keys.length - 1]);

//            // Remove the "UmlsConcepts" key-value pair
//            entityObject.remove("umlsConcepts");
            JsonObject attrs = entityObject.getAsJsonObject("attrs");
            String basicInfo = getBasicInfo(entityObject);
            if (entityObject.getAsJsonPrimitive("type").getAsString().equals("Entity")) {
                System.out.println("Entity: " + basicInfo);
            } else {
                JsonObject fromEnt, toEnt;
                fromEnt = entityObject.getAsJsonObject("fromEnt");
                toEnt = entityObject.getAsJsonObject("toEnt");
                System.out.println("Relation: " + basicInfo);
                System.out.println("\tFrom: " + getBasicInfo(fromEnt));
                System.out.println("\t  To: " + getBasicInfo(toEnt));
            }
            if (attrs != null) {
                for (String key : attrs.keySet()) {
                    System.out.println("\t attribute: " + key + " mapped to " + attrs.getAsJsonPrimitive(key).getAsString());
                }
            }
        }
    }

    private static String getBasicInfo(JsonObject entityObject) {
        String begin, end, semantic;
        begin = entityObject.getAsJsonPrimitive("begin").getAsString();
        end = entityObject.getAsJsonPrimitive("end").getAsString();
        semantic = entityObject.getAsJsonPrimitive("semantic").getAsString();
        return begin + " - " + end + " of type " + semantic;
    }


}
