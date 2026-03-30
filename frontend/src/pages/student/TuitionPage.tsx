import { useEffect, useState } from 'react';
import { Card, Table, Tag, Button, Spin, Typography, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { studentApi } from '../../api/student.api';
import { paymentApi } from '../../api/payment.api';
import { StudentTuitionResponse } from '../../types/tuition.types';
import {
  TUITION_STATUS_LABEL,
  TUITION_STATUS_COLOR,
  TuitionStatus,
  formatCurrency,
  formatDate,
} from '../../types/common.types';

const { Title } = Typography;

export default function StudentTuitionPage() {
  const [tuitions, setTuitions] = useState<StudentTuitionResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [payingId, setPayingId] = useState<number | null>(null);

  useEffect(() => {
    studentApi
      .getTuition()
      .then(setTuitions)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const handlePayMomo = async (tuitionId: number) => {
    setPayingId(tuitionId);
    try {
      const result = await paymentApi.createMomoPayment(tuitionId);
      if (result.payUrl) {
        window.location.href = result.payUrl;
      } else {
        message.error('Không lấy được link thanh toán MoMo');
      }
    } catch {
      // interceptor handles
    } finally {
      setPayingId(null);
    }
  };

  const columns: ColumnsType<StudentTuitionResponse> = [
    {
      title: 'Học kỳ',
      key: 'semester',
      render: (_, r) => `${r.semester?.name} — ${r.semester?.academicYear}`,
    },
    {
      title: 'Học phí gốc',
      dataIndex: 'amount',
      key: 'amount',
      render: (v: number) => formatCurrency(v),
    },
    {
      title: 'Giảm giá',
      dataIndex: 'discount',
      key: 'discount',
      render: (v: number) => formatCurrency(v),
    },
    {
      title: 'Thực nộp',
      dataIndex: 'finalAmount',
      key: 'finalAmount',
      render: (v: number) => <strong>{formatCurrency(v)}</strong>,
    },
    {
      title: 'Hạn nộp',
      dataIndex: 'dueDate',
      key: 'dueDate',
      render: (v: string) => formatDate(v),
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      key: 'status',
      render: (s: TuitionStatus) => (
        <Tag color={TUITION_STATUS_COLOR[s]}>{TUITION_STATUS_LABEL[s]}</Tag>
      ),
    },
    {
      title: 'Thao tác',
      key: 'action',
      render: (_, r) =>
        r.status === TuitionStatus.PENDING || r.status === TuitionStatus.OVERDUE ? (
          <Button
            type="primary"
            size="small"
            loading={payingId === r.id}
            onClick={() => handlePayMomo(r.id)}
          >
            Thanh toán MoMo
          </Button>
        ) : null,
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
        Học phí
      </Title>
      <Card>
        <Table
          columns={columns}
          dataSource={tuitions.map((t) => ({ ...t, key: t.id }))}
          pagination={{ pageSize: 10 }}
          locale={{ emptyText: 'Chưa có thông tin học phí' }}
        />
      </Card>
    </div>
  );
}
