import { useState, useEffect } from 'react';
import { notificationApi } from '../api/notification.api';

export const useNotification = () => {
  const [unreadCount, setUnreadCount] = useState<number>(0);

  useEffect(() => {
    const fetchUnreadCount = () => {
      notificationApi
        .getUnreadCount()
        .then((count) => setUnreadCount(count))
        .catch(() => {
          // Silently fail — do not disrupt the UI
        });
    };

    fetchUnreadCount();

    const interval = setInterval(fetchUnreadCount, 30000);

    return () => clearInterval(interval);
  }, []);

  const refresh = () => {
    notificationApi
      .getUnreadCount()
      .then((count) => setUnreadCount(count))
      .catch(() => {});
  };

  return { unreadCount, refresh };
};
