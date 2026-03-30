import { useEffect, useState } from 'react';
import { Card, Table, Tag, Spin, Typography } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { studentApi } from '../../api/student.api';
import { StudentPaymentHistoryResponse } from '../../types/tuition.types';
import {
  PAYMENT_STATUS_LABEL,
  PAYMENT_STATUS_COLOR,
  PAYMENT_METHOD_LABEL,
  PaymentStatus,
  formatCurrency,
  formatDateTime,
} from '../../types/common.types';

const { Title } = Typography;

export default function StudentPaymentsPage() {
  const [payments, setPayments] = useState<StudentPaymentHistoryResponse[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    studentApi
      .getPayments()
      .then(setPayments)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const columns: ColumnsType<StudentPaymentHistoryResponse> = [
    { title: 'Mã giao dịch', dataIndex: 'transactionCode', key: 'transactionCode', width: 160 },
    {
      title: 'Học kỳ',
      key: 'semester',
      render: (_, r) => `${r.tuition?.semester?.name} — ${r.tuition?.semester?.academicYear}`,
    },
    {
      title: 'Số tiền',
      dataIndex: 'amount',
      key: 'amount',
      render: (v: number) => formatCurrency(v),
    },
    {
      title: 'Phương thức',
      dataIndex: 'method',
      key: 'method',
      render: (m) => PAYMENT_METHOD_LABEL[m] ?? m,
    },
    {
      title: 'Trạng thái',
      dataIndex: 'paymentStatus',
      key: 'paymentStatus',
      render: (s: PaymentStatus) => (
        <Tag color={PAYMENT_STATUS_COLOR[s]}>{PAYMENT_STATUS_LABEL[s]}</Tag>
      ),
    },
    {
      title: 'Thời gian thanh toán',
      dataIndex: 'paidAt',
      key: 'paidAt',
      render: (v: string | null) => (v ? formatDateTime(v) : '—'),
    },
    {
      title: 'Ngày tạo',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (v: string) => formatDateTime(v),
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
        Lịch sử thanh toán
      </Title>
      <Card>
        <Table
          columns={columns}
          dataSource={payments.map((p) => ({ ...p, key: p.paymentId }))}
          pagination={{ pageSize: 10, showTotal: (t) => `Tổng ${t} giao dịch` }}
          scroll={{ x: 900 }}
          locale={{ emptyText: 'Chưa có lịch sử thanh toán' }}
        />
      </Card>
    </div>
  );
}
