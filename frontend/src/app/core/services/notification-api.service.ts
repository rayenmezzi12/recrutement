import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface AppNotification {
  id: number;
  username: string;
  title: string;
  body: string;
  type: string;
  isRead: boolean;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationApiService {
  private readonly baseUrl = `${environment.logiqueMetierApiUrl}/notifications`;

  constructor(private http: HttpClient) {}

  getForUser(username: string): Observable<AppNotification[]> {
    return this.http.get<AppNotification[]>(`${this.baseUrl}/user/${username}`);
  }

  getUnreadCount(username: string): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.baseUrl}/user/${username}/unread-count`);
  }

  markAllRead(username: string): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/user/${username}/read-all`, null);
  }
}
