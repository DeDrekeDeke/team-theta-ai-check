import {
  CvEducationEntry,
  CvLanguage,
  CvLink,
  CvPersonalDetails,
  CvSkill,
  CvWorkExperienceEntry
} from '../cvApi';
import { EducationEntriesForm, emptyEducationEntry } from './EducationEntriesForm';
import { emptyLanguage, LanguagesForm } from './LanguagesForm';
import { emptyLink, LinksForm } from './LinksForm';
import { emptyPersonalDetails, PersonalDetailsForm } from './PersonalDetailsForm';
import { emptySkill, SkillsForm } from './SkillsForm';
import { emptyWorkExperienceEntry, WorkExperienceEntriesForm } from './WorkExperienceEntriesForm';

export type CvStructuredFormValue = {
  personalDetails: CvPersonalDetails | null;
  educationEntries: CvEducationEntry[];
  workExperienceEntries: CvWorkExperienceEntry[];
  skills: CvSkill[];
  languages: CvLanguage[];
  links: CvLink[];
};

type CvStructuredFormProps = {
  value: CvStructuredFormValue;
  onChange: (value: CvStructuredFormValue) => void;
};

export function emptyStructuredCvValue(): CvStructuredFormValue {
  return {
    personalDetails: null,
    educationEntries: [],
    workExperienceEntries: [],
    skills: [],
    languages: [],
    links: []
  };
}

export function CvStructuredForm({ value, onChange }: CvStructuredFormProps) {
  const canAddPersonalDetails = value.personalDetails === null;
  const canAddEducation = value.educationEntries.length === 0;
  const canAddWorkExperience = value.workExperienceEntries.length === 0;
  const canAddSkills = value.skills.length === 0;
  const canAddLanguages = value.languages.length === 0;
  const canAddLinks = value.links.length === 0;
  const hasAddableSections = canAddPersonalDetails
    || canAddEducation
    || canAddWorkExperience
    || canAddSkills
    || canAddLanguages
    || canAddLinks;

  return (
    <div className="form-stack">
      {hasAddableSections ? (
        <section className="panel">
          <h3>Add section</h3>
          <div className="section-add-grid">
            {canAddPersonalDetails ? (
              <button type="button" className="button secondary" onClick={() => onChange({ ...value, personalDetails: emptyPersonalDetails() })}>
                Personal details
              </button>
            ) : null}
            {canAddEducation ? (
              <button type="button" className="button secondary" onClick={() => onChange({ ...value, educationEntries: [emptyEducationEntry(0)] })}>
                Education
              </button>
            ) : null}
            {canAddWorkExperience ? (
              <button
                type="button"
                className="button secondary"
                onClick={() => onChange({ ...value, workExperienceEntries: [emptyWorkExperienceEntry(0)] })}
              >
                Work experience
              </button>
            ) : null}
            {canAddSkills ? (
              <button type="button" className="button secondary" onClick={() => onChange({ ...value, skills: [emptySkill(0)] })}>
                Skills
              </button>
            ) : null}
            {canAddLanguages ? (
              <button type="button" className="button secondary" onClick={() => onChange({ ...value, languages: [emptyLanguage(0)] })}>
                Languages
              </button>
            ) : null}
            {canAddLinks ? (
              <button type="button" className="button secondary" onClick={() => onChange({ ...value, links: [emptyLink(0)] })}>
                Links
              </button>
            ) : null}
          </div>
        </section>
      ) : null}

      {value.personalDetails ? (
        <section className="panel">
          <h3>Personal details</h3>
          <PersonalDetailsForm
            value={value.personalDetails}
            onChange={(personalDetails) => onChange({ ...value, personalDetails })}
          />
        </section>
      ) : null}

      {value.educationEntries.length > 0 ? (
        <section className="panel">
          <h3>Education</h3>
          <EducationEntriesForm
            entries={value.educationEntries}
            onChange={(educationEntries) => onChange({ ...value, educationEntries })}
          />
        </section>
      ) : null}

      {value.workExperienceEntries.length > 0 ? (
        <section className="panel">
          <h3>Work experience</h3>
          <WorkExperienceEntriesForm
            entries={value.workExperienceEntries}
            onChange={(workExperienceEntries) => onChange({ ...value, workExperienceEntries })}
          />
        </section>
      ) : null}

      {value.skills.length > 0 ? (
        <section className="panel">
          <h3>Skills</h3>
          <SkillsForm skills={value.skills} onChange={(skills) => onChange({ ...value, skills })} />
        </section>
      ) : null}

      {value.languages.length > 0 ? (
        <section className="panel">
          <h3>Languages</h3>
          <LanguagesForm languages={value.languages} onChange={(languages) => onChange({ ...value, languages })} />
        </section>
      ) : null}

      {value.links.length > 0 ? (
        <section className="panel">
          <h3>Links</h3>
          <LinksForm links={value.links} onChange={(links) => onChange({ ...value, links })} />
        </section>
      ) : null}
    </div>
  );
}
