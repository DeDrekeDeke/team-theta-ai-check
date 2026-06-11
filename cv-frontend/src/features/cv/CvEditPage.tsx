import { FormEvent, useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button } from '../../components/Button';
import { ErrorMessage } from '../../components/ErrorMessage';
import { FormField, TextInput } from '../../components/FormField';
import { LoadingState } from '../../components/LoadingState';
import { PageHeader } from '../../components/PageHeader';
import { compactErrors, MAX_TITLE_LENGTH, validateRequiredTitle } from '../../lib/validation';
import { getCurrentUser } from '../auth/authStore';
import { CvStructuredForm, CvStructuredFormValue, emptyStructuredCvValue } from './components/CvStructuredForm';
import { CvPreview } from './components/CvPreview';
import { Cv, getCv, updateCv } from './cvApi';

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

        <div className="panel">
          <div className="panel-header">
            <h3>Preview</h3>
          </div>
          <CvPreview cv={previewCv} />
        </div>
      </div>
    </section>
  );
}
