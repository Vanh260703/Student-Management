import { useEffect, useState } from 'react';
import { Row, Col, Card, Statistic, Tag, Table, Spin, Progress, Typography } from 'antd';
import {
  BookOutlined,
  TrophyOutlined,
  CalendarOutlined,
  DollarOutlined,
} from '@ant-design/icons';
import { studentApi } from '../../api/student.api';
import { StudentDashboardResponse } from '../../types/student.types';
import {
  STUDENT_STATUS_LABEL,
  STUDENT_STATUS_COLOR,
  TUITION_STATUS_LABEL,
  TUITION_STATUS_COLOR,
  LETTER_GRADE_COLOR,
  formatCurrency,
  formatGPA,
} from '../../types/common.types';
import type { ColumnsType } from 'antd/es/table';

const { Text } = Typography;

interface RecentGradeRow {
  key: number;
  subjectName: string;
  score: number;
  letterGrade: string;
}

const recentGradeColumns: ColumnsType<RecentGradeRow> = [
  { title: 'Môn học', dataIndex: 'subjectName', key: 'subjectName' },
  { title: 'Điểm', dataIndex: 'score', key: 'score', align: 'center', width: 80 },
  {
    title: 'Xếp loại',
    dataIndex: 'letterGrade',
    key: 'letterGrade',
    align: 'center',
    width: 80,
    render: (grade: string) => (
      <Tag color={LETTER_GRADE_COLOR[grade as keyof typeof LETTER_GRADE_COLOR] ?? 'default'}>
        {grade}
      </Tag>
    ),
  },
];

export default function StudentDashboardPage() {
  const [data, setData] = useState<StudentDashboardResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    studentApi
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
      {/* Welcome & Status */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <Typography.Title level={4} style={{ marginBottom: 2 }}>
            Xin chào, {data.studentInfo.fullName}!
          </Typography.Title>
          <Text type="secondary">Mã SV: {data.studentInfo.studentCode}</Text>
        </div>
        <Tag color={STUDENT_STATUS_COLOR[data.studentStatus]} style={{ fontSize: 13, padding: '4px 12px' }}>
          {STUDENT_STATUS_LABEL[data.studentStatus]}
        </Tag>
      </div>

      {/* Stats row */}
      <Row gutter={[16, 16]} className="mb-6">
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Lớp đang học"
              value={data.totalEnrolledClasses}
              prefix={<BookOutlined />}
              valueStyle={{ color: '#1677ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="GPA hiện tại"
              value={formatGPA(data.currentGPA)}
              prefix={<TrophyOutlined />}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Tín chỉ tích lũy"
              value={data.totalCompletedCredits}
              prefix={<CalendarOutlined />}
              valueStyle={{ color: '#722ed1' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Học phí còn lại"
              value={formatCurrency(data.remainingAmount)}
              prefix={<DollarOutlined />}
              valueStyle={{ color: data.remainingAmount > 0 ? '#ff4d4f' : '#52c41a' }}
              formatter={(v) => String(v)}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]}>
        {/* Attendance */}
        <Col xs={24} lg={8}>
          <Card title="Chuyên cần" className="h-full">
            <div className="text-center mb-4">
              <Progress
                type="circle"
                percent={Math.round(data.attendanceRate * 100)}
                strokeColor="#52c41a"
                format={(p) => `${p}%`}
              />
            </div>
            <div className="flex justify-between text-sm">
              <Text type="secondary">Vắng: <Text strong>{data.totalAbsentDays}</Text> buổi</Text>
              <Text type="secondary">Trễ: <Text strong>{data.totalLateArrivals}</Text> lần</Text>
            </div>
          </Card>
        </Col>

        {/* Tuition */}
        <Col xs={24} lg={8}>
          <Card title="Học phí học kỳ" className="h-full">
            {data.tuitionInfo ? (
              <div className="space-y-3">
                <div className="flex justify-between">
                  <Text type="secondary">Học phí:</Text>
                  <Text strong>{formatCurrency(data.tuitionInfo.amount)}</Text>
                </div>
                <div className="flex justify-between">
                  <Text type="secondary">Giảm giá:</Text>
                  <Text>{formatCurrency(data.tuitionInfo.discount)}</Text>
                </div>
                <div className="flex justify-between">
                  <Text type="secondary">Thực nộp:</Text>
                  <Text strong style={{ color: '#1677ff' }}>
                    {formatCurrency(data.tuitionInfo.finalAmount)}
                  </Text>
                </div>
                {data.tuitionStatus && (
                  <div className="flex justify-between items-center">
                    <Text type="secondary">Trạng thái:</Text>
                    <Tag color={TUITION_STATUS_COLOR[data.tuitionStatus]}>
                      {TUITION_STATUS_LABEL[data.tuitionStatus]}
                    </Tag>
                  </div>
                )}
              </div>
            ) : (
              <div className="text-center text-gray-400">Chưa có thông tin học phí</div>
            )}
          </Card>
        </Col>

        {/* Academic summary */}
        <Col xs={24} lg={8}>
          <Card title="Kết quả học tập" className="h-full">
            <div className="space-y-3">
              <div className="flex justify-between">
                <Text type="secondary">Môn đã qua:</Text>
                <Text strong style={{ color: '#52c41a' }}>
                  {data.totalPassedSubjects}
                </Text>
              </div>
              <div className="flex justify-between">
                <Text type="secondary">Môn chưa qua:</Text>
                <Text strong style={{ color: '#ff4d4f' }}>
                  {data.totalFailedSubjects}
                </Text>
              </div>
              <div className="flex justify-between">
                <Text type="secondary">Điểm trung bình:</Text>
                <Text strong>{data.averageScore.toFixed(1)}</Text>
              </div>
              <div className="flex justify-between">
                <Text type="secondary">Chương trình:</Text>
                <Text>{data.programName}</Text>
              </div>
            </div>
          </Card>
        </Col>

        {/* Recent grades */}
        {data.recentGrades.length > 0 && (
          <Col xs={24} lg={12}>
            <Card title="Điểm gần đây">
              <Table
                columns={recentGradeColumns}
                dataSource={data.recentGrades.map((g, i) => ({ ...g, key: i }))}
                pagination={false}
                size="small"
              />
            </Card>
          </Col>
        )}

        {/* Upcoming classes */}
        {data.upcomingClasses.length > 0 && (
          <Col xs={24} lg={12}>
            <Card title="Lớp học sắp tới">
              {data.upcomingClasses.map((cls, i) => (
                <div key={i} className="flex justify-between items-center py-2 border-b last:border-0">
                  <div>
                    <Text strong>{cls.nextClassName}</Text>
                    <br />
                    <Text type="secondary" style={{ fontSize: 12 }}>
                      Phòng: {cls.nextClassRoom}
                    </Text>
                  </div>
                  <Text type="secondary" style={{ fontSize: 12 }}>
                    {cls.nextClassTime}
                  </Text>
                </div>
              ))}
            </Card>
          </Col>
        )}
      </Row>
    </div>
  );
}
