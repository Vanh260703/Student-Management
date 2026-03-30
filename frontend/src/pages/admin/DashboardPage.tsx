import { useEffect, useState } from 'react';
import { Row, Col, Card, Statistic, Spin, Typography } from 'antd';
import {
  TeamOutlined,
  BookOutlined,
  DollarOutlined,
  BellOutlined,
  UserOutlined,
  ApartmentOutlined,
} from '@ant-design/icons';
import { adminApi, AdminDashboardResponse } from '../../api/admin.api';
import { formatCurrency } from '../../types/common.types';

const { Title, Text } = Typography;

export default function AdminDashboardPage() {
  const [data, setData] = useState<AdminDashboardResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminApi
      .getDashboard()
      .then(setData)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="flex justify-center items-center" style={{ minHeight: 300 }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!data) return null;

  return (
    <div>
      <div className="mb-6">
        <Title level={4} style={{ marginBottom: 2 }}>
          Dashboard Quản trị
        </Title>
        <Text type="secondary">Tổng quan hệ thống quản lý sinh viên</Text>
      </div>

      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Tổng sinh viên"
              value={data.totalStudents}
              prefix={<TeamOutlined />}
              valueStyle={{ color: '#1677ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Tổng giáo viên"
              value={data.totalTeachers}
              prefix={<UserOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Tổng lớp học"
              value={data.totalClasses}
              prefix={<BookOutlined />}
              valueStyle={{ color: '#722ed1' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Học phí thu được"
              value={formatCurrency(data.totalTuitionCollected)}
              prefix={<DollarOutlined />}
              valueStyle={{ color: '#fa8c16' }}
              formatter={(v) => String(v)}
            />
          </Card>
        </Col>

        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Lớp đang mở"
              value={data.totalOpenClasses}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Tổng đăng ký học phần"
              value={data.totalEnrollments}
              prefix={<ApartmentOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic title="Môn học" value={data.totalSubjects} />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Thông báo đã gửi"
              value={data.totalNotificationsSent}
              prefix={<BellOutlined />}
            />
          </Card>
        </Col>

        <Col xs={24} sm={12} lg={8}>
          <Card title="Người dùng">
            <div className="space-y-2">
              <div className="flex justify-between">
                <Text type="secondary">Đang hoạt động:</Text>
                <Text strong style={{ color: '#52c41a' }}>
                  {data.totalActiveUsers}
                </Text>
              </div>
              <div className="flex justify-between">
                <Text type="secondary">Không hoạt động:</Text>
                <Text strong style={{ color: '#ff4d4f' }}>
                  {data.totalInactiveUsers}
                </Text>
              </div>
              <div className="flex justify-between">
                <Text type="secondary">Quản trị viên:</Text>
                <Text strong>{data.totalAdmins}</Text>
              </div>
            </div>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={8}>
          <Card title="Học vụ">
            <div className="space-y-2">
              <div className="flex justify-between">
                <Text type="secondary">Chương trình đào tạo:</Text>
                <Text strong>{data.totalPrograms}</Text>
              </div>
              <div className="flex justify-between">
                <Text type="secondary">Khoa / Bộ môn:</Text>
                <Text strong>{data.totalDepartments}</Text>
              </div>
              <div className="flex justify-between">
                <Text type="secondary">Học kỳ:</Text>
                <Text strong>{data.totalSemesters}</Text>
              </div>
              <div className="flex justify-between">
                <Text type="secondary">GPA trung bình:</Text>
                <Text strong>{data.averageGPA?.toFixed(2)}</Text>
              </div>
            </div>
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={8}>
          <Card title="Thanh toán">
            <div className="space-y-2">
              <div className="flex justify-between">
                <Text type="secondary">Giao dịch:</Text>
                <Text strong>{data.totalPaymentTransactions}</Text>
              </div>
              <div className="flex justify-between">
                <Text type="secondary">Thành công:</Text>
                <Text strong style={{ color: '#52c41a' }}>
                  {data.totalPaidPayments}
                </Text>
              </div>
              <div className="flex justify-between">
                <Text type="secondary">Chờ thanh toán:</Text>
                <Text strong style={{ color: '#fa8c16' }}>
                  {data.totalPendingPayments}
                </Text>
              </div>
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  );
}
