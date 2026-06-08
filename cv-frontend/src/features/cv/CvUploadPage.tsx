import { FormEvent, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '../../components/Button';
import { ErrorMessage } from '../../components/ErrorMessage';
import { FormField, TextInput } from '../../components/FormField';
import { PageHeader } from '../../components/PageHeader';
import { uploadCv } from './cvApi';

export function CvUploadPage() {
  const navigate = useNavigate();
  const [ownerUserId, setOwnerUserId] = useState('2');
  const [title, setTitle] = useState('');
  const [file, setFile] = useState<File | null>(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!file) {
      setError('Choose an HTML file first.');
      return;
    }

    const data = new FormData();
    data.append('ownerUserId', ownerUserId);
    data.append('title', title);
    data.append('file', file);

    setLoading(true);
    setError('');
    try {
      const cv = await uploadCv(data);
      navigate(`/cvs/${cv.id}`);
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : 'Upload failed');
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="page-section narrow">
      <PageHeader title="Upload CV" description="Upload an HTML CV file to the AS-IS backend." />
      <form className="form-stack" onSubmit={handleSubmit}>
        <FormField label="Owner user id" htmlFor="ownerUserId">
          <TextInput
            id="ownerUserId"
            value={ownerUserId}
            onChange={(event) => setOwnerUserId(event.target.value)}
          />
        </FormField>
        <FormField label="Title" htmlFor="title">
          <TextInput id="title" value={title} onChange={(event) => setTitle(event.target.value)} />
        </FormField>
        <FormField label="HTML file" htmlFor="file">
          <input
            id="file"
            className="file-input"
            type="file"
            accept=".html,.htm,text/html"
            onChange={(event) => setFile(event.target.files?.[0] ?? null)}
          />
        </FormField>
        {error ? <ErrorMessage message={error} /> : null}
        <Button type="submit" disabled={loading}>
          {loading ? 'Uploading...' : 'Upload'}
        </Button>
      </form>
    </section>
  );
}
