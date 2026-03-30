import { useEffect, useState } from 'react';
import { Card, Table, Button, Typography, Popconfirm, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { programApi, Program } from '../../api/program.api';

const { Title } = Typography;

export default function AdminProgramsPage() {
  const [programs, setPrograms] = useState<Program[]>([]);
  const [loading, setLoading] = useState(true);
  const [deletingId, setDeletingId] = useState<number | null>(null);

  useEffect(() => {
    programApi
      .getPrograms()
      .then(setPrograms)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const handleDelete = async (id: number) => {
    setDeletingId(id);
    try {
      await programApi.deleteProgram(id);
      message.success('Đã xoá chương trình!');
      setPrograms((prev) => prev.filter((p) => p.id !== id));
    } catch {
      // interceptor handles
    } finally {
      setDeletingId(null);
    }
  };

  const columns: ColumnsType<Program> = [
    { title: 'Mã CT', dataIndex: 'code', key: 'code', width: 110 },
    { title: 'Tên chương trình', dataIndex: 'name', key: 'name' },
    { title: 'Mô tả', dataIndex: 'description', key: 'description' },
    { title: 'Tổng TC', dataIndex: 'totalCredits', key: 'totalCredits', width: 80, align: 'center' },
    { title: 'Thời gian (năm)', dataIndex: 'durationYears', key: 'durationYears', width: 120, align: 'center' },
    {
      title: 'Thao tác',
      key: 'action',
      width: 80,
      render: (_, r) => (
        <Popconfirm
          title="Xác nhận xoá chương trình này?"
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
        Chương trình đào tạo
      </Title>
      <Card>
        <Table
          columns={columns}
          dataSource={programs.map((p) => ({ ...p, key: p.id }))}
          loading={loading}
          pagination={{ pageSize: 15 }}
        />
      </Card>
    </div>
  );
}
