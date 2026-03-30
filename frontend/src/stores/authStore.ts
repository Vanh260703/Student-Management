import { create } from 'zustand';
import { Role } from '../types/auth.types';

interface AuthState {
  accessToken: string | null;
  role: Role | null;
  email: string | null;
  isAuthenticated: boolean;
  isInitialized: boolean;
  setAuth: (token: string, role: Role, email: string) => void;
  clearAuth: () => void;
  setInitialized: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  accessToken: null,
  role: null,
  email: null,
  isAuthenticated: false,
  isInitialized: false,
  setAuth: (token, role, email) =>
    set({ accessToken: token, role, email, isAuthenticated: true }),
  clearAuth: () =>
    set({ accessToken: null, role: null, email: null, isAuthenticated: false }),
  setInitialized: () => set({ isInitialized: true }),
}));
