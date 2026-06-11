import { FormEvent, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Button } from '../../components/Button';
import { ErrorMessage } from '../../components/ErrorMessage';
import { LoadingState } from '../../components/LoadingState';
import { PageHeader } from '../../components/PageHeader';
import { CvTable } from './components/CvTable';
import { Cv, listCvs, searchCvs } from './cvApi';

export function CvListPage() {
  const [cvs, setCvs] = useState<Cv[]>([]);
  const [query, setQuery] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadCvs();
  }, []);

  async function loadCvs() {
    setLoading(true);
    setError('');
    try {
      setCvs(await listCvs());
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : 'Could not load CVs');
    } finally {
      setLoading(false);
    }
  }

  async function handleSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setLoading(true);
    setError('');
    try {
      setCvs(query.trim() ? await searchCvs(query) : await listCvs());
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : 'Search failed');
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="page-section">
      <PageHeader
        title="CVs"
        description="Structured CVs available to the current user."
        actions={<Link className="button primary" to="/create">Create CV</Link>}
      />

      <form className="toolbar" onSubmit={handleSearch}>
        <input
          className="text-input"
          placeholder="Search title, owner, or summary"
          value={query}
          onChange={(event) => setQuery(event.target.value)}
        />
        <Button type="submit" variant="secondary">
          Search
        </Button>
      </form>

      {error ? <ErrorMessage message={error} /> : null}
      {loading ? <LoadingState /> : <CvTable cvs={cvs} />}
    </section>
  );
}
