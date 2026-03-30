import { useEffect, useState } from 'react';
import { Card, Table, Tag, Typography } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { academicYearApi, AcademicYear } from '../../api/academicYear.api';
import { formatDate } from '../../types/common.types';

const { Title } = Typography;

export default function AdminAcademicYearsPage() {
  const [years, setYears] = useState<AcademicYear[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    academicYearApi
      .getAcademicYears()
      .then(setYears)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const columns: ColumnsType<AcademicYear> = [
    { title: 'Năm học', dataIndex: 'name', key: 'name' },
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
      title: 'Hiện tại',
      dataIndex: 'isCurrent',
      key: 'isCurrent',
      width: 100,
      render: (v: boolean) => (v ? <Tag color="green">Hiện tại</Tag> : <Tag>Cũ</Tag>),
    },
  ];

  return (
    <div>
      <Title level={4} style={{ marginBottom: 16 }}>
        Quản lý Năm học
      </Title>
      <Card>
        <Table
          columns={columns}
          dataSource={years.map((y) => ({ ...y, key: y.id }))}
          loading={loading}
          pagination={{ pageSize: 15 }}
        />
      </Card>
    </div>
  );
}
