import { PropsWithChildren, useEffect, useState } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { AUTH_CHANGED_EVENT, getCurrentUser } from './authStore';

type ProtectedRouteProps = PropsWithChildren<{
  requireAdmin?: boolean;
}>;

export function ProtectedRoute({ children, requireAdmin = false }: ProtectedRouteProps) {
  const location = useLocation();
  const [user, setUser] = useState(getCurrentUser());

  useEffect(() => {
    function handleAuthChanged() {
      setUser(getCurrentUser());
    }

    window.addEventListener(AUTH_CHANGED_EVENT, handleAuthChanged);

    return () => {
      window.removeEventListener(AUTH_CHANGED_EVENT, handleAuthChanged);
    };
  }, []);

  if (!user) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  }

  if (requireAdmin && !user.admin) {
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
}
