import { useEffect, useState } from 'react';
import { Card, Table, Spin, Typography, Tag } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { studentApi } from '../../api/student.api';
import { ScheduleClass, StudentTimetableResponse } from '../../types/student.types';
import { DAY_OF_WEEK_LABEL } from '../../types/common.types';

const { Title, Text } = Typography;

interface ScheduleRow extends ScheduleClass {
  key: string;
  dayLabel: string;
}

const columns: ColumnsType<ScheduleRow> = [
  {
    title: 'Thứ',
    dataIndex: 'dayLabel',
    key: 'dayLabel',
    width: 100,
    render: (day: string) => <Tag color="blue">{day}</Tag>,
  },
  { title: 'Mã lớp', dataIndex: 'classCode', key: 'classCode', width: 120 },
  { title: 'Môn học', dataIndex: 'subjectName', key: 'subjectName' },
  { title: 'Phòng', dataIndex: 'room', key: 'room', width: 90 },
  { title: 'Giáo viên', dataIndex: 'teacherName', key: 'teacherName', width: 150 },
  {
    title: 'Tiết học',
    key: 'period',
    width: 110,
    render: (_, record) => (
      <Text>
        {record.startPeriod} — {record.endPeriod}
      </Text>
    ),
  },
];

export default function StudentSchedulePage() {
  const [data, setData] = useState<StudentTimetableResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    studentApi
      .getSchedules()
      .then(setData)
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="flex justify-center items-center" style={{ minHeight: 300 }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!data) return null;

  const rows: ScheduleRow[] = data.schedule.flatMap((day) =>
    day.classes.map((cls) => ({
      ...cls,
      key: `${day.dayOfWeek}-${cls.classId}`,
      dayLabel: DAY_OF_WEEK_LABEL[day.dayOfWeek] ?? `Thứ ${day.dayOfWeek}`,
    })),
  );

  return (
    <div>
      <Title level={4} style={{ marginBottom: 16 }}>
        Thời khóa biểu
      </Title>
      <Card>
        <Table
          columns={columns}
          dataSource={rows}
          pagination={false}
          scroll={{ x: 700 }}
          locale={{ emptyText: 'Không có lịch học' }}
        />
      </Card>
    </div>
  );
}
