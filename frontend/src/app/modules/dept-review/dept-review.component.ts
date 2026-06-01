import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { Application, RecruitmentStep, STEP_LABELS } from '../../core/models/recruitment.model';
import { RecruitmentService } from '../../core/services/recruitment.service';
import { CandidateService } from '../../core/services/candidate.service';
import { DeptReviewService } from '../../core/services/dept-review.service';
import { AuthService } from '../../core/services/auth.service';
import { Candidate } from '../../core/models/candidate.model';

const DEPT_STEPS: RecruitmentStep[] = ['TEST_TECHNIQUE', 'ENTRETIEN_FINAL'];

interface DeptApplicationView {
  app: Application;
  candidateName: string;
  jobTitle: string;
  department: string;
  comment: string;
  techRating: number;
  commRating: number;
  fitRating: number;
}

import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-dept-review',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dept-review.component.html',
  styleUrls: ['./dept-review.component.css']
})
export class DeptReviewComponent implements OnInit {
  readonly stepLabels = STEP_LABELS;
  items: DeptApplicationView[] = [];
  departments: string[] = [];
  filterDepartment = '';
  loading = true;
  error = '';
  success = '';

  constructor(
    private recruitmentService: RecruitmentService,
    private candidateService: CandidateService,
    private deptReviewService: DeptReviewService,
    private auth: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.loading = true;
    this.error = '';
    forkJoin({
      applications: this.recruitmentService.getApplications({ archived: false }),
      jobs: this.recruitmentService.getJobs(),
      candidates: this.candidateService.getAll()
    }).subscribe({
      next: ({ applications, jobs, candidates }) => {
        const deptSet = new Set<string>();
        jobs.forEach((j) => j.department && deptSet.add(j.department));
        this.departments = [...deptSet].sort();

        const filtered = applications.filter((a) =>
          DEPT_STEPS.includes((a.currentStep ?? 'PRE_SELECTION') as RecruitmentStep)
        );

        this.items = filtered
          .map((app) => {
            const job = jobs.find((j) => j.id === app.jobId);
            const c = candidates.find((x) => x.id === app.candidateId);
            return {
              app,
              candidateName: c ? `${c.firstName} ${c.lastName}` : `Candidat #${app.candidateId}`,
              jobTitle: job?.title ?? `Poste #${app.jobId}`,
              department: job?.department ?? '—',
              comment: '',
              techRating: 0,
              commRating: 0,
              fitRating: 0
            };
          })
          .filter((row) =>
            !this.filterDepartment || row.department === this.filterDepartment
          );

        this.loading = false;
      },
      error: () => {
        this.error = 'Impossible de charger les candidatures à évaluer';
        this.loading = false;
      }
    });
  }

  applyDepartmentFilter() {
    this.load();
  }

  signal(item: DeptApplicationView, decision: 'avancer' | 'rejeter') {
    if ((item.techRating === 0 || item.commRating === 0 || item.fitRating === 0) && !confirm('Certains critères ne sont pas évalués. Voulez-vous continuer ?')) {
      return;
    }
    this.deptReviewService.submit({
      applicationId: item.app.id!,
      decision,
      techRating: item.techRating,
      commRating: item.commRating,
      fitRating: item.fitRating,
      comment: item.comment
    }).subscribe({
      next: () => {
        this.success = `Avis transmis au recruteur pour ${item.candidateName}.`;
        item.comment = '';
        item.techRating = 0;
        item.commRating = 0;
        item.fitRating = 0;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'Échec de l\'envoi de l\'avis au recruteur';
        this.cdr.detectChanges();
      }
    });
  }
}
