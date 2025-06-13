package com.Network;

import com.Repository.Hibernate.CompetitionHibernateRepository;
import com.Repository.Hibernate.ParticipantHibernateRepository;
import com.Repository.Hibernate.UserHibernateRepository;
import com.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.Service.ParticipantService;
import com.Service.CompetitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private final ParticipantService   participantService;
    private final CompetitionService   competitionService;
    private final UserService          userService;
    private final ObjectMapper         mapper;
    private final Set<OutputStream>    clientStreams;
    private final int                  port;

    public Server(int port) {
        this.port = port;
        this.mapper             = new ObjectMapper();
        this.clientStreams      = ConcurrentHashMap.newKeySet();
        this.participantService = new ParticipantService(new ParticipantHibernateRepository());
        this.competitionService = new CompetitionService(new CompetitionHibernateRepository());
        this.userService        = new UserService       (new UserHibernateRepository());
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Server listening on port {}", port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Client connected: {}", clientSocket.getInetAddress());
                new Thread(new ClientHandler(
                        clientSocket,
                        participantService,
                        competitionService,
                        userService,
                        mapper,
                        clientStreams
                )).start();
            }
        } catch (IOException ex) {
            logger.error("Failed to start server on port {}: {}", port, ex.getMessage(), ex);
        }
    }

    public static void main(String[] args) {
        new Server(5000).start();
    }
}
