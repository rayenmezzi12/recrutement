export interface AuthRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  fullName: string;
  roles?: string[];
}

export interface AuthResponse {
  token: string;
  refreshToken: string;
  username: string;
  email: string;
  roles: string[];
}

export interface UserDto {
  id: number;
  username: string;
  email: string;
  fullName: string;
  roles: string[];
}
