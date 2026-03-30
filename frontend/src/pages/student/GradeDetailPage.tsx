import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Table, Tag, Spin, Button, Typography } from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { studentApi } from '../../api/student.api';
import { StudentGradeResponse } from '../../types/teacher.types';
import { GRADE_COMPONENT_LABEL, GradeComponentType } from '../../types/common.types';

const { Title } = Typography;

interface GradeRow {
  key: string;
  component: string;
  score: number | undefined;
}

export default function StudentGradeDetailPage() {
  const { classId } = useParams<{ classId: string }>();
  const navigate = useNavigate();
  const [data, setData] = useState<StudentGradeResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!classId) return;
    studentApi
      .getGradeByClass(Number(classId))
      .then(setData)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [classId]);

  if (loading) {
    return (
      <div className="flex justify-center items-center" style={{ minHeight: 300 }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!data) return null;

  const columns: ColumnsType<GradeRow> = [
    { title: 'Thành phần', dataIndex: 'component', key: 'component' },
    {
      title: 'Điểm',
      dataIndex: 'score',
      key: 'score',
      render: (v: number | undefined) => (v !== undefined ? v.toFixed(1) : '—'),
    },
  ];

  const rows: GradeRow[] = Object.values(GradeComponentType).map((type) => ({
    key: type,
    component: GRADE_COMPONENT_LABEL[type],
    score: data.grades?.[type],
  }));

  return (
    <div>
      <Button
        icon={<ArrowLeftOutlined />}
        onClick={() => navigate('/student/grades')}
        style={{ marginBottom: 16 }}
      >
        Quay lại
      </Button>
      <Title level={4} style={{ marginBottom: 16 }}>
        Điểm lớp #{classId} — {data.studentCode}
      </Title>
      <Card>
        <Table
          columns={columns}
          dataSource={rows}
          pagination={false}
          size="small"
        />
      </Card>
    </div>
  );
}
