import { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { Result, Button, Card, Typography, Spin } from 'antd';

const { Text } = Typography;

export default function MomoReturnPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    // Momo sends resultCode=0 for success
    const resultCode = searchParams.get('resultCode');
    const message = searchParams.get('message');
    setSuccess(resultCode === '0');
    setLoading(false);
  }, [searchParams]);

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-50">
      <Card style={{ maxWidth: 480, width: '100%', borderRadius: 12 }}>
        {success ? (
          <Result
            status="success"
            title="Thanh toán thành công!"
            subTitle={`Mã đơn hàng: ${searchParams.get('orderId') ?? '—'} — Mã giao dịch: ${
              searchParams.get('transId') ?? '—'
            }`}
            extra={[
              <Button
                key="payment"
                type="primary"
                onClick={() => navigate('/student/payments')}
              >
                Xem lịch sử thanh toán
              </Button>,
              <Button key="home" onClick={() => navigate('/student/dashboard')}>
                Về trang chủ
              </Button>,
            ]}
          />
        ) : (
          <Result
            status="error"
            title="Thanh toán thất bại"
            subTitle={searchParams.get('message') ?? 'Giao dịch không thành công. Vui lòng thử lại.'}
            extra={[
              <Button
                key="tuition"
                type="primary"
                onClick={() => navigate('/student/tuition')}
              >
                Quay lại học phí
              </Button>,
              <Button key="home" onClick={() => navigate('/student/dashboard')}>
                Về trang chủ
              </Button>,
            ]}
          />
        )}
        <div className="text-center mt-2">
          <Text type="secondary" style={{ fontSize: 12 }}>
            {success ? '' : `Mã lỗi: ${searchParams.get('resultCode') ?? '—'}`}
          </Text>
        </div>
      </Card>
    </div>
  );
}
