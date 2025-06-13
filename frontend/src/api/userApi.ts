// API functions to interact with the User endpoint

import type { User, CreateUserDTO, LoginRequestDTO } from "../lib/types";

const BASE_URL = "/api/users";

/**
 * Fetch all users.  
 * GET /api/users
 */
export async function fetchAllUsers(): Promise<User[]> {
  const res = await fetch(`${BASE_URL}`);
  if (!res.ok) {
    throw new Error("Failed to fetch users");
  }
  return res.json();
}

/**
 * Fetch a single user by ID.  
 * GET /api/users/{id}
 */
export async function fetchUserById(id: number): Promise<User> {
  const res = await fetch(`${BASE_URL}/${id}`);
  if (!res.ok) {
    throw new Error(`Failed to fetch user with id=${id}`);
  }
  return res.json();
}

/**
 * Create a new user.  
 * POST /api/users  
 * Body: { userName: string; password: string }
 */
export async function createUser(user: CreateUserDTO): Promise<User> {
  const res = await fetch(`${BASE_URL}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(user),
  });
  if (!res.ok) {
    throw new Error("Failed to create user");
  }
  return res.json();
}

/**
 * Login an existing user.  
 * POST /api/users/login  
 * Body: { userName: string; password: string }
 */
export async function loginUser(credentials: LoginRequestDTO): Promise<User> {
  const res = await fetch(`${BASE_URL}/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(credentials),
  });
  if (!res.ok) {
    throw new Error("Invalid username or password");
  }
  return res.json();
}
