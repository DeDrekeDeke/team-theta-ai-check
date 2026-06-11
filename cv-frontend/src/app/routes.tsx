import { createBrowserRouter } from 'react-router-dom';
import { App } from './App';
import { ProtectedRoute } from '../features/auth/components/ProtectedRoute';
import { LoginPage } from '../features/auth/LoginPage';
import { CvCreatePage } from '../features/cv/CvCreatePage';
import { CvDetailPage } from '../features/cv/CvDetailPage';
import { CvEditPage } from '../features/cv/CvEditPage';
import { CvListPage } from '../features/cv/CvListPage';
import { SettingsPage } from '../features/admin/SettingsPage';
import { UsersPage } from '../features/admin/UsersPage';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    children: [
      { index: true, element: <ProtectedRoute><CvListPage /></ProtectedRoute> },
      { path: 'login', element: <LoginPage /> },
      { path: 'create', element: <ProtectedRoute><CvCreatePage /></ProtectedRoute> },
      { path: 'cvs/:id/edit', element: <ProtectedRoute><CvEditPage /></ProtectedRoute> },
      { path: 'cvs/:id', element: <ProtectedRoute><CvDetailPage /></ProtectedRoute> },
      {
        path: 'admin/users',
        element: (
          <ProtectedRoute requireAdmin>
            <UsersPage />
          </ProtectedRoute>
        )
      },
      {
        path: 'admin/settings',
        element: (
          <ProtectedRoute requireAdmin>
            <SettingsPage />
          </ProtectedRoute>
        )
      }
    ]
  }
]);
