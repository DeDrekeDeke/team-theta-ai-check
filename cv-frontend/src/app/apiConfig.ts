function normalizeApiBaseUrl(value: string) {
  return /^[a-z][a-z\d+\-.]*:\/\//i.test(value) ? value : `https://${value}`;
}

const configuredApiBaseUrl = import.meta.env.VITE_API_BASE_URL?.trim();

export const API_BASE_URL = (configuredApiBaseUrl
  ? normalizeApiBaseUrl(configuredApiBaseUrl)
  : 'http://localhost:8080'
).replace(/\/+$/, '');
