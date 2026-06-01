import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import {
  CdkDrag,
  CdkDragDrop,
  CdkDragHandle,
  CdkDropList,
  DragDropModule,
  moveItemInArray,
  transferArrayItem
} from '@angular/cdk/drag-drop';
import { forkJoin, of, switchMap } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { ApplicationFilters } from '../../core/services/recruitment.service';
import {
  Application,
  normalizeRecruitmentStep,
  RECRUITMENT_STEPS,
  RecruitmentStep,
  STEP_LABELS,
  STATUS_LABELS
} from '../../core/models/recruitment.model';
import { RecruitmentService } from '../../core/services/recruitment.service';
import { CandidateService } from '../../core/services/candidate.service';
import { RoleService } from '../../core/services/role.service';
import { Candidate } from '../../core/models/candidate.model';
import { Job } from '../../core/models/recruitment.model';

interface PipelineStage {
  step: RecruitmentStep;
  title: string;
  color: string;
  applications: ApplicationView[];
}

interface ApplicationView extends Application {
  candidateName: string;
  jobTitle: string;
}

@Component({
  selector: 'app-recruitment',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, DragDropModule, CdkDropList, CdkDrag, CdkDragHandle],
  templateUrl: './recruitment.component.html',
  styleUrls: ['./recruitment.component.css']
})
export class RecruitmentComponent implements OnInit {
  readonly allSteps = RECRUITMENT_STEPS;
  readonly stepLabels = STEP_LABELS;
  stages: PipelineStage[] = [];
  candidates: Candidate[] = [];
  jobs: Job[] = [];
  loading = true;
  error = '';
  showApplyForm = false;
  canManage = false;
  canDrag = false;
  isCandidat = false;
  readonlyMode = false;
  myApplications: ApplicationView[] = [];
  myCandidateId: number | null = null;
  moving = false;

  filters: ApplicationFilters = { archived: false };
  filterJobId: number | null = null;
  filterStep: RecruitmentStep | '' = '';
  filterStatus: Application['status'] | '' = '';

  applyForm = {
    jobId: 0,
    candidateId: 0,
    notes: ''
  };

  private readonly stageColors: Record<RecruitmentStep, string> = {
    PRE_SELECTION: '#0061ff',
    ENTRETIEN_RH: '#ed8936',
    TEST_TECHNIQUE: '#60efff',
    ENTRETIEN_FINAL: '#9f7aea',
    OFFRE_EMBAUCHE: '#48bb78'
  };

  constructor(
    private recruitmentService: RecruitmentService,
    private candidateService: CandidateService,
    private roleService: RoleService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.isCandidat = this.roleService.isCandidat();
    this.readonlyMode = this.isCandidat;
    this.canManage = this.roleService.canAccessPipeline() && !this.isCandidat;
    this.canDrag = this.roleService.canDragPipeline();
    this.loadData();
  }

  loadData() {
    this.loading = true;
    this.error = '';
    const apps$ = this.isCandidat
      ? this.candidateService.getByUsername(this.authService.getUsername()!).pipe(
          switchMap((c) => {
            this.myCandidateId = c.id ?? null;
            return this.recruitmentService.getApplications({
              candidateId: c.id,
              archived: this.filters.archived
            });
          })
        )
      : this.recruitmentService.getApplications(this.buildFilters());

    forkJoin({
      applications: apps$,
      candidates: this.canManage ? this.candidateService.getAll() : of([]),
      jobs: this.recruitmentService.getJobs()
    }).subscribe({
      next: ({ applications, candidates, jobs }) => {
        this.candidates = candidates;
        this.jobs = jobs;
        const views = applications.map((a) => this.toView(a));
        this.myApplications = views;
        this.stages = RECRUITMENT_STEPS.map((step) => ({
          step,
          title: STEP_LABELS[step],
          color: this.stageColors[step],
          applications: views.filter((a) => normalizeRecruitmentStep(a.currentStep) === step)
        }));
        this.loading = false;
      },
      error: () => {
        this.error = 'Impossible de charger le pipeline';
        this.loading = false;
      }
    });
  }

  buildFilters(): ApplicationFilters {
    return {
      jobId: this.filterJobId ?? undefined,
      step: this.filterStep || undefined,
      status: this.filterStatus || undefined,
      archived: this.filters.archived
    };
  }

  applyFilters() {
    this.loadData();
  }

  openApplyForm() {
    this.applyForm = {
      jobId: this.jobs[0]?.id ?? 0,
      candidateId: this.myCandidateId ?? this.candidates[0]?.id ?? 0,
      notes: ''
    };
    this.showApplyForm = true;
  }

  closeApplyForm() {
    this.showApplyForm = false;
  }

  submitApplication() {
    if (!this.applyForm.jobId || !this.applyForm.candidateId) {
      this.error = 'Sélectionnez un poste et un candidat';
      return;
    }
    const application: Application = {
      jobId: this.applyForm.jobId,
      candidateId: this.applyForm.candidateId,
      notes: this.applyForm.notes,
      currentStep: 'PRE_SELECTION',
      status: 'EN_ATTENTE'
    };
    this.recruitmentService.apply(application).subscribe({
      next: () => {
        this.closeApplyForm();
        this.loadData();
      },
      error: () => {
        this.error = 'Erreur lors de la candidature';
      }
    });
  }

  isStepReached(step: RecruitmentStep, current?: RecruitmentStep): boolean {
    const cur = normalizeRecruitmentStep(current);
    return RECRUITMENT_STEPS.indexOf(step) <= RECRUITMENT_STEPS.indexOf(cur);
  }

  onKanbanDrop(event: CdkDragDrop<ApplicationView[]>, targetStep: RecruitmentStep) {
    if (!this.canDrag || this.moving) return;

    const app = event.previousContainer.data[event.previousIndex];
    if (!app?.id) return;

    const previousStep = normalizeRecruitmentStep(app.currentStep);

    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
      return;
    }

    if (previousStep === targetStep) {
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
      return;
    }

    transferArrayItem(
      event.previousContainer.data,
      event.container.data,
      event.previousIndex,
      event.currentIndex
    );
    app.currentStep = targetStep;

    this.moving = true;
    this.error = '';
    this.recruitmentService.updateStep(app.id, targetStep).subscribe({
      next: () => {
        this.moving = false;
      },
      error: () => {
        this.moving = false;
        this.error = 'Impossible de déplacer la candidature — rechargement.';
        this.loadData();
      }
    });
  }

  updateStatus(app: ApplicationView, status: Application['status'], event?: MouseEvent) {
    event?.stopPropagation();
    if (!app.id || !status) return;
    if (status === 'REJETE' && !window.confirm(`Êtes-vous sûr de vouloir rejeter la candidature de ${app.candidateName} ?`)) {
      return;
    }
    this.recruitmentService.updateStatus(app.id, status).subscribe({
      next: () => this.loadData(),
      error: () => {
        this.error = 'Impossible de mettre à jour le statut';
      }
    });
  }

  getStatusLabel(status?: string): string {
    return STATUS_LABELS[status as keyof typeof STATUS_LABELS] ?? status ?? '—';
  }

  private toView(app: Application): ApplicationView {
    const candidate = this.candidates.find((c) => c.id === app.candidateId);
    const job = this.jobs.find((j) => j.id === app.jobId);
    const step = normalizeRecruitmentStep(app.currentStep);
    return {
      ...app,
      currentStep: step,
      candidateName: candidate
        ? `${candidate.firstName} ${candidate.lastName}`
        : `Candidat #${app.candidateId}`,
      jobTitle: job?.title ?? `Poste #${app.jobId}`
    };
  }
}
