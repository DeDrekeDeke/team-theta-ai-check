<<<<<<< HEAD
import { PropsWithChildren, useEffect, useState } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { AUTH_CHANGED_EVENT, getCurrentUser } from '../authStore';
=======
import { ReactNode } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { getCurrentUser, isAdminUser } from '../authStore';
>>>>>>> dd5e38eb875017553478591173c3399509703e21

type ProtectedRouteProps = PropsWithChildren<{
  requireAdmin?: boolean;
}>;

export function ProtectedRoute({ children, requireAdmin = false }: ProtectedRouteProps) {
  const location = useLocation();
<<<<<<< HEAD
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

=======
  const user = getCurrentUser();

>>>>>>> dd5e38eb875017553478591173c3399509703e21
  if (!user) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  }

<<<<<<< HEAD
  if (requireAdmin && !user.admin) {
=======
  if (requireAdmin && !isAdminUser(user)) {
>>>>>>> dd5e38eb875017553478591173c3399509703e21
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
}
