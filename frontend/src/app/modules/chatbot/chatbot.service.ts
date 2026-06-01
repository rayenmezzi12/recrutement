import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/services/auth.service';

export interface ChatMessage {
  text: string;
  sender: 'user' | 'bot';
  timestamp: Date;
}

export interface ChatbotResponse {
  response: string;
}

@Injectable({ providedIn: 'root' })
export class ChatbotService {
  private apiUrl = `${environment.chatbotApiUrl}/chatbot/ask`;

  constructor(private http: HttpClient, private auth: AuthService) {}

  askQuestion(question: string): Observable<ChatbotResponse> {
    return this.http.post<ChatbotResponse>(this.apiUrl, {
      userId: this.auth.getUsername() ?? '',
      message: question
    });
  }
}
