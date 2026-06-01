import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { ROLES } from './core/constants/roles';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./modules/auth/login.component').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./modules/auth/register.component').then(m => m.RegisterComponent) },
  {
    path: '',
    loadComponent: () => import('./core/layout/main-layout.component').then(m => m.MainLayoutComponent),
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () => import('./modules/dashboard/dashboard.component').then(m => m.DashboardComponent),
        canActivate: [roleGuard([ROLES.CANDIDAT, ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH, ROLES.RESPONSABLE_DEPT])]
      },
      {
        path: 'chatbot',
        loadComponent: () => import('./modules/chatbot/chatbot.component').then(m => m.ChatbotComponent),
        canActivate: [roleGuard([ROLES.CANDIDAT, ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH])]
      },
      {
        path: 'offers',
        loadComponent: () => import('./modules/offer/offer.component').then(m => m.OfferComponent),
        canActivate: [roleGuard([ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH])]
      },
      {
        path: 'my-profile',
        loadComponent: () => import('./modules/my-profile/my-profile.component').then(m => m.MyProfileComponent),
        canActivate: [roleGuard([ROLES.CANDIDAT])]
      },
      {
        path: 'candidates',
        loadComponent: () => import('./modules/candidat/candidat.component').then(m => m.CandidatComponent),
        canActivate: [roleGuard([ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH])]
      },
      {
        path: 'interviews',
        loadComponent: () => import('./modules/interview/interview.component').then(m => m.InterviewComponent),
        canActivate: [roleGuard([ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH])]
      },
      {
        path: 'recruitment',
        loadComponent: () => import('./modules/recruitment/recruitment.component').then(m => m.RecruitmentComponent),
        canActivate: [roleGuard([ROLES.CANDIDAT, ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH])]
      },
      {
        path: 'dept-review',
        loadComponent: () => import('./modules/dept-review/dept-review.component').then(m => m.DeptReviewComponent),
        canActivate: [roleGuard([ROLES.RESPONSABLE_DEPT])]
      },
      {
        path: 'employment-offers',
        loadComponent: () => import('./modules/employment-offer/employment-offer.component').then(m => m.EmploymentOfferComponent),
        canActivate: [roleGuard([ROLES.CANDIDAT, ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH])]
      },
      {
        path: 'reports',
        loadComponent: () => import('./modules/report/report.component').then(m => m.ReportComponent),
        canActivate: [roleGuard([ROLES.RESPONSABLE_RH])]
      },
      {
        path: 'admin',
        loadComponent: () => import('./modules/admin/admin.component').then(m => m.AdminComponent),
        canActivate: [roleGuard([ROLES.RESPONSABLE_RH])]
      },
      {
        path: 'messages',
        loadComponent: () => import('./modules/messaging/messaging.component').then(m => m.MessagingComponent),
        canActivate: [roleGuard([ROLES.CANDIDAT, ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH])]
      },
      {
        path: 'notifications',
        loadComponent: () => import('./modules/notification/notification.component').then(m => m.NotificationComponent),
        canActivate: [roleGuard([ROLES.CANDIDAT, ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH, ROLES.RESPONSABLE_DEPT])]
      }
    ]
  },
  { path: '**', redirectTo: '/login' }
];
