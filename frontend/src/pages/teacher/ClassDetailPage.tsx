import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import {
  Card,
  Tabs,
  Table,
  Select,
  Button,
  DatePicker,
  Spin,
  message,
  Typography,
  Space,
  Tag,
  Modal,
  Form,
  InputNumber,
  Upload,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import type { UploadProps } from 'antd';
import dayjs from 'dayjs';
import { teacherApi } from '../../api/teacher.api';
import {
  AttendanceResponse,
  GradeComponentResponse,
  ClassGradesResponse,
  StudentGradeRecord,
} from '../../types/teacher.types';
import {
  AttendanceStatus,
  GradeComponentType,
  ATTENDANCE_STATUS_LABEL,
  ATTENDANCE_STATUS_COLOR,
  GRADE_COMPONENT_LABEL,
} from '../../types/common.types';

const { Title, Text } = Typography;
const { Option } = Select;

export default function TeacherClassDetailPage() {
  const { classId } = useParams<{ classId: string }>();
  const numClassId = Number(classId);

  // Attendance state
  const [attendance, setAttendance] = useState<AttendanceResponse | null>(null);
  const [attendanceDate, setAttendanceDate] = useState<string>(dayjs().format('YYYY-MM-DD'));
  const [loadingAttendance, setLoadingAttendance] = useState(true);
  const [savingAttendance, setSavingAttendance] = useState(false);
  const [attendanceMap, setAttendanceMap] = useState<Record<number, AttendanceStatus>>({});

  // Grade components state
  const [components, setComponents] = useState<GradeComponentResponse[]>([]);
  const [loadingComponents, setLoadingComponents] = useState(true);
  const [componentModalOpen, setComponentModalOpen] = useState(false);
  const [componentForm] = Form.useForm();

  // Grades state
  const [gradesData, setGradesData] = useState<ClassGradesResponse | null>(null);
  const [loadingGrades, setLoadingGrades] = useState(true);
  const [publishing, setPublishing] = useState(false);

  // Fetch attendance
  const fetchAttendance = (date: string) => {
    setLoadingAttendance(true);
    teacherApi
      .getAttendance(numClassId, date)
      .then((data) => {
        setAttendance(data);
        const map: Record<number, AttendanceStatus> = {};
        data.students.forEach((s) => {
          map[s.enrollmentId] = s.status;
        });
        setAttendanceMap(map);
      })
      .catch(() => {})
      .finally(() => setLoadingAttendance(false));
  };

  const fetchComponents = () => {
    setLoadingComponents(true);
    teacherApi
      .getGradeComponents(numClassId)
      .then(setComponents)
      .catch(() => {})
      .finally(() => setLoadingComponents(false));
  };

  const fetchGrades = () => {
    setLoadingGrades(true);
    teacherApi
      .getGrades(numClassId)
      .then(setGradesData)
      .catch(() => {})
      .finally(() => setLoadingGrades(false));
  };

  useEffect(() => {
    fetchAttendance(attendanceDate);
    fetchComponents();
    fetchGrades();
  }, [numClassId]);

  const handleSaveAttendance = async () => {
    if (!attendance) return;
    setSavingAttendance(true);
    try {
      const payload = attendance.students.map((s) => ({
        enrollmentId: s.enrollmentId,
        status: attendanceMap[s.enrollmentId] ?? s.status,
      }));
      await teacherApi.saveAttendance(numClassId, payload, attendanceDate);
      message.success('Lưu điểm danh thành công!');
    } catch {
      // interceptor handles
    } finally {
      setSavingAttendance(false);
    }
  };

  const handleCreateComponent = async (values: {
    type: GradeComponentType;
    weight: number;
    maxScore: number;
  }) => {
    try {
      await teacherApi.createGradeComponent(numClassId, values);
      message.success('Tạo cấu phần điểm thành công!');
      componentForm.resetFields();
      setComponentModalOpen(false);
      fetchComponents();
    } catch {
      // interceptor handles
    }
  };

  const handleDeleteComponent = async (componentId: number) => {
    try {
      await teacherApi.deleteGradeComponent(numClassId, componentId);
      message.success('Đã xoá cấu phần điểm!');
      fetchComponents();
    } catch {
      // interceptor handles
    }
  };

  const handlePublishGrades = async () => {
    setPublishing(true);
    try {
      const count = await teacherApi.publishGrades(numClassId);
      message.success(`Đã công bố ${count} điểm!`);
      fetchGrades();
    } catch {
      // interceptor handles
    } finally {
      setPublishing(false);
    }
  };

  const handleImportGrades: UploadProps['beforeUpload'] = async (file) => {
    try {
      await teacherApi.importGrades(numClassId, file);
      message.success('Import điểm thành công!');
      fetchGrades();
    } catch {
      // interceptor handles
    }
    return false;
  };

  // Attendance columns
  const attendanceColumns: ColumnsType<(typeof attendance)['students'][number]> = [
    { title: 'Mã SV', dataIndex: 'studentCode', key: 'studentCode', width: 120 },
    { title: 'Họ tên', dataIndex: 'name', key: 'name' },
    {
      title: 'Trạng thái',
      key: 'status',
      width: 180,
      render: (_, r) => (
        <Select
          value={attendanceMap[r.enrollmentId] ?? r.status}
          onChange={(v) => setAttendanceMap((prev) => ({ ...prev, [r.enrollmentId]: v }))}
          style={{ width: 160 }}
        >
          {Object.values(AttendanceStatus).map((s) => (
            <Option key={s} value={s}>
              <Tag color={ATTENDANCE_STATUS_COLOR[s]}>{ATTENDANCE_STATUS_LABEL[s]}</Tag>
            </Option>
          ))}
        </Select>
      ),
    },
  ];

  // Grades columns
  const gradeColumns: ColumnsType<StudentGradeRecord> = [
    { title: 'Mã SV', dataIndex: 'studentCode', key: 'studentCode', width: 120 },
    { title: 'Họ tên', dataIndex: 'name', key: 'name' },
    ...components.map((comp) => ({
      title: `${GRADE_COMPONENT_LABEL[comp.type]} (${comp.weight}%)`,
      key: `grade-${comp.id}`,
      align: 'center' as const,
      width: 120,
      render: (_: unknown, r: StudentGradeRecord) => {
        const score = r.grades?.[comp.type];
        return score !== undefined ? score.toFixed(1) : '—';
      },
    })),
  ];

  return (
    <div>
      <Title level={4} style={{ marginBottom: 16 }}>
        Chi tiết lớp #{classId}
      </Title>

      <Tabs
        defaultActiveKey="attendance"
        items={[
          {
            key: 'attendance',
            label: 'Điểm danh',
            children: (
              <Card>
                <Space className="mb-4" wrap>
                  <DatePicker
                    defaultValue={dayjs()}
                    format="DD/MM/YYYY"
                    onChange={(date) => {
                      const dateStr = date?.format('YYYY-MM-DD') ?? dayjs().format('YYYY-MM-DD');
                      setAttendanceDate(dateStr);
                      fetchAttendance(dateStr);
                    }}
                  />
                  <Button
                    type="primary"
                    onClick={handleSaveAttendance}
                    loading={savingAttendance}
                  >
                    Lưu điểm danh
                  </Button>
                </Space>
                {loadingAttendance ? (
                  <div className="flex justify-center py-8">
                    <Spin />
                  </div>
                ) : (
                  <Table
                    columns={attendanceColumns}
                    dataSource={(attendance?.students ?? []).map((s) => ({
                      ...s,
                      key: s.enrollmentId,
                    }))}
                    pagination={{ pageSize: 20 }}
                    locale={{ emptyText: 'Không có sinh viên' }}
                  />
                )}
              </Card>
            ),
          },
          {
            key: 'grades',
            label: 'Bảng điểm',
            children: (
              <div>
                {/* Grade components */}
                <Card
                  title="Cấu phần điểm"
                  className="mb-4"
                  extra={
                    <Button type="primary" size="small" onClick={() => setComponentModalOpen(true)}>
                      Thêm cấu phần
                    </Button>
                  }
                >
                  {loadingComponents ? (
                    <Spin />
                  ) : (
                    <Space wrap>
                      {components.map((comp) => (
                        <Tag
                          key={comp.id}
                          closable
                          onClose={() => handleDeleteComponent(comp.id)}
                          color="blue"
                        >
                          {GRADE_COMPONENT_LABEL[comp.type]} — {comp.weight}%
                        </Tag>
                      ))}
                      {components.length === 0 && (
                        <Text type="secondary">Chưa có cấu phần điểm</Text>
                      )}
                    </Space>
                  )}
                </Card>

                {/* Grade table */}
                <Card
                  title="Bảng điểm sinh viên"
                  extra={
                    <Space>
                      <Upload
                        accept=".xlsx"
                        showUploadList={false}
                        beforeUpload={handleImportGrades}
                      >
                        <Button size="small">Import Excel</Button>
                      </Upload>
                      <Button
                        type="primary"
                        size="small"
                        loading={publishing}
                        onClick={handlePublishGrades}
                      >
                        Công bố điểm
                      </Button>
                    </Space>
                  }
                >
                  {loadingGrades ? (
                    <div className="flex justify-center py-8">
                      <Spin />
                    </div>
                  ) : (
                    <Table
                      columns={gradeColumns}
                      dataSource={(gradesData?.students ?? []).map((s) => ({
                        ...s,
                        key: s.enrollmentId,
                      }))}
                      pagination={{ pageSize: 20 }}
                      scroll={{ x: 600 + components.length * 120 }}
                      locale={{ emptyText: 'Chưa có dữ liệu điểm' }}
                    />
                  )}
                </Card>
              </div>
            ),
          },
        ]}
      />

      {/* Create component modal */}
      <Modal
        title="Thêm cấu phần điểm"
        open={componentModalOpen}
        onCancel={() => {
          componentForm.resetFields();
          setComponentModalOpen(false);
        }}
        footer={null}
        destroyOnClose
        width={400}
      >
        <Form
          form={componentForm}
          layout="vertical"
          onFinish={handleCreateComponent}
          style={{ marginTop: 16 }}
        >
          <Form.Item
            label="Loại cấu phần"
            name="type"
            rules={[{ required: true, message: 'Vui lòng chọn loại' }]}
          >
            <Select placeholder="Chọn loại">
              {Object.values(GradeComponentType).map((t) => (
                <Option key={t} value={t}>
                  {GRADE_COMPONENT_LABEL[t]}
                </Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item
            label="Tỷ trọng (%)"
            name="weight"
            rules={[{ required: true, message: 'Nhập tỷ trọng' }]}
          >
            <InputNumber min={1} max={100} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item
            label="Điểm tối đa"
            name="maxScore"
            rules={[{ required: true, message: 'Nhập điểm tối đa' }]}
          >
            <InputNumber min={1} max={100} style={{ width: '100%' }} />
          </Form.Item>
          <div className="flex justify-end gap-2">
            <Button onClick={() => setComponentModalOpen(false)}>Huỷ</Button>
            <Button type="primary" htmlType="submit">
              Tạo
            </Button>
          </div>
        </Form>
      </Modal>
    </div>
  );
}
