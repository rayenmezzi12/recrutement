import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EmploymentOfferService } from '../../core/services/employment-offer.service';
import { RecruitmentService } from '../../core/services/recruitment.service';
import {
  EmploymentOffer,
  OFFER_STATUS_LABELS,
  offerCanRespond,
  offerCanSend
} from '../../core/models/offer.model';
import { Application, Job } from '../../core/models/recruitment.model';
import { RoleService } from '../../core/services/role.service';
import { CandidateService } from '../../core/services/candidate.service';
import { AuthService } from '../../core/services/auth.service';
import { Candidate } from '../../core/models/candidate.model';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-employment-offer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './employment-offer.component.html'
})
export class EmploymentOfferComponent implements OnInit {
  offers: EmploymentOffer[] = [];
  myOffers: EmploymentOffer[] = [];
  applications: Application[] = [];
  jobs: Job[] = [];
  candidates: Candidate[] = [];
  loading = true;
  showForm = false;
  form = { applicationId: 0, salary: 45000, startDate: '' };

  constructor(
    private offerService: EmploymentOfferService,
    private recruitmentService: RecruitmentService,
    private candidateService: CandidateService,
    private authService: AuthService,
    public roleService: RoleService
  ) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.loading = true;
    const isCandidat = this.roleService.isCandidat();

    if (isCandidat) {
      const username = this.authService.getUsername() ?? '';
      forkJoin({
        candidate: this.candidateService.getByUsername(username),
        offers: this.offerService.getAll()
      }).subscribe({
        next: ({ candidate, offers }) => {
          this.myOffers = offers.filter((o) => o.candidateId === candidate.id);
          this.offers = this.myOffers;
          this.loading = false;
        },
        error: () => (this.loading = false)
      });
      return;
    }

    forkJoin({
      offers: this.offerService.getAll(),
      applications: this.recruitmentService.getApplications({ archived: false }),
      jobs: this.recruitmentService.getJobs(),
      candidates: this.candidateService.getAll()
    }).subscribe({
      next: ({ offers, applications, jobs, candidates }) => {
        this.offers = offers;
        this.applications = applications;
        this.jobs = jobs;
        this.candidates = candidates;
        this.loading = false;
      },
      error: () => (this.loading = false)
    });
  }

  openForm() {
    this.form.applicationId = this.applications[0]?.id ?? 0;
    this.showForm = true;
  }

  generate() {
    this.offerService.generate(this.form.applicationId, this.form.salary, this.form.startDate).subscribe({
      next: () => {
        this.showForm = false;
        this.load();
      }
    });
  }

  send(id: number) {
    this.offerService.send(id).subscribe({ next: () => this.load() });
  }

  respond(id: number, accepted: boolean) {
    this.offerService.respond(id, accepted).subscribe({ next: () => this.load() });
  }

  statusLabel(status?: string): string {
    return OFFER_STATUS_LABELS[status ?? ''] ?? status ?? '—';
  }

  canSend(status?: string): boolean {
    return offerCanSend(status);
  }

  canRespond(status?: string): boolean {
    return offerCanRespond(status);
  }

  getJobTitle(jobId: number): string {
    const job = this.jobs.find(j => j.id === jobId);
    return job?.title ?? `Poste #${jobId}`;
  }

  getApplicationLabel(a: Application): string {
    const jobTitle = this.getJobTitle(a.jobId);
    const candidate = this.candidates.find(c => c.id === a.candidateId);
    const candidateName = candidate ? `${candidate.firstName} ${candidate.lastName}` : `Candidat #${a.candidateId}`;
    return `${candidateName} — ${jobTitle}`;
  }
}
