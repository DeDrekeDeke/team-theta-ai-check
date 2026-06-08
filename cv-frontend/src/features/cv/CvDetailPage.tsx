import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { ErrorMessage } from '../../components/ErrorMessage';
import { LoadingState } from '../../components/LoadingState';
import { PageHeader } from '../../components/PageHeader';
import { AiActionPanel } from '../ai/AiActionPanel';
import { Cv, getCv, getCvHtml, getCvHtmlUrl } from './cvApi';

export function CvDetailPage() {
  const { id } = useParams();
  const [cv, setCv] = useState<Cv | null>(null);
  const [html, setHtml] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    if (!id) {
      return;
    }

    Promise.all([getCv(id), getCvHtml(id)])
      .then(([loadedCv, loadedHtml]) => {
        setCv(loadedCv);
        setHtml(loadedHtml);
      })
      .catch((exception) => setError(exception instanceof Error ? exception.message : 'Could not load CV'));
  }, [id]);

  if (error) {
    return <ErrorMessage message={error} />;
  }

  if (!cv) {
    return <LoadingState />;
  }

  return (
    <section className="page-section">
      <PageHeader title={cv.title} description={`Owner: ${cv.ownerEmail}`} />

      <div className="detail-grid">
        <div className="panel">
          <div className="panel-header">
            <h3>Uploaded HTML Content</h3>
            <a className="button secondary" href={getCvHtmlUrl(cv.id)} target="_blank" rel="noreferrer">
              Open raw HTML
            </a>
          </div>
          <div className="html-render" dangerouslySetInnerHTML={{ __html: html }} />
        </div>
        <AiActionPanel cvId={cv.id} />
      </div>
    </section>
  );
}
