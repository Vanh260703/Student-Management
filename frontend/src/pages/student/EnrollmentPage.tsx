import { useEffect, useState } from 'react';
import { Card, Table, Tag, Button, Spin, Input, Space, message, Typography, Tabs } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { enrollmentApi } from '../../api/enrollment.api';
import { ClassResponse } from '../../types/class.types';
import { Enrollment } from '../../types/enrollment.types';
import {
  CLASS_STATUS_LABEL,
  CLASS_STATUS_COLOR,
  ENROLLMENT_STATUS_LABEL,
} from '../../types/common.types';

const { Title } = Typography;
const { Search } = Input;

export default function EnrollmentPage() {
  const [available, setAvailable] = useState<ClassResponse[]>([]);
  const [enrollments, setEnrollments] = useState<Enrollment[]>([]);
  const [loadingAvailable, setLoadingAvailable] = useState(true);
  const [loadingEnrollments, setLoadingEnrollments] = useState(true);
  const [enrollingId, setEnrollingId] = useState<number | null>(null);
  const [droppingId, setDroppingId] = useState<number | null>(null);
  const [search, setSearch] = useState('');

  const fetchAvailable = (q?: string) => {
    setLoadingAvailable(true);
    enrollmentApi
      .getAvailableClasses({ notEnrolled: true, search: q })
      .then(setAvailable)
      .catch(() => {})
      .finally(() => setLoadingAvailable(false));
  };

  const fetchEnrollments = () => {
    setLoadingEnrollments(true);
    enrollmentApi
      .getMyEnrollments()
      .then(setEnrollments)
      .catch(() => {})
      .finally(() => setLoadingEnrollments(false));
  };

  useEffect(() => {
    fetchAvailable();
    fetchEnrollments();
  }, []);

  const handleEnroll = async (classId: number) => {
    setEnrollingId(classId);
    try {
      await enrollmentApi.enrollClass(classId);
      message.success('Đăng ký lớp thành công!');
      fetchAvailable(search);
      fetchEnrollments();
    } catch {
      // interceptor handles
    } finally {
      setEnrollingId(null);
    }
  };

  const handleDrop = async (enrollmentId: number) => {
    setDroppingId(enrollmentId);
    try {
      await enrollmentApi.dropEnrollment(enrollmentId);
      message.success('Huỷ đăng ký thành công!');
      fetchEnrollments();
      fetchAvailable(search);
    } catch {
      // interceptor handles
    } finally {
      setDroppingId(null);
    }
  };

  const availableColumns: ColumnsType<ClassResponse> = [
    { title: 'Mã lớp', dataIndex: 'classCode', key: 'classCode', width: 120 },
    {
      title: 'Môn học',
      key: 'subject',
      render: (_, r) => r.subjectResponse?.name,
    },
    {
      title: 'Tín chỉ',
      key: 'credits',
      width: 80,
      align: 'center',
      render: (_, r) => r.subjectResponse?.credits,
    },
    {
      title: 'Giáo viên',
      key: 'teacher',
      render: (_, r) => r.teacherResponse?.fullName,
    },
    { title: 'Phòng', dataIndex: 'room', key: 'room', width: 90 },
    {
      title: 'Sĩ số',
      key: 'slots',
      width: 90,
      align: 'center',
      render: (_, r) => `${r.currentStudents}/${r.maxStudents}`,
    },
    {
      title: 'Trạng thái',
      key: 'status',
      width: 110,
      render: (_, r) => (
        <Tag color={CLASS_STATUS_COLOR[r.status]}>{CLASS_STATUS_LABEL[r.status]}</Tag>
      ),
    },
    {
      title: 'Thao tác',
      key: 'action',
      width: 120,
      render: (_, r) => (
        <Button
          type="primary"
          size="small"
          loading={enrollingId === r.id}
          disabled={r.currentStudents >= r.maxStudents}
          onClick={() => handleEnroll(r.id)}
        >
          Đăng ký
        </Button>
      ),
    },
  ];

  const enrollmentColumns: ColumnsType<Enrollment> = [
    {
      title: 'Mã lớp',
      key: 'classCode',
      render: (_, r) => r.classEntity?.classCode,
      width: 120,
    },
    {
      title: 'Môn học',
      key: 'subject',
      render: (_, r) => r.classEntity?.subjectResponse?.name,
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (s) => <Tag>{ENROLLMENT_STATUS_LABEL[s]}</Tag>,
    },
    {
      title: 'Thao tác',
      key: 'action',
      width: 120,
      render: (_, r) =>
        r.status === 'ENROLLED' ? (
          <Button
            danger
            size="small"
            loading={droppingId === r.id}
            onClick={() => handleDrop(r.id)}
          >
            Huỷ đăng ký
          </Button>
        ) : null,
    },
  ];

  return (
    <div>
      <Title level={4} style={{ marginBottom: 16 }}>
        Đăng ký học phần
      </Title>
      <Tabs
        defaultActiveKey="available"
        items={[
          {
            key: 'available',
            label: 'Lớp có thể đăng ký',
            children: (
              <Card>
                <Space className="mb-4">
                  <Search
                    placeholder="Tìm kiếm lớp học..."
                    onSearch={(v) => {
                      setSearch(v);
                      fetchAvailable(v);
                    }}
                    allowClear
                    style={{ width: 280 }}
                  />
                </Space>
                <Table
                  columns={availableColumns}
                  dataSource={available.map((r) => ({ ...r, key: r.id }))}
                  loading={loadingAvailable}
                  pagination={{ pageSize: 10 }}
                  scroll={{ x: 800 }}
                />
              </Card>
            ),
          },
          {
            key: 'my',
            label: 'Lớp đã đăng ký',
            children: (
              <Card>
                <Table
                  columns={enrollmentColumns}
                  dataSource={enrollments.map((e) => ({ ...e, key: e.id }))}
                  loading={loadingEnrollments}
                  pagination={{ pageSize: 10 }}
                />
              </Card>
            ),
          },
        ]}
      />
    </div>
  );
}
