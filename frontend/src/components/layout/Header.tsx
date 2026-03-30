import { useState } from 'react';
import {
  Layout,
  Button,
  Avatar,
  Dropdown,
  Modal,
  Form,
  Input,
  message,
  Typography,
} from 'antd';
import type { MenuProps } from 'antd';
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  UserOutlined,
  LockOutlined,
  LogoutOutlined,
} from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';
import { axiosPrivate } from '../../api/axiosInstance';
import { useAuthStore } from '../../stores/authStore';
import { Role } from '../../types/auth.types';
import NotificationBell from './NotificationBell';

const { Header: AntHeader } = Layout;
const { Text } = Typography;

interface Props {
  collapsed: boolean;
  onToggle: () => void;
  role: Role;
}

interface ChangePasswordForm {
  oldPassword: string;
  newPassword: string;
  confirmPassword: string;
}

const ROLE_PATH_PREFIX: Record<Role, string> = {
  [Role.ADMIN]: '/admin',
  [Role.TEACHER]: '/teacher',
  [Role.STUDENT]: '/student',
};

const ROUTE_TITLE_MAP: Record<string, string> = {
  // Student
  '/student/dashboard': 'Dashboard',
  '/student/profile': 'Hồ sơ cá nhân',
  '/student/enrollment': 'Đăng ký học phần',
  '/student/schedule': 'Thời khóa biểu',
  '/student/grades': 'Bảng điểm',
  '/student/program-progress': 'Tiến trình học tập',
  '/student/tuition': 'Học phí',
  '/student/payments': 'Lịch sử thanh toán',
  '/student/notifications': 'Thông báo',
  // Teacher
  '/teacher/dashboard': 'Dashboard',
  '/teacher/profile': 'Hồ sơ cá nhân',
  '/teacher/classes': 'Danh sách lớp học',
  '/teacher/notifications': 'Thông báo',
  // Admin
  '/admin/dashboard': 'Dashboard',
  '/admin/students': 'Quản lý Sinh viên',
  '/admin/teachers': 'Quản lý Giáo viên',
  '/admin/users': 'Tài khoản hệ thống',
  '/admin/classes': 'Lớp học',
  '/admin/subjects': 'Môn học',
  '/admin/departments': 'Khoa / Bộ môn',
  '/admin/programs': 'Chương trình đào tạo',
  '/admin/academic-years': 'Năm học',
  '/admin/semesters': 'Học kỳ',
  '/admin/tuition': 'Học phí',
  '/admin/payments': 'Lịch sử thanh toán',
  '/admin/notifications': 'Gửi thông báo',
};

function getPageTitle(pathname: string): string {
  // Exact match first
  if (ROUTE_TITLE_MAP[pathname]) return ROUTE_TITLE_MAP[pathname];
  // Prefix match for detail pages (e.g. /admin/students/123)
  const found = Object.keys(ROUTE_TITLE_MAP).find((key) => pathname.startsWith(key + '/'));
  return found ? ROUTE_TITLE_MAP[found] : 'Hệ thống Quản lý Sinh viên';
}

export default function AppHeader({ collapsed, onToggle, role }: Props) {
  const navigate = useNavigate();
  const location = useLocation();
  const { clearAuth, email } = useAuthStore();

  const [passwordModalOpen, setPasswordModalOpen] = useState(false);
  const [changingPassword, setChangingPassword] = useState(false);
  const [form] = Form.useForm<ChangePasswordForm>();

  const pageTitle = getPageTitle(location.pathname);

  const handleLogout = async () => {
    try {
      await axiosPrivate.post('/api/v2/auth/logout');
    } catch {
      // ignore — still clear local state
    } finally {
      clearAuth();
      navigate('/login', { replace: true });
    }
  };

  const handleChangePassword = async (values: ChangePasswordForm) => {
    setChangingPassword(true);
    try {
      await axiosPrivate.patch('/api/v2/auth/change-password', {
        oldPassword: values.oldPassword,
        newPassword: values.newPassword,
        confirmPassword: values.confirmPassword,
      });
      message.success('Đổi mật khẩu thành công!');
      form.resetFields();
      setPasswordModalOpen(false);
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: { message?: string } } };
      message.error(axiosErr?.response?.data?.message ?? 'Đổi mật khẩu thất bại!');
    } finally {
      setChangingPassword(false);
    }
  };

  const prefix = ROLE_PATH_PREFIX[role];

  const dropdownItems: MenuProps['items'] = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: 'Hồ sơ cá nhân',
      onClick: () => navigate(`${prefix}/profile`),
    },
    {
      key: 'change-password',
      icon: <LockOutlined />,
      label: 'Đổi mật khẩu',
      onClick: () => setPasswordModalOpen(true),
    },
    { type: 'divider' },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: 'Đăng xuất',
      danger: true,
      onClick: handleLogout,
    },
  ];

  return (
    <>
      <AntHeader
        style={{
          position: 'fixed',
          top: 0,
          right: 0,
          left: collapsed ? 80 : 240,
          zIndex: 99,
          height: 64,
          padding: '0 24px',
          background: '#fff',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          borderBottom: '1px solid #f0f0f0',
          transition: 'left 0.2s',
          boxShadow: '0 1px 4px rgba(0,21,41,.08)',
        }}
      >
        {/* Left: toggle + page title */}
        <div className="flex items-center gap-4">
          <Button
            type="text"
            icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            onClick={onToggle}
            style={{ fontSize: 16, width: 40, height: 40 }}
          />
          <Text strong style={{ fontSize: 16, color: '#262626' }}>
            {pageTitle}
          </Text>
        </div>

        {/* Right: notification bell + avatar */}
        <div className="flex items-center gap-3">
          <NotificationBell />

          <Dropdown menu={{ items: dropdownItems }} placement="bottomRight" arrow>
            <div
              className="flex items-center gap-2 cursor-pointer px-2 py-1 rounded-lg hover:bg-gray-50 transition-colors"
              style={{ userSelect: 'none' }}
            >
              <Avatar
                size={34}
                icon={<UserOutlined />}
                style={{ background: '#1677ff', flexShrink: 0 }}
              />
              <div className="hidden md:flex flex-col leading-tight">
                <Text style={{ fontSize: 13, fontWeight: 600, lineHeight: '16px' }}>
                  {email ?? 'Người dùng'}
                </Text>
                <Text type="secondary" style={{ fontSize: 11, lineHeight: '14px' }}>
                  {role === Role.ADMIN
                    ? 'Quản trị viên'
                    : role === Role.TEACHER
                    ? 'Giáo viên'
                    : 'Sinh viên'}
                </Text>
              </div>
            </div>
          </Dropdown>
        </div>
      </AntHeader>

      {/* Change Password Modal */}
      <Modal
        title="Đổi mật khẩu"
        open={passwordModalOpen}
        onCancel={() => {
          form.resetFields();
          setPasswordModalOpen(false);
        }}
        footer={null}
        destroyOnClose
        width={420}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleChangePassword}
          style={{ marginTop: 16 }}
        >
          <Form.Item
            label="Mật khẩu hiện tại"
            name="oldPassword"
            rules={[
              { required: true, message: 'Vui lòng nhập mật khẩu hiện tại' },
              { min: 6, message: 'Mật khẩu phải có ít nhất 6 ký tự' },
            ]}
          >
            <Input.Password placeholder="Nhập mật khẩu hiện tại" />
          </Form.Item>

          <Form.Item
            label="Mật khẩu mới"
            name="newPassword"
            rules={[
              { required: true, message: 'Vui lòng nhập mật khẩu mới' },
              { min: 6, message: 'Mật khẩu phải có ít nhất 6 ký tự' },
            ]}
          >
            <Input.Password placeholder="Nhập mật khẩu mới" />
          </Form.Item>

          <Form.Item
            label="Xác nhận mật khẩu mới"
            name="confirmPassword"
            dependencies={['newPassword']}
            rules={[
              { required: true, message: 'Vui lòng xác nhận mật khẩu mới' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('newPassword') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('Mật khẩu xác nhận không khớp!'));
                },
              }),
            ]}
          >
            <Input.Password placeholder="Nhập lại mật khẩu mới" />
          </Form.Item>

          <div className="flex justify-end gap-2 mt-2">
            <Button
              onClick={() => {
                form.resetFields();
                setPasswordModalOpen(false);
              }}
            >
              Huỷ
            </Button>
            <Button type="primary" htmlType="submit" loading={changingPassword}>
              Xác nhận
            </Button>
          </div>
        </Form>
      </Modal>
    </>
  );
}
