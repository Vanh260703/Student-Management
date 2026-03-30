import { useEffect, useState } from 'react';
import { Card, Table, Tag, Button, Typography, Space, Popconfirm, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { adminApi } from '../../api/admin.api';
import { TuitionResponse, GenerateTuitionResponse } from '../../types/tuition.types';
import {
  TUITION_STATUS_LABEL,
  TUITION_STATUS_COLOR,
  TuitionStatus,
  formatCurrency,
  formatDate,
} from '../../types/common.types';

const { Title } = Typography;

export default function AdminTuitionPage() {
  const [tuitions, setTuitions] = useState<TuitionResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [genResult, setGenResult] = useState<GenerateTuitionResponse | null>(null);

  useEffect(() => {
    adminApi
      .getTuitions()
      .then(setTuitions)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const handleGenerate = async () => {
    setGenerating(true);
    try {
      const result = await adminApi.generateTuition();
      setGenResult(result);
      message.success(`Đã tạo ${result.generatedCount} hoá đơn học phí!`);
      adminApi.getTuitions().then(setTuitions).catch(() => {});
    } catch {
      // interceptor handles
    } finally {
      setGenerating(false);
    }
  };

  const columns: ColumnsType<TuitionResponse> = [
    { title: 'Mã SV', key: 'studentCode', render: (_, r) => r.student?.studentCode, width: 120 },
    { title: 'Họ tên', key: 'studentName', render: (_, r) => r.student?.fullName },
    { title: 'Học kỳ', key: 'semester', render: (_, r) => `${r.semester?.name} — ${r.semester?.academicYear}` },
    { title: 'Học phí', dataIndex: 'amount', key: 'amount', render: (v: number) => formatCurrency(v) },
    { title: 'Giảm giá', dataIndex: 'discount', key: 'discount', render: (v: number) => formatCurrency(v) },
    { title: 'Thực nộp', dataIndex: 'finalAmount', key: 'finalAmount', render: (v: number) => <strong>{formatCurrency(v)}</strong> },
    { title: 'Hạn nộp', dataIndex: 'dueDate', key: 'dueDate', render: (v: string) => formatDate(v) },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      key: 'status',
      render: (s: TuitionStatus) => (
        <Tag color={TUITION_STATUS_COLOR[s]}>{TUITION_STATUS_LABEL[s]}</Tag>
      ),
    },
  ];

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <Title level={4} style={{ marginBottom: 0 }}>
          Quản lý Học phí
        </Title>
        <Popconfirm
          title="Tạo học phí cho tất cả sinh viên học kỳ hiện tại?"
          okText="Tạo"
          cancelText="Huỷ"
          onConfirm={handleGenerate}
        >
          <Button type="primary" loading={generating}>
            Tạo học phí tự động
          </Button>
        </Popconfirm>
      </div>
      {genResult && (
        <Card className="mb-4" size="small">
          <Space wrap>
            <span>Học kỳ: <strong>{genResult.semesterName}</strong></span>
            <span>Đã tạo: <strong>{genResult.generatedCount}</strong></span>
            <span>Bỏ qua: <strong>{genResult.skippedCount}</strong></span>
            <span>Tổng SV: <strong>{genResult.totalStudents}</strong></span>
          </Space>
        </Card>
      )}
      <Card>
        <Table
          columns={columns}
          dataSource={tuitions.map((t) => ({ ...t, key: t.id }))}
          loading={loading}
          pagination={{ pageSize: 15, showTotal: (t) => `Tổng ${t} hoá đơn` }}
          scroll={{ x: 1000 }}
        />
      </Card>
    </div>
  );
}
