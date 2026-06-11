import { useEffect, useMemo, useState } from 'react';
import { APP_CONFIG_CHANGED_EVENT, DEFAULT_APP_CONFIG, getAppConfig } from '../../app/appConfig';
import { Button } from '../../components/Button';
import { ErrorMessage } from '../../components/ErrorMessage';
import { LoadingState } from '../../components/LoadingState';
import {
  AiSuggestion,
  acceptSuggestion,
  declineSuggestion,
  improveWording,
  listSuggestions
} from './aiApi';

export type AiEditableSection = {
  key: string;
  section: 'summary' | 'education' | 'workExperience' | 'skills';
  label: string;
  text: string;
  onAccept: (suggestedText: string) => void;
};

type AiActionPanelProps = {
  cvId: number;
  sections?: AiEditableSection[];
};

const PENDING_STATUS = 'PENDING';

function actionTypeLabel(actionType: string) {
  if (actionType === 'IMPROVE_SUMMARY') {
    return 'Summary';
  }
  if (actionType === 'IMPROVE_EDUCATION') {
    return 'Education';
  }
  if (actionType === 'IMPROVE_WORK_EXPERIENCE') {
    return 'Work experience';
  }
  if (actionType === 'IMPROVE_SKILLS') {
    return 'Skills';
  }
  return actionType.replace(/_/g, ' ').toLowerCase();
}

function updateSuggestion(suggestions: AiSuggestion[], updatedSuggestion: AiSuggestion) {
  return suggestions.map((suggestion) => (
    suggestion.id === updatedSuggestion.id ? updatedSuggestion : suggestion
  ));
}

export function AiActionPanel({ cvId, sections = [] }: AiActionPanelProps) {
  const [suggestions, setSuggestions] = useState<AiSuggestion[]>([]);
  const [loading, setLoading] = useState(true);
  const [runningAction, setRunningAction] = useState(false);
  const [reviewingSuggestionId, setReviewingSuggestionId] = useState<number | null>(null);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [aiToolsetEnabled, setAiToolsetEnabled] = useState(DEFAULT_APP_CONFIG.aiToolsetEnabled);

  const editableSections = useMemo(
    () => sections.filter((section) => section.text.trim().length > 0),
    [sections]
  );
  const canEditSections = sections.length > 0;

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

  function findSectionForSuggestion(suggestion: AiSuggestion) {
    return sections.find((section) => section.key === suggestion.targetKey);
  }

  function suggestionLabel(suggestion: AiSuggestion) {
    return findSectionForSuggestion(suggestion)?.label ?? actionTypeLabel(suggestion.actionType);
  }

  async function handleImproveWording() {
    if (editableSections.length === 0) {
      setNotice('Add text to at least one CV topic before improving wording.');
      return;
    }

    setRunningAction(true);
    setError('');
    setNotice('');
    try {
      const suggestionResults = await Promise.allSettled(
        editableSections.map((section) => improveWording(cvId, section.section, section.key, section.text))
      );
      const createdSuggestions = suggestionResults
        .filter((result): result is PromiseFulfilledResult<AiSuggestion> => result.status === 'fulfilled')
        .map((result) => result.value);
      const failedSuggestions = suggestionResults.filter((result) => result.status === 'rejected');

      if (createdSuggestions.length > 0) {
        setSuggestions((current) => [...createdSuggestions, ...current]);
      }

      if (failedSuggestions.length > 0) {
        const firstFailure = failedSuggestions[0] as PromiseRejectedResult;
        setError(firstFailure.reason instanceof Error ? firstFailure.reason.message : 'Some AI suggestions failed');
      }

      if (createdSuggestions.length === 0) {
        setNotice('');
      } else if (failedSuggestions.length > 0) {
        setNotice('Review the generated topic suggestions. Some topics could not be improved.');
      } else {
        setNotice('Review each topic suggestion before applying it.');
      }
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : 'AI action failed');
    } finally {
      setRunningAction(false);
    }
  }

  async function handleAcceptSuggestion(suggestion: AiSuggestion) {
    const section = findSectionForSuggestion(suggestion);
    if (!section) {
      setError('This suggestion is for a CV topic that is no longer available in the form.');
      return;
    }

    setReviewingSuggestionId(suggestion.id);
    setError('');
    setNotice('');
    try {
      const updatedSuggestion = await acceptSuggestion(cvId, suggestion.id);
      setSuggestions((current) => updateSuggestion(current, updatedSuggestion));
      section.onAccept(suggestion.suggestedText);
      setNotice(`${section.label} suggestion applied to the form. Save changes to keep it.`);
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : 'Could not accept suggestion');
    } finally {
      setReviewingSuggestionId(null);
    }
  }

  async function handleDeclineSuggestion(suggestion: AiSuggestion) {
    setReviewingSuggestionId(suggestion.id);
    setError('');
    setNotice('');
    try {
      const updatedSuggestion = await declineSuggestion(cvId, suggestion.id);
      setSuggestions((current) => updateSuggestion(current, updatedSuggestion));
      setNotice(`${suggestionLabel(suggestion)} suggestion declined.`);
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : 'Could not decline suggestion');
    } finally {
      setReviewingSuggestionId(null);
    }
  }

  return (
    <aside className="panel">
      <div className="panel-header">
        <h3>AI Actions</h3>
      </div>
      {canEditSections ? (
        <div className="action-list">
          <Button
            type="button"
            onClick={handleImproveWording}
            disabled={!aiToolsetEnabled || runningAction || editableSections.length === 0}
          >
            {runningAction ? 'Running...' : 'Improve wording'}
          </Button>
        </div>
      ) : null}
      {error ? <ErrorMessage message={error} /> : null}
      {canEditSections && !aiToolsetEnabled ? <p className="notice-message">AI actions are disabled by an admin.</p> : null}
      {canEditSections && editableSections.length === 0 ? (
        <p className="notice-message">Add text to at least one CV topic before improving wording.</p>
      ) : null}
      {notice ? <p className="notice-message">{notice}</p> : null}
      {loading ? (
        <LoadingState />
      ) : suggestions.length === 0 ? (
        <p className="muted">No suggestions yet.</p>
      ) : (
        <div className="suggestion-list">
          {suggestions.map((suggestion) => {
            const matchingSection = findSectionForSuggestion(suggestion);
            return (
              <article className="suggestion" key={suggestion.id}>
                <div className="suggestion-title">
                  <strong>{suggestionLabel(suggestion)}</strong>
                  <span>{suggestion.status}</span>
                </div>
                <div className="suggestion-comparison">
                  <div>
                    <span>Original</span>
                    <p>{suggestion.originalText}</p>
                  </div>
                  <div>
                    <span>Suggestion</span>
                    <p>{suggestion.suggestedText}</p>
                  </div>
                </div>
                {suggestion.status === PENDING_STATUS ? (
                  canEditSections && matchingSection ? (
                    <div className="inline-actions">
                      <Button
                        type="button"
                        onClick={() => handleAcceptSuggestion(suggestion)}
                        disabled={reviewingSuggestionId === suggestion.id}
                      >
                        {reviewingSuggestionId === suggestion.id ? 'Applying...' : 'Accept'}
                      </Button>
                      <Button
                        type="button"
                        variant="secondary"
                        onClick={() => handleDeclineSuggestion(suggestion)}
                        disabled={reviewingSuggestionId === suggestion.id}
                      >
                        Decline
                      </Button>
                    </div>
                  ) : (
                    <p className="muted">Open edit mode to apply this suggestion.</p>
                  )
                ) : null}
              </article>
            );
          })}
        </div>
      )}
    </aside>
  );
}
