import { useEffect, useState } from 'react';
import { Card, Table, Tag, Button, Input, Space, Typography } from 'antd';
import { useNavigate } from 'react-router-dom';
import type { ColumnsType } from 'antd/es/table';
import { classApi } from '../../api/class.api';
import { ClassResponse } from '../../types/class.types';
import { CLASS_STATUS_LABEL, CLASS_STATUS_COLOR } from '../../types/common.types';

const { Title } = Typography;
const { Search } = Input;

export default function AdminClassesPage() {
  const navigate = useNavigate();
  const [classes, setClasses] = useState<ClassResponse[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchClasses = (search?: string) => {
    setLoading(true);
    classApi
      .getClasses({ search })
      .then(setClasses)
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchClasses();
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
      width: 80,
      align: 'center',
      render: (_, r) => r.subjectResponse?.credits,
    },
    {
      title: 'Giáo viên',
      key: 'teacher',
      render: (_, r) => r.teacherResponse?.fullName,
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
      width: 90,
      align: 'center',
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
          onClick={() => navigate(`/admin/classes/${r.id}`)}
        >
          Chi tiết
        </Button>
      ),
    },
  ];

  return (
    <div>
      <Title level={4} style={{ marginBottom: 16 }}>
        Quản lý Lớp học
      </Title>
      <Card>
        <Space className="mb-4">
          <Search
            placeholder="Tìm kiếm lớp học..."
            onSearch={fetchClasses}
            allowClear
            style={{ width: 280 }}
          />
        </Space>
        <Table
          columns={columns}
          dataSource={classes.map((c) => ({ ...c, key: c.id }))}
          loading={loading}
          pagination={{ pageSize: 15, showTotal: (t) => `Tổng ${t} lớp` }}
          scroll={{ x: 1000 }}
        />
      </Card>
    </div>
  );
}
