import { apiRequest } from '../../app/apiClient';

export type LoginRequest = {
  email: string;
  password: string;
};

export type RegisterRequest = {
  email: string;
  displayName: string;
  password: string;
};

export type LoginResponse = {
  userId: number;
  email: string;
  displayName: string;
  role: 'USER' | 'ADMIN';
  admin: boolean;
  token: string;
};

export function login(request: LoginRequest) {
  return apiRequest<LoginResponse>('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify(request)
  });
}

export function register(request: RegisterRequest) {
  return apiRequest<LoginResponse>('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify(request)
  });
}

export function logoutRequest() {
  return apiRequest<void>('/api/auth/logout', {
    method: 'POST'
  });
}
