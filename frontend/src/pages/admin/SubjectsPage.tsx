import { useEffect, useState } from 'react';
import { Card, Table, Tag, Button, Input, Space, Typography, Popconfirm, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { subjectApi, Subject } from '../../api/subject.api';

const { Title } = Typography;
const { Search } = Input;

export default function AdminSubjectsPage() {
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [loading, setLoading] = useState(true);
  const [deletingId, setDeletingId] = useState<number | null>(null);

  const fetchSubjects = (search?: string) => {
    setLoading(true);
    subjectApi
      .getSubjects({ search })
      .then(setSubjects)
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchSubjects();
  }, []);

  const handleDelete = async (id: number) => {
    setDeletingId(id);
    try {
      await subjectApi.deleteSubject(id);
      message.success('Đã xoá môn học!');
      setSubjects((prev) => prev.filter((s) => s.id !== id));
    } catch {
      // interceptor handles
    } finally {
      setDeletingId(null);
    }
  };

  const columns: ColumnsType<Subject> = [
    { title: 'Mã môn', dataIndex: 'code', key: 'code', width: 110 },
    { title: 'Tên môn học', dataIndex: 'name', key: 'name' },
    { title: 'Khoa', dataIndex: 'departmentName', key: 'departmentName' },
    { title: 'Tín chỉ', dataIndex: 'credits', key: 'credits', width: 80, align: 'center' },
    {
      title: 'Trạng thái',
      dataIndex: 'isActive',
      key: 'isActive',
      width: 100,
      render: (v: boolean) => <Tag color={v ? 'green' : 'default'}>{v ? 'Hoạt động' : 'Ẩn'}</Tag>,
    },
    {
      title: 'Thao tác',
      key: 'action',
      width: 80,
      render: (_, r) => (
        <Popconfirm
          title="Xác nhận xoá môn học này?"
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
        Quản lý Môn học
      </Title>
      <Card>
        <Space className="mb-4">
          <Search
            placeholder="Tìm kiếm môn học..."
            onSearch={fetchSubjects}
            allowClear
            style={{ width: 280 }}
          />
        </Space>
        <Table
          columns={columns}
          dataSource={subjects.map((s) => ({ ...s, key: s.id }))}
          loading={loading}
          pagination={{ pageSize: 15, showTotal: (t) => `Tổng ${t} môn học` }}
        />
      </Card>
    </div>
  );
}
