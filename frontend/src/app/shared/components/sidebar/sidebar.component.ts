import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { RoleService } from '../../../core/services/role.service';
import { ROLES } from '../../../core/constants/roles';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
  
  allMenuItems = [
    { name: 'Tableau de bord', route: '/dashboard', icon: 'dashboard', roles: [ROLES.CANDIDAT, ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH, ROLES.RESPONSABLE_DEPT] },
    { name: 'Mon Profil', route: '/my-profile', icon: 'profile', roles: [ROLES.CANDIDAT] },
    { name: 'Candidats', route: '/candidates', icon: 'users', roles: [ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH] },
    { name: 'Recrutements', route: '/recruitment', icon: 'briefcase', roles: [ROLES.CANDIDAT, ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH] },
    { name: 'Entretiens', route: '/interviews', icon: 'calendar', roles: [ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH] },
    { name: 'Offres d\'emploi', route: '/offers', icon: 'file-text', roles: [ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH] },
    { name: 'Offres d\'embauche', route: '/employment-offers', icon: 'file-text', roles: [ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH] },
    { name: 'Évaluation Dept', route: '/dept-review', icon: 'calendar', roles: [ROLES.RESPONSABLE_DEPT] },
    { name: 'Rapports', route: '/reports', icon: 'pie-chart', roles: [ROLES.RESPONSABLE_RH] },
    { name: 'Messagerie', route: '/messages', icon: 'message', roles: [ROLES.CANDIDAT, ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH] },
    { name: 'Administration', route: '/admin', icon: 'settings', roles: [ROLES.RESPONSABLE_RH] }
  ];

  menuItems: any[] = [];
  showAiAssistant = false;

  constructor(
    private router: Router, 
    private authService: AuthService,
    private roleService: RoleService
  ) {}

  ngOnInit(): void {
    // Filter menu items by user roles
    this.menuItems = this.allMenuItems.filter(item => 
      this.roleService.hasAnyRole(item.roles)
    );
    // Show AI Assistant to candidate, recruiter, hr manager
    this.showAiAssistant = this.roleService.hasAnyRole([ROLES.CANDIDAT, ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH]);
  }

  isActive(route: string): boolean {
    return this.router.url.includes(route);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
