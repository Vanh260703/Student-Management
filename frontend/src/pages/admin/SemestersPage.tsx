import { useEffect, useState } from 'react';
import { Card, Table, Tag, Button, Typography, message, Space } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { semesterApi, Semester } from '../../api/semester.api';
import { formatDate } from '../../types/common.types';

const { Title } = Typography;

export default function AdminSemestersPage() {
  const [semesters, setSemesters] = useState<Semester[]>([]);
  const [loading, setLoading] = useState(true);
  const [togglingId, setTogglingId] = useState<number | null>(null);

  useEffect(() => {
    semesterApi
      .getSemesters()
      .then(setSemesters)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const handleToggle = async (id: number) => {
    setTogglingId(id);
    try {
      await semesterApi.toggleSemesterActive(id);
      message.success('Đã cập nhật trạng thái học kỳ!');
      setSemesters((prev) =>
        prev.map((s) => (s.id === id ? { ...s, isActive: !s.isActive } : s)),
      );
    } catch {
      // interceptor handles
    } finally {
      setTogglingId(null);
    }
  };

  const columns: ColumnsType<Semester> = [
    { title: 'Tên HK', dataIndex: 'name', key: 'name', width: 100 },
    {
      title: 'Năm học',
      key: 'academicYear',
      render: (_, r) => r.academicYear?.name,
    },
    {
      title: 'Bắt đầu',
      dataIndex: 'startDate',
      key: 'startDate',
      render: (v: string) => formatDate(v),
    },
    {
      title: 'Kết thúc',
      dataIndex: 'endDate',
      key: 'endDate',
      render: (v: string) => formatDate(v),
    },
    {
      title: 'Đăng ký từ',
      dataIndex: 'registrationStart',
      key: 'registrationStart',
      render: (v: string) => formatDate(v),
    },
    {
      title: 'Đăng ký đến',
      dataIndex: 'registrationEnd',
      key: 'registrationEnd',
      render: (v: string) => formatDate(v),
    },
    {
      title: 'Trạng thái',
      dataIndex: 'isActive',
      key: 'isActive',
      width: 110,
      render: (v: boolean) => (
        <Tag color={v ? 'green' : 'default'}>{v ? 'Đang hoạt động' : 'Kết thúc'}</Tag>
      ),
    },
    {
      title: 'Thao tác',
      key: 'action',
      width: 120,
      render: (_, r) => (
        <Button
          size="small"
          loading={togglingId === r.id}
          onClick={() => handleToggle(r.id)}
        >
          {r.isActive ? 'Kết thúc' : 'Kích hoạt'}
        </Button>
      ),
    },
  ];

  return (
    <div>
      <Title level={4} style={{ marginBottom: 16 }}>
        Quản lý Học kỳ
      </Title>
      <Card>
        <Table
          columns={columns}
          dataSource={semesters.map((s) => ({ ...s, key: s.id }))}
          loading={loading}
          pagination={{ pageSize: 15 }}
          scroll={{ x: 900 }}
        />
      </Card>
    </div>
  );
}
