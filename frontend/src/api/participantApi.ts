// API functions to interact with the Participant endpoint

import type { CreateParticipantDTO, Participant } from "../lib/types";

const BASE_URL = "/api/participants";

/**
 * Fetch all participants.  
 * GET /api/participants
 */
export async function fetchAllParticipants(): Promise<Participant[]> {
  const res = await fetch(`${BASE_URL}`);
  if (!res.ok) {
    throw new Error("Failed to fetch all participants");
  }
  return res.json();
}

/**
 * Fetch a single participant by ID.  
 * GET /api/participants/{id}
 */
export async function fetchParticipantById(id: number): Promise<Participant> {
  const res = await fetch(`${BASE_URL}/${id}`);
  if (!res.ok) {
    throw new Error(`Failed to fetch participant with id=${id}`);
  }
  return res.json();
}

/**
 * Create a new participant.  
 * POST /api/participants  
 * Body: { name: string; age: number; competitionIds?: number[] }  
 */
export async function createParticipant(p: CreateParticipantDTO): Promise<Participant> {
  const res = await fetch(`${BASE_URL}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(p),
  });
  if (!res.ok) {
    throw new Error("Failed to create participant");
  }
  return res.json();
}

/**
 * Update an existing participant.  
 * PUT /api/participants/{id}  
 * Body: Partial<{ name: string; age: number; competitionIds: number[] }>
 */
export async function updateParticipant(
  id: number,
  updatedFields: Partial<Omit<Participant, "id">>
): Promise<Participant> {
  const res = await fetch(`${BASE_URL}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(updatedFields),
  });
  if (!res.ok) {
    throw new Error(`Failed to update participant with id=${id}`);
  }
  return res.json();
}

/**
 * Delete a participant.  
 * DELETE /api/participants/{id}
 */
export async function deleteParticipant(id: number): Promise<void> {
  const res = await fetch(`${BASE_URL}/${id}`, {
    method: "DELETE",
  });
  if (!res.ok) {
    throw new Error(`Failed to delete participant with id=${id}`);
  }
}

/**
 * Fetch participants for a specific competition.  
 * GET /api/participants/{competitionId}/participants
 */
export async function fetchParticipantsForCompetition(competitionId: number): Promise<Participant[]> {
  const res = await fetch(`${BASE_URL}/comp/${competitionId}`);
  if (!res.ok) {
    throw new Error(`Failed to fetch participants for competitionId=${competitionId}`);
  }
  return res.json();
}
