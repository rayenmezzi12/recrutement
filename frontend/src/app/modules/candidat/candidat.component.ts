import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Candidate } from '../../core/models/candidate.model';
import { CandidateService } from '../../core/services/candidate.service';
import { RecruitmentService } from '../../core/services/recruitment.service';
import { RoleService } from '../../core/services/role.service';
import { Application, STATUS_LABELS } from '../../core/models/recruitment.model';

interface CandidateView extends Candidate {
  applicationStatus?: string;
}

@Component({
  selector: 'app-candidat',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './candidat.component.html',
  styleUrls: ['./candidat.component.css']
})
export class CandidatComponent implements OnInit {
  candidats: CandidateView[] = [];
  allCandidats: CandidateView[] = [];
  applications: Application[] = [];
  loading = true;
  error = '';
  showForm = false;
  editingId: number | null = null;
  selectedCandidate: CandidateView | null = null;

  form: Candidate = {
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    title: '',
    skills: '',
    experienceYears: 0,
    coverLetter: ''
  };

  canManage = false;
  searchQ = '';
  searchSkills = '';
  searchTitle = '';
  history: { actionType: string; details: string; createdAt: string; actorUsername: string }[] = [];
  historyLoading = false;
  cvFile: File | null = null;

  constructor(
    private candidateService: CandidateService,
    private recruitmentService: RecruitmentService,
    private roleService: RoleService
  ) {}

  ngOnInit() {
    this.canManage = this.roleService.canManageCandidates();
    this.loadData();
  }

  loadData() {
    this.loading = true;
    this.error = '';
    const hasSearch = !!(this.searchQ?.trim() || this.searchSkills?.trim() || this.searchTitle?.trim());

    this.candidateService.getAll({
      q: this.searchQ?.trim() || undefined,
      skills: this.searchSkills?.trim() || undefined,
      title: this.searchTitle?.trim() || undefined
    }).subscribe({
      next: (candidates) => {
        this.recruitmentService.getApplications().subscribe({
          next: (apps) => {
            this.applications = apps;
            const mapped = candidates.map((c) => ({
              ...c,
              applicationStatus: this.getLatestStatus(c.id!)
            }));
            this.allCandidats = hasSearch && this.allCandidats.length ? this.allCandidats : mapped;
            this.candidats = mapped;
            this.loading = false;
          },
          error: () => {
            this.allCandidats = hasSearch && this.allCandidats.length ? this.allCandidats : candidates;
            this.candidats = candidates;
            this.loading = false;
          }
        });
      },
      error: (err) => {
        this.error = err.error?.error || 'Impossible de charger les candidats';
        this.applyLocalFilter();
        this.loading = false;
      }
    });
  }

  applyLocalFilter() {
    const q = this.searchQ.trim().toLowerCase();
    const skills = this.searchSkills.trim().toLowerCase();
    const title = this.searchTitle.trim().toLowerCase();
    const source = this.allCandidats.length ? this.allCandidats : this.candidats;

    this.candidats = source.filter((c) => {
      const matchQ = !q || `${c.firstName} ${c.lastName} ${c.email}`.toLowerCase().includes(q);
      const matchSkills = !skills || (c.skills ?? '').toLowerCase().includes(skills);
      const matchTitle = !title || (c.title ?? '').toLowerCase().includes(title);
      return matchQ && matchSkills && matchTitle;
    });
  }

  getSkillsList(skills?: string): string[] {
    return skills ? skills.split(',').map((s) => s.trim()).filter(Boolean) : [];
  }

  getFullName(c: Candidate): string {
    return `${c.firstName} ${c.lastName}`;
  }

  getStatusLabel(status?: string): string {
    if (!status) return 'Nouveau';
    return STATUS_LABELS[status as keyof typeof STATUS_LABELS] ?? status;
  }

  getStatusClass(status?: string): string {
    if (status === 'SELECTIONNE' || status === 'EMBAUCHE') return 'active';
    if (status === 'REJETE') return 'closed';
    if (status === 'EN_ATTENTE') return 'pending';
    return 'pending';
  }

  getScore(c: CandidateView): number {
    const app = this.applications.find((a) => a.candidateId === c.id);
    if (app?.globalScore != null && app.globalScore > 0) {
      return Math.min(100, Math.round((app.globalScore / 5) * 100));
    }
    const base = (c.experienceYears ?? 0) * 10;
    const skillsBonus = this.getSkillsList(c.skills).length * 5;
    return Math.min(100, base + skillsBonus + 40);
  }

  openCreateForm() {
    this.editingId = null;
    this.form = {
      firstName: '',
      lastName: '',
      email: '',
      phone: '',
      title: '',
      skills: '',
      experienceYears: 0,
      coverLetter: ''
    };
    this.showForm = true;
  }

  openEditForm(c: CandidateView) {
    this.editingId = c.id ?? null;
    this.form = { ...c };
    this.showForm = true;
  }

  closeForm() {
    this.showForm = false;
    this.editingId = null;
  }

  saveCandidate() {
    if (!this.form.firstName || !this.form.lastName || !this.form.email) {
      this.error = 'Prénom, nom et email sont obligatoires';
      return;
    }

    const payload = { ...this.form };
    if (!payload.dateOfBirth) delete payload.dateOfBirth;
    if (!payload.phone) delete payload.phone;
    if (!payload.address) delete payload.address;
    if (!payload.title) delete payload.title;
    if (!payload.skills) delete payload.skills;
    if (!payload.linkedin) delete payload.linkedin;
    if (!payload.coverLetter) delete payload.coverLetter;

    const request = this.editingId
      ? this.candidateService.update(this.editingId, payload)
      : this.candidateService.create(payload);

    request.subscribe({
      next: () => {
        this.closeForm();
        this.loadData();
      },
      error: (err) => {
        this.error = err.error?.error || err.error?.message || 'Erreur lors de l\'enregistrement du candidat';
      }
    });
  }

  deleteCandidate(id?: number) {
    if (!id || !confirm('Supprimer ce candidat ?')) return;
    this.candidateService.delete(id).subscribe({
      next: () => this.loadData(),
      error: () => {
        this.error = 'Impossible de supprimer le candidat';
      }
    });
  }

  applySearch() {
    this.loadData();
  }

  viewProfile(c: CandidateView) {
    this.selectedCandidate = c;
    this.history = [];
    this.cvFile = null;
    if (c.id) {
      this.historyLoading = true;
      this.candidateService.getHistory(c.id).subscribe({
        next: (h) => {
          this.history = h;
          this.historyLoading = false;
        },
        error: () => (this.historyLoading = false)
      });
    }
  }

  closeProfile() {
    this.selectedCandidate = null;
    this.history = [];
  }

  onCvSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    this.cvFile = input.files?.[0] ?? null;
  }

  uploadCvForSelected() {
    if (!this.selectedCandidate?.id || !this.cvFile) return;
    this.candidateService.uploadCv(this.selectedCandidate.id, this.cvFile).subscribe({
      next: (updated) => {
        this.selectedCandidate = { ...this.selectedCandidate!, ...updated };
        this.cvFile = null;
        this.loadData();
      },
      error: () => {
        this.error = 'Échec de l\'upload du CV';
      }
    });
  }

  contactCandidate(c: CandidateView) {
    if (c.email) {
      window.location.href = `mailto:${c.email}`;
    }
  }

  downloadCv(c: CandidateView) {
    if (!c.resumeUrl) return;
    this.candidateService.downloadFile(c.resumeUrl).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        const filename = c.resumeUrl?.substring(c.resumeUrl.lastIndexOf('/') + 1) || 'cv.pdf';
        a.download = filename;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: () => {
        this.error = 'Erreur lors du téléchargement du CV';
      }
    });
  }

  private getLatestStatus(candidateId: number): string | undefined {
    const apps = this.applications.filter((a) => a.candidateId === candidateId);
    if (!apps.length) return undefined;
    return apps[apps.length - 1].status;
  }
}
