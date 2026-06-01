import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Candidate } from '../models/candidate.model';

@Injectable({ providedIn: 'root' })
export class CandidateService {
  private readonly baseUrl = `${environment.logiqueMetierApiUrl}/candidates`;

  constructor(private http: HttpClient) {}

  getAll(search?: { title?: string; skills?: string; q?: string }): Observable<Candidate[]> {
    let params = new HttpParams();
    if (search?.title) params = params.set('title', search.title);
    if (search?.skills) params = params.set('skills', search.skills);
    if (search?.q) params = params.set('search', search.q);
    const hasParams = search?.title || search?.skills || search?.q;
    return this.http.get<Candidate[]>(this.baseUrl, hasParams ? { params } : {});
  }

  uploadCv(id: number, file: File): Observable<Candidate> {
    const form = new FormData();
    form.append('file', file);
    return this.http.post<Candidate>(`${this.baseUrl}/${id}/upload-cv`, form);
  }

  downloadFile(url: string): Observable<Blob> {
    return this.http.get(url, { responseType: 'blob' });
  }

  getHistory(candidateId: number): Observable<{ actionType: string; details: string; createdAt: string; actorUsername: string }[]> {
    return this.http.get<{ actionType: string; details: string; createdAt: string; actorUsername: string }[]>(
      `${environment.logiqueMetierApiUrl}/history/candidate/${candidateId}`
    );
  }

  getById(id: number): Observable<Candidate> {
    return this.http.get<Candidate>(`${this.baseUrl}/${id}`);
  }

  getByUsername(username: string): Observable<Candidate> {
    return this.http.get<Candidate>(`${this.baseUrl}/user/${username}`);
  }

  create(candidate: Candidate): Observable<Candidate> {
    return this.http.post<Candidate>(this.baseUrl, candidate);
  }

  update(id: number, candidate: Candidate): Observable<Candidate> {
    return this.http.put<Candidate>(`${this.baseUrl}/${id}`, candidate);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
