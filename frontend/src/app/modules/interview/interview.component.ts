import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { forkJoin } from 'rxjs';
import {
  Evaluation,
  Interview,
  INTERVIEW_STATUS_LABELS,
  INTERVIEW_TYPE_LABELS
} from '../../core/models/interview.model';
import { InterviewService } from '../../core/services/interview.service';
import { CandidateService } from '../../core/services/candidate.service';
import { RecruitmentService } from '../../core/services/recruitment.service';
import { Candidate } from '../../core/models/candidate.model';
import { Application, Job } from '../../core/models/recruitment.model';

interface InterviewView extends Interview {
  candidatName: string;
  role: string;
}

@Component({
  selector: 'app-interview',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './interview.component.html',
  styleUrls: ['./interview.component.css']
})
export class InterviewComponent implements OnInit {
  interviews: InterviewView[] = [];
  candidates: Candidate[] = [];
  applications: Application[] = [];
  jobs: Job[] = [];
  loading = true;
  error = '';
  showForm = false;
  showEvaluationForm = false;
  selectedInterview: InterviewView | null = null;

  form: Interview = {
    candidateId: 0,
    applicationId: 0,
    interviewerName: '',
    interviewDate: '',
    type: 'ONLINE',
    status: 'SCHEDULED'
  };

  evaluationForm: Evaluation = {
    interviewId: 0,
    technicalRating: 3,
    communicationRating: 3,
    generalComments: '',
    recommendation: 'HOLD'
  };

  constructor(
    private interviewService: InterviewService,
    private candidateService: CandidateService,
    private recruitmentService: RecruitmentService
  ) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.loading = true;
    this.error = '';
    forkJoin({
      interviews: this.interviewService.getAll(),
      candidates: this.candidateService.getAll(),
      applications: this.recruitmentService.getApplications(),
      jobs: this.recruitmentService.getJobs()
    }).subscribe({
      next: ({ interviews, candidates, applications, jobs }) => {
        this.candidates = candidates;
        this.applications = applications;
        this.jobs = jobs;
        this.interviews = interviews.map((i) => this.toView(i));
        this.loading = false;
      },
      error: () => {
        this.error = 'Impossible de charger les entretiens';
        this.loading = false;
      }
    });
  }

  getStatusLabel(status?: string): string {
    return INTERVIEW_STATUS_LABELS[status ?? ''] ?? status ?? '—';
  }

  getTypeLabel(type?: string): string {
    return INTERVIEW_TYPE_LABELS[type ?? ''] ?? type ?? '—';
  }

  openScheduleForm() {
    this.form = {
      candidateId: this.candidates[0]?.id ?? 0,
      applicationId: this.getApplicationsForCandidate(this.candidates[0]?.id ?? 0)[0]?.id ?? 0,
      interviewerName: '',
      interviewDate: new Date().toISOString().slice(0, 16),
      type: 'ONLINE',
      status: 'SCHEDULED'
    };
    this.showForm = true;
  }

  onCandidateChange() {
    const apps = this.getApplicationsForCandidate(this.form.candidateId);
    this.form.applicationId = apps[0]?.id ?? 0;
  }

  getApplicationsForCandidate(candidateId: number): Application[] {
    return this.applications.filter((a) => a.candidateId === candidateId);
  }

  getJobTitleForApplication(applicationId: number): string {
    const app = this.applications.find(a => a.id === applicationId);
    if (!app) return `Poste inconnu`;
    const job = this.jobs.find(j => j.id === app.jobId);
    return job?.title ?? `Poste #${app.jobId}`;
  }

  closeForm() {
    this.showForm = false;
  }

  scheduleInterview() {
    if (!this.form.candidateId || !this.form.applicationId || !this.form.interviewDate) {
      this.error = 'Candidat, candidature et date sont obligatoires';
      return;
    }
    this.interviewService.schedule(this.form).subscribe({
      next: () => {
        this.closeForm();
        this.loadData();
      },
      error: () => {
        this.error = 'Erreur lors de la planification';
      }
    });
  }

  completeInterview(interview: InterviewView) {
    if (!interview.id) return;
    this.interviewService.updateStatus(interview.id, 'COMPLETED').subscribe({
      next: () => this.loadData(),
      error: () => {
        this.error = 'Impossible de mettre à jour le statut';
      }
    });
  }

  openEvaluation(interview: InterviewView) {
    this.selectedInterview = interview;
    this.evaluationForm = {
      interviewId: interview.id!,
      technicalRating: 3,
      communicationRating: 3,
      generalComments: '',
      recommendation: 'HOLD'
    };
    this.showEvaluationForm = true;
  }

  closeEvaluation() {
    this.showEvaluationForm = false;
    this.selectedInterview = null;
  }

  submitEvaluation() {
    this.interviewService.submitEvaluation(this.evaluationForm).subscribe({
      next: () => {
        this.closeEvaluation();
        this.loadData();
      },
      error: () => {
        this.error = 'Erreur lors de l\'évaluation';
      }
    });
  }

  joinMeeting(interview: InterviewView) {
    const link = interview.location;
    if (link?.startsWith('http')) {
      window.open(link, '_blank');
    } else {
      alert(link ? `Lieu : ${link}` : `Lien visio pour l'entretien #${interview.id} (non renseigné)`);
    }
  }

  computedGlobalScore(): string {
    const tech = this.evaluationForm.technicalRating ?? 0;
    const comm = this.evaluationForm.communicationRating ?? 0;
    const score = tech * 0.6 + comm * 0.4;
    return score.toFixed(1);
  }

  private toView(interview: Interview): InterviewView {
    const candidate = this.candidates.find((c) => c.id === interview.candidateId);
    return {
      ...interview,
      candidatName: candidate ? `${candidate.firstName} ${candidate.lastName}` : `Candidat #${interview.candidateId}`,
      role: candidate?.title ?? '—'
    };
  }
}
