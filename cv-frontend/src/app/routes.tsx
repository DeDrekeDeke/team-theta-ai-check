import { createBrowserRouter } from 'react-router-dom';
import { App } from './App';
import { LoginPage } from '../features/auth/LoginPage';
import { ProtectedRoute } from '../features/auth/ProtectedRoute';
import { CvDetailPage } from '../features/cv/CvDetailPage';
import { CvListPage } from '../features/cv/CvListPage';
import { CvUploadPage } from '../features/cv/CvUploadPage';
import { SettingsPage } from '../features/admin/SettingsPage';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    children: [
      { index: true, element: <ProtectedRoute><CvListPage /></ProtectedRoute> },
      { path: 'login', element: <LoginPage /> },
      { path: 'upload', element: <ProtectedRoute><CvUploadPage /></ProtectedRoute> },
      { path: 'cvs/:id', element: <ProtectedRoute><CvDetailPage /></ProtectedRoute> },
      { path: 'admin/settings', element: <ProtectedRoute requireAdmin><SettingsPage /></ProtectedRoute> }
    ]
  }
]);
