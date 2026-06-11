import { apiRequest } from './apiClient';

export const APP_CONFIG_CHANGED_EVENT = 'cv-manager-app-config-changed';

export type AppConfig = {
  applicationDisplayName: string;
  aiToolsetEnabled: boolean;
};

export const DEFAULT_APP_CONFIG: AppConfig = {
  applicationDisplayName: 'CV Manager',
  aiToolsetEnabled: true
};

export function getAppConfig() {
  return apiRequest<AppConfig>('/api/app-config');
}

export function notifyAppConfigChanged() {
  window.dispatchEvent(new Event(APP_CONFIG_CHANGED_EVENT));
}
