import type { LoginResponse } from './authApi';

export const AUTH_CHANGED_EVENT = 'cv-manager-auth-changed';

let currentUser: LoginResponse | null = null;
let authMessage: string | null = null;

export function saveCurrentUser(user: LoginResponse) {
  currentUser = user;
  authMessage = null;
  window.dispatchEvent(new Event(AUTH_CHANGED_EVENT));
}

export function getCurrentUser(): LoginResponse | null {
  return currentUser;
}

export function getAuthToken(): string | null {
  return getCurrentUser()?.token ?? null;
}

export function isAdminUser(user: LoginResponse | null) {
  return user?.role === 'ADMIN';
}

export function consumeAuthMessage(): string | null {
  const message = authMessage;
  authMessage = null;
  return message;
}

export function logout(message?: string) {
  currentUser = null;
  authMessage = message ?? null;
  window.dispatchEvent(new Event(AUTH_CHANGED_EVENT));
}
