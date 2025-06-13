import React from "react";
import type { Competition } from "../lib/types";

interface CompetitionListProps {
  competitions: Competition[];
  selected: Competition | null;
  onSelect: (competition: Competition) => void;
}

/**
 * Renders a scrollable list of competitions.
 * Highlights the currently selected competition.
 */
const CompetitionList: React.FC<CompetitionListProps> = ({
  competitions,
  selected,
  onSelect,
}) => {
  if (competitions.length === 0) {
    return <p className="has-text-grey">No competitions available.</p>;
  }
  console.log("Competitions:", competitions);
  console.log("Selected Competition:", selected);
  return (
    <ul className="panel">
      {competitions.map((comp) => {
        const isSelected = selected?.competitionID === comp.competitionID
        return (
          <li
            key={comp.competitionID}
            className={
              "panel-block " + (isSelected ? "has-background-link-dark" : "")
            }
            style={{ color: "white", cursor: "pointer" }}
            onClick={() => onSelect(comp)}
          >
          <div className="is-flex is-justify-content-space-between is-align-items-center" style={{ width: "100%" }}>
            <span>{comp.type} - {comp.ageCategory}</span>
            <span className="has-text-grey-light">{comp.nrOfParticipants} participant{comp.nrOfParticipants !== 1 ? "s" : ""}</span>
          </div>
          </li>
        );
      })}
    </ul>
  );
};

export default CompetitionList;
