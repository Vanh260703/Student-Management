import { useEffect, useState } from 'react';
import { Row, Col, Card, Statistic, Table, Tag, Spin, Typography } from 'antd';
import {
  TeamOutlined,
  BookOutlined,
  CheckCircleOutlined,
  BarChartOutlined,
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { teacherApi } from '../../api/teacher.api';
import { TeacherDashboardResponse } from '../../types/teacher.types';
import { ClassResponse } from '../../types/class.types';
import { CLASS_STATUS_LABEL, CLASS_STATUS_COLOR } from '../../types/common.types';

const { Title, Text } = Typography;

const classColumns: ColumnsType<ClassResponse> = [
  { title: 'Mã lớp', dataIndex: 'classCode', key: 'classCode', width: 120 },
  {
    title: 'Môn học',
    key: 'subject',
    render: (_, r) => r.subjectResponse?.name,
  },
  {
    title: 'Sĩ số',
    key: 'slots',
    align: 'center',
    width: 100,
    render: (_, r) => `${r.currentStudents}/${r.maxStudents}`,
  },
  {
    title: 'Trạng thái',
    key: 'status',
    width: 110,
    render: (_, r) => (
      <Tag color={CLASS_STATUS_COLOR[r.status]}>{CLASS_STATUS_LABEL[r.status]}</Tag>
    ),
  },
];

export default function TeacherDashboardPage() {
  const [data, setData] = useState<TeacherDashboardResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    teacherApi
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
          Xin chào, {data.teacherInfo.fullName}!
        </Title>
        <Text type="secondary">
          {data.teacherInfo.teacherCode} — {data.departmentName}
        </Text>
      </div>

      <Row gutter={[16, 16]} className="mb-6">
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Tổng lớp dạy"
              value={data.totalClasses}
              prefix={<BookOutlined />}
              valueStyle={{ color: '#1677ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Tổng sinh viên"
              value={data.totalStudents}
              prefix={<TeamOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Điểm đã nhập"
              value={data.totalGradesPosted}
              prefix={<CheckCircleOutlined />}
              valueStyle={{ color: '#722ed1' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="GPA trung bình"
              value={data.averageClassGPA?.toFixed(2) ?? '—'}
              prefix={<BarChartOutlined />}
              valueStyle={{ color: '#fa8c16' }}
              formatter={(v) => String(v)}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]}>
        <Col xs={24} lg={14}>
          <Card title="Danh sách lớp học">
            <Table
              columns={classColumns}
              dataSource={(data.classes ?? []).map((c) => ({ ...c, key: c.id }))}
              pagination={{ pageSize: 5 }}
              size="small"
            />
          </Card>
        </Col>
        <Col xs={24} lg={10}>
          <Card title="Thống kê nhanh">
            <div className="space-y-3">
              {[
                { label: 'Tỷ lệ điểm danh TB', value: `${(data.averageAttendanceRate * 100).toFixed(1)}%` },
                { label: 'SV xuất sắc', value: data.totalExcellentStudents },
                { label: 'SV không đạt', value: data.totalFailedStudents },
                { label: 'Lớp lớn nhất', value: `${data.largestClassName} (${data.largestClassSize} SV)` },
                { label: 'Lớp nhỏ nhất', value: `${data.smallestClassName} (${data.smallestClassSize} SV)` },
                { label: 'Điểm chờ nhập', value: data.totalGradesPending },
              ].map(({ label, value }) => (
                <div key={label} className="flex justify-between">
                  <Text type="secondary">{label}:</Text>
                  <Text strong>{value}</Text>
                </div>
              ))}
            </div>
          </Card>
        </Col>
      </Row>
    </div>
  );
}
