import { NotificationType } from './common.types';
import { Role } from './auth.types';

export interface NotificationResponse {
  id: number;
  title: string;
  content: string;
  type: NotificationType;
  isRead: boolean;
  referenceId: number | null;
  referenceType: string | null;
  createdAt: string;
}

export interface BroadcastNotificationRequest {
  title: string;
  content: string;
  type: NotificationType;
  referenceId?: number;
  referenceType?: string;
  targetRoles: Role[];
}

export interface SendNotificationRequest {
  title: string;
  content: string;
  type: NotificationType;
  referenceId?: number;
  referenceType?: string;
  targetUserIds: number[];
}
