import { useEffect, useState } from 'react';
import { Card, Table, Tag, Button, Spin, Typography } from 'antd';
import { useNavigate } from 'react-router-dom';
import type { ColumnsType } from 'antd/es/table';
import { teacherApi } from '../../api/teacher.api';
import { ClassResponse } from '../../types/class.types';
import { CLASS_STATUS_LABEL, CLASS_STATUS_COLOR } from '../../types/common.types';

const { Title } = Typography;

export default function TeacherClassesPage() {
  const navigate = useNavigate();
  const [classes, setClasses] = useState<ClassResponse[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    teacherApi
      .getClasses()
      .then(setClasses)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const columns: ColumnsType<ClassResponse> = [
    { title: 'Mã lớp', dataIndex: 'classCode', key: 'classCode', width: 130 },
    {
      title: 'Môn học',
      key: 'subject',
      render: (_, r) => r.subjectResponse?.name,
    },
    {
      title: 'Tín chỉ',
      key: 'credits',
      align: 'center',
      width: 80,
      render: (_, r) => r.subjectResponse?.credits,
    },
    {
      title: 'Học kỳ',
      key: 'semester',
      render: (_, r) => `${r.semesterResponse?.name} — ${r.semesterResponse?.academicYear}`,
    },
    { title: 'Phòng', dataIndex: 'room', key: 'room', width: 90 },
    {
      title: 'Sĩ số',
      key: 'slots',
      align: 'center',
      width: 90,
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
    {
      title: 'Thao tác',
      key: 'action',
      width: 100,
      render: (_, r) => (
        <Button
          type="link"
          size="small"
          onClick={() => navigate(`/teacher/classes/${r.id}`)}
        >
          Chi tiết
        </Button>
      ),
    },
  ];

  if (loading) {
    return (
      <div className="flex justify-center items-center" style={{ minHeight: 300 }}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div>
      <Title level={4} style={{ marginBottom: 16 }}>
        Danh sách lớp học
      </Title>
      <Card>
        <Table
          columns={columns}
          dataSource={classes.map((c) => ({ ...c, key: c.id }))}
          pagination={{ pageSize: 10 }}
          scroll={{ x: 800 }}
          locale={{ emptyText: 'Không có lớp học nào' }}
        />
      </Card>
    </div>
  );
}
