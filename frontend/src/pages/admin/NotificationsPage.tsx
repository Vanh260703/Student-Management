import { useState } from 'react';
import {
  Card,
  Form,
  Input,
  Select,
  Button,
  message,
  Typography,
  Tabs,
  InputNumber,
} from 'antd';
import { adminApi } from '../../api/admin.api';
import { BroadcastNotificationRequest, SendNotificationRequest } from '../../types/notification.types';
import { NotificationType, NOTIFICATION_TYPE_LABEL } from '../../types/common.types';
import { Role } from '../../types/auth.types';

const { Title, Text } = Typography;
const { TextArea } = Input;

export default function AdminNotificationsPage() {
  const [broadcastForm] = Form.useForm<BroadcastNotificationRequest>();
  const [sendForm] = Form.useForm<SendNotificationRequest>();
  const [sending, setSending] = useState(false);

  const handleBroadcast = async (values: BroadcastNotificationRequest) => {
    setSending(true);
    try {
      const count = await adminApi.broadcastNotification(values);
      message.success(`Đã gửi thông báo tới ${count} người dùng!`);
      broadcastForm.resetFields();
    } catch {
      // interceptor handles
    } finally {
      setSending(false);
    }
  };

  const handleSend = async (values: SendNotificationRequest) => {
    setSending(true);
    try {
      const count = await adminApi.sendNotification(values);
      message.success(`Đã gửi thông báo tới ${count} người dùng!`);
      sendForm.resetFields();
    } catch {
      // interceptor handles
    } finally {
      setSending(false);
    }
  };

  const commonFields = (form: typeof broadcastForm) => (
    <>
      <Form.Item
        label="Tiêu đề"
        name="title"
        rules={[{ required: true, message: 'Vui lòng nhập tiêu đề' }]}
      >
        <Input placeholder="Nhập tiêu đề thông báo" />
      </Form.Item>
      <Form.Item
        label="Nội dung"
        name="content"
        rules={[{ required: true, message: 'Vui lòng nhập nội dung' }]}
      >
        <TextArea rows={4} placeholder="Nhập nội dung thông báo" />
      </Form.Item>
      <Form.Item
        label="Loại thông báo"
        name="type"
        rules={[{ required: true, message: 'Vui lòng chọn loại' }]}
      >
        <Select placeholder="Chọn loại thông báo">
          {Object.values(NotificationType).map((t) => (
            <Select.Option key={t} value={t}>
              {NOTIFICATION_TYPE_LABEL[t]}
            </Select.Option>
          ))}
        </Select>
      </Form.Item>
    </>
  );

  return (
    <div>
      <Title level={4} style={{ marginBottom: 16 }}>
        Gửi thông báo
      </Title>

      <Tabs
        defaultActiveKey="broadcast"
        items={[
          {
            key: 'broadcast',
            label: 'Gửi theo vai trò',
            children: (
              <Card>
                <Text type="secondary" className="block mb-4">
                  Gửi thông báo đến tất cả người dùng theo vai trò được chọn.
                </Text>
                <Form
                  form={broadcastForm}
                  layout="vertical"
                  onFinish={handleBroadcast}
                  style={{ maxWidth: 600 }}
                >
                  {commonFields(broadcastForm)}
                  <Form.Item
                    label="Gửi đến vai trò"
                    name="targetRoles"
                    rules={[{ required: true, message: 'Vui lòng chọn ít nhất một vai trò' }]}
                  >
                    <Select mode="multiple" placeholder="Chọn vai trò">
                      <Select.Option value={Role.STUDENT}>Sinh viên</Select.Option>
                      <Select.Option value={Role.TEACHER}>Giáo viên</Select.Option>
                      <Select.Option value={Role.ADMIN}>Quản trị viên</Select.Option>
                    </Select>
                  </Form.Item>
                  <Form.Item style={{ marginBottom: 0 }}>
                    <Button type="primary" htmlType="submit" loading={sending}>
                      Gửi thông báo
                    </Button>
                  </Form.Item>
                </Form>
              </Card>
            ),
          },
          {
            key: 'send',
            label: 'Gửi theo ID người dùng',
            children: (
              <Card>
                <Text type="secondary" className="block mb-4">
                  Gửi thông báo đến người dùng cụ thể theo ID.
                </Text>
                <Form
                  form={sendForm}
                  layout="vertical"
                  onFinish={handleSend}
                  style={{ maxWidth: 600 }}
                >
                  {commonFields(sendForm)}
                  <Form.Item
                    label="ID người dùng (cách nhau bằng dấu phẩy)"
                    name="targetUserIds"
                    rules={[{ required: true, message: 'Vui lòng nhập ID người dùng' }]}
                  >
                    <Select
                      mode="tags"
                      placeholder="Nhập ID người dùng, VD: 1, 2, 3"
                      tokenSeparators={[',']}
                    />
                  </Form.Item>
                  <Form.Item style={{ marginBottom: 0 }}>
                    <Button type="primary" htmlType="submit" loading={sending}>
                      Gửi thông báo
                    </Button>
                  </Form.Item>
                </Form>
              </Card>
            ),
          },
        ]}
      />
    </div>
  );
}
