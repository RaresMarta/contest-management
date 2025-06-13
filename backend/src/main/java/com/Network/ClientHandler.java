package com.Network;

import Network.UpdateMessage;
import com.Domain.Competition;
import com.Domain.Participant;
import com.Domain.User;
import com.Service.CompetitionService;
import com.Service.ParticipantService;
import com.Service.UserService;
import com.Validators.EntryValidator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

@AllArgsConstructor
public class ClientHandler implements Runnable {
    private static final Logger        logger = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket               socket;
    private final ParticipantService   participantService;
    private final CompetitionService   competitionService;
    private final UserService          userService;
    private final ObjectMapper         mapper;
    private final Set<OutputStream>    clientStreams;

    @Override
    public void run() {
        OutputStream out = null;
        try (InputStream in = socket.getInputStream()) {
            out = socket.getOutputStream();
            clientStreams.add(out);
            logger.info("Client connected: {}", socket.getInetAddress());

            UpdateMessage msg;
            while ((msg = UpdateMessage.parseDelimitedFrom(in)) != null) {
                try {
                    switch (msg.getType()) {
                        case "ADD_PARTICIPANT" -> handleAddParticipant(out, msg);
                        case "GET_PARTICIPANTS_FOR_COMP" -> handleGetParticipantsForComp(out, msg);
                        case "GET_ALL_COMPETITIONS" -> handleGetAllCompetitions(out);
                        case "GET_ALL_PARTICIPANTS" -> handleGetAllParticipants(out);
                        case "GET_ALL_USERS" -> handleGetAllUsers(out);
                        case "AUTH_USER" -> handleAuthUser(out, msg);
                        case "ENROLL_PARTICIPANT" -> handleEnrollParticipant(out, msg);
                        case "FILTER_COMPETITIONS" -> handleFilterCompetitions(out, msg);
                        default -> logger.warn("Unknown message type: {}", msg.getType());
                    }
                } catch (IOException e) {
                    logger.error("Error processing message: {}", e.getMessage());
                }
            }
        } catch (InvalidProtocolBufferException ipbe) {
            logger.error("Malformed protobuf message", ipbe);
        } catch (IOException ioe) {
            logger.info("Client disconnected: {}", ioe.getMessage());
        } finally {
            if (out != null) {
                try { out.close(); } catch (IOException ignored) {}
                clientStreams.remove(out);
            }
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    private void handleFilterCompetitions(OutputStream out, UpdateMessage msg) throws IOException {
        // Parse JSON filters from the message payload
        Map<String,String> filters = mapper.readValue(msg.getPayload(), new TypeReference<>() {});

        // Extract type and age category
        String type = filters.getOrDefault("type", "All competitions");
        String age  = filters.getOrDefault("age",  "All ages");

        List<Competition> filtered;
        if ("All competitions".equals(type) && "All ages".equals(age) ) {
            filtered = competitionService.getAll();
        }
        else if ( "All competitions".equals(type) ) {
            filtered = competitionService.getByAgeCategory(age);
        }
        else if ( "All ages".equals(age) ) {
            filtered = competitionService.getByType(type);
        }
        else {
            filtered = competitionService.getByTypeAndAgeCat(type, age);
        }

        String json = mapper.writeValueAsString(filtered);
        reply(out, "FILTER_COMPETITIONS_REPLY", json);
    }

    private void broadcastUpdate() {
        UpdateMessage update = UpdateMessage.newBuilder()
                .setType("UPDATE")
                .setPayload("")
                .build();
        clientStreams.forEach(os -> {
            try {
                synchronized (os) {
                    update.writeDelimitedTo(os);
                    os.flush();
                }
            } catch (IOException e) {
                logger.error("Failed to broadcastUpdate to client", e);
                clientStreams.remove(os);
            }
        });
    }

    private void reply(OutputStream to, String type, String payload) throws IOException {
        UpdateMessage reply = UpdateMessage.newBuilder()
                .setType(type)
                .setPayload(payload)
                .build();
        synchronized (to) {
        reply.writeDelimitedTo(to);
        to.flush();
        }
    }

    private void handleAddParticipant(OutputStream out, UpdateMessage msg) throws IOException {
        logger.info("Received ADD_PARTICIPANT message");
        Map<String,Object> map = mapper.readValue(msg.getPayload(), Map.class);

        String name = (String) map.get("name");
        int age  = (Integer) map.get("age");
        if (EntryValidator.validateNameAndAge(name, age)) {
            logger.info("Valid participant data: name={}, age={}", name, age);
        } else {
            logger.error("Invalid participant data: name={}, age={}", name, age);
            reply(out, "ERROR", "Invalid participant data");
            return;
        }
        Participant temp = new Participant(null, name, age);
        Participant p = participantService.add(temp);
        String json   = mapper.writeValueAsString(p);

        reply(out, "ADD_PARTICIPANT_REPLY", json);
        broadcastUpdate();
    }

    private void handleGetParticipantsForComp(OutputStream out, UpdateMessage msg) throws IOException {
        int compId = Integer.parseInt(msg.getPayload());
        List<Participant> list = participantService.getParticipantsForCompetition(compId);
        String json = mapper.writeValueAsString(list);
        reply(out, "GET_PARTICIPANTS_FOR_COMP_REPLY", json);
    }

    private void handleGetAllCompetitions(OutputStream out) throws IOException {
        List<Competition> all = competitionService.getAll();
        String json = mapper.writeValueAsString(all);
        reply(out, "GET_ALL_COMPETITIONS_REPLY", json);
    }

    private void handleGetAllParticipants(OutputStream out) throws IOException {
        List<Participant> all = participantService.getAll();
        String json = mapper.writeValueAsString(all);
        reply(out, "GET_ALL_PARTICIPANTS_REPLY", json);
    }

    private void handleGetAllUsers(OutputStream out) throws IOException {
        List<User> all = userService.getAll();
        String json = mapper.writeValueAsString(all);
        reply(out, "GET_ALL_USERS_REPLY", json);
    }

    private void handleAuthUser(OutputStream out, UpdateMessage msg) throws IOException {
        var creds = mapper.readValue(msg.getPayload(), new TypeReference<Map<String, String>>() {});
        String username = creds.get("userName");
        String password = creds.get("password");
        Optional<User> opt = userService.authenticate(username, password);
        reply(out, "AUTH_USER_REPLY", opt.map(user -> {
            try {
                return mapper.writeValueAsString(user);
            } catch (IOException e) {
                logger.error("Failed to serialize authenticated user", e);
                return "null";
            }
        }).orElse("null"));
    }

    private void handleEnrollParticipant(OutputStream out, UpdateMessage msg) throws IOException {
        // Validate payload
        String[] parts = msg.getPayload().split(",");
        if (parts.length != 2) {
            logger.error("Bad ENROLL payload: {}", msg.getPayload());
            reply(out, "ERROR", "Payload must be: participantID,competitionID");
            return;
        }
        // Parse IDs and enroll
        int participantID = Integer.parseInt(parts[0]);
        int competitionID = Integer.parseInt(parts[1]);
        competitionService.enrollParticipant(participantID, competitionID);
        broadcastUpdate();
    }
}