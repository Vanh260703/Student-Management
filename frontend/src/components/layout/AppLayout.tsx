import { useState } from 'react';
import { Layout } from 'antd';
import { Outlet } from 'react-router-dom';
import { Role } from '../../types/auth.types';
import Sidebar from './Sidebar';
import AppHeader from './Header';

const { Content } = Layout;

interface Props {
  role: Role;
}

export default function AppLayout({ role }: Props) {
  const [collapsed, setCollapsed] = useState(false);

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sidebar role={role} collapsed={collapsed} onCollapse={setCollapsed} />

      <Layout
        style={{
          marginLeft: collapsed ? 80 : 240,
          transition: 'margin-left 0.2s',
        }}
      >
        <AppHeader
          collapsed={collapsed}
          onToggle={() => setCollapsed((prev) => !prev)}
          role={role}
        />

        <Content
          style={{
            margin: '24px',
            marginTop: 88,
            padding: '24px',
            background: '#fff',
            borderRadius: 8,
            minHeight: 280,
          }}
        >
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
