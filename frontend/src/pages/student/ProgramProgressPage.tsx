import { useEffect, useState } from 'react';
import { Card, Collapse, Table, Tag, Spin, Progress, Row, Col, Typography, Statistic } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { studentApi } from '../../api/student.api';
import { ProgramResponse, ProgramSubjectItem } from '../../types/student.types';

const { Title, Text } = Typography;
const { Panel } = Collapse;

const subjectColumns: ColumnsType<ProgramSubjectItem> = [
  { title: 'Mã môn', dataIndex: 'code', key: 'code', width: 110 },
  { title: 'Tên môn học', dataIndex: 'name', key: 'name' },
  { title: 'Tín chỉ', dataIndex: 'credits', key: 'credits', align: 'center', width: 80 },
  {
    title: 'Bắt buộc',
    dataIndex: 'isRequired',
    key: 'isRequired',
    align: 'center',
    width: 90,
    render: (v: boolean) => (v ? <Tag color="red">Bắt buộc</Tag> : <Tag>Tự chọn</Tag>),
  },
  {
    title: 'Môn tiên quyết',
    dataIndex: 'prerequisiteSubject',
    key: 'prerequisiteSubject',
    render: (v: string | null) => v ?? '—',
  },
];

export default function ProgramProgressPage() {
  const [program, setProgram] = useState<ProgramResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    studentApi
      .getProgramProgress()
      .then(setProgram)
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

  if (!program) return null;

  const totalSubjects = program.subjectBySemester.reduce(
    (sum, sem) => sum + sem.subjects.length,
    0,
  );

  return (
    <div>
      <Title level={4} style={{ marginBottom: 16 }}>
        Tiến trình học tập — {program.name}
      </Title>

      <Row gutter={16} className="mb-6">
        <Col xs={24} sm={8}>
          <Card>
            <Statistic title="Chương trình" value={program.name} formatter={(v) => String(v)} />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic title="Tổng tín chỉ" value={program.totalCredits} />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic title="Thời gian đào tạo" value={`${program.durationYears} năm`} formatter={(v) => String(v)} />
          </Card>
        </Col>
      </Row>

      <Card className="mb-6">
        <div className="flex items-center justify-between mb-2">
          <Text strong>Tổng số môn học: {totalSubjects}</Text>
        </div>
        <Progress
          percent={Math.round((totalSubjects / (totalSubjects || 1)) * 100)}
          strokeColor="#1677ff"
          format={() => `${totalSubjects} môn`}
        />
      </Card>

      <Collapse defaultActiveKey={program.subjectBySemester.map((s) => String(s.semester))}>
        {program.subjectBySemester.map((sem) => (
          <Panel
            key={String(sem.semester)}
            header={
              <Text strong>
                Học kỳ {sem.semester} — {sem.subjects.length} môn
              </Text>
            }
          >
            <Table
              columns={subjectColumns}
              dataSource={sem.subjects.map((s) => ({ ...s, key: s.id }))}
              pagination={false}
              size="small"
            />
          </Panel>
        ))}
      </Collapse>
    </div>
  );
}
