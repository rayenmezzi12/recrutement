import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { forkJoin } from 'rxjs';
import { CandidateService } from '../../core/services/candidate.service';
import { RecruitmentService } from '../../core/services/recruitment.service';
import { InterviewService } from '../../core/services/interview.service';
import { ReportService } from '../../core/services/report.service';

interface Metric {
  title: string;
  value: string | number;
  trend: string;
  isPositive: boolean;
}

@Component({
  selector: 'app-report',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.css']
})
export class ReportComponent implements OnInit {
  metrics: Metric[] = [];
  chartData: number[] = [];
  monthLabels: string[] = [];
  yTicks: number[] = [];
  loading = true;
  error = '';
  reportMessage = '';

  constructor(
    private candidateService: CandidateService,
    private recruitmentService: RecruitmentService,
    private interviewService: InterviewService,
    private reportService: ReportService
  ) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.loading = true;
    forkJoin({
      candidates: this.candidateService.getAll(),
      applications: this.recruitmentService.getApplications(),
      jobs: this.recruitmentService.getJobs(),
      interviews: this.interviewService.getAll()
    }).subscribe({
      next: ({ candidates, applications, jobs, interviews }) => {
        const selected = applications.filter((a) => a.status === 'SELECTIONNE').length;
        const rejected = applications.filter((a) => a.status === 'REJETE').length;
        const openJobs = jobs.filter((j) => j.status === 'OPEN').length;
        const completedInterviews = interviews.filter((i) => i.status === 'COMPLETED').length;

        this.metrics = [
          { title: 'Total Candidats', value: candidates.length, trend: `${applications.length} candidatures`, isPositive: true },
          { title: 'Offres actives', value: openJobs, trend: `${jobs.length} au total`, isPositive: true },
          { title: 'Sélectionnés', value: selected, trend: `${rejected} rejetés`, isPositive: selected >= rejected },
          { title: 'Entretiens terminés', value: completedInterviews, trend: `${interviews.length} planifiés`, isPositive: true }
        ];

        this.generateLabels();
        this.chartData = this.buildMonthlyCounts(applications.map((a) => a.appliedDate).filter(Boolean) as string[]);
        this.yTicks = this.generateYTicks();
        this.loading = false;
      },
      error: () => {
        this.error = 'Impossible de charger les statistiques';
        this.loading = false;
      }
    });
  }

  downloadReport(type: 'PDF' | 'CSV') {
    const end = new Date();
    const start = new Date();
    start.setMonth(start.getMonth() - 6);
    this.reportService
      .generate({
        startDate: start.toISOString().slice(0, 10),
        endDate: end.toISOString().slice(0, 10),
        type
      })
      .subscribe({
        next: (blob) => {
          const ext = type === 'PDF' ? 'pdf' : 'csv';
          const url = URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = `rapport-recrutement.${ext}`;
          a.click();
          URL.revokeObjectURL(url);
          this.reportMessage = `Rapport ${type} téléchargé.`;
        },
        error: () => {
          this.error = 'Erreur lors de la génération du rapport';
        }
      });
  }

  getBarHeight(value: number): string {
    const max = Math.max(...this.yTicks, 1);
    return `${(value / max) * 100}%`;
  }

  private generateLabels() {
    const months = ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Juin', 'Juil', 'Aoû', 'Sep', 'Oct', 'Nov', 'Déc'];
    this.monthLabels = [];
    const now = new Date();
    for (let i = 5; i >= 0; i--) {
      const d = new Date(now.getFullYear(), now.getMonth() - i, 1);
      this.monthLabels.push(months[d.getMonth()]);
    }
  }

  private generateYTicks(): number[] {
    const max = Math.max(...this.chartData, 1);
    const step = Math.ceil(max / 4);
    return [max, step * 3, step * 2, step, 0];
  }

  private buildMonthlyCounts(dates: string[]): number[] {
    const counts = new Array(6).fill(0);
    const now = new Date();
    
    dates.forEach((dateStr) => {
      const d = new Date(dateStr);
      const diffMonths = (now.getFullYear() - d.getFullYear()) * 12 + now.getMonth() - d.getMonth();
      if (diffMonths >= 0 && diffMonths < 6) {
        counts[5 - diffMonths]++;
      }
    });
    return counts;
  }
}
