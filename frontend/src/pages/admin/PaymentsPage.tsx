import { useEffect, useState } from 'react';
import { Card, Table, Tag, Typography } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { adminApi } from '../../api/admin.api';
import { AdminPaymentResponse } from '../../types/tuition.types';
import {
  PAYMENT_STATUS_LABEL,
  PAYMENT_STATUS_COLOR,
  PAYMENT_METHOD_LABEL,
  PaymentStatus,
  formatCurrency,
  formatDateTime,
} from '../../types/common.types';

const { Title } = Typography;

export default function AdminPaymentsPage() {
  const [payments, setPayments] = useState<AdminPaymentResponse[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminApi
      .getPayments()
      .then(setPayments)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const columns: ColumnsType<AdminPaymentResponse> = [
    { title: 'Mã GD', dataIndex: 'transactionCode', key: 'transactionCode', width: 160 },
    { title: 'Sinh viên', key: 'student', render: (_, r) => `${r.student?.studentCode} — ${r.student?.fullName}` },
    { title: 'Học kỳ', key: 'semester', render: (_, r) => `${r.tuition?.semester?.name} — ${r.tuition?.semester?.academicYear}` },
    { title: 'Số tiền', dataIndex: 'amount', key: 'amount', render: (v: number) => formatCurrency(v) },
    { title: 'Phương thức', dataIndex: 'method', key: 'method', render: (m) => PAYMENT_METHOD_LABEL[m] ?? m },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      key: 'status',
      render: (s: PaymentStatus) => (
        <Tag color={PAYMENT_STATUS_COLOR[s]}>{PAYMENT_STATUS_LABEL[s]}</Tag>
      ),
    },
    { title: 'Ngày thanh toán', dataIndex: 'paidAt', key: 'paidAt', render: (v: string | null) => v ? formatDateTime(v) : '—' },
    { title: 'Ngày tạo', dataIndex: 'createdAt', key: 'createdAt', render: (v: string) => formatDateTime(v) },
  ];

  return (
    <div>
      <Title level={4} style={{ marginBottom: 16 }}>
        Lịch sử thanh toán
      </Title>
      <Card>
        <Table
          columns={columns}
          dataSource={payments.map((p) => ({ ...p, key: p.paymentId }))}
          loading={loading}
          pagination={{ pageSize: 15, showTotal: (t) => `Tổng ${t} giao dịch` }}
          scroll={{ x: 1100 }}
        />
      </Card>
    </div>
  );
}
