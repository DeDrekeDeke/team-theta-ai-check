import { apiRequest } from '../../app/apiClient';

export type AiSuggestion = {
  id: number;
  cvId: number;
  actionType: string;
  targetKey: string | null;
  originalText: string;
  suggestedText: string;
  status: string;
  createdAt: string;
};

export function improveWording(cvId: number, section: string, targetKey: string, text: string) {
  return apiRequest<AiSuggestion>(`/api/cvs/${cvId}/ai-actions/improve-wording`, {
    method: 'POST',
    body: JSON.stringify({ section, targetKey, text })
  });
}

export function improveSummary(cvId: number) {
  return apiRequest<AiSuggestion>(`/api/cvs/${cvId}/ai-actions/improve-summary`, {
    method: 'POST'
  });
}

export function listSuggestions(cvId: number) {
  return apiRequest<AiSuggestion[]>(`/api/cvs/${cvId}/ai-actions/suggestions`);
}

export function acceptSuggestion(cvId: number, suggestionId: number) {
  return apiRequest<AiSuggestion>(`/api/cvs/${cvId}/ai-actions/suggestions/${suggestionId}/accept`, {
    method: 'POST'
  });
}

export function declineSuggestion(cvId: number, suggestionId: number) {
  return apiRequest<AiSuggestion>(`/api/cvs/${cvId}/ai-actions/suggestions/${suggestionId}/decline`, {
    method: 'POST'
  });
}
