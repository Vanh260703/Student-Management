import { useEffect, useState } from 'react';
import { Card, Table, Button, Typography, Popconfirm, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { departmentApi, Department } from '../../api/department.api';

const { Title } = Typography;

export default function AdminDepartmentsPage() {
  const [departments, setDepartments] = useState<Department[]>([]);
  const [loading, setLoading] = useState(true);
  const [deletingId, setDeletingId] = useState<number | null>(null);

  useEffect(() => {
    departmentApi
      .getDepartments()
      .then(setDepartments)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const handleDelete = async (id: number) => {
    setDeletingId(id);
    try {
      await departmentApi.deleteDepartment(id);
      message.success('Đã xoá khoa!');
      setDepartments((prev) => prev.filter((d) => d.id !== id));
    } catch {
      // interceptor handles
    } finally {
      setDeletingId(null);
    }
  };

  const columns: ColumnsType<Department> = [
    { title: 'Mã khoa', dataIndex: 'code', key: 'code', width: 110 },
    { title: 'Tên khoa', dataIndex: 'name', key: 'name' },
    { title: 'Mô tả', dataIndex: 'description', key: 'description' },
    {
      title: 'Trưởng khoa',
      key: 'headTeacher',
      render: (_, r) => r.headTeacher?.fullName ?? '—',
    },
    {
      title: 'Thao tác',
      key: 'action',
      width: 80,
      render: (_, r) => (
        <Popconfirm
          title="Xác nhận xoá khoa này?"
          okText="Xoá"
          okType="danger"
          cancelText="Huỷ"
          onConfirm={() => handleDelete(r.id)}
        >
          <Button danger size="small" loading={deletingId === r.id}>
            Xoá
          </Button>
        </Popconfirm>
      ),
    },
  ];

  return (
    <div>
      <Title level={4} style={{ marginBottom: 16 }}>
        Quản lý Khoa / Bộ môn
      </Title>
      <Card>
        <Table
          columns={columns}
          dataSource={departments.map((d) => ({ ...d, key: d.id }))}
          loading={loading}
          pagination={{ pageSize: 15 }}
        />
      </Card>
    </div>
  );
}
