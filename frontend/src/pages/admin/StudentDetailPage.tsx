import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Card,
  Avatar,
  Row,
  Col,
  Spin,
  Tag,
  Button,
  Form,
  Input,
  Select,
  DatePicker,
  Typography,
  message,
} from 'antd';
import { ArrowLeftOutlined, UserOutlined } from '@ant-design/icons';
import { adminApi, UpdateStudentRequest } from '../../api/admin.api';
import { StudentProfile } from '../../types/student.types';
import {
  Gender,
  GENDER_LABEL,
  StudentStatus,
  STUDENT_STATUS_LABEL,
  STUDENT_STATUS_COLOR,
  formatDate,
  formatGPA,
} from '../../types/common.types';

const { Title, Text } = Typography;

export default function AdminStudentDetailPage() {
  const { studentId } = useParams<{ studentId: string }>();
  const navigate = useNavigate();
  const [student, setStudent] = useState<StudentProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [form] = Form.useForm<UpdateStudentRequest>();

  useEffect(() => {
    if (!studentId) return;
    adminApi
      .getStudent(Number(studentId))
      .then((data) => {
        setStudent(data);
        form.setFieldsValue({
          fullName: data.fullName,
          phone: data.phone,
          personalEmail: data.personalEmail,
          dayOfBirth: data.dayOfBirth,
          address: data.address,
          gender: data.gender,
          className: data.className,
          enrollmentYear: data.enrollmentYear,
          status: data.status,
          isActive: true,
        });
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [studentId, form]);

  const onFinish = async (values: UpdateStudentRequest) => {
    if (!studentId) return;
    setSaving(true);
    try {
      const updated = await adminApi.updateStudent(Number(studentId), values);
      setStudent(updated);
      message.success('Cập nhật thành công!');
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

  if (!student) return null;

  return (
    <div>
      <Button
        icon={<ArrowLeftOutlined />}
        onClick={() => navigate('/admin/students')}
        style={{ marginBottom: 16 }}
      >
        Quay lại
      </Button>

      <Row gutter={[24, 24]}>
        <Col xs={24} lg={7}>
          <Card>
            <div className="text-center mb-4">
              <Avatar
                size={96}
                src={student.avatarUrl}
                icon={<UserOutlined />}
                style={{ background: '#1677ff' }}
              />
              <Title level={5} style={{ marginTop: 12, marginBottom: 4 }}>
                {student.fullName}
              </Title>
              <Text type="secondary">{student.studentCode}</Text>
              <div style={{ marginTop: 8 }}>
                <Tag color={STUDENT_STATUS_COLOR[student.status]}>
                  {STUDENT_STATUS_LABEL[student.status]}
                </Tag>
              </div>
            </div>
            <div className="space-y-2">
              {[
                { label: 'Email', value: student.schoolEmail },
                { label: 'GPA', value: formatGPA(student.gpa) },
                { label: 'Tín chỉ', value: student.accumulatedCredits },
                { label: 'Lớp', value: student.className },
                { label: 'Năm nhập học', value: student.enrollmentYear },
                { label: 'Ngày sinh', value: student.dayOfBirth ? formatDate(student.dayOfBirth) : '—' },
              ].map(({ label, value }) => (
                <div key={label} className="flex justify-between">
                  <Text type="secondary">{label}:</Text>
                  <Text strong>{value ?? '—'}</Text>
                </div>
              ))}
            </div>
          </Card>
        </Col>
        <Col xs={24} lg={17}>
          <Card title="Chỉnh sửa thông tin">
            <Form form={form} layout="vertical" onFinish={onFinish}>
              <Row gutter={16}>
                <Col xs={24} md={12}>
                  <Form.Item label="Họ và tên" name="fullName" rules={[{ required: true }]}>
                    <Input />
                  </Form.Item>
                </Col>
                <Col xs={24} md={12}>
                  <Form.Item label="Số điện thoại" name="phone">
                    <Input />
                  </Form.Item>
                </Col>
                <Col xs={24} md={12}>
                  <Form.Item label="Email cá nhân" name="personalEmail">
                    <Input />
                  </Form.Item>
                </Col>
                <Col xs={24} md={12}>
                  <Form.Item label="Giới tính" name="gender">
                    <Select>
                      {Object.values(Gender).map((g) => (
                        <Select.Option key={g} value={g}>
                          {GENDER_LABEL[g]}
                        </Select.Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col xs={24} md={12}>
                  <Form.Item label="Lớp" name="className">
                    <Input />
                  </Form.Item>
                </Col>
                <Col xs={24} md={12}>
                  <Form.Item label="Năm nhập học" name="enrollmentYear">
                    <Input type="number" />
                  </Form.Item>
                </Col>
                <Col xs={24} md={12}>
                  <Form.Item label="Trạng thái" name="status">
                    <Select>
                      {Object.values(StudentStatus).map((s) => (
                        <Select.Option key={s} value={s}>
                          {STUDENT_STATUS_LABEL[s]}
                        </Select.Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
                <Col xs={24}>
                  <Form.Item label="Địa chỉ" name="address">
                    <Input.TextArea rows={3} />
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
    </div>
  );
}
