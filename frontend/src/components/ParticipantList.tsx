import React from "react";
import type { Participant } from "../lib/types";

interface ParticipantListProps {
  participants: Participant[];
}

/**
 * Renders a scrollable list of participants.  
 * If no participants exist, shows a placeholder message.
 */
const ParticipantList: React.FC<ParticipantListProps> = ({ participants }) => {
  if (participants.length === 0) {
    return <p className="has-text-grey">No participants for this competition.</p>;
  }

  return (
    <ul className="panel">
      {participants.map((p) => (
        <li key={p.participantID} className="panel-block">
          <div className="is-flex is-justify-content-space-between is-align-items-center" style={{ width: "100%" }}>
            <span>{p.name}</span>
            <span className="has-text-grey-light">({p.age} yo)</span>
          </div>
        </li>
      ))}
    </ul>
  );
};

export default ParticipantList;
