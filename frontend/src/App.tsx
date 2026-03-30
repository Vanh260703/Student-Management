import { useEffect, useState } from 'react';
import { BrowserRouter } from 'react-router-dom';
import { axiosPublic } from './api/axiosInstance';
import { useAuthStore } from './stores/authStore';
import AppRouter from './router';
import LoadingScreen from './components/common/LoadingScreen';

export default function App() {
  const [isInitializing, setIsInitializing] = useState(true);
  const { setAuth, setInitialized } = useAuthStore();

  useEffect(() => {
    axiosPublic
      .post('/api/v2/auth/refresh')
      .then((res) => {
        const { accessToken, role, email } = res.data.result;
        setAuth(accessToken, role, email);
      })
      .catch(() => {
        // No valid session — router will redirect to /login
      })
      .finally(() => {
        setInitialized();
        setIsInitializing(false);
      });
  }, []);

  if (isInitializing) return <LoadingScreen />;

  return (
    <BrowserRouter>
      <AppRouter />
    </BrowserRouter>
  );
}
