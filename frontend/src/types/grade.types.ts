import { GradeComponentType, LetterGrade } from './common.types';

export interface GradeEntry {
  id: number;
  enrollmentId: number;
  componentId: number;
  score: number;
  isPublished: boolean;
}

export interface SaveGradeRequest {
  enrollmentId: number;
  componentId: number;
  score: number;
}

export interface UpdateGradeRequest {
  componentId: number;
  score: number;
}

export interface CreateGradeComponentRequest {
  weight: number;
  type: GradeComponentType;
  maxScore: number;
}

export interface UpdateGradeComponentRequest {
  weight: number;
  maxScore: number;
}

export interface StudentFinalGrade {
  enrollmentId: number;
  studentCode: string;
  studentName: string;
  scores: Record<GradeComponentType, number>;
  finalScore: number | null;
  finalLetterGrade: LetterGrade | null;
  isPassed: boolean | null;
  isPublished: boolean;
}
