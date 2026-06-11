import { useEffect, useState } from 'react';
import { notifyAppConfigChanged } from '../../app/appConfig';
import { Button } from '../../components/Button';
import { ErrorMessage } from '../../components/ErrorMessage';
import { FormField, TextInput } from '../../components/FormField';
import { LoadingState } from '../../components/LoadingState';
import { PageHeader } from '../../components/PageHeader';
import { AppSetting, listSettings, updateSetting } from './adminApi';

export function SettingsPage() {
  const [settings, setSettings] = useState<AppSetting[]>([]);
  const [draftValues, setDraftValues] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(true);
  const [savingKey, setSavingKey] = useState('');
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');

  useEffect(() => {
    let active = true;

    listSettings()
      .then((loadedSettings) => {
        if (!active) {
          return;
        }
        setSettings(loadedSettings);
        setDraftValues(Object.fromEntries(loadedSettings.map((setting) => [setting.key, setting.value])));
      })
      .catch((exception) => {
        if (active) {
          setError(exception instanceof Error ? exception.message : 'Could not load settings');
        }
      })
      .finally(() => {
        if (active) {
          setLoading(false);
        }
      });

    return () => {
      active = false;
    };
  }, []);

  function setDraftValue(key: string, value: string) {
    setDraftValues((current) => ({ ...current, [key]: value }));
  }

  async function handleSave(setting: AppSetting) {
    setSavingKey(setting.key);
    setError('');
    setNotice('');

    try {
      const updatedSetting = await updateSetting(setting.key, {
        value: draftValues[setting.key] ?? setting.value
      });
      setSettings((current) => current.map((item) => (item.key === updatedSetting.key ? updatedSetting : item)));
      setDraftValue(updatedSetting.key, updatedSetting.value);
      setNotice(`${updatedSetting.label} saved.`);
      notifyAppConfigChanged();
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : 'Could not save setting');
    } finally {
      setSavingKey('');
    }
  }

  function renderSettingControl(setting: AppSetting) {
    const draftValue = draftValues[setting.key] ?? setting.value;

    if (setting.valueType === 'BOOLEAN') {
      return (
        <label className="checkbox-field">
          <input
            type="checkbox"
            checked={draftValue === 'true'}
            onChange={(event) => setDraftValue(setting.key, String(event.target.checked))}
          />
          Enabled
        </label>
      );
    }

    return (
      <FormField label={setting.label} htmlFor={setting.key}>
        <TextInput
          id={setting.key}
          value={draftValue}
          maxLength={100}
          onChange={(event) => setDraftValue(setting.key, event.target.value)}
        />
      </FormField>
    );
  }

  return (
    <section className="page-section">
      <PageHeader
        title="Admin Settings"
        description="Manage basic non-sensitive application settings."
      />
      {error ? <ErrorMessage message={error} /> : null}
      {notice ? <p className="notice-message">{notice}</p> : null}
      {loading ? (
        <LoadingState />
      ) : (
        <div className="settings-list panel">
          {settings.map((setting) => {
            const draftValue = draftValues[setting.key] ?? setting.value;
            const unchanged = draftValue === setting.value;

            return (
              <article className="setting-row" key={setting.key}>
                <div className="setting-copy">
                  <h3>{setting.label}</h3>
                  <p>{setting.description}</p>
                  <code>{setting.key}</code>
                </div>
                <div className="setting-control">
                  {renderSettingControl(setting)}
                  <Button
                    type="button"
                    onClick={() => handleSave(setting)}
                    disabled={unchanged || savingKey === setting.key}
                  >
                    {savingKey === setting.key ? 'Saving...' : 'Save'}
                  </Button>
                </div>
              </article>
            );
          })}
        </div>
      )}
    </section>
  );
}
