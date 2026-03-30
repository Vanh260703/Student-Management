import { useEffect, useState } from 'react';
import { Card, Table, Tag, Button, Input, Space, Typography } from 'antd';
import { useNavigate } from 'react-router-dom';
import type { ColumnsType } from 'antd/es/table';
import { adminApi } from '../../api/admin.api';
import { StudentProfile } from '../../types/student.types';
import {
  STUDENT_STATUS_LABEL,
  STUDENT_STATUS_COLOR,
  GENDER_LABEL,
  formatGPA,
} from '../../types/common.types';

const { Title } = Typography;
const { Search } = Input;

export default function AdminStudentsPage() {
  const navigate = useNavigate();
  const [students, setStudents] = useState<StudentProfile[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchStudents = (search?: string) => {
    setLoading(true);
    adminApi
      .getStudents({ search })
      .then(setStudents)
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchStudents();
  }, []);

  const columns: ColumnsType<StudentProfile> = [
    { title: 'Mã SV', dataIndex: 'studentCode', key: 'studentCode', width: 120 },
    { title: 'Họ tên', dataIndex: 'fullName', key: 'fullName' },
    { title: 'Email trường', dataIndex: 'schoolEmail', key: 'schoolEmail' },
    {
      title: 'Giới tính',
      dataIndex: 'gender',
      key: 'gender',
      width: 80,
      render: (g) => GENDER_LABEL[g] ?? g,
    },
    { title: 'Lớp', dataIndex: 'className', key: 'className', width: 100 },
    {
      title: 'GPA',
      dataIndex: 'gpa',
      key: 'gpa',
      width: 70,
      align: 'center',
      render: (v: number) => formatGPA(v),
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (s) => (
        <Tag color={STUDENT_STATUS_COLOR[s]}>{STUDENT_STATUS_LABEL[s]}</Tag>
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
          onClick={() => navigate(`/admin/students/${r.id}`)}
        >
          Chi tiết
        </Button>
      ),
    },
  ];

  return (
    <div>
      <Title level={4} style={{ marginBottom: 16 }}>
        Quản lý Sinh viên
      </Title>
      <Card>
        <Space className="mb-4">
          <Search
            placeholder="Tìm kiếm sinh viên..."
            onSearch={fetchStudents}
            allowClear
            style={{ width: 280 }}
          />
        </Space>
        <Table
          columns={columns}
          dataSource={students.map((s) => ({ ...s, key: s.id }))}
          loading={loading}
          pagination={{ pageSize: 15, showTotal: (t) => `Tổng ${t} sinh viên` }}
          scroll={{ x: 900 }}
        />
      </Card>
    </div>
  );
}
