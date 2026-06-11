import { FormEvent, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '../../components/Button';
import { ErrorMessage } from '../../components/ErrorMessage';
import { FormField, TextInput } from '../../components/FormField';
import { PageHeader } from '../../components/PageHeader';
import { compactErrors, MAX_TITLE_LENGTH, validateOwnerUserId, validateRequiredTitle } from '../../lib/validation';
import { getCurrentUser } from '../auth/authStore';
import { CvStructuredForm, emptyStructuredCvValue } from './components/CvStructuredForm';
import { createCv } from './cvApi';

export function CvCreatePage() {
  const user = getCurrentUser();
  const navigate = useNavigate();
  const [ownerUserId, setOwnerUserId] = useState(user ? String(user.userId) : '');
  const [title, setTitle] = useState('');
  const [summary, setSummary] = useState('');
  const [structuredCv, setStructuredCv] = useState(emptyStructuredCvValue());
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const canChooseOwner = user?.admin ?? false;

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const currentUser = getCurrentUser();

    if (!currentUser) {
      setError('Log in to create a CV.');
      return;
    }

    const validationErrors = compactErrors([
      validateRequiredTitle(title),
      canChooseOwner ? validateOwnerUserId(ownerUserId) : ''
    ]);

    if (validationErrors.length > 0) {
      setError(validationErrors.join('\n'));
      return;
    }

    setLoading(true);
    setError('');
    try {
      const cv = await createCv({
        ownerUserId: canChooseOwner ? Number(ownerUserId) : currentUser.userId,
        title,
        summary,
        ...structuredCv
      });
      navigate(`/cvs/${cv.id}`);
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : 'Could not create CV');
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="page-section">
      <PageHeader title="Create CV" description="Create a structured CV without uploading HTML." />
      <form className="form-stack" onSubmit={handleSubmit} noValidate>
        {canChooseOwner ? (
          <FormField label="Owner user ID" htmlFor="owner-user-id">
            <TextInput
              id="owner-user-id"
              inputMode="numeric"
              value={ownerUserId}
              onChange={(event) => setOwnerUserId(event.target.value)}
            />
          </FormField>
        ) : null}

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

        {error ? <ErrorMessage message={error} /> : null}
        <div className="inline-actions end">
          <Button type="submit" disabled={loading || !title.trim()}>
            {loading ? 'Creating...' : 'Create CV'}
          </Button>
        </div>
      </form>
    </section>
  );
}
