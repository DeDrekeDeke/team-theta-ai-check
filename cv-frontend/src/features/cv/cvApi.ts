import { apiRequest } from '../../app/apiClient';

export type Cv = {
  id: number;
  ownerUserId: number;
  ownerEmail: string;
  title: string;
  summary: string | null;
  personalDetails: CvPersonalDetails | null;
  educationEntries: CvEducationEntry[];
  workExperienceEntries: CvWorkExperienceEntry[];
  skills: CvSkill[];
  languages: CvLanguage[];
  links: CvLink[];
  createdAt: string;
  updatedAt: string;
  archivedAt: string | null;
};

export type CvPersonalDetails = {
  id?: number;
  fullName: string | null;
  email: string | null;
  phone: string | null;
  location: string | null;
  headline: string | null;
};

export type CvEducationEntry = {
  id?: number;
  institution: string;
  degree: string | null;
  fieldOfStudy: string | null;
  startDate: string | null;
  endDate: string | null;
  description: string | null;
  displayOrder: number;
};

export type CvWorkExperienceEntry = {
  id?: number;
  employer: string;
  jobTitle: string;
  location: string | null;
  startDate: string | null;
  endDate: string | null;
  description: string | null;
  displayOrder: number;
};

export type CvSkill = {
  id?: number;
  name: string;
  category: string | null;
  proficiency: string | null;
  displayOrder: number;
};

export type CvLanguage = {
  id?: number;
  name: string;
  proficiency: string | null;
  displayOrder: number;
};

export type CvLink = {
  id?: number;
  label: string;
  url: string;
  displayOrder: number;
};

export type CvCreateRequest = {
  ownerUserId: number;
  title: string;
  summary: string;
  personalDetails?: CvPersonalDetails | null;
  educationEntries?: CvEducationEntry[];
  workExperienceEntries?: CvWorkExperienceEntry[];
  skills?: CvSkill[];
  languages?: CvLanguage[];
  links?: CvLink[];
};

export function listCvs() {
  return apiRequest<Cv[]>('/api/cvs');
}

export function searchCvs(query: string) {
  return apiRequest<Cv[]>(`/api/cvs/search?q=${encodeURIComponent(query)}`);
}

export function getCv(id: string) {
  return apiRequest<Cv>(`/api/cvs/${id}`);
}

export function createCv(request: CvCreateRequest) {
  return apiRequest<Cv>('/api/cvs', {
    method: 'POST',
    body: JSON.stringify(request)
  });
}

export type CvUpdateRequest = {
  title: string;
  summary: string;
  personalDetails?: CvPersonalDetails | null;
  educationEntries?: CvEducationEntry[];
  workExperienceEntries?: CvWorkExperienceEntry[];
  skills?: CvSkill[];
  languages?: CvLanguage[];
  links?: CvLink[];
};

export function updateCv(id: number | string, request: CvUpdateRequest) {
  return apiRequest<Cv>(`/api/cvs/${id}`, {
    method: 'PUT',
    body: JSON.stringify(request)
  });
}

export function archiveCv(id: number | string) {
  return apiRequest<void>(`/api/cvs/${id}`, {
    method: 'DELETE'
  });
}
