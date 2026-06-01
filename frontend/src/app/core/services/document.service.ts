import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CandidateDocument {
  id?: number;
  candidateId: number;
  applicationId?: number;
  type: string;
  fileName?: string;
  fileUrl?: string;
  uploadedAt?: string;
}

@Injectable({ providedIn: 'root' })
export class DocumentService {
  private readonly base = '/api/documents';

  constructor(private http: HttpClient) {}

  listByCandidate(candidateId: number): Observable<CandidateDocument[]> {
    return this.http.get<CandidateDocument[]>(`${this.base}/candidate/${candidateId}`);
  }

  listByApplication(applicationId: number): Observable<CandidateDocument[]> {
    return this.http.get<CandidateDocument[]>(`${this.base}/application/${applicationId}`);
  }

  upload(candidateId: number, file: File, type = 'CV', applicationId?: number): Observable<CandidateDocument> {
    const form = new FormData();
    form.append('candidateId', String(candidateId));
    form.append('type', type);
    form.append('file', file);
    if (applicationId != null) {
      form.append('applicationId', String(applicationId));
    }
    return this.http.post<CandidateDocument>(`${this.base}/upload`, form);
  }
}
