import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthRequest, AuthResponse, RegisterRequest, UserDto } from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly baseUrl = `${environment.authApiUrl}/auth`;

  constructor(private http: HttpClient) {}

  login(request: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, request).pipe(
      tap((res) => this.persistSession(res))
    );
  }

  register(request: RegisterRequest): Observable<UserDto> {
    return this.http.post<UserDto>(`${this.baseUrl}/register`, request);
  }

  getUser(username: string): Observable<UserDto> {
    return this.http.get<UserDto>(`${this.baseUrl}/user/${username}`);
  }

  getToken(): string | null {
    return localStorage.getItem('jwt');
  }

  getUsername(): string | null {
    return localStorage.getItem('username');
  }

  getRoles(): string[] {
    const raw = localStorage.getItem('roles');
    return raw ? JSON.parse(raw) : [];
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem('jwt');
    localStorage.removeItem('username');
    localStorage.removeItem('email');
    localStorage.removeItem('roles');
  }

  private persistSession(res: AuthResponse): void {
    localStorage.setItem('jwt', res.token);
    localStorage.setItem('username', res.username);
    localStorage.setItem('email', res.email ?? '');
    localStorage.setItem('roles', JSON.stringify(res.roles ?? []));
  }
}
