import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ChatMessage {
  id: number;
  senderUsername: string;
  recipientUsername: string;
  content: string;
  read: boolean;
  sentAt: string;
}

@Injectable({ providedIn: 'root' })
export class MessageApiService {
  private readonly baseUrl = `${environment.logiqueMetierApiUrl}/messages`;

  constructor(private http: HttpClient) {}

  getConversation(user1: string, user2: string): Observable<ChatMessage[]> {
    const params = new HttpParams().set('user1', user1).set('user2', user2);
    return this.http.get<ChatMessage[]>(`${this.baseUrl}/conversation`, { params });
  }

  send(sender: string, recipient: string, content: string): Observable<ChatMessage> {
    return this.http.post<ChatMessage>(this.baseUrl, { senderUsername: sender, recipientUsername: recipient, content });
  }

  getUnreadCount(username: string): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.baseUrl}/unread-count/${username}`);
  }
}
