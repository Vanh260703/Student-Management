import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Form, Input, Button, Card, Typography, message } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { authApi } from '../../api/auth.api';
import { useAuthStore } from '../../stores/authStore';
import { Role } from '../../types/auth.types';

const { Title, Text } = Typography;

interface LoginForm {
  email: string;
  password: string;
}

export default function LoginPage() {
  const navigate = useNavigate();
  const { setAuth } = useAuthStore();
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm<LoginForm>();

  const onFinish = async (values: LoginForm) => {
    setLoading(true);
    try {
      const result = await authApi.login(values.email, values.password);
      setAuth(result.accessToken, result.role, result.email);

      message.success('Đăng nhập thành công!');

      if (result.role === Role.ADMIN) {
        navigate('/admin/dashboard', { replace: true });
      } else if (result.role === Role.TEACHER) {
        navigate('/teacher/dashboard', { replace: true });
      } else {
        navigate('/student/dashboard', { replace: true });
      }
    } catch {
      // Errors handled by interceptor
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      className="flex items-center justify-center min-h-screen"
      style={{ background: 'linear-gradient(135deg, #1677ff 0%, #0050b3 100%)' }}
    >
      <Card style={{ width: 400, borderRadius: 12 }} bordered={false}>
        <div className="text-center mb-8">
          <div
            style={{
              width: 56,
              height: 56,
              borderRadius: 14,
              background: 'linear-gradient(135deg, #1677ff 0%, #0958d9 100%)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              margin: '0 auto 16px',
            }}
          >
            <span style={{ color: '#fff', fontWeight: 800, fontSize: 22 }}>Q</span>
          </div>
          <Title level={3} style={{ marginBottom: 4 }}>
            Quản Lý Sinh Viên
          </Title>
          <Text type="secondary">Đăng nhập để tiếp tục</Text>
        </div>

        <Form form={form} layout="vertical" onFinish={onFinish} size="large">
          <Form.Item
            name="email"
            rules={[
              { required: true, message: 'Vui lòng nhập email' },
              { type: 'email', message: 'Email không hợp lệ' },
            ]}
          >
            <Input
              prefix={<UserOutlined style={{ color: '#bfbfbf' }} />}
              placeholder="Email"
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[
              { required: true, message: 'Vui lòng nhập mật khẩu' },
              { min: 6, message: 'Mật khẩu ít nhất 6 ký tự' },
            ]}
          >
            <Input.Password
              prefix={<LockOutlined style={{ color: '#bfbfbf' }} />}
              placeholder="Mật khẩu"
            />
          </Form.Item>

          <div className="flex justify-end mb-4">
            <Button
              type="link"
              style={{ padding: 0, height: 'auto' }}
              onClick={() => navigate('/forgot-password')}
            >
              Quên mật khẩu?
            </Button>
          </div>

          <Form.Item style={{ marginBottom: 0 }}>
            <Button
              type="primary"
              htmlType="submit"
              block
              loading={loading}
              style={{ height: 44, fontWeight: 600 }}
            >
              Đăng nhập
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}
