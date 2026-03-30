import { EnrollmentStatus, LetterGrade } from './common.types';
import { ClassResponse } from './class.types';

export interface Enrollment {
  id: number;
  classEntity: ClassResponse;
  enrolledAt: string;
  status: EnrollmentStatus;
  finalScore: number | null;
  finalLetterGrade: LetterGrade | null;
  isPassed: boolean | null;
}
