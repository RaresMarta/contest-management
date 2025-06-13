import com.Domain.Competition;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class CompetitionTest {
    private static final String BASE_URL = "http://localhost:8080/api/competitions";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        // 1. POST - Create new competition
        Competition newComp = new Competition("Drawing", "9-11 years old", 0);
        Competition created = sendPost(newComp);

        // 2. GET ALL
        sendGetAll();

        // 3. GET BY ID
        sendGetById(created.getCompetitionID());

        // 4. PUT - Update competition
        Competition updated = new Competition("Poetry", "12-15 years old", 5);
        sendPut(created.getCompetitionID(), updated);

        // 5. DELETE
        sendDelete(created.getCompetitionID());
    }

    private static Competition sendPost(Competition comp) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(BASE_URL).openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");

        String json = mapper.writeValueAsString(comp);
        try (OutputStream os = con.getOutputStream()) {
            os.write(json.getBytes());
        }

        String response = new String(con.getInputStream().readAllBytes());
        System.out.println("POST response: " + response);

        con.disconnect();
        return mapper.readValue(response, Competition.class);
    }

    private static void sendGetAll() throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(BASE_URL).openConnection();
        con.setRequestMethod("GET");

        String response = new String(con.getInputStream().readAllBytes());
        System.out.println("GET ALL response: " + response);

        con.disconnect();
    }

    private static void sendGetById(int id) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(BASE_URL + "/" + id).openConnection();
        con.setRequestMethod("GET");

        String response = new String(con.getInputStream().readAllBytes());
        System.out.println("GET BY ID response: " + response);

        con.disconnect();
    }

    private static void sendPut(int id, Competition updated) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(BASE_URL + "/" + id).openConnection();
        con.setRequestMethod("PUT");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");

        String json = mapper.writeValueAsString(updated);
        try (OutputStream os = con.getOutputStream()) {
            os.write(json.getBytes());
        }

        String response = new String(con.getInputStream().readAllBytes());
        System.out.println("PUT response: " + response);

        con.disconnect();
    }

    private static void sendDelete(int id) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(BASE_URL + "/" + id).openConnection();
        con.setRequestMethod("DELETE");

        int status = con.getResponseCode();
        System.out.println("DELETE status: " + status);

        con.disconnect();
    }
}
