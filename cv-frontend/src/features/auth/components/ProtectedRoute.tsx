import { ReactNode } from 'react';
import { Navigate } from 'react-router-dom';
import { getCurrentUser } from '../authStore';

type ProtectedRouteProps = {
  children: ReactNode;
  requireAdmin?: boolean;
};

export function ProtectedRoute({ children, requireAdmin = false }: ProtectedRouteProps) {
  const user = getCurrentUser();

  if (requireAdmin && !user?.admin) {
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
}
