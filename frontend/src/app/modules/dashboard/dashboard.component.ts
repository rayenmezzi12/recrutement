import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { RoleService } from '../../core/services/role.service';
import { DASHBOARD_MODULES, DashboardModule } from '../../core/navigation/nav-items';
import { ROLES } from '../../core/constants/roles';
import { DashboardService, DashboardKpi } from '../../core/services/dashboard.service';
import { DeptReviewService, DeptReviewItem } from '../../core/services/dept-review.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  username = '';
  roleLabel = '';
  modules: DashboardModule[] = [];
  accessDenied = false;
  dashboardTitle = 'Tableau de bord';
  kpis: DashboardKpi | null = null;
  showKpis = false;
  stepKeys: string[] = [];
  deptReviews: DeptReviewItem[] = [];
  showDeptReviews = false;
  errorDeptReviews = '';

  constructor(
    private authService: AuthService,
    private roleService: RoleService,
    private route: ActivatedRoute,
    private dashboardService: DashboardService,
    private deptReviewService: DeptReviewService
  ) {}

  ngOnInit(): void {
    this.username = this.authService.getUsername() ?? '';
    this.roleLabel = this.roleService.primaryRoleLabel();
    this.modules = DASHBOARD_MODULES.filter((m) =>
      this.roleService.hasAnyRole(m.roles)
    );
    this.accessDenied = this.route.snapshot.queryParamMap.get('accessDenied') === '1';
    this.showKpis = this.roleService.showGlobalKpis();

    if (this.roleService.hasRole(ROLES.CANDIDAT) && !this.roleService.isStaff()) {
      this.dashboardTitle = 'Espace candidat';
    } else if (this.roleService.isResponsableRh()) {
      this.dashboardTitle = 'Espace responsable RH';
    } else if (this.roleService.isResponsableDept()) {
      this.dashboardTitle = 'Espace responsable département';
    } else if (this.roleService.hasRole(ROLES.RECRUTEUR)) {
      this.dashboardTitle = 'Espace recruteur';
    }

    if (this.showKpis) {
      this.dashboardService.getKpis(7).subscribe({
        next: (k) => {
          this.kpis = k;
          this.stepKeys = Object.keys(k.applicationsByStep ?? {});
        },
        error: () => (this.kpis = null)
      });
    }

    if (this.roleService.isRecruteur() || this.roleService.isResponsableRh()) {
      this.showDeptReviews = true;
      this.deptReviewService.getRecent().subscribe({
        next: (items) => {
          this.deptReviews = items;
          this.errorDeptReviews = '';
        },
        error: (err) => {
          this.deptReviews = [];
          this.errorDeptReviews = 'Impossible de charger les avis récents (erreur réseau ou accès refusé).';
        }
      });
    }
  }
}
