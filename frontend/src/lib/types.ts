export interface Competition {
  competitionID: number;
  type: string;
  ageCategory: string;
  nrOfParticipants: number;
}

export interface Participant {
  participantID: number;
  name: string;
  age: number;
}

export interface EnrollDTO {
  participant: Participant;
  compTypes: String[];
}

export interface CreateParticipantDTO {
  name: string;
  age: number;
}

export interface User {
  userID: number;
  userName: string;
  password: string;
}

export interface CreateUserDTO {
  userName: string;
  password: string;
}

export interface LoginRequestDTO {
  userName: string;
  password: string;
}
