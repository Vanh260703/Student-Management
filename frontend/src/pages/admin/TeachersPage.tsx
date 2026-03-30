import { useEffect, useState } from 'react';
import { Card, Table, Tag, Button, Input, Space, Typography } from 'antd';
import { useNavigate } from 'react-router-dom';
import type { ColumnsType } from 'antd/es/table';
import { adminApi } from '../../api/admin.api';
import { TeacherProfile } from '../../types/teacher.types';
import { GENDER_LABEL, formatDate } from '../../types/common.types';

const { Title } = Typography;
const { Search } = Input;

export default function AdminTeachersPage() {
  const navigate = useNavigate();
  const [teachers, setTeachers] = useState<TeacherProfile[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchTeachers = (search?: string) => {
    setLoading(true);
    adminApi
      .getTeachers({ search })
      .then(setTeachers)
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchTeachers();
  }, []);

  const columns: ColumnsType<TeacherProfile> = [
    { title: 'Mã GV', dataIndex: 'teacherCode', key: 'teacherCode', width: 110 },
    { title: 'Họ tên', dataIndex: 'fullName', key: 'fullName' },
    { title: 'Email', dataIndex: 'email', key: 'email' },
    {
      title: 'Giới tính',
      dataIndex: 'gender',
      key: 'gender',
      width: 80,
      render: (g) => GENDER_LABEL[g] ?? g,
    },
    { title: 'Khoa', dataIndex: 'department', key: 'department' },
    { title: 'Học vị', dataIndex: 'degree', key: 'degree', width: 100 },
    {
      title: 'Trạng thái',
      dataIndex: 'isActive',
      key: 'isActive',
      width: 110,
      render: (v: boolean) => (
        <Tag color={v ? 'green' : 'red'}>{v ? 'Hoạt động' : 'Khoá'}</Tag>
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
          onClick={() => navigate(`/admin/teachers/${r.id}`)}
        >
          Chi tiết
        </Button>
      ),
    },
  ];

  return (
    <div>
      <Title level={4} style={{ marginBottom: 16 }}>
        Quản lý Giáo viên
      </Title>
      <Card>
        <Space className="mb-4">
          <Search
            placeholder="Tìm kiếm giáo viên..."
            onSearch={fetchTeachers}
            allowClear
            style={{ width: 280 }}
          />
        </Space>
        <Table
          columns={columns}
          dataSource={teachers.map((t) => ({ ...t, key: t.id }))}
          loading={loading}
          pagination={{ pageSize: 15, showTotal: (total) => `Tổng ${total} giáo viên` }}
          scroll={{ x: 900 }}
        />
      </Card>
    </div>
  );
}
