import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Job } from '../../core/models/recruitment.model';
import { RecruitmentService } from '../../core/services/recruitment.service';
import { RoleService } from '../../core/services/role.service';
import { Application } from '../../core/models/recruitment.model';

interface JobView extends Job {
  applicants: number;
}

@Component({
  selector: 'app-offer',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './offer.component.html',
  styleUrls: ['./offer.component.css']
})
export class OfferComponent implements OnInit {
  offers: JobView[] = [];
  applications: Application[] = [];
  loading = true;
  error = '';
  showForm = false;
  editingId: number | null = null;
  selectedJob: JobView | null = null;

  form: Job = {
    title: '',
    department: '',
    description: '',
    location: '',
    status: 'OPEN'
  };

  canManage = false;

  constructor(
    private recruitmentService: RecruitmentService,
    private roleService: RoleService
  ) {}

  ngOnInit() {
    this.canManage = this.roleService.canAccessJobOffers();
    this.loadData();
  }

  loadData() {
    this.loading = true;
    this.error = '';
    forkJoin({
      jobs: this.recruitmentService.getJobs(),
      applications: this.recruitmentService.getApplications()
    }).subscribe({
      next: ({ jobs, applications }) => {
        this.applications = applications;
        this.offers = jobs.map((job) => ({
          ...job,
          applicants: applications.filter((a) => a.jobId === job.id).length
        }));
        this.loading = false;
      },
      error: () => {
        this.error = 'Impossible de charger les offres';
        this.loading = false;
      }
    });
  }

  getStatusLabel(status?: string): string {
    return status === 'OPEN' ? 'Active' : 'Clôturée';
  }

  openCreateForm() {
    this.editingId = null;
    this.form = { title: '', department: '', description: '', location: '', status: 'OPEN' };
    this.showForm = true;
  }

  openEditForm(job: JobView) {
    this.editingId = job.id ?? null;
    this.form = { ...job };
    this.showForm = true;
  }

  closeForm() {
    this.showForm = false;
    this.editingId = null;
  }

  saveJob() {
    if (!this.form.title) {
      this.error = 'Le titre est obligatoire';
      return;
    }
    const request = this.editingId
      ? this.recruitmentService.updateJob(this.editingId, this.form)
      : this.recruitmentService.createJob(this.form);

    request.subscribe({
      next: () => {
        this.closeForm();
        this.loadData();
      },
      error: () => {
        this.error = 'Erreur lors de l\'enregistrement de l\'offre';
      }
    });
  }

  deleteJob(id?: number) {
    if (!id || !confirm('Supprimer cette offre ?')) return;
    this.recruitmentService.deleteJob(id).subscribe({
      next: () => this.loadData(),
      error: () => {
        this.error = 'Impossible de supprimer l\'offre';
      }
    });
  }

  viewJob(job: JobView) {
    this.selectedJob = job;
  }

  closeView() {
    this.selectedJob = null;
  }
}
