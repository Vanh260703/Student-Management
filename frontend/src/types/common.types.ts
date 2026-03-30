import dayjs from 'dayjs';

export enum Gender {
  MALE = 'MALE',
  FEMALE = 'FEMALE',
  OTHER = 'OTHER',
}

export enum StudentStatus {
  ACTIVE = 'ACTIVE',
  SUSPENDED = 'SUSPENDED',
  GRADUATED = 'GRADUATED',
}

export enum ClassStatus {
  OPEN = 'OPEN',
  CLOSE = 'CLOSE',
  CANCELLED = 'CANCELLED',
}

export enum EnrollmentStatus {
  ENROLLED = 'ENROLLED',
  DROPPED = 'DROPPED',
  COMPLETED = 'COMPLETED',
}

export enum PaymentStatus {
  PENDING = 'PENDING',
  SUCCESS = 'SUCCESS',
  FAILED = 'FAILED',
  REFUNDED = 'REFUNDED',
}

export enum PaymentMethod {
  BANK_TRANSFER = 'BANK_TRANSFER',
  MOMO = 'MOMO',
  CASH = 'CASH',
}

export enum GradeComponentType {
  ATTENDANCE = 'ATTENDANCE',
  MIDTERM = 'MIDTERM',
  FINAL = 'FINAL',
  ASSIGNMENT = 'ASSIGNMENT',
}

export enum LetterGrade {
  A = 'A',
  B = 'B',
  C = 'C',
  D = 'D',
  F = 'F',
}

export enum NotificationType {
  GRADE = 'GRADE',
  SCHEDULE = 'SCHEDULE',
  PAYMENT = 'PAYMENT',
  SYSTEM = 'SYSTEM',
  ATTENDANCE = 'ATTENDANCE',
}

export enum AttendanceStatus {
  PRESENT = 'PRESENT',
  ABSENT = 'ABSENT',
  LATE = 'LATE',
  EXCUSED = 'EXCUSED',
}

export enum SemesterName {
  HK1 = 'HK1',
  HK2 = 'HK2',
  HK3 = 'HK3',
}

export enum TuitionStatus {
  PENDING = 'PENDING',
  PAID = 'PAID',
  OVERDUE = 'OVERDUE',
  WAIVED = 'WAIVED',
}

// Label mappings
export const GENDER_LABEL: Record<Gender, string> = {
  [Gender.MALE]: 'Nam',
  [Gender.FEMALE]: 'Nữ',
  [Gender.OTHER]: 'Khác',
};

export const STUDENT_STATUS_LABEL: Record<StudentStatus, string> = {
  [StudentStatus.ACTIVE]: 'Đang học',
  [StudentStatus.SUSPENDED]: 'Đình chỉ',
  [StudentStatus.GRADUATED]: 'Đã tốt nghiệp',
};

export const STUDENT_STATUS_COLOR: Record<StudentStatus, string> = {
  [StudentStatus.ACTIVE]: 'green',
  [StudentStatus.SUSPENDED]: 'red',
  [StudentStatus.GRADUATED]: 'blue',
};

export const CLASS_STATUS_LABEL: Record<ClassStatus, string> = {
  [ClassStatus.OPEN]: 'Đang mở',
  [ClassStatus.CLOSE]: 'Đã đóng',
  [ClassStatus.CANCELLED]: 'Đã huỷ',
};

export const CLASS_STATUS_COLOR: Record<ClassStatus, string> = {
  [ClassStatus.OPEN]: 'green',
  [ClassStatus.CLOSE]: 'default',
  [ClassStatus.CANCELLED]: 'red',
};

export const ENROLLMENT_STATUS_LABEL: Record<EnrollmentStatus, string> = {
  [EnrollmentStatus.ENROLLED]: 'Đang học',
  [EnrollmentStatus.DROPPED]: 'Đã rút',
  [EnrollmentStatus.COMPLETED]: 'Hoàn thành',
};

export const PAYMENT_STATUS_LABEL: Record<PaymentStatus, string> = {
  [PaymentStatus.PENDING]: 'Chờ thanh toán',
  [PaymentStatus.SUCCESS]: 'Thành công',
  [PaymentStatus.FAILED]: 'Thất bại',
  [PaymentStatus.REFUNDED]: 'Đã hoàn tiền',
};

export const PAYMENT_STATUS_COLOR: Record<PaymentStatus, string> = {
  [PaymentStatus.PENDING]: 'orange',
  [PaymentStatus.SUCCESS]: 'green',
  [PaymentStatus.FAILED]: 'red',
  [PaymentStatus.REFUNDED]: 'purple',
};

export const PAYMENT_METHOD_LABEL: Record<PaymentMethod, string> = {
  [PaymentMethod.BANK_TRANSFER]: 'Chuyển khoản',
  [PaymentMethod.MOMO]: 'Ví MoMo',
  [PaymentMethod.CASH]: 'Tiền mặt',
};

export const TUITION_STATUS_LABEL: Record<TuitionStatus, string> = {
  [TuitionStatus.PENDING]: 'Chưa đóng',
  [TuitionStatus.PAID]: 'Đã đóng',
  [TuitionStatus.OVERDUE]: 'Quá hạn',
  [TuitionStatus.WAIVED]: 'Được miễn',
};

export const TUITION_STATUS_COLOR: Record<TuitionStatus, string> = {
  [TuitionStatus.PENDING]: 'orange',
  [TuitionStatus.PAID]: 'green',
  [TuitionStatus.OVERDUE]: 'red',
  [TuitionStatus.WAIVED]: 'blue',
};

export const GRADE_COMPONENT_LABEL: Record<GradeComponentType, string> = {
  [GradeComponentType.ATTENDANCE]: 'Chuyên cần',
  [GradeComponentType.MIDTERM]: 'Giữa kỳ',
  [GradeComponentType.FINAL]: 'Cuối kỳ',
  [GradeComponentType.ASSIGNMENT]: 'Bài tập',
};

export const ATTENDANCE_STATUS_LABEL: Record<AttendanceStatus, string> = {
  [AttendanceStatus.PRESENT]: 'Có mặt',
  [AttendanceStatus.ABSENT]: 'Vắng',
  [AttendanceStatus.LATE]: 'Đi trễ',
  [AttendanceStatus.EXCUSED]: 'Có phép',
};

export const ATTENDANCE_STATUS_COLOR: Record<AttendanceStatus, string> = {
  [AttendanceStatus.PRESENT]: 'green',
  [AttendanceStatus.ABSENT]: 'red',
  [AttendanceStatus.LATE]: 'orange',
  [AttendanceStatus.EXCUSED]: 'blue',
};

export const NOTIFICATION_TYPE_LABEL: Record<NotificationType, string> = {
  [NotificationType.GRADE]: 'Điểm số',
  [NotificationType.SCHEDULE]: 'Lịch học',
  [NotificationType.PAYMENT]: 'Học phí',
  [NotificationType.SYSTEM]: 'Hệ thống',
  [NotificationType.ATTENDANCE]: 'Điểm danh',
};

export const DAY_OF_WEEK_LABEL: Record<number, string> = {
  1: 'Thứ Hai',
  2: 'Thứ Ba',
  3: 'Thứ Tư',
  4: 'Thứ Năm',
  5: 'Thứ Sáu',
  6: 'Thứ Bảy',
  7: 'Chủ Nhật',
};

export const ROLE_LABEL: Record<string, string> = {
  ROLE_ADMIN: 'Quản trị viên',
  ROLE_TEACHER: 'Giáo viên',
  ROLE_STUDENT: 'Sinh viên',
};

export const LETTER_GRADE_COLOR: Record<LetterGrade, string> = {
  [LetterGrade.A]: 'green',
  [LetterGrade.B]: 'blue',
  [LetterGrade.C]: 'orange',
  [LetterGrade.D]: 'gold',
  [LetterGrade.F]: 'red',
};

// Format utilities
export const formatCurrency = (amount: number): string =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);

export const formatDate = (dateStr: string): string =>
  dayjs(dateStr).format('DD/MM/YYYY');

export const formatDateTime = (dateStr: string): string =>
  dayjs(dateStr).format('DD/MM/YYYY HH:mm');

export const formatGPA = (gpa: number): string => gpa.toFixed(2);
