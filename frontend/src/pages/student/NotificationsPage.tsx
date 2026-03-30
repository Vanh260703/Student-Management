import { useEffect, useState } from 'react';
import { Card, List, Tag, Button, Spin, Typography, Space, Empty, message } from 'antd';
import { BellOutlined, DeleteOutlined } from '@ant-design/icons';
import { notificationApi } from '../../api/notification.api';
import { NotificationResponse } from '../../types/notification.types';
import { NOTIFICATION_TYPE_LABEL, formatDateTime } from '../../types/common.types';

const { Title, Text, Paragraph } = Typography;

export default function NotificationsPage() {
  const [notifications, setNotifications] = useState<NotificationResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [markingAll, setMarkingAll] = useState(false);

  const fetchAll = () => {
    setLoading(true);
    notificationApi
      .getAll()
      .then(setNotifications)
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchAll();
  }, []);

  const handleMarkAsRead = async (id: number) => {
    try {
      const updated = await notificationApi.markAsRead(id);
      setNotifications((prev) => prev.map((n) => (n.id === id ? updated : n)));
    } catch {
      // interceptor handles
    }
  };

  const handleMarkAllAsRead = async () => {
    setMarkingAll(true);
    try {
      await notificationApi.markAllAsRead();
      setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })));
      message.success('Đã đánh dấu tất cả đã đọc!');
    } catch {
      // interceptor handles
    } finally {
      setMarkingAll(false);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await notificationApi.deleteNotification(id);
      setNotifications((prev) => prev.filter((n) => n.id !== id));
      message.success('Đã xoá thông báo!');
    } catch {
      // interceptor handles
    }
  };

  const unreadCount = notifications.filter((n) => !n.isRead).length;

  if (loading) {
    return (
      <div className="flex justify-center items-center" style={{ minHeight: 300 }}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <Title level={4} style={{ marginBottom: 0 }}>
          Thông báo{unreadCount > 0 && <span className="ml-2 text-base text-blue-500">({unreadCount} chưa đọc)</span>}
        </Title>
        {unreadCount > 0 && (
          <Button loading={markingAll} onClick={handleMarkAllAsRead}>
            Đánh dấu tất cả đã đọc
          </Button>
        )}
      </div>

      <Card>
        {notifications.length === 0 ? (
          <Empty
            image={<BellOutlined style={{ fontSize: 48, color: '#d9d9d9' }} />}
            description="Không có thông báo"
          />
        ) : (
          <List
            dataSource={notifications}
            renderItem={(item) => (
              <List.Item
                key={item.id}
                style={{
                  background: item.isRead ? 'transparent' : '#f0f7ff',
                  borderRadius: 8,
                  padding: '12px 16px',
                  marginBottom: 4,
                }}
                actions={[
                  !item.isRead && (
                    <Button
                      key="read"
                      type="link"
                      size="small"
                      onClick={() => handleMarkAsRead(item.id)}
                    >
                      Đánh dấu đã đọc
                    </Button>
                  ),
                  <Button
                    key="delete"
                    type="text"
                    danger
                    icon={<DeleteOutlined />}
                    size="small"
                    onClick={() => handleDelete(item.id)}
                  />,
                ].filter(Boolean)}
              >
                <List.Item.Meta
                  title={
                    <Space>
                      <Text strong={!item.isRead}>{item.title}</Text>
                      <Tag>{NOTIFICATION_TYPE_LABEL[item.type]}</Tag>
                      {!item.isRead && <Tag color="blue">Mới</Tag>}
                    </Space>
                  }
                  description={
                    <div>
                      <Paragraph ellipsis={{ rows: 2 }} style={{ marginBottom: 4 }}>
                        {item.content}
                      </Paragraph>
                      <Text type="secondary" style={{ fontSize: 12 }}>
                        {formatDateTime(item.createdAt)}
                      </Text>
                    </div>
                  }
                />
              </List.Item>
            )}
          />
        )}
      </Card>
    </div>
  );
}
