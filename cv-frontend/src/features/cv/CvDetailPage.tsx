import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button } from '../../components/Button';
import { ErrorMessage } from '../../components/ErrorMessage';
import { LoadingState } from '../../components/LoadingState';
import { PageHeader } from '../../components/PageHeader';
import { AiActionPanel } from '../ai/AiActionPanel';
import { getCurrentUser } from '../auth/authStore';
import { CvPreview } from './components/CvPreview';
import { archiveCv, Cv, getCv } from './cvApi';

export function CvDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [cv, setCv] = useState<Cv | null>(null);
  const [error, setError] = useState('');
  const [archiving, setArchiving] = useState(false);

  useEffect(() => {
    if (!id) {
      return;
    }

    getCv(id)
      .then((loadedCv) => {
        const user = getCurrentUser();

        if (!user) {
          throw new Error('Log in to view this CV');
        }

        if (!user.admin && loadedCv.ownerUserId !== user.userId) {
          throw new Error('You can only view your own CVs');
        }

        setCv(loadedCv);
      })
      .catch((exception) => setError(exception instanceof Error ? exception.message : 'Could not load CV'));
  }, [id]);

  async function handleArchive() {
    if (!cv) {
      return;
    }

    setArchiving(true);
    setError('');

    try {
      await archiveCv(cv.id);
      navigate('/');
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : 'Could not archive CV');
      setArchiving(false);
    }
  }

  if (error) {
    return <ErrorMessage message={error} />;
  }

  if (!cv) {
    return <LoadingState />;
  }

  return (
    <section className="page-section">
      <PageHeader
        title={cv.title}
        description={`Owner: ${cv.ownerEmail}`}
        actions={
          <div className="page-actions">
            <Link className="button primary" to={`/cvs/${cv.id}/edit`}>
              Edit CV
            </Link>
          </div>
        }
      />

      <div className="detail-grid">
        <div className="panel">
          <div className="panel-header">
            <h3>CV Preview</h3>
          </div>
          <CvPreview cv={cv} />
        </div>
        <div className="form-stack">
          <AiActionPanel cvId={cv.id} />
          <Button type="button" variant="secondary" disabled={archiving} onClick={handleArchive}>
            {archiving ? 'Archiving...' : 'Archive CV'}
          </Button>
        </div>
      </div>
    </section>
  );
}
