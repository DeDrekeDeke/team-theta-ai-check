import { apiRequest } from '../../app/apiClient';

export type AiSuggestion = {
  id: number;
  cvId: number;
  actionType: string;
  originalText: string;
  suggestedText: string;
  status: string;
  createdAt: string;
};

export function improveSummary(cvId: number) {
  return apiRequest<AiSuggestion>(`/api/cvs/${cvId}/ai-actions/improve-summary`, {
    method: 'POST'
  });
}

export function listSuggestions(cvId: number) {
  return apiRequest<AiSuggestion[]>(`/api/cvs/${cvId}/ai-actions/suggestions`);
}
