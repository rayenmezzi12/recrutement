import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { AppRole, ROLES, STAFF_ROLES } from '../constants/roles';

@Injectable({ providedIn: 'root' })
export class RoleService {
  constructor(private auth: AuthService) {}

  getRoles(): string[] {
    return this.auth.getRoles();
  }

  hasRole(role: string): boolean {
    return this.getRoles().includes(role);
  }

  hasAnyRole(roles: string[]): boolean {
    if (!roles.length) return true;
    return roles.some((r) => this.hasRole(r));
  }

  isCandidat(): boolean {
    return this.hasRole(ROLES.CANDIDAT) && !this.isStaff();
  }

  isRecruteur(): boolean {
    return this.hasRole(ROLES.RECRUTEUR);
  }

  isResponsableDept(): boolean {
    return this.hasRole(ROLES.RESPONSABLE_DEPT);
  }

  isStaff(): boolean {
    return this.hasAnyRole(STAFF_ROLES);
  }

  isResponsableRh(): boolean {
    return this.hasRole(ROLES.RESPONSABLE_RH) || this.hasRole(ROLES.ADMIN);
  }

  isAdmin(): boolean {
    return this.hasRole(ROLES.ADMIN);
  }

  /** Pipeline Kanban complet */
  canAccessPipeline(): boolean {
    return this.isRecruteur() || this.isResponsableRh();
  }

  canDragPipeline(): boolean {
    return this.isRecruteur();
  }

  canManageCandidates(): boolean {
    return this.isRecruteur() || this.isResponsableRh();
  }

  canAccessInterviews(): boolean {
    return this.isRecruteur() || this.isResponsableRh();
  }

  canAccessJobOffers(): boolean {
    return this.isRecruteur() || this.isResponsableRh();
  }

  canAccessEmploymentOffers(): boolean {
    return this.isRecruteur() || this.isResponsableRh() || this.isCandidat();
  }

  canAccessReports(): boolean {
    return this.isResponsableRh();
  }

  canAccessAdmin(): boolean {
    return this.isResponsableRh();
  }

  canAccessDeptReview(): boolean {
    return this.isResponsableDept();
  }

  showGlobalKpis(): boolean {
    return this.isResponsableRh();
  }

  primaryRoleLabel(): string {
    const roles = this.getRoles();
    const order: AppRole[] = [
      ROLES.ADMIN,
      ROLES.RESPONSABLE_RH,
      ROLES.RESPONSABLE_DEPT,
      ROLES.RECRUTEUR,
      ROLES.CANDIDAT
    ];
    const found = order.find((r) => roles.includes(r));
    return found ? this.roleDisplayName(found) : roles[0] ?? 'Utilisateur';
  }

  roleDisplayName(role: string): string {
    const labels: Record<string, string> = {
      [ROLES.CANDIDAT]: 'Candidat',
      [ROLES.RECRUTEUR]: 'Recruteur',
      [ROLES.RESPONSABLE_RH]: 'Responsable RH',
      [ROLES.RESPONSABLE_DEPT]: 'Resp. département',
      [ROLES.ADMIN]: 'Administrateur'
    };
    return labels[role] ?? role;
  }
}
