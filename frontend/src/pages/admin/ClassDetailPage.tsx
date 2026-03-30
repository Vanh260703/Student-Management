import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Card,
  Row,
  Col,
  Spin,
  Tag,
  Button,
  Table,
  Space,
  Typography,
  Select,
  message,
  Popconfirm,
} from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { classApi } from '../../api/class.api';
import { ClassResponse } from '../../types/class.types';
import { StudentProfile } from '../../types/student.types';
import {
  CLASS_STATUS_LABEL,
  CLASS_STATUS_COLOR,
  ClassStatus,
} from '../../types/common.types';

const { Title, Text } = Typography;

export default function AdminClassDetailPage() {
  const { classId } = useParams<{ classId: string }>();
  const navigate = useNavigate();
  const numId = Number(classId);
  const [classData, setClassData] = useState<ClassResponse | null>(null);
  const [students, setStudents] = useState<StudentProfile[]>([]);
  const [loading, setLoading] = useState(true);
  const [loadingStudents, setLoadingStudents] = useState(true);
  const [changingStatus, setChangingStatus] = useState(false);

  useEffect(() => {
    classApi
      .getClass(numId)
      .then(setClassData)
      .catch(() => {})
      .finally(() => setLoading(false));

    classApi
      .getClassStudents(numId)
      .then(setStudents)
      .catch(() => {})
      .finally(() => setLoadingStudents(false));
  }, [numId]);

  const handleChangeStatus = async (status: ClassStatus) => {
    setChangingStatus(true);
    try {
      await classApi.changeClassStatus(numId, status);
      setClassData((prev) => (prev ? { ...prev, status } : prev));
      message.success('Đã cập nhật trạng thái lớp!');
    } catch {
      // interceptor handles
    } finally {
      setChangingStatus(false);
    }
  };

  const studentColumns: ColumnsType<StudentProfile> = [
    { title: 'Mã SV', dataIndex: 'studentCode', key: 'studentCode', width: 120 },
    { title: 'Họ tên', dataIndex: 'fullName', key: 'fullName' },
    { title: 'Email', dataIndex: 'schoolEmail', key: 'schoolEmail' },
    { title: 'Lớp', dataIndex: 'className', key: 'className', width: 100 },
  ];

  if (loading) {
    return (
      <div className="flex justify-center items-center" style={{ minHeight: 300 }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!classData) return null;

  return (
    <div>
      <Button
        icon={<ArrowLeftOutlined />}
        onClick={() => navigate('/admin/classes')}
        style={{ marginBottom: 16 }}
      >
        Quay lại
      </Button>

      <Row gutter={[16, 16]}>
        <Col xs={24}>
          <Card
            title={`Lớp ${classData.classCode}`}
            extra={
              <Space>
                <Tag color={CLASS_STATUS_COLOR[classData.status]}>
                  {CLASS_STATUS_LABEL[classData.status]}
                </Tag>
                <Select
                  value={classData.status}
                  onChange={handleChangeStatus}
                  loading={changingStatus}
                  style={{ width: 150 }}
                >
                  {Object.values(ClassStatus).map((s) => (
                    <Select.Option key={s} value={s}>
                      {CLASS_STATUS_LABEL[s]}
                    </Select.Option>
                  ))}
                </Select>
              </Space>
            }
          >
            <Row gutter={16}>
              {[
                { label: 'Môn học', value: classData.subjectResponse?.name },
                { label: 'Tín chỉ', value: classData.subjectResponse?.credits },
                { label: 'Giáo viên', value: classData.teacherResponse?.fullName },
                { label: 'Phòng', value: classData.room },
                { label: 'Sĩ số', value: `${classData.currentStudents}/${classData.maxStudents}` },
                {
                  label: 'Học kỳ',
                  value: `${classData.semesterResponse?.name} — ${classData.semesterResponse?.academicYear}`,
                },
              ].map(({ label, value }) => (
                <Col key={label} xs={24} sm={12} md={8} className="mb-3">
                  <Text type="secondary">{label}:</Text>
                  <br />
                  <Text strong>{value ?? '—'}</Text>
                </Col>
              ))}
            </Row>
          </Card>
        </Col>

        <Col xs={24}>
          <Card title={`Danh sách sinh viên (${students.length})`}>
            <Table
              columns={studentColumns}
              dataSource={students.map((s) => ({ ...s, key: s.id }))}
              loading={loadingStudents}
              pagination={{ pageSize: 15 }}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
}
