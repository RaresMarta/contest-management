import React, { useState } from "react";
import { loginUser } from "../api/userApi";
import type { LoginRequestDTO, User } from "../lib/types";

interface LoginFormProps {
  onLoginSuccess: (user: User) => void;
}

const LoginForm: React.FC<LoginFormProps> = ({ onLoginSuccess }) => {
  const [userName, setUserName] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    const credentials: LoginRequestDTO = { userName, password };

    try {
      setIsSubmitting(true);
      const user = await loginUser(credentials);
      onLoginSuccess(user);
    } catch (err) {
      console.error("Login failed:", err);
      setError("Invalid username or password.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="container mt-6" style={{ maxWidth: "400px" }}>
      <h2 className="title is-4 has-text-centered">Login</h2>

      {error && (
        <div className="notification is-danger is-light">
          <button className="delete" onClick={() => setError(null)}></button>
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="field">
          <label className="label">Username</label>
          <div className="control">
            <input
              className="input"
              type="text"
              value={userName}
              onChange={(e) => setUserName(e.target.value)}
              required
              disabled={isSubmitting}
            />
          </div>
        </div>

        <div className="field">
          <label className="label">Password</label>
          <div className="control">
            <input
              className="input"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              disabled={isSubmitting}
            />
          </div>
        </div>

        <div className="field mt-4">
          <div className="control">
            <button
              className={`button is-primary is-fullwidth ${isSubmitting ? "is-loading" : ""}`}
              type="submit"
              disabled={isSubmitting}
            >
              Login
            </button>
          </div>
        </div>
      </form>
    </div>
  );
};

export default LoginForm;
