import { useEffect, useState } from 'react';
import { Card, Table, Tag, Spin, Collapse, Typography, Statistic, Row, Col } from 'antd';
import { useNavigate } from 'react-router-dom';
import type { ColumnsType } from 'antd/es/table';
import { studentApi } from '../../api/student.api';
import { AllGradeStudent, GradeCourse } from '../../types/student.types';
import {
  LETTER_GRADE_COLOR,
  GRADE_COMPONENT_LABEL,
  GradeComponentType,
  formatGPA,
} from '../../types/common.types';

const { Text, Title } = Typography;
const { Panel } = Collapse;

const courseColumns = (navigate: ReturnType<typeof useNavigate>): ColumnsType<GradeCourse> => [
  {
    title: 'Mã lớp',
    dataIndex: 'classCode',
    key: 'classCode',
    render: (code: string, record: GradeCourse) => (
      <a onClick={() => navigate(`/student/grades/${record.classId}`)}>{code}</a>
    ),
    width: 120,
  },
  { title: 'Môn học', dataIndex: 'subjectName', key: 'subjectName' },
  { title: 'Tín chỉ', dataIndex: 'credits', key: 'credits', align: 'center', width: 80 },
  ...Object.values(GradeComponentType).map((type) => ({
    title: GRADE_COMPONENT_LABEL[type],
    key: type,
    align: 'center' as const,
    width: 100,
    render: (_: unknown, record: GradeCourse) => {
      const score = record.grades?.[type];
      return score !== undefined ? score.toFixed(1) : '—';
    },
  })),
  {
    title: 'Điểm TK',
    dataIndex: 'finalScore',
    key: 'finalScore',
    align: 'center',
    width: 90,
    render: (score: number | null) => (score !== null ? score.toFixed(1) : '—'),
  },
  {
    title: 'Xếp loại',
    dataIndex: 'finalLetterGrade',
    key: 'finalLetterGrade',
    align: 'center',
    width: 80,
    render: (grade: string | null) =>
      grade ? (
        <Tag color={LETTER_GRADE_COLOR[grade as keyof typeof LETTER_GRADE_COLOR]}>{grade}</Tag>
      ) : (
        '—'
      ),
  },
  {
    title: 'Kết quả',
    dataIndex: 'isPassed',
    key: 'isPassed',
    align: 'center',
    width: 90,
    render: (isPassed: boolean | null) =>
      isPassed === null ? '—' : isPassed ? (
        <Tag color="green">Đạt</Tag>
      ) : (
        <Tag color="red">Không đạt</Tag>
      ),
  },
  {
    title: 'Công bố',
    dataIndex: 'isPublished',
    key: 'isPublished',
    align: 'center',
    width: 80,
    render: (v: boolean) => (v ? <Tag color="green">Đã công bố</Tag> : <Tag>Chờ</Tag>),
  },
];

export default function StudentGradesPage() {
  const navigate = useNavigate();
  const [data, setData] = useState<AllGradeStudent | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    studentApi
      .getGrades()
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
      <Title level={4} style={{ marginBottom: 16 }}>
        Bảng điểm — {data.studentInfo.studentCode} / {data.studentInfo.name}
      </Title>

      {/* Summary */}
      <Row gutter={16} className="mb-6">
        <Col xs={24} sm={8}>
          <Card>
            <Statistic title="GPA tích lũy" value={formatGPA(data.summary.gpa)} />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic title="Tín chỉ tích lũy" value={data.summary.totalCredits} />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic title="Tín chỉ đã qua" value={data.summary.passedCredits} />
          </Card>
        </Col>
      </Row>

      {/* Semesters */}
      <Collapse defaultActiveKey={data.semesters.map((s) => String(s.semesterId))}>
        {data.semesters.map((semester) => (
          <Panel
            key={String(semester.semesterId)}
            header={
              <div className="flex items-center justify-between">
                <Text strong>{semester.semesterName}</Text>
                <Text type="secondary" style={{ fontSize: 12 }}>
                  {semester.courses.length} môn
                </Text>
              </div>
            }
          >
            <Table
              columns={courseColumns(navigate)}
              dataSource={semester.courses.map((c) => ({ ...c, key: c.classId }))}
              pagination={false}
              scroll={{ x: 900 }}
              size="small"
            />
          </Panel>
        ))}
      </Collapse>
    </div>
  );
}
