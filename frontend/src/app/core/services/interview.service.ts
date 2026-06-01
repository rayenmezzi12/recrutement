import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Evaluation, Interview } from '../models/interview.model';

@Injectable({ providedIn: 'root' })
export class InterviewService {
  private readonly baseUrl = `${environment.logiqueMetierApiUrl}/interviews`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Interview[]> {
    return this.http.get<Interview[]>(this.baseUrl);
  }

  getById(id: number): Observable<Interview> {
    return this.http.get<Interview>(`${this.baseUrl}/${id}`);
  }

  getByCandidate(candidateId: number): Observable<Interview[]> {
    return this.http.get<Interview[]>(`${this.baseUrl}/candidate/${candidateId}`);
  }

  schedule(interview: Interview): Observable<Interview> {
    return this.http.post<Interview>(this.baseUrl, interview);
  }

  updateStatus(id: number, status: string): Observable<Interview> {
    const params = new HttpParams().set('status', status);
    return this.http.put<Interview>(`${this.baseUrl}/${id}/status`, null, { params });
  }

  submitEvaluation(evaluation: Evaluation): Observable<Evaluation> {
    return this.http.post<Evaluation>(`${this.baseUrl}/evaluations`, evaluation);
  }

  getEvaluation(interviewId: number): Observable<Evaluation> {
    return this.http.get<Evaluation>(`${this.baseUrl}/${interviewId}/evaluation`);
  }
}
