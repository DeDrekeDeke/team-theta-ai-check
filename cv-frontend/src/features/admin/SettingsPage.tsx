import { useEffect, useState } from 'react';
import { ErrorMessage } from '../../components/ErrorMessage';
import { LoadingState } from '../../components/LoadingState';
import { PageHeader } from '../../components/PageHeader';
import { AppSetting, listSettings } from './adminApi';

export function SettingsPage() {
  const [settings, setSettings] = useState<AppSetting[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    listSettings()
      .then(setSettings)
      .catch((exception) => setError(exception instanceof Error ? exception.message : 'Could not load settings'))
      .finally(() => setLoading(false));
  }, []);

  return (
    <section className="page-section">
      <PageHeader
        title="Admin Settings"
        description="Simple configuration-style view for the starter backend."
      />
      {error ? <ErrorMessage message={error} /> : null}
      {loading ? (
        <LoadingState />
      ) : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Key</th>
                <th>Value</th>
                <th>Description</th>
              </tr>
            </thead>
            <tbody>
              {settings.map((setting) => (
                <tr key={setting.key}>
                  <td>{setting.key}</td>
                  <td>{setting.value}</td>
                  <td>{setting.description}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}
