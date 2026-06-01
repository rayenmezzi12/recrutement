import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface DashboardKpi {
  totalApplications: number;
  totalCandidates: number;
  openJobs: number;
  applicationsByStep: Record<string, number>;
  applicationsByStatus: Record<string, number>;
  applicationsByJob: Record<string, number>;
  averageDaysInPipeline: number;
  staleApplications: { applicationId: number; candidateId: number; jobId: number; currentStep: string; daysWaiting: number }[];
}

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly baseUrl = `${environment.logiqueMetierApiUrl}/dashboard`;

  constructor(private http: HttpClient) {}

  getKpis(staleDays = 7, department?: string): Observable<DashboardKpi> {
    let params = new HttpParams().set('staleDays', staleDays);
    if (department) params = params.set('department', department);
    return this.http.get<DashboardKpi>(`${this.baseUrl}/kpis`, { params });
  }
}
