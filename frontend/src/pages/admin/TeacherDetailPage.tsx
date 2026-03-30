import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Avatar, Row, Col, Spin, Tag, Button, Typography } from 'antd';
import { ArrowLeftOutlined, UserOutlined } from '@ant-design/icons';
import { adminApi } from '../../api/admin.api';
import { TeacherProfile } from '../../types/teacher.types';
import { GENDER_LABEL, formatDate } from '../../types/common.types';

const { Title, Text } = Typography;

export default function AdminTeacherDetailPage() {
  const { teacherId } = useParams<{ teacherId: string }>();
  const navigate = useNavigate();
  const [teacher, setTeacher] = useState<TeacherProfile | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!teacherId) return;
    adminApi
      .getTeacher(Number(teacherId))
      .then(setTeacher)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [teacherId]);

  if (loading) {
    return (
      <div className="flex justify-center items-center" style={{ minHeight: 300 }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!teacher) return null;

  return (
    <div>
      <Button
        icon={<ArrowLeftOutlined />}
        onClick={() => navigate('/admin/teachers')}
        style={{ marginBottom: 16 }}
      >
        Quay lại
      </Button>
      <Card>
        <Row gutter={[24, 24]}>
          <Col xs={24} sm={6} style={{ textAlign: 'center' }}>
            <Avatar
              size={100}
              src={teacher.avatarUrl}
              icon={<UserOutlined />}
              style={{ background: '#1677ff' }}
            />
            <Title level={5} style={{ marginTop: 12, marginBottom: 4 }}>
              {teacher.fullName}
            </Title>
            <Text type="secondary">{teacher.teacherCode}</Text>
            <div style={{ marginTop: 8 }}>
              <Tag color={teacher.isActive ? 'green' : 'red'}>
                {teacher.isActive ? 'Đang hoạt động' : 'Khoá'}
              </Tag>
            </div>
          </Col>
          <Col xs={24} sm={18}>
            <div className="grid grid-cols-2 gap-4">
              {[
                { label: 'Email trường', value: teacher.email },
                { label: 'Email cá nhân', value: teacher.personalEmail },
                { label: 'Điện thoại', value: teacher.phone },
                { label: 'Giới tính', value: GENDER_LABEL[teacher.gender] ?? teacher.gender },
                { label: 'Khoa', value: teacher.department },
                { label: 'Học vị', value: teacher.degree },
                { label: 'Ngày vào làm', value: teacher.joinedDate ? formatDate(teacher.joinedDate) : '—' },
                { label: 'Ngày sinh', value: teacher.dateOfBirth ? formatDate(teacher.dateOfBirth) : '—' },
              ].map(({ label, value }) => (
                <div key={label}>
                  <Text type="secondary">{label}:</Text>
                  <br />
                  <Text strong>{value ?? '—'}</Text>
                </div>
              ))}
            </div>
          </Col>
        </Row>
      </Card>
    </div>
  );
}
