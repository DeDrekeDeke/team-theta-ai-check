import { useEffect, useState } from 'react';
import { APP_CONFIG_CHANGED_EVENT, DEFAULT_APP_CONFIG, getAppConfig } from '../../app/appConfig';
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
  const [aiToolsetEnabled, setAiToolsetEnabled] = useState(DEFAULT_APP_CONFIG.aiToolsetEnabled);

  useEffect(() => {
    listSuggestions(cvId)
      .then(setSuggestions)
      .catch((exception) => setError(exception instanceof Error ? exception.message : 'Could not load suggestions'))
      .finally(() => setLoading(false));
  }, [cvId]);

  useEffect(() => {
    let active = true;

    function loadAppConfig() {
      getAppConfig()
        .then((config) => {
          if (active) {
            setAiToolsetEnabled(config.aiToolsetEnabled);
          }
        })
        .catch(() => {
          if (active) {
            setAiToolsetEnabled(DEFAULT_APP_CONFIG.aiToolsetEnabled);
          }
        });
    }

    loadAppConfig();
    window.addEventListener(APP_CONFIG_CHANGED_EVENT, loadAppConfig);

    return () => {
      active = false;
      window.removeEventListener(APP_CONFIG_CHANGED_EVENT, loadAppConfig);
    };
  }, []);

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
        <Button type="button" onClick={handleImproveSummary} disabled={!aiToolsetEnabled || runningAction === 'summary'}>
          {runningAction === 'summary' ? 'Running...' : 'Improve summary'}
        </Button>
        <Button
          type="button"
          variant="secondary"
          onClick={() => handlePlaceholderAction('Improve education')}
          disabled={!aiToolsetEnabled}
        >
          Improve education
        </Button>
        <Button
          type="button"
          variant="secondary"
          onClick={() => handlePlaceholderAction('Improve work experience')}
          disabled={!aiToolsetEnabled}
        >
          Improve work experience
        </Button>
        <Button
          type="button"
          variant="secondary"
          onClick={() => handlePlaceholderAction('Improve skills')}
          disabled={!aiToolsetEnabled}
        >
          Improve skills
        </Button>
        <Button
          type="button"
          variant="secondary"
          onClick={() => handlePlaceholderAction('Evaluate fit')}
          disabled={!aiToolsetEnabled}
        >
          Evaluate fit
        </Button>
      </div>
      {error ? <ErrorMessage message={error} /> : null}
      {!aiToolsetEnabled ? <p className="notice-message">AI actions are disabled by an admin.</p> : null}
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
