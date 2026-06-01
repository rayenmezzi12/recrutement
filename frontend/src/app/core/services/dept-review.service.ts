import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';

export interface DeptReviewItem {
  applicationId: number;
  candidateId: number;
  actorUsername: string;
  details: string;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class DeptReviewService {
  private readonly baseUrl = `${environment.logiqueMetierApiUrl}/dept-reviews`;

  constructor(private http: HttpClient, private auth: AuthService) {}

  submit(payload: {
    applicationId: number;
    decision: 'avancer' | 'rejeter';
    techRating: number;
    commRating: number;
    fitRating: number;
    comment: string;
  }): Observable<{ success: boolean; message: string }> {
    return this.http.post<{ success: boolean; message: string }>(this.baseUrl, payload, {
      headers: new HttpHeaders({ 'X-Actor': this.auth.getUsername() ?? 'dept' })
    });
  }

  getRecent(): Observable<DeptReviewItem[]> {
    const headers = new HttpHeaders({
      'X-Username': this.auth.getUsername() ?? '',
      'X-User-Roles': this.auth.getRoles().join(',')
    });
    return this.http.get<DeptReviewItem[]>(`${this.baseUrl}/recent`, { headers });
  }
}