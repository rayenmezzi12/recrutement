import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ReportRequest {
  startDate: string;
  endDate: string;
  type: 'PDF' | 'CSV';
}

@Injectable({ providedIn: 'root' })
export class ReportService {
  private readonly baseUrl = `${environment.logiqueMetierApiUrl}/reports`;

  constructor(private http: HttpClient) {}

  generate(request: ReportRequest): Observable<Blob> {
    return this.http.post(`${this.baseUrl}/generate`, request, { responseType: 'blob' });
  }
}
