// API functions to interact with the Competitions endpoint

import type { Competition, EnrollDTO, Participant } from "../lib/types";

const BASE_URL = "/api/competitions";

/**
 * Fetch all competitions.
 */
export async function fetchAllCompetitions(): Promise<Competition[]> {
  const res = await fetch(`${BASE_URL}`);
  if (!res.ok) {
    throw new Error("Failed to fetch all competitions");
  }
  return res.json();
}

/**
 * Fetch a single competition by its ID.
 */
export async function fetchCompetitionById(id: number): Promise<Competition> {
  const res = await fetch(`${BASE_URL}/${id}`);
  if (!res.ok) {
    throw new Error(`Failed to fetch competition with id=${id}`);
  }
  return res.json();
}

/**
 * Fetch competitions filtered by both type and age.
 * Example endpoint: GET /api/competitions/type/SPORT/age/TEENS
 */
export async function fetchCompetitionsByTypeAndAge(
  type: string,
  ageCat: string
): Promise<Competition[]> {
  const res = await fetch(`${BASE_URL}/type/${encodeURIComponent(type)}/age/${encodeURIComponent(ageCat)}`);
  if (!res.ok) {
    throw new Error(`Failed to fetch competitions with type=${type} and age=${ageCat}`);
  }
  return res.json();
}

/**
 * Fetch competitions filtered **by type**.
 * Example endpoint: GET /api/competitions/type/SPORT
 */
export async function fetchCompetitionsByType(type: string): Promise<Competition[]> {
  const res = await fetch(`${BASE_URL}/type/${encodeURIComponent(type)}`);
  if (!res.ok) {
    throw new Error(`Failed to fetch competitions with type=${type}`);
  }
  return res.json();
}

/**
 * Fetch competitions filtered **by age category**.
 * Example endpoint: GET /api/competitions/age/TEENS
 */
export async function fetchCompetitionsByAge(ageCat: string): Promise<Competition[]> {
  const res = await fetch(`${BASE_URL}/age/${encodeURIComponent(ageCat)}`);
  if (!res.ok) {
    throw new Error(`Failed to fetch competitions with age=${ageCat}`);
  }
  return res.json();
}


/**
 * Enroll a single participant into one or more competitions.
 * POST /api/competitions/enroll
 * Body: { participantId: number, competitionIds: number[] }
 */
export async function enrollParticipant(
  participant: Participant,
  compTypes: String[]
): Promise<void> {
  const payload: EnrollDTO = { participant, compTypes };

  const res = await fetch(`${BASE_URL}/enroll`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

  if (!res.ok) {
    throw new Error(`Failed to enroll participant ${participant}`);
  }
}


/**
 * Create a new competition.
 */
export async function createCompetition(
  newCompetition: Omit<Competition, "id">
): Promise<Competition> {
  const res = await fetch(`${BASE_URL}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(newCompetition),
  });
  if (!res.ok) {
    throw new Error("Failed to create competition");
  }
  return res.json();
}

/**
 * Update an existing competition.
 */
export async function updateCompetition(
  id: number,
  updatedCompetition: Partial<Omit<Competition, "id">>
): Promise<Competition> {
  const res = await fetch(`${BASE_URL}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(updatedCompetition),
  });
  if (!res.ok) {
    throw new Error(`Failed to update competition with id=${id}`);
  }
  return res.json();
}

/**
 * Delete a competition by its ID.
 */
export async function deleteCompetition(id: number): Promise<void> {
  const res = await fetch(`${BASE_URL}/${id}`, {
    method: "DELETE",
  });
  if (!res.ok) {
    throw new Error(`Failed to delete competition with id=${id}`);
  }
}
