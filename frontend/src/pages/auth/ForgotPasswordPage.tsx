import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Form, Input, Button, Card, Typography, Result, message } from 'antd';
import { MailOutlined, ArrowLeftOutlined } from '@ant-design/icons';
import { authApi } from '../../api/auth.api';

const { Title, Text } = Typography;

interface ForgotForm {
  email: string;
}

export default function ForgotPasswordPage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [form] = Form.useForm<ForgotForm>();

  const onFinish = async (values: ForgotForm) => {
    setLoading(true);
    try {
      await authApi.forgotPassword(values.email);
      setSubmitted(true);
      message.success('Email khôi phục đã được gửi!');
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
        {submitted ? (
          <Result
            status="success"
            title="Email đã được gửi!"
            subTitle="Vui lòng kiểm tra hộp thư của bạn và làm theo hướng dẫn để đặt lại mật khẩu."
            extra={
              <Button
                type="primary"
                onClick={() => navigate('/login')}
                icon={<ArrowLeftOutlined />}
              >
                Quay về đăng nhập
              </Button>
            }
          />
        ) : (
          <>
            <div className="mb-6">
              <Button
                type="text"
                icon={<ArrowLeftOutlined />}
                onClick={() => navigate('/login')}
                style={{ padding: 0, marginBottom: 12 }}
              >
                Quay lại
              </Button>
              <Title level={3} style={{ marginBottom: 4 }}>
                Quên mật khẩu
              </Title>
              <Text type="secondary">
                Nhập email của bạn. Chúng tôi sẽ gửi link đặt lại mật khẩu.
              </Text>
            </div>

            <Form form={form} layout="vertical" onFinish={onFinish} size="large">
              <Form.Item
                name="email"
                label="Email"
                rules={[
                  { required: true, message: 'Vui lòng nhập email' },
                  { type: 'email', message: 'Email không hợp lệ' },
                ]}
              >
                <Input
                  prefix={<MailOutlined style={{ color: '#bfbfbf' }} />}
                  placeholder="Nhập email của bạn"
                />
              </Form.Item>

              <Form.Item style={{ marginBottom: 0 }}>
                <Button
                  type="primary"
                  htmlType="submit"
                  block
                  loading={loading}
                  style={{ height: 44, fontWeight: 600 }}
                >
                  Gửi email khôi phục
                </Button>
              </Form.Item>
            </Form>
          </>
        )}
      </Card>
    </div>
  );
}
