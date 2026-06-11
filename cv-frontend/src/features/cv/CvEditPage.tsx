import { FormEvent, useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button } from '../../components/Button';
import { ErrorMessage } from '../../components/ErrorMessage';
import { FormField, TextInput } from '../../components/FormField';
import { LoadingState } from '../../components/LoadingState';
import { PageHeader } from '../../components/PageHeader';
import { compactErrors, MAX_TITLE_LENGTH, validateRequiredTitle } from '../../lib/validation';
import { AiActionPanel, type AiEditableSection } from '../ai/AiActionPanel';
import { getCurrentUser } from '../auth/authStore';
import { CvStructuredForm, CvStructuredFormValue, emptyStructuredCvValue } from './components/CvStructuredForm';
import { CvPreview } from './components/CvPreview';
import { Cv, CvEducationEntry, CvSkill, CvWorkExperienceEntry, getCv, updateCv } from './cvApi';

function structuredValueFromCv(cv: Cv): CvStructuredFormValue {
  return {
    personalDetails: cv.personalDetails,
    educationEntries: cv.educationEntries,
    workExperienceEntries: cv.workExperienceEntries,
    skills: cv.skills,
    languages: cv.languages,
    links: cv.links
  };
}

function compactText(parts: Array<string | null | undefined>) {
  return parts.map((part) => part?.trim()).filter(Boolean).join(' | ');
}

function educationTargetKey(entry: CvEducationEntry, index: number) {
  return `education:${entry.id ?? index}`;
}

function workTargetKey(entry: CvWorkExperienceEntry, index: number) {
  return `workExperience:${entry.id ?? index}`;
}

function educationText(entry: CvEducationEntry) {
  return compactText([
    entry.institution,
    entry.degree,
    entry.fieldOfStudy,
    compactText([entry.startDate, entry.endDate]),
    entry.description
  ]);
}

function workExperienceText(entry: CvWorkExperienceEntry) {
  return compactText([
    entry.jobTitle,
    entry.employer,
    entry.location,
    compactText([entry.startDate, entry.endDate]),
    entry.description
  ]);
}

function skillsText(skills: CvSkill[]) {
  return skills
    .map((skill) => compactText([skill.name, skill.category, skill.proficiency]))
    .filter(Boolean)
    .join('\n');
}

function parseSkillSuggestion(suggestedText: string, currentSkills: CvSkill[]) {
  const suggestedItems = suggestedText
    .split(/\r?\n|,/)
    .map((item) => item.replace(/^[-*]\s*/, '').trim())
    .filter(Boolean);

  if (suggestedItems.length === 0) {
    return currentSkills;
  }

  return suggestedItems.map((item, index) => {
    const fallback = currentSkills[index];
    const parts = item.split(/\s*(?:\|| - | – | — )\s*/).filter(Boolean);

    return {
      ...fallback,
      name: parts[0] ?? fallback?.name ?? item,
      category: fallback?.category ?? null,
      proficiency: parts.length > 1 ? parts.slice(1).join(' - ') : fallback?.proficiency ?? null,
      displayOrder: fallback?.displayOrder ?? index
    };
  });
}

export function CvEditPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [cv, setCv] = useState<Cv | null>(null);
  const [title, setTitle] = useState('');
  const [summary, setSummary] = useState('');
  const [structuredCv, setStructuredCv] = useState<CvStructuredFormValue>(emptyStructuredCvValue());
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!id) {
      return;
    }

    getCv(id)
      .then((loadedCv) => {
        const user = getCurrentUser();

        if (!user) {
          throw new Error('Log in to edit this CV');
        }

        if (!user.admin && loadedCv.ownerUserId !== user.userId) {
          throw new Error('You can only edit your own CVs');
        }

        setCv(loadedCv);
        setTitle(loadedCv.title);
        setSummary(loadedCv.summary ?? '');
        setStructuredCv(structuredValueFromCv(loadedCv));
      })
      .catch((exception) => setError(exception instanceof Error ? exception.message : 'Could not load CV'));
  }, [id]);

  async function handleSave(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!cv) {
      return;
    }

    const validationErrors = compactErrors([validateRequiredTitle(title)]);

    if (validationErrors.length > 0) {
      setError(validationErrors.join('\n'));
      return;
    }

    setSaving(true);
    setError('');
    try {
      const updatedCv = await updateCv(cv.id, {
        title,
        summary,
        ...structuredCv
      });
      navigate(`/cvs/${updatedCv.id}`);
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : 'Could not update CV');
    } finally {
      setSaving(false);
    }
  }

  if (error) {
    return <ErrorMessage message={error} />;
  }

  if (!cv) {
    return <LoadingState />;
  }

  const previewCv: Cv = {
    ...cv,
    title,
    summary,
    ...structuredCv
  };

  function applyEducationSuggestion(targetKey: string, suggestedText: string) {
    setStructuredCv((current) => ({
      ...current,
      educationEntries: current.educationEntries.map((entry, index) => (
        educationTargetKey(entry, index) === targetKey ? { ...entry, description: suggestedText } : entry
      ))
    }));
  }

  function applyWorkExperienceSuggestion(targetKey: string, suggestedText: string) {
    setStructuredCv((current) => ({
      ...current,
      workExperienceEntries: current.workExperienceEntries.map((entry, index) => (
        workTargetKey(entry, index) === targetKey ? { ...entry, description: suggestedText } : entry
      ))
    }));
  }

  function applySkillsSuggestion(suggestedText: string) {
    setStructuredCv((current) => ({
      ...current,
      skills: parseSkillSuggestion(suggestedText, current.skills)
    }));
  }

  const aiSections: AiEditableSection[] = [
    {
      key: 'summary',
      section: 'summary',
      label: 'Summary',
      text: summary,
      onAccept: setSummary
    },
    ...structuredCv.educationEntries.map((entry, index) => {
      const key = educationTargetKey(entry, index);
      return {
        key,
        section: 'education' as const,
        label: `Education: ${entry.institution || `Entry ${index + 1}`}`,
        text: educationText(entry),
        onAccept: (suggestedText: string) => applyEducationSuggestion(key, suggestedText)
      };
    }),
    ...structuredCv.workExperienceEntries.map((entry, index) => {
      const key = workTargetKey(entry, index);
      return {
        key,
        section: 'workExperience' as const,
        label: `Work experience: ${entry.employer || `Entry ${index + 1}`}`,
        text: workExperienceText(entry),
        onAccept: (suggestedText: string) => applyWorkExperienceSuggestion(key, suggestedText)
      };
    })
  ];

  if (structuredCv.skills.length > 0) {
    aiSections.push({
      key: 'skills',
      section: 'skills',
      label: 'Skills',
      text: skillsText(structuredCv.skills),
      onAccept: applySkillsSuggestion
    });
  }

  return (
    <section className="page-section">
      <PageHeader
        title={`Edit ${cv.title}`}
        description={`Owner: ${cv.ownerEmail}`}
        actions={<Link className="button secondary" to={`/cvs/${cv.id}`}>Cancel</Link>}
      />

      <div className="detail-grid">
        <form className="form-stack" onSubmit={handleSave} noValidate>
          <FormField label="Title" htmlFor="title">
            <TextInput
              id="title"
              required
              maxLength={MAX_TITLE_LENGTH}
              value={title}
              onChange={(event) => setTitle(event.target.value)}
            />
          </FormField>
          <label className="form-field" htmlFor="summary">
            <span>Summary</span>
            <textarea
              id="summary"
              className="text-input"
              rows={6}
              value={summary}
              onChange={(event) => setSummary(event.target.value)}
            />
          </label>

          <CvStructuredForm value={structuredCv} onChange={setStructuredCv} />

          <div className="inline-actions end">
            <Button type="submit" disabled={saving || !title.trim()}>
              {saving ? 'Saving...' : 'Save changes'}
            </Button>
          </div>
        </form>

        <div className="form-stack">
          <AiActionPanel cvId={cv.id} sections={aiSections} />

          <div className="panel">
            <div className="panel-header">
              <h3>Preview</h3>
            </div>
            <CvPreview cv={previewCv} />
          </div>
        </div>
      </div>
    </section>
  );
}
