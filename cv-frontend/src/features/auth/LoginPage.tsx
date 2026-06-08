import { FormEvent, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '../../components/Button';
import { ErrorMessage } from '../../components/ErrorMessage';
import { FormField, TextInput } from '../../components/FormField';
import { PageHeader } from '../../components/PageHeader';
import { login } from './authApi';
import { saveCurrentUser } from './authStore';

export function LoginPage() {
  const navigate = useNavigate();
  const [email, setEmail] = useState('alice@example.com');
  const [password, setPassword] = useState('user123');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setLoading(true);
    setError('');

    try {
      const user = await login({ email, password });
      saveCurrentUser(user);
      navigate('/');
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : 'Login failed');
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="page-section narrow">
      <PageHeader title="Log in" description="Use one of the seeded demo users." />
      <form className="form-stack" onSubmit={handleSubmit}>
        <FormField label="Email" htmlFor="email">
          <TextInput id="email" value={email} onChange={(event) => setEmail(event.target.value)} />
        </FormField>
        <FormField label="Password" htmlFor="password">
          <TextInput
            id="password"
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
          />
        </FormField>
        {error ? <ErrorMessage message={error} /> : null}
        <Button type="submit" disabled={loading}>
          {loading ? 'Logging in...' : 'Log in'}
        </Button>
      </form>
    </section>
  );
}
