import { useEffect, useState } from 'react';
import { Card, Table, Tag, Button, Input, Space, Typography, Popconfirm, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { adminApi } from '../../api/admin.api';
import { UserInfo } from '../../types/auth.types';
import { ROLE_LABEL } from '../../types/common.types';

const { Title } = Typography;
const { Search } = Input;

export default function AdminUsersPage() {
  const [users, setUsers] = useState<UserInfo[]>([]);
  const [loading, setLoading] = useState(true);
  const [togglingId, setTogglingId] = useState<number | null>(null);
  const [deletingId, setDeletingId] = useState<number | null>(null);

  const fetchUsers = (search?: string) => {
    setLoading(true);
    adminApi
      .getUsers({ search })
      .then(setUsers)
      .catch(() => {})
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleToggleActive = async (id: number) => {
    setTogglingId(id);
    try {
      await adminApi.toggleUserActive(id);
      message.success('Đã cập nhật trạng thái!');
      fetchUsers();
    } catch {
      // interceptor handles
    } finally {
      setTogglingId(null);
    }
  };

  const handleDelete = async (id: number) => {
    setDeletingId(id);
    try {
      await adminApi.deleteUser(id);
      message.success('Đã xoá tài khoản!');
      setUsers((prev) => prev.filter((u) => u.id !== id));
    } catch {
      // interceptor handles
    } finally {
      setDeletingId(null);
    }
  };

  const columns: ColumnsType<UserInfo> = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
    { title: 'Họ tên', dataIndex: 'fullName', key: 'fullName' },
    { title: 'Email', dataIndex: 'email', key: 'email' },
    { title: 'Điện thoại', dataIndex: 'phone', key: 'phone', width: 130 },
    {
      title: 'Vai trò',
      dataIndex: 'role',
      key: 'role',
      width: 130,
      render: (r: string) => <Tag>{ROLE_LABEL[r] ?? r}</Tag>,
    },
    {
      title: 'Thao tác',
      key: 'action',
      width: 180,
      render: (_, r) => (
        <Space>
          <Button
            size="small"
            loading={togglingId === r.id}
            onClick={() => handleToggleActive(r.id)}
          >
            Khoá / Mở
          </Button>
          <Popconfirm
            title="Xác nhận xoá tài khoản này?"
            okText="Xoá"
            okType="danger"
            cancelText="Huỷ"
            onConfirm={() => handleDelete(r.id)}
          >
            <Button danger size="small" loading={deletingId === r.id}>
              Xoá
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <Title level={4} style={{ marginBottom: 16 }}>
        Tài khoản hệ thống
      </Title>
      <Card>
        <Space className="mb-4">
          <Search
            placeholder="Tìm kiếm tài khoản..."
            onSearch={fetchUsers}
            allowClear
            style={{ width: 280 }}
          />
        </Space>
        <Table
          columns={columns}
          dataSource={users.map((u) => ({ ...u, key: u.id }))}
          loading={loading}
          pagination={{ pageSize: 15, showTotal: (t) => `Tổng ${t} tài khoản` }}
          scroll={{ x: 800 }}
        />
      </Card>
    </div>
  );
}
