export enum Role {
  ADMIN = 'ROLE_ADMIN',
  TEACHER = 'ROLE_TEACHER',
  STUDENT = 'ROLE_STUDENT',
}

export interface AuthResult {
  email: string;
  role: Role;
  accessToken: string;
  refreshToken: string;
}

export interface UserInfo {
  id: number;
  email: string;
  fullName: string;
  phone: string;
  avatarUrl: string;
  role: Role;
}

export interface APIResponse<T> {
  code: number;
  message: string;
  result: T;
}
