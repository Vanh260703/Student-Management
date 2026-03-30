import { Spin } from 'antd';

export default function LoadingScreen() {
  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-50">
      <div className="text-center">
        <Spin size="large" />
        <p className="mt-4 text-gray-500 text-base">Đang khởi động...</p>
      </div>
    </div>
  );
}
