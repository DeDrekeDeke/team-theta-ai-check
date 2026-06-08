import { apiRequest } from '../../app/apiClient';

export type AppSetting = {
  key: string;
  value: string;
  description: string | null;
};

export function listSettings() {
  return apiRequest<AppSetting[]>('/api/admin/settings');
}
