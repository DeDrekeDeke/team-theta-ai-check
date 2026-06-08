import { useEffect, useState } from 'react';
import { Button } from '../../components/Button';
import { ErrorMessage } from '../../components/ErrorMessage';
import { LoadingState } from '../../components/LoadingState';
import { AiSuggestion, improveSummary, listSuggestions } from './aiApi';

type AiActionPanelProps = {
  cvId: number;
};

export function AiActionPanel({ cvId }: AiActionPanelProps) {
  const [suggestions, setSuggestions] = useState<AiSuggestion[]>([]);
  const [loading, setLoading] = useState(true);
  const [runningAction, setRunningAction] = useState('');
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');

  useEffect(() => {
    listSuggestions(cvId)
      .then(setSuggestions)
      .catch((exception) => setError(exception instanceof Error ? exception.message : 'Could not load suggestions'))
      .finally(() => setLoading(false));
  }, [cvId]);

  async function handleImproveSummary() {
    setRunningAction('summary');
    setError('');
    setNotice('');
    try {
      const suggestion = await improveSummary(cvId);
      setSuggestions((current) => [suggestion, ...current]);
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : 'AI action failed');
    } finally {
      setRunningAction('');
    }
  }

  function handlePlaceholderAction(label: string) {
    setError('');
    setNotice(`${label} is listed as a planned AI action. The AS-IS backend currently implements summary improvement only.`);
  }

  return (
    <aside className="panel">
      <div className="panel-header">
        <h3>AI Actions</h3>
      </div>
      <div className="action-list">
        <Button type="button" onClick={handleImproveSummary} disabled={runningAction === 'summary'}>
          {runningAction === 'summary' ? 'Running...' : 'Improve summary'}
        </Button>
        <Button type="button" variant="secondary" onClick={() => handlePlaceholderAction('Improve education')}>
          Improve education
        </Button>
        <Button type="button" variant="secondary" onClick={() => handlePlaceholderAction('Improve work experience')}>
          Improve work experience
        </Button>
        <Button type="button" variant="secondary" onClick={() => handlePlaceholderAction('Improve skills')}>
          Improve skills
        </Button>
        <Button type="button" variant="secondary" onClick={() => handlePlaceholderAction('Evaluate fit')}>
          Evaluate fit
        </Button>
      </div>
      {error ? <ErrorMessage message={error} /> : null}
      {notice ? <p className="notice-message">{notice}</p> : null}
      {loading ? (
        <LoadingState />
      ) : suggestions.length === 0 ? (
        <p className="muted">No suggestions yet.</p>
      ) : (
        <div className="suggestion-list">
          {suggestions.map((suggestion) => (
            <article className="suggestion" key={suggestion.id}>
              <strong>{suggestion.actionType}</strong>
              <span>{suggestion.status}</span>
              <p>{suggestion.suggestedText}</p>
            </article>
          ))}
        </div>
      )}
    </aside>
  );
}
