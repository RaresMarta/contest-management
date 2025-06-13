import React, { useState} from "react";
import type { Competition, CreateParticipantDTO } from "../lib/types";
import { createParticipant } from "../api/participantApi";
import { enrollParticipant } from "../api/competitionApi";

interface AddParticipantFormProps {
  competitions: Competition[];
  onAdded: () => void;
}

const competitionTypes = new Set<string>("Drawing,Poetry,Treasure Hunt".split(","));

const AddParticipantForm: React.FC<AddParticipantFormProps> = ({
  onAdded
}) => {
  const [name, setName] = useState("");
  const [age, setAge] = useState<number | "">("");
  const [comp1, setComp1] = useState<string>("");
  const [comp2, setComp2] = useState<string>("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!name.trim()) {
      setError("Name is required.");
      return;
    }
    if (age === "" || age < 0) {
      setError("Valid age is required.");
      return;
    }

    const selCompTypes: string[] = [];
    if (comp1 !== "") selCompTypes.push(String(comp1));
    if (comp2 !== "" && comp2 !== comp1) selCompTypes.push(String(comp2));

    setIsSubmitting(true);
    try {
      const newParticipant: CreateParticipantDTO = {
        name: name.trim(),
        age: Number(age),
      };

      const created = await createParticipant(newParticipant);

      if (selCompTypes.length > 0) {
        await enrollParticipant(created, selCompTypes);
      }

      onAdded();
      setName("");
      setAge("");
      setComp1("");
      setComp2("");
    } catch (err) {
      console.error("Error adding participant:", err);
      setError("Failed to add participant. Please try again.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      {error && (
        <div className="notification is-danger is-light">
          <button className="delete" onClick={() => setError(null)} type="button"></button>
          {error}
        </div>
      )}

      <div className="field is-grouped is-grouped-multiline">
        {/* Name */}
        <div className="control">
          <input
            className="input"
            type="text"
            placeholder="Name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            disabled={isSubmitting}
            required
          />
        </div>

        {/* Age */}
        <div className="control">
          <input
            className="input"
            type="number"
            placeholder="Age"
            min={0}
            value={age === "" ? "" : age}
            onChange={(e) => {
              const val = e.target.value;
              setAge(val === "" ? "" : Number(val));
            }}
            disabled={isSubmitting}
            required
          />
        </div>

        {/* Comp 1 */}
        <div className="control">
          <div className="select">
            <select
              value={comp1}
              onChange={(e) => setComp1(e.target.value === "" ? "" : String(e.target.value))}
              disabled={isSubmitting}
            >
              <option value="">Comp 1</option>
              {
                Array.from(competitionTypes).map((type) => (
                  <option value={type} key={type}>
                    {type}
                  </option>
                ))
              }
            </select>
          </div>
        </div>

        {/* Comp 2 */}
        <div className="control">
          <div className="select">
            <select
              value={comp2}
              onChange={(e) => setComp2(e.target.value === "" ? "" : String(e.target.value))}
              disabled={isSubmitting}
            >
              <option value="">Comp 2</option>
              {
                Array.from(competitionTypes).map((type) => (
                  <option value={type} key={type}>
                    {type}
                  </option>
                ))
              }
            </select>
          </div>
        </div>

        <div className="control">
          <button
            className={`button is-primary ${isSubmitting ? "is-loading" : ""}`}
            type="submit"
            disabled={isSubmitting}
          >
            Add Participant
          </button>
        </div>
      </div>
    </form>
  );
};

export default AddParticipantForm;
