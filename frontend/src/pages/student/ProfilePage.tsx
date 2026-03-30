import { useEffect, useState } from 'react';
import {
  Card,
  Avatar,
  Form,
  Input,
  Select,
  DatePicker,
  Button,
  Upload,
  Row,
  Col,
  Tag,
  Spin,
  message,
  Typography,
} from 'antd';
import { UserOutlined, CameraOutlined } from '@ant-design/icons';
import type { UploadProps } from 'antd';
import dayjs from 'dayjs';
import { studentApi } from '../../api/student.api';
import { StudentProfile, UpdateProfileRequest } from '../../types/student.types';
import {
  Gender,
  GENDER_LABEL,
  STUDENT_STATUS_LABEL,
  STUDENT_STATUS_COLOR,
  formatDate,
} from '../../types/common.types';

const { Title, Text } = Typography;

export default function StudentProfilePage() {
  const [profile, setProfile] = useState<StudentProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [form] = Form.useForm<UpdateProfileRequest>();

  useEffect(() => {
    studentApi
      .getProfile()
      .then((data) => {
        setProfile(data);
        form.setFieldsValue({
          fullName: data.fullName,
          phone: data.phone,
          personalEmail: data.personalEmail,
          dayOfBirth: data.dayOfBirth,
          address: data.address,
          gender: data.gender,
        });
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [form]);

  const onFinish = async (values: UpdateProfileRequest) => {
    setSaving(true);
    try {
      const updated = await studentApi.updateProfile({
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

  const uploadProps: UploadProps = {
    name: 'file',
    showUploadList: false,
    beforeUpload: async (file) => {
      setUploading(true);
      try {
        const result = await studentApi.uploadAvatar(file);
        setProfile((prev) => (prev ? { ...prev, avatarUrl: result.avatarUrl } : prev));
        message.success('Tải ảnh thành công!');
      } catch {
        // interceptor handles
      } finally {
        setUploading(false);
      }
      return false;
    },
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
      {/* Left: Avatar & basic info */}
      <Col xs={24} lg={8}>
        <Card>
          <div className="text-center">
            <div className="relative inline-block mb-4">
              <Avatar
                size={100}
                src={profile.avatarUrl}
                icon={<UserOutlined />}
                style={{ background: '#1677ff' }}
              />
              <Upload {...uploadProps}>
                <Button
                  type="primary"
                  shape="circle"
                  icon={<CameraOutlined />}
                  size="small"
                  loading={uploading}
                  style={{
                    position: 'absolute',
                    bottom: 0,
                    right: 0,
                    width: 28,
                    height: 28,
                    minWidth: 28,
                  }}
                />
              </Upload>
            </div>
            <Title level={5} style={{ marginBottom: 4 }}>
              {profile.fullName}
            </Title>
            <Text type="secondary" style={{ display: 'block', marginBottom: 8 }}>
              {profile.studentCode}
            </Text>
            <Tag color={STUDENT_STATUS_COLOR[profile.status]}>
              {STUDENT_STATUS_LABEL[profile.status]}
            </Tag>
          </div>

          <div className="mt-6 space-y-3">
            {[
              { label: 'Email trường', value: profile.schoolEmail },
              { label: 'Lớp', value: profile.className },
              { label: 'GPA', value: profile.gpa?.toFixed(2) },
              { label: 'Tín chỉ', value: profile.accumulatedCredits },
              { label: 'Năm nhập học', value: profile.enrollmentYear },
              { label: 'Ngày sinh', value: profile.dayOfBirth ? formatDate(profile.dayOfBirth) : '—' },
            ].map(({ label, value }) => (
              <div key={label} className="flex justify-between">
                <Text type="secondary">{label}:</Text>
                <Text strong>{value ?? '—'}</Text>
              </div>
            ))}
          </div>
        </Card>
      </Col>

      {/* Right: Edit form */}
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
                  <Input placeholder="Nhập họ và tên" />
                </Form.Item>
              </Col>
              <Col xs={24} md={12}>
                <Form.Item
                  label="Số điện thoại"
                  name="phone"
                  rules={[
                    { required: true, message: 'Vui lòng nhập số điện thoại' },
                    { pattern: /^[0-9]{9,11}$/, message: 'Số điện thoại không hợp lệ' },
                  ]}
                >
                  <Input placeholder="Nhập số điện thoại" />
                </Form.Item>
              </Col>
              <Col xs={24} md={12}>
                <Form.Item
                  label="Email cá nhân"
                  name="personalEmail"
                  rules={[
                    { required: true, message: 'Vui lòng nhập email' },
                    { type: 'email', message: 'Email không hợp lệ' },
                  ]}
                >
                  <Input placeholder="Nhập email cá nhân" />
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
                  <DatePicker
                    format="DD/MM/YYYY"
                    style={{ width: '100%' }}
                    placeholder="Chọn ngày sinh"
                  />
                </Form.Item>
              </Col>
              <Col xs={24}>
                <Form.Item label="Địa chỉ" name="address">
                  <Input.TextArea rows={3} placeholder="Nhập địa chỉ" />
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
