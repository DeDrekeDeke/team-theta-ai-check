import { FormEvent, useEffect, useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { Button } from '../../components/Button';
import { ErrorMessage } from '../../components/ErrorMessage';
import { FormField, TextInput } from '../../components/FormField';
import { PageHeader } from '../../components/PageHeader';
import { compactErrors } from '../../lib/validation';
import { register } from './authApi';
import { consumeAuthMessage, getCurrentUser, saveCurrentUser } from './authStore';

function validateRegisterEmail(value: string) {
  const trimmed = value.trim();

  if (!trimmed) {
    return 'Email is required.';
  }

  if (trimmed.length > 100) {
    return 'Email must be 100 characters or fewer.';
  }

  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(trimmed)) {
    return 'Enter a valid email address.';
  }

  return '';
}

function validateDisplayName(value: string) {
  const trimmed = value.trim();

  if (!trimmed) {
    return 'Display name is required.';
  }

  if (trimmed.length > 100) {
    return 'Display name must be 100 characters or fewer.';
  }

  return '';
}

function validateRegisterPassword(value: string) {
  if (!value) {
    return 'Password is required.';
  }

  if (value.length < 6 || value.length > 100) {
    return 'Password must be between 6 and 100 characters.';
  }

  if (!/\S/.test(value)) {
    return 'Password must contain at least one non-whitespace character.';
  }

  return '';
}

export function RegisterPage() {
  const currentUser = getCurrentUser();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [displayName, setDisplayName] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const message = consumeAuthMessage();
    if (message) {
      setError(message);
    }
  }, []);

  if (currentUser) {
    return <Navigate to="/" replace />;
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const validationErrors = compactErrors([
      validateRegisterEmail(email),
      validateDisplayName(displayName),
      validateRegisterPassword(password)
    ]);

    if (validationErrors.length) {
      setError(validationErrors.join('\n'));
      return;
    }

    setLoading(true);
    setError('');

    try {
      const user = await register({
        email,
        displayName,
        password
      });
      saveCurrentUser(user);
      navigate('/', { replace: true });
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : 'Registration failed');
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="page-section narrow">
      <PageHeader title="Register" description="Create a regular user account." />
      <form className="form-stack" onSubmit={handleSubmit} noValidate>
        <FormField label="Email" htmlFor="register-email">
          <TextInput
            id="register-email"
            type="email"
            required
            maxLength={100}
            value={email}
            onChange={(event) => setEmail(event.target.value)}
          />
        </FormField>
        <FormField label="Display name" htmlFor="display-name">
          <TextInput
            id="display-name"
            type="text"
            required
            maxLength={100}
            value={displayName}
            onChange={(event) => setDisplayName(event.target.value)}
          />
        </FormField>
        <FormField label="Password" htmlFor="register-password">
          <TextInput
            id="register-password"
            type="password"
            required
            minLength={6}
            maxLength={100}
            value={password}
            onChange={(event) => setPassword(event.target.value)}
          />
        </FormField>
        {error ? <ErrorMessage message={error} /> : null}
        <div className="form-actions">
          <Button type="submit" disabled={loading}>
            {loading ? 'Creating account...' : 'Register'}
          </Button>
        </div>
      </form>
    </section>
  );
}
