import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  Application,
  ApplicationStatus,
  Job,
  RecruitmentStep
} from '../models/recruitment.model';
import { AuthService } from './auth.service';

export interface ApplicationFilters {
  jobId?: number;
  candidateId?: number;
  step?: RecruitmentStep;
  status?: ApplicationStatus;
  recruiterUsername?: string;
  archived?: boolean;
  fromDate?: string;
  toDate?: string;
}

@Injectable({ providedIn: 'root' })
export class RecruitmentService {
  private readonly baseUrl = `${environment.logiqueMetierApiUrl}/recruitment`;

  constructor(private http: HttpClient, private auth: AuthService) {}

  private actorHeaders(): HttpHeaders {
    return new HttpHeaders({ 'X-Actor': this.auth.getUsername() ?? 'system' });
  }

  getJobs(): Observable<Job[]> {
    return this.http.get<Job[]>(`${this.baseUrl}/jobs`);
  }

  getJob(id: number): Observable<Job> {
    return this.http.get<Job>(`${this.baseUrl}/jobs/${id}`);
  }

  createJob(job: Job): Observable<Job> {
    return this.http.post<Job>(`${this.baseUrl}/jobs`, job);
  }

  updateJob(id: number, job: Job): Observable<Job> {
    return this.http.put<Job>(`${this.baseUrl}/jobs/${id}`, job);
  }

  deleteJob(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/jobs/${id}`);
  }

  getApplications(filters?: ApplicationFilters): Observable<Application[]> {
    let params = new HttpParams();
    if (filters) {
      if (filters.jobId != null) params = params.set('jobId', filters.jobId);
      if (filters.candidateId != null) params = params.set('candidateId', filters.candidateId);
      if (filters.step) params = params.set('step', filters.step);
      if (filters.status) params = params.set('status', filters.status);
      if (filters.recruiterUsername) params = params.set('recruiterUsername', filters.recruiterUsername);
      if (filters.archived != null) params = params.set('archived', filters.archived);
      if (filters.fromDate) params = params.set('fromDate', filters.fromDate);
      if (filters.toDate) params = params.set('toDate', filters.toDate);
    }
    return this.http.get<Application[]>(`${this.baseUrl}/applications`, { params });
  }

  getApplicationsByJob(jobId: number): Observable<Application[]> {
    return this.http.get<Application[]>(`${this.baseUrl}/applications/job/${jobId}`);
  }

  getApplicationsByCandidate(candidateId: number): Observable<Application[]> {
    return this.http.get<Application[]>(`${this.baseUrl}/applications/candidate/${candidateId}`);
  }

  apply(application: Application): Observable<Application> {
    return this.http.post<Application>(`${this.baseUrl}/applications`, application, {
      headers: this.actorHeaders()
    });
  }

  updateStep(id: number, step: RecruitmentStep): Observable<Application> {
    const params = new HttpParams().set('step', step);
    return this.http.put<Application>(`${this.baseUrl}/applications/${id}/step`, null, {
      params,
      headers: this.actorHeaders()
    });
  }

  updateStatus(id: number, status: ApplicationStatus): Observable<Application> {
    const params = new HttpParams().set('status', status);
    return this.http.put<Application>(`${this.baseUrl}/applications/${id}/status`, null, {
      params,
      headers: this.actorHeaders()
    });
  }
}
