import { useEffect, useState } from 'react';
import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { AUTH_CHANGED_EVENT, getCurrentUser, logout } from '../features/auth/authStore';
import { logoutRequest } from '../features/auth/authApi';

type NavItem = {
  to: string;
  label: string;
};

const navItems: NavItem[] = [
  { to: '/', label: 'CVs' },
  { to: '/upload', label: 'Upload' },
  { to: '/admin/users', label: 'Users' },
  { to: '/admin/settings', label: 'Settings' }
];

function isAdminRoute(path: string) {
  return path === '/admin' || path.startsWith('/admin/');
}

export function App() {
  const location = useLocation();
  const navigate = useNavigate();
  const [user, setUser] = useState(getCurrentUser());
  const isLoginPage = location.pathname === '/login';
  const visibleNavItems = isLoginPage
    ? []
    : navItems.filter((item) => !isAdminRoute(item.to) || user?.admin);

  useEffect(() => {
    function handleAuthChanged() {
      setUser(getCurrentUser());
    }

    window.addEventListener(AUTH_CHANGED_EVENT, handleAuthChanged);
    window.addEventListener('storage', handleAuthChanged);

    return () => {
      window.removeEventListener(AUTH_CHANGED_EVENT, handleAuthChanged);
      window.removeEventListener('storage', handleAuthChanged);
    };
  }, []);

  async function handleLogout() {
    try {
      await logoutRequest();
    } catch {
      // Clear local auth state even if the token is already expired.
    } finally {
      logout();
      navigate('/login', { replace: true });
    }
  }

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div>
          <p className="eyebrow">Summer School</p>
          <h1>CV Manager</h1>
        </div>

        <nav className="nav-list" aria-label="Main navigation">
          {visibleNavItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
            >
              {item.label}
            </NavLink>
          ))}
        </nav>

        <div className="sidebar-footer">
          {isLoginPage ? (
            <NavLink to="/login" className="nav-link">
              Log in
            </NavLink>
          ) : user ? (
            <>
              <span>{user.email}</span>
              <button className="link-button" type="button" onClick={handleLogout}>
                Log out
              </button>
            </>
          ) : (
            <NavLink to="/login" className="nav-link">
              Log in
            </NavLink>
          )}
        </div>
      </aside>

      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}
