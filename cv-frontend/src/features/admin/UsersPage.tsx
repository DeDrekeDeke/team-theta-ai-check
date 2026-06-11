import { FormEvent, useEffect, useState } from 'react';
import { Button } from '../../components/Button';
import { ErrorMessage } from '../../components/ErrorMessage';
import { FormField, TextInput } from '../../components/FormField';
import { LoadingState } from '../../components/LoadingState';
import { PageHeader } from '../../components/PageHeader';
import { formatDateTime } from '../../lib/formatters';
import { getCurrentUser } from '../auth/authStore';
import { AdminUser, createUser, listUsers, updateUser } from './adminApi';

const emptyForm = {
  email: '',
  displayName: '',
  password: ''
};

const emptyEditForm = {
  email: '',
  displayName: '',
  password: '',
  role: 'USER' as 'USER' | 'ADMIN'
};

export function UsersPage() {
  const currentUser = getCurrentUser();
  const [users, setUsers] = useState<AdminUser[]>([]);
  const [form, setForm] = useState(emptyForm);
  const [editingUser, setEditingUser] = useState<AdminUser | null>(null);
  const [editForm, setEditForm] = useState(emptyEditForm);
  const [loading, setLoading] = useState(true);
  const [creating, setCreating] = useState(false);
  const [savingEdit, setSavingEdit] = useState(false);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const editingOwnAccount = Boolean(editingUser && currentUser?.userId === editingUser.id);

  useEffect(() => {
    listUsers()
      .then(setUsers)
      .catch((exception) => setError(exception instanceof Error ? exception.message : 'Could not load users'))
      .finally(() => setLoading(false));
  }, []);

  async function handleCreate(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError('');
    setNotice('');

    if (form.password.length < 8) {
      setError('Password must be at least 8 characters.');
      return;
    }

    setCreating(true);
    try {
      const created = await createUser(form);
      setUsers((current) => [...current, created].sort((a, b) => a.id - b.id));
      setForm(emptyForm);
      setNotice(`${created.email} was created.`);
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : 'Could not create user');
    } finally {
      setCreating(false);
    }
  }

  function openEditModal(user: AdminUser) {
    setError('');
    setNotice('');
    setEditingUser(user);
    setEditForm({
      email: user.email,
      displayName: user.displayName,
      password: '',
      role: user.role
    });
  }

  function closeEditModal() {
    if (savingEdit) {
      return;
    }

    setEditingUser(null);
    setEditForm(emptyEditForm);
  }

  async function handleEditSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!editingUser) {
      return;
    }

    setError('');
    setNotice('');

    if (editForm.password && editForm.password.length < 8) {
      setError('Password must be at least 8 characters.');
      return;
    }

    setSavingEdit(true);
    try {
      const updated = await updateUser(editingUser.id, {
        email: editForm.email,
        displayName: editForm.displayName,
        password: editForm.password || undefined,
        role: editForm.role
      });
      setUsers((current) => current.map((item) => (item.id === updated.id ? updated : item)));
      setNotice(`${updated.email} was updated.`);
      setEditingUser(null);
      setEditForm(emptyEditForm);
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : 'Could not update user');
    } finally {
      setSavingEdit(false);
    }
  }

  return (
    <section className="page-section">
      <PageHeader title="Users" description="Create users and manage basic admin access." />

      {error ? <ErrorMessage message={error} /> : null}
      {notice ? <p className="notice-message">{notice}</p> : null}

      <form className="panel form-stack" onSubmit={handleCreate}>
        <div className="form-grid">
          <FormField label="Email" htmlFor="new-user-email">
            <TextInput
              id="new-user-email"
              type="email"
              value={form.email}
              onChange={(event) => setForm((current) => ({ ...current, email: event.target.value }))}
              required
            />
          </FormField>
          <FormField label="Display name" htmlFor="new-user-display-name">
            <TextInput
              id="new-user-display-name"
              value={form.displayName}
              onChange={(event) => setForm((current) => ({ ...current, displayName: event.target.value }))}
              required
            />
          </FormField>
          <FormField label="Password" htmlFor="new-user-password">
            <TextInput
              id="new-user-password"
              type="password"
              value={form.password}
              onChange={(event) => setForm((current) => ({ ...current, password: event.target.value }))}
              minLength={8}
              required
            />
          </FormField>
        </div>
        <div className="inline-actions end">
          <Button type="submit" disabled={creating}>
            {creating ? 'Creating...' : 'Create user'}
          </Button>
        </div>
      </form>

      {loading ? (
        <LoadingState />
      ) : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Role</th>
                <th>Created</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user) => (
                <tr key={user.id}>
                  <td>{user.displayName}</td>
                  <td>{user.email}</td>
                  <td>
                    <span className={user.role === 'ADMIN' ? 'role-badge admin' : 'role-badge'}>
                      {user.role === 'ADMIN' ? 'Admin' : 'User'}
                    </span>
                  </td>
                  <td>{formatDateTime(user.createdAt)}</td>
                  <td>
                    <div className="row-actions">
                      <Button type="button" variant="secondary" onClick={() => openEditModal(user)}>
                        Edit
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {editingUser ? (
        <div className="modal-backdrop" role="presentation" onClick={closeEditModal}>
          <div
            className="modal-panel"
            role="dialog"
            aria-modal="true"
            aria-labelledby="edit-user-title"
            onClick={(event) => event.stopPropagation()}
          >
            <form className="form-stack" onSubmit={handleEditSubmit}>
              <div className="panel-header">
                <h3 id="edit-user-title">Edit user</h3>
              </div>
              <FormField label="Email" htmlFor="edit-user-email">
                <TextInput
                  id="edit-user-email"
                  type="email"
                  value={editForm.email}
                  onChange={(event) => setEditForm((current) => ({ ...current, email: event.target.value }))}
                  required
                />
              </FormField>
              <FormField label="Display name" htmlFor="edit-user-display-name">
                <TextInput
                  id="edit-user-display-name"
                  value={editForm.displayName}
                  onChange={(event) => setEditForm((current) => ({ ...current, displayName: event.target.value }))}
                  required
                />
              </FormField>
              <FormField label="New password" htmlFor="edit-user-password">
                <TextInput
                  id="edit-user-password"
                  type="password"
                  value={editForm.password}
                  onChange={(event) => setEditForm((current) => ({ ...current, password: event.target.value }))}
                  minLength={8}
                />
              </FormField>
              <FormField label="Role" htmlFor="edit-user-role">
                <select
                  id="edit-user-role"
                  className="text-input"
                  value={editForm.role}
                  disabled={editingOwnAccount}
                  onChange={(event) =>
                    setEditForm((current) => ({ ...current, role: event.target.value as 'USER' | 'ADMIN' }))
                  }
                >
                  <option value="USER">User</option>
                  <option value="ADMIN">Admin</option>
                </select>
              </FormField>
              <div className="inline-actions end">
                <Button type="button" variant="secondary" disabled={savingEdit} onClick={closeEditModal}>
                  Cancel
                </Button>
                <Button type="submit" disabled={savingEdit}>
                  {savingEdit ? 'Saving...' : 'Save'}
                </Button>
              </div>
            </form>
          </div>
        </div>
      ) : null}
    </section>
  );
}
