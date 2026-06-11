import { createBrowserRouter } from 'react-router-dom';
import { App } from './App';
import { ProtectedRoute } from '../features/auth/components/ProtectedRoute';
import { LoginPage } from '../features/auth/LoginPage';
import { CvDetailPage } from '../features/cv/CvDetailPage';
import { CvListPage } from '../features/cv/CvListPage';
import { CvUploadPage } from '../features/cv/CvUploadPage';
import { SettingsPage } from '../features/admin/SettingsPage';
import { UsersPage } from '../features/admin/UsersPage';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    children: [
      { index: true, element: <ProtectedRoute><CvListPage /></ProtectedRoute> },
      { path: 'login', element: <LoginPage /> },
      { path: 'upload', element: <ProtectedRoute><CvUploadPage /></ProtectedRoute> },
      { path: 'cvs/:id', element: <ProtectedRoute><CvDetailPage /></ProtectedRoute> },
      { path: 'admin/users', element: <ProtectedRoute requireAdmin><UsersPage /></ProtectedRoute>},
      { path: 'admin/settings', element: <ProtectedRoute requireAdmin><SettingsPage /></ProtectedRoute> }
    ]
  }
]);
