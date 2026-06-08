import type { LoginResponse } from './authApi';

export const AUTH_KEY = 'cv-manager-auth';
export const AUTH_CHANGED_EVENT = 'cv-manager-auth-changed';

export function saveCurrentUser(user: LoginResponse) {
  localStorage.setItem(AUTH_KEY, JSON.stringify(user));
  window.dispatchEvent(new Event(AUTH_CHANGED_EVENT));
}

export function getCurrentUser(): LoginResponse | null {
  const raw = localStorage.getItem(AUTH_KEY);
  if (!raw) {
    return null;
  }

  try {
    return JSON.parse(raw) as LoginResponse;
  } catch {
    localStorage.removeItem(AUTH_KEY);
    return null;
  }
}

export function getAuthToken(): string | null {
  return getCurrentUser()?.token ?? null;
}

export function logout() {
  localStorage.removeItem(AUTH_KEY);
  window.dispatchEvent(new Event(AUTH_CHANGED_EVENT));
}
