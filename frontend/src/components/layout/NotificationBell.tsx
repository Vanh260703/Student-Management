import { useState, useEffect, useCallback } from 'react';
import { Badge, Drawer, List, Button, Tag, Typography, Space, Spin } from 'antd';
import { BellOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { axiosPrivate } from '../../api/axiosInstance';
import { NotificationResponse } from '../../types/notification.types';
import { NotificationType, NOTIFICATION_TYPE_LABEL } from '../../types/common.types';
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import 'dayjs/locale/vi';

dayjs.extend(relativeTime);
dayjs.locale('vi');

const { Text, Paragraph } = Typography;

const NOTIFICATION_TYPE_COLOR: Record<NotificationType, string> = {
  [NotificationType.GRADE]: 'blue',
  [NotificationType.SCHEDULE]: 'green',
  [NotificationType.PAYMENT]: 'gold',
  [NotificationType.SYSTEM]: 'default',
  [NotificationType.ATTENDANCE]: 'purple',
};

export default function NotificationBell() {
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);
  const [notifications, setNotifications] = useState<NotificationResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [markingAll, setMarkingAll] = useState(false);

  const fetchUnreadCount = useCallback(async () => {
    try {
      const res = await axiosPrivate.get('/api/v2/notifications/unread/count');
      setUnreadCount(res.data.result ?? 0);
    } catch {
      // silently ignore — user may not be authenticated yet
    }
  }, []);

  const fetchNotifications = useCallback(async () => {
    setLoading(true);
    try {
      const res = await axiosPrivate.get('/api/v2/notifications', {
        params: { isRead: false, page: 0, size: 20 },
      });
      const data = res.data.result;
      // Support both paginated and plain array responses
      const list: NotificationResponse[] = Array.isArray(data)
        ? data
        : Array.isArray(data?.content)
        ? data.content
        : [];
      setNotifications(list);
    } catch {
      setNotifications([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchUnreadCount();
    const interval = setInterval(fetchUnreadCount, 30_000);
    return () => clearInterval(interval);
  }, [fetchUnreadCount]);

  const handleOpen = () => {
    setOpen(true);
    fetchNotifications();
  };

  const handleMarkAsRead = async (id: number) => {
    try {
      await axiosPrivate.put(`/api/v2/notifications/${id}/mark-as-read`);
      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? { ...n, isRead: true } : n))
      );
      setUnreadCount((prev) => Math.max(0, prev - 1));
    } catch {
      // ignore
    }
  };

  const handleMarkAllAsRead = async () => {
    setMarkingAll(true);
    try {
      await axiosPrivate.put('/api/v2/notifications/mark-all-as-read');
      setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })));
      setUnreadCount(0);
    } catch {
      // ignore
    } finally {
      setMarkingAll(false);
    }
  };

  const handleViewAll = () => {
    setOpen(false);
    navigate('/student/notifications');
  };

  return (
    <>
      <Badge count={unreadCount} size="small" offset={[-2, 2]}>
        <Button
          type="text"
          icon={<BellOutlined style={{ fontSize: 20 }} />}
          onClick={handleOpen}
          aria-label="Thông báo"
        />
      </Badge>

      <Drawer
        title={
          <div className="flex items-center justify-between">
            <span className="font-semibold text-base">Thông báo</span>
            {unreadCount > 0 && (
              <Badge count={unreadCount} style={{ backgroundColor: '#1677ff' }} />
            )}
          </div>
        }
        placement="right"
        width={400}
        open={open}
        onClose={() => setOpen(false)}
        footer={
          <div className="flex items-center justify-between py-1">
            <Button
              type="link"
              loading={markingAll}
              disabled={unreadCount === 0}
              onClick={handleMarkAllAsRead}
              style={{ padding: 0 }}
            >
              Đánh dấu tất cả đã đọc
            </Button>
            <Button type="link" onClick={handleViewAll} style={{ padding: 0 }}>
              Xem tất cả →
            </Button>
          </div>
        }
      >
        {loading ? (
          <div className="flex justify-center items-center h-48">
            <Spin />
          </div>
        ) : notifications.length === 0 ? (
          <div className="flex justify-center items-center h-48 text-gray-400">
            Không có thông báo chưa đọc
          </div>
        ) : (
          <List
            dataSource={notifications}
            renderItem={(item) => (
              <List.Item
                key={item.id}
                style={{
                  background: item.isRead ? 'transparent' : '#f0f7ff',
                  borderRadius: 8,
                  marginBottom: 4,
                  padding: '10px 12px',
                  cursor: item.isRead ? 'default' : 'pointer',
                  transition: 'background 0.2s',
                }}
                onClick={() => !item.isRead && handleMarkAsRead(item.id)}
              >
                <Space direction="vertical" size={4} style={{ width: '100%' }}>
                  <div className="flex items-start justify-between gap-2">
                    <Text strong style={{ flex: 1, fontSize: 13 }}>
                      {item.title}
                    </Text>
                    <Tag
                      color={NOTIFICATION_TYPE_COLOR[item.type]}
                      style={{ fontSize: 11, marginRight: 0 }}
                    >
                      {NOTIFICATION_TYPE_LABEL[item.type]}
                    </Tag>
                  </div>
                  <Paragraph
                    ellipsis={{ rows: 2 }}
                    style={{ marginBottom: 0, fontSize: 12, color: '#555' }}
                  >
                    {item.content}
                  </Paragraph>
                  <Text type="secondary" style={{ fontSize: 11 }}>
                    {dayjs(item.createdAt).fromNow()}
                  </Text>
                </Space>
              </List.Item>
            )}
          />
        )}
      </Drawer>
    </>
  );
}
