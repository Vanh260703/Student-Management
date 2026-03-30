import { useEffect, useState } from 'react';
import { Layout, Menu, Badge } from 'antd';
import type { MenuProps } from 'antd';
import {
  DashboardOutlined,
  UserOutlined,
  FormOutlined,
  CalendarOutlined,
  BookOutlined,
  BarChartOutlined,
  DollarOutlined,
  CreditCardOutlined,
  BellOutlined,
  TeamOutlined,
  SolutionOutlined,
  KeyOutlined,
  ReadOutlined,
  BankOutlined,
  ApartmentOutlined,
  ScheduleOutlined,
  NotificationOutlined,
} from '@ant-design/icons';
import { useLocation, useNavigate } from 'react-router-dom';
import { Role } from '../../types/auth.types';
import { axiosPrivate } from '../../api/axiosInstance';

const { Sider } = Layout;

interface Props {
  role: Role;
  collapsed: boolean;
  onCollapse: (collapsed: boolean) => void;
}

type MenuItem = Required<MenuProps>['items'][number];

function makeItem(
  label: React.ReactNode,
  key: string,
  icon?: React.ReactNode,
  children?: MenuItem[],
  type?: 'group'
): MenuItem {
  return { key, icon, children, label, type } as MenuItem;
}

function useUnreadCount() {
  const [count, setCount] = useState(0);

  useEffect(() => {
    const fetch = async () => {
      try {
        const res = await axiosPrivate.get('/api/v2/notifications/unread/count');
        setCount(res.data.result ?? 0);
      } catch {
        // ignore
      }
    };
    fetch();
    const id = setInterval(fetch, 30_000);
    return () => clearInterval(id);
  }, []);

  return count;
}

function getStudentItems(unreadCount: number): MenuItem[] {
  return [
    makeItem('Dashboard', '/student/dashboard', <DashboardOutlined />),
    makeItem('Hồ sơ cá nhân', '/student/profile', <UserOutlined />),
    makeItem('Đăng ký học phần', '/student/enrollment', <FormOutlined />),
    makeItem('Thời khóa biểu', '/student/schedule', <CalendarOutlined />),
    makeItem('Bảng điểm', '/student/grades', <BookOutlined />),
    makeItem('Tiến trình học tập', '/student/program-progress', <BarChartOutlined />),
    makeItem('Học phí', '/student/tuition', <DollarOutlined />),
    makeItem('Lịch sử thanh toán', '/student/payments', <CreditCardOutlined />),
    makeItem(
      <span className="flex items-center gap-2">
        Thông báo
        {unreadCount > 0 && (
          <Badge count={unreadCount} size="small" style={{ marginLeft: 4 }} />
        )}
      </span>,
      '/student/notifications',
      <BellOutlined />
    ),
  ];
}

function getTeacherItems(unreadCount: number): MenuItem[] {
  return [
    makeItem('Dashboard', '/teacher/dashboard', <DashboardOutlined />),
    makeItem('Hồ sơ cá nhân', '/teacher/profile', <UserOutlined />),
    makeItem('Danh sách lớp học', '/teacher/classes', <TeamOutlined />),
    makeItem(
      <span className="flex items-center gap-2">
        Thông báo
        {unreadCount > 0 && (
          <Badge count={unreadCount} size="small" style={{ marginLeft: 4 }} />
        )}
      </span>,
      '/teacher/notifications',
      <BellOutlined />
    ),
  ];
}

function getAdminItems(): MenuItem[] {
  return [
    makeItem('Dashboard', '/admin/dashboard', <DashboardOutlined />),
    makeItem(
      'QUẢN LÝ NGƯỜI DÙNG',
      'group-users',
      undefined,
      [
        makeItem('Sinh viên', '/admin/students', <TeamOutlined />),
        makeItem('Giáo viên', '/admin/teachers', <SolutionOutlined />),
        makeItem('Tài khoản hệ thống', '/admin/users', <KeyOutlined />),
      ],
      'group'
    ),
    makeItem(
      'HỌC VỤ',
      'group-academic',
      undefined,
      [
        makeItem('Lớp học', '/admin/classes', <ReadOutlined />),
        makeItem('Môn học', '/admin/subjects', <BookOutlined />),
        makeItem('Khoa / Bộ môn', '/admin/departments', <BankOutlined />),
        makeItem('Chương trình đào tạo', '/admin/programs', <ApartmentOutlined />),
      ],
      'group'
    ),
    makeItem(
      'HỌC KỲ',
      'group-semester',
      undefined,
      [
        makeItem('Năm học', '/admin/academic-years', <CalendarOutlined />),
        makeItem('Học kỳ', '/admin/semesters', <ScheduleOutlined />),
      ],
      'group'
    ),
    makeItem(
      'TÀI CHÍNH',
      'group-finance',
      undefined,
      [
        makeItem('Học phí', '/admin/tuition', <DollarOutlined />),
        makeItem('Lịch sử thanh toán', '/admin/payments', <CreditCardOutlined />),
      ],
      'group'
    ),
    makeItem(
      'HỆ THỐNG',
      'group-system',
      undefined,
      [makeItem('Gửi thông báo', '/admin/notifications', <NotificationOutlined />)],
      'group'
    ),
  ];
}

export default function Sidebar({ role, collapsed, onCollapse }: Props) {
  const location = useLocation();
  const navigate = useNavigate();
  const unreadCount = useUnreadCount();

  const selectedKey = location.pathname;

  const items =
    role === Role.STUDENT
      ? getStudentItems(unreadCount)
      : role === Role.TEACHER
      ? getTeacherItems(unreadCount)
      : getAdminItems();

  const handleMenuClick: MenuProps['onClick'] = ({ key }) => {
    navigate(key);
  };

  return (
    <Sider
      collapsible
      collapsed={collapsed}
      onCollapse={onCollapse}
      width={240}
      style={{
        overflow: 'auto',
        height: '100vh',
        position: 'fixed',
        left: 0,
        top: 0,
        bottom: 0,
        zIndex: 100,
        background: '#001529',
      }}
      trigger={null}
    >
      {/* Logo */}
      <div
        style={{
          height: 64,
          display: 'flex',
          alignItems: 'center',
          justifyContent: collapsed ? 'center' : 'flex-start',
          padding: collapsed ? 0 : '0 20px',
          borderBottom: '1px solid rgba(255,255,255,0.08)',
          transition: 'all 0.2s',
        }}
      >
        <div
          style={{
            width: 32,
            height: 32,
            borderRadius: 8,
            background: 'linear-gradient(135deg, #1677ff 0%, #0958d9 100%)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            flexShrink: 0,
          }}
        >
          <span style={{ color: '#fff', fontWeight: 800, fontSize: 14 }}>Q</span>
        </div>
        {!collapsed && (
          <div style={{ marginLeft: 10 }}>
            <div style={{ color: '#fff', fontWeight: 700, fontSize: 15, lineHeight: 1.2 }}>
              QLSV
            </div>
            <div style={{ color: 'rgba(255,255,255,0.45)', fontSize: 11, lineHeight: 1.2 }}>
              Quản lý Sinh viên
            </div>
          </div>
        )}
      </div>

      <Menu
        theme="dark"
        mode="inline"
        selectedKeys={[selectedKey]}
        items={items}
        onClick={handleMenuClick}
        style={{ borderRight: 0, marginTop: 8 }}
      />
    </Sider>
  );
}
