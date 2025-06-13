package com.Main;

import com.Domain.Competition;
import com.Domain.Participant;
import com.Network.NetworkClient;
import Network.UpdateMessage;
import com.Repository.Manual.CompetitionRepository;
import com.Service.CompetitionService;
import com.Validators.EntryValidator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.Map;

public class MainController {
    private final NetworkClient networkClient = new NetworkClient();
    private final ObjectMapper  mapper        = new ObjectMapper();
    CompetitionService competitionService;

    // UI controls
    @FXML private ListView<Competition> competitionsListView;
    @FXML private ListView<Participant> participantsListView;
    @FXML private ComboBox<String> typeComboBox, ageComboBox;
    @FXML private ComboBox<String> comp1ComboBox, comp2ComboBox;
    @FXML private TextField nameField, ageField;

    // Track chosen comps for enrollment
    private String pendingComp1, pendingComp2;

    public void initialize() {
        competitionService = new CompetitionService(new CompetitionRepository());

        initFilterComboBoxes();
        initCompComboBoxes();

        // On competition click, request its participants
        competitionsListView.setOnMouseClicked(this::onCompetitionSelected);

        // Start listening to server messages
        networkClient.start("localhost", 8080, this::dispatchMessage);

        // Initial load of competitions
        networkClient.send("GET_ALL_COMPETITIONS", "");
    }

    private void dispatchMessage(UpdateMessage msg) {
        Platform.runLater(() -> {
            try {
                switch (msg.getType()) {
                    case "GET_ALL_COMPETITIONS_REPLY", "FILTER_COMPETITIONS_REPLY" -> handleAllCompetitions(msg.getPayload());
                    case "GET_PARTICIPANTS_FOR_COMP_REPLY" -> handleParticipantsForComp(msg.getPayload());
                    case "ADD_PARTICIPANT_REPLY" -> handleAddParticipantReply(msg.getPayload());
                    case "ENROLL_PARTICIPANT_REPLY", "UPDATE" -> onEnrollOrUpdate();
                    default -> System.out.println("Unhandled message: " + msg.getType());
                }
            } catch (Exception e) {
                showAlert("Error processing server reply: " + e.getMessage());
            }
        });
    }

    private void refreshCompetitionList() {
        networkClient.send("GET_ALL_COMPETITIONS", "");
    }

    // --- Message handlers ---

    private void handleAllCompetitions(String payload) throws Exception {
        List<Competition> comps = mapper.readValue(payload, new TypeReference<>() {});
        competitionsListView.setItems(FXCollections.observableArrayList(comps));
    }

    private void handleParticipantsForComp(String payload) throws Exception {
        List<Participant> parts = mapper.readValue(payload, new TypeReference<>() {});
        participantsListView.setItems(FXCollections.observableArrayList(parts));
    }

    private void handleAddParticipantReply(String payload) throws Exception {
        Participant p = mapper.readValue(payload, Participant.class);
        if (!"Empty".equals(pendingComp1)) sendEnroll(p, pendingComp1);
        if (!"Empty".equals(pendingComp2)) sendEnroll(p, pendingComp2);
        pendingComp1 = pendingComp2 = "Empty";
        refreshCompetitionList();
    }

    private void onEnrollOrUpdate() {
        Competition sel = competitionsListView.getSelectionModel().getSelectedItem();
        if (sel != null) {
            networkClient.send("GET_PARTICIPANTS_FOR_COMP", String.valueOf(sel.getCompetitionID()));
        }
    }

    // --- UI event handlers ---

    @FXML
    private void onAddParticipantClicked() {
        try {
            String name = nameField.getText().trim();
            int age    = Integer.parseInt(ageField.getText().trim());
            String c1  = comp1ComboBox.getValue();
            String c2  = comp2ComboBox.getValue();

            if (!EntryValidator.validateInput(name, age, c1, c2)) return;

            pendingComp1 = c1;
            pendingComp2 = c2;

            String json = mapper.writeValueAsString(Map.of(
                    "name", name,
                    "age", age
            ));

            networkClient.send("ADD_PARTICIPANT", json);
            clearForm();
        } catch (Exception e) {
            showAlert(e.getMessage());
        }
    }

    private void onCompetitionSelected(MouseEvent e) {
        Competition sel = competitionsListView.getSelectionModel().getSelectedItem();
        if (sel != null) {
            networkClient.send("GET_PARTICIPANTS_FOR_COMP", String.valueOf(sel.getCompetitionID()));
        }
    }

    // --- Helper methods ---

    private void sendEnroll(Participant p, String compType) {
        Competition target = competitionService.competitionToEnrollIn(p.getAge(), compType);
        networkClient.send(
                "ENROLL_PARTICIPANT",
                p.getParticipantID() + "," + target.getCompetitionID()
        );
    }

    private void initFilterComboBoxes() {
        List<String> types = List.of("All competitions", "Drawing", "Treasure Hunt", "Poetry");
        typeComboBox.setItems(FXCollections.observableArrayList(types));
        typeComboBox.setValue("All competitions");
        typeComboBox.setOnAction(e -> fetchCompetitions());

        List<String> ages = List.of("All ages", "6-8 years old", "9-11 years old", "12-15 years old");
        ageComboBox.setItems(FXCollections.observableArrayList(ages));
        ageComboBox.setValue("All ages");
        ageComboBox.setOnAction(e -> fetchCompetitions());
    }

    private void fetchCompetitions() {
        String selectedType = typeComboBox.getValue();
        String selectedAge = ageComboBox.getValue();
        try {
            String json = mapper.writeValueAsString(Map.of(
                    "type", selectedType,
                    "age", selectedAge
            ));
            networkClient.send("FILTER_COMPETITIONS", json);
        } catch (Exception e) {
            showAlert("Failed to send filter request: " + e.getMessage());
        }
    }

    private void initCompComboBoxes() {
        List<String> comps = List.of("Drawing", "Treasure Hunt", "Poetry", "Empty");
        comp1ComboBox.setItems(FXCollections.observableArrayList(comps));
        comp1ComboBox.setValue("Empty");
        comp2ComboBox.setItems(FXCollections.observableArrayList(comps));
        comp2ComboBox.setValue("Empty");
    }

    private void clearForm() {
        nameField.clear(); ageField.clear();
        comp1ComboBox.setValue("Empty");
        comp2ComboBox.setValue("Empty");
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}
