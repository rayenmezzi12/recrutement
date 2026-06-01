import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { EmploymentOffer } from '../models/offer.model';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class EmploymentOfferService {
  private readonly baseUrl = `${environment.logiqueMetierApiUrl}/offers`;

  constructor(private http: HttpClient, private auth: AuthService) {}

  getAll(): Observable<EmploymentOffer[]> {
    return this.http.get<EmploymentOffer[]>(this.baseUrl);
  }

  generate(applicationId: number, salary: number, startDate: string): Observable<EmploymentOffer> {
    return this.http.post<EmploymentOffer>(
      `${this.baseUrl}/generate`,
      { applicationId, salary, startDate },
      { headers: { 'X-Actor': this.auth.getUsername() ?? 'system' } }
    );
  }

  send(id: number): Observable<EmploymentOffer> {
    return this.http.post<EmploymentOffer>(`${this.baseUrl}/${id}/send`, null, {
      headers: { 'X-Actor': this.auth.getUsername() ?? 'system' }
    });
  }

  respond(id: number, accepted: boolean): Observable<EmploymentOffer> {
    return this.http.put<EmploymentOffer>(
      `${this.baseUrl}/${id}/respond?accepted=${accepted}`,
      null,
      { headers: { 'X-Actor': this.auth.getUsername() ?? 'system' } }
    );
  }
}
