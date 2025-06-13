import React, { useEffect, useState } from "react";
import type { Competition, Participant, User } from "./lib/types";
import {
  fetchCompetitionsByTypeAndAge,
  fetchAllCompetitions,
  fetchCompetitionsByType,
  fetchCompetitionsByAge,
} from "./api/competitionApi";
import {
  fetchParticipantsForCompetition,
} from "./api/participantApi";
import FilterBar from "./components/FilterBar";
import CompetitionList from "./components/CompetitionList";
import ParticipantList from "./components/ParticipantList";
import AddParticipantForm from "./components/AddParticipantForm";
import Header from "./components/Header";
import LoginForm from "./components/LoginForm";

const App: React.FC = () => {
  const [filters, setFilters] = useState<{ type: string; age: string }>({ type: "ALL", age: "ALL" });
  const [competitions, setCompetitions] = useState<Competition[]>([]);
  const [selectedCompetition, setSelectedCompetition] = useState<Competition | null>(null);
  const [participants, setParticipants] = useState<Participant[]>([]);
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    const loadCompetitions = async () => {
      const { type, age } = filters;
      let fetchFn;

      try {
        if (type === "ALL" && age === "ALL") {
          fetchFn = fetchAllCompetitions;
        } else if (type !== "ALL" && age === "ALL") {
          fetchFn = () => fetchCompetitionsByType(type);
        } else if (type === "ALL" && age !== "ALL") {
          fetchFn = () => fetchCompetitionsByAge(age);
        } else {
          fetchFn = () => fetchCompetitionsByTypeAndAge(type, age);
        }

        const data = await fetchFn();
        setCompetitions(data);
      } catch (err) {
        console.error("Error fetching competitions:", err);
        setCompetitions([]);
      } finally {
        setSelectedCompetition(null);
        setParticipants([]);
      }
    };

    if (user) loadCompetitions();
  }, [filters, user]); // <- also depends on user to wait for login

  useEffect(() => {
    if (!selectedCompetition) return;
    fetchParticipantsForCompetition(selectedCompetition.competitionID)
      .then(setParticipants)
      .catch((err) => {
        console.error(`Error fetching participants for competitionId=${selectedCompetition.competitionID}:`, err);
        setParticipants([]);
      });
  }, [selectedCompetition]);

  const refreshUI = async () => {
    const data = await fetchAllCompetitions();
    setCompetitions(data);
    setSelectedCompetition(null);
    setParticipants([]);
  };

  return (
    <div className="container mt-5">
      {!user ? (
        <LoginForm onLoginSuccess={setUser} />
      ) : (
        <>
          <Header />
          <div className="box mb-5">
            <FilterBar filters={filters} setFilters={setFilters} />
          </div>
          <div className="columns">
            <div className="column is-half">
              <div className="box" style={{ height: "400px", overflowY: "auto" }}>
                <CompetitionList
                  competitions={competitions}
                  selected={selectedCompetition}
                  onSelect={setSelectedCompetition}
                />
              </div>
            </div>
            <div className="column">
              <div className="box" style={{ height: "400px", overflowY: "auto" }}>
                <ParticipantList participants={participants} />
              </div>
            </div>
          </div>
          <div className="box mt-5">
            <AddParticipantForm competitions={competitions} onAdded={refreshUI} />
          </div>
        </>
      )}
    </div>
  );
};

export default App;
