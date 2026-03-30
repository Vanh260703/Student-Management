import { useEffect, useState } from 'react';
import {
  Card,
  Avatar,
  Form,
  Input,
  Select,
  DatePicker,
  Button,
  Row,
  Col,
  Spin,
  Tag,
  message,
  Typography,
} from 'antd';
import { UserOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { teacherApi } from '../../api/teacher.api';
import { TeacherProfile, UpdateTeacherProfileRequest } from '../../types/teacher.types';
import { Gender, GENDER_LABEL, formatDate } from '../../types/common.types';

const { Title, Text } = Typography;

export default function TeacherProfilePage() {
  const [profile, setProfile] = useState<TeacherProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [form] = Form.useForm<UpdateTeacherProfileRequest>();

  useEffect(() => {
    teacherApi
      .getProfile()
      .then((data) => {
        setProfile(data);
        form.setFieldsValue({
          fullName: data.fullName,
          phone: data.phone,
          personalEmail: data.personalEmail,
          dayOfBirth: data.dateOfBirth,
          gender: data.gender,
        });
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [form]);

  const onFinish = async (values: UpdateTeacherProfileRequest) => {
    setSaving(true);
    try {
      const updated = await teacherApi.updateProfile({
        ...values,
        dayOfBirth:
          values.dayOfBirth && dayjs.isDayjs(values.dayOfBirth)
            ? (values.dayOfBirth as unknown as dayjs.Dayjs).format('YYYY-MM-DD')
            : values.dayOfBirth,
      });
      setProfile(updated);
      message.success('Cập nhật hồ sơ thành công!');
    } catch {
      // interceptor handles
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center" style={{ minHeight: 300 }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!profile) return null;

  return (
    <Row gutter={[24, 24]}>
      <Col xs={24} lg={8}>
        <Card>
          <div className="text-center mb-6">
            <Avatar
              size={100}
              src={profile.avatarUrl}
              icon={<UserOutlined />}
              style={{ background: '#1677ff' }}
            />
            <Title level={5} style={{ marginTop: 12, marginBottom: 4 }}>
              {profile.fullName}
            </Title>
            <Text type="secondary" style={{ display: 'block' }}>
              {profile.teacherCode}
            </Text>
            <Tag color={profile.isActive ? 'green' : 'red'} style={{ marginTop: 8 }}>
              {profile.isActive ? 'Đang hoạt động' : 'Không hoạt động'}
            </Tag>
          </div>

          <div className="space-y-3">
            {[
              { label: 'Email trường', value: profile.email },
              { label: 'Khoa', value: profile.department },
              { label: 'Học vị', value: profile.degree },
              { label: 'Ngày vào làm', value: profile.joinedDate ? formatDate(profile.joinedDate) : '—' },
            ].map(({ label, value }) => (
              <div key={label} className="flex justify-between">
                <Text type="secondary">{label}:</Text>
                <Text strong>{value ?? '—'}</Text>
              </div>
            ))}
          </div>
        </Card>
      </Col>

      <Col xs={24} lg={16}>
        <Card title="Chỉnh sửa hồ sơ">
          <Form form={form} layout="vertical" onFinish={onFinish}>
            <Row gutter={16}>
              <Col xs={24} md={12}>
                <Form.Item
                  label="Họ và tên"
                  name="fullName"
                  rules={[{ required: true, message: 'Vui lòng nhập họ tên' }]}
                >
                  <Input />
                </Form.Item>
              </Col>
              <Col xs={24} md={12}>
                <Form.Item label="Số điện thoại" name="phone">
                  <Input />
                </Form.Item>
              </Col>
              <Col xs={24} md={12}>
                <Form.Item
                  label="Email cá nhân"
                  name="personalEmail"
                  rules={[{ type: 'email', message: 'Email không hợp lệ' }]}
                >
                  <Input />
                </Form.Item>
              </Col>
              <Col xs={24} md={12}>
                <Form.Item label="Giới tính" name="gender">
                  <Select placeholder="Chọn giới tính">
                    {Object.values(Gender).map((g) => (
                      <Select.Option key={g} value={g}>
                        {GENDER_LABEL[g]}
                      </Select.Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} md={12}>
                <Form.Item label="Ngày sinh" name="dayOfBirth">
                  <DatePicker format="DD/MM/YYYY" style={{ width: '100%' }} />
                </Form.Item>
              </Col>
            </Row>
            <Form.Item style={{ marginBottom: 0 }}>
              <Button type="primary" htmlType="submit" loading={saving}>
                Lưu thay đổi
              </Button>
            </Form.Item>
          </Form>
        </Card>
      </Col>
    </Row>
  );
}
