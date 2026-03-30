import { PaymentMethod, PaymentStatus, TuitionStatus } from './common.types';
import { SemesterInfo } from './class.types';
import { StudentProfile } from './student.types';

export interface StudentTuitionResponse {
  id: number;
  semester: SemesterInfo;
  amount: number;
  discount: number;
  finalAmount: number;
  dueDate: string;
  status: TuitionStatus;
  createdAt: string;
}

export interface TuitionResponse {
  id: number;
  student: StudentProfile;
  semester: SemesterInfo;
  amount: number;
  discount: number;
  finalAmount: number;
  dueDate: string;
  status: TuitionStatus;
  createdAt: string;
}

export interface UpdateTuitionRequest {
  amount: number;
  discount: number;
  dueDate: string;
  status: TuitionStatus;
}

export interface GenerateTuitionResponse {
  semesterId: number;
  semesterName: string;
  creditPrice: number;
  totalEnrollments: number;
  totalStudents: number;
  generatedCount: number;
  skippedCount: number;
}

export interface MomoPaymentResponse {
  paymentId: number;
  orderId: string;
  requestId: string;
  amount: number;
  status: string;
  payUrl: string;
  deeplink: string;
  qrCodeUrl: string;
}

export interface StudentPaymentHistoryResponse {
  paymentId: number;
  tuitionId: number;
  transactionCode: string;
  amount: number;
  method: PaymentMethod;
  paymentStatus: PaymentStatus;
  paidAt: string | null;
  createdAt: string;
  tuition: StudentTuitionResponse;
}

export interface AdminPaymentResponse {
  paymentId: number;
  transactionCode: string;
  amount: number;
  method: PaymentMethod;
  status: PaymentStatus;
  paidAt: string | null;
  createdAt: string;
  student: StudentProfile;
  tuition: StudentTuitionResponse;
}

export interface MomoCallbackResponse {
  orderId: string;
  requestId: string;
  transId: string;
  paymentStatus: PaymentStatus;
  resultCode: number;
  message: string;
  signatureValid: boolean;
  success: boolean;
}
