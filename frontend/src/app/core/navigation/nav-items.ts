import { ROLES } from '../constants/roles';

export interface NavItem {
  label: string;
  route: string;
  icon: string;
  roles: string[];
}

export const NAV_ITEMS: NavItem[] = [
  { label: 'Tableau de bord', route: '/dashboard', icon: '🏠', roles: [ROLES.CANDIDAT, ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH, ROLES.RESPONSABLE_DEPT] },
  { label: 'Mon profil', route: '/my-profile', icon: '👤', roles: [ROLES.CANDIDAT] },
  { label: 'Mes candidatures', route: '/recruitment', icon: '📋', roles: [ROLES.CANDIDAT] },
  { label: 'Candidats', route: '/candidates', icon: '👥', roles: [ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH] },
  { label: 'Entretiens', route: '/interviews', icon: '📅', roles: [ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH] },
  { label: 'Pipeline', route: '/recruitment', icon: '🔄', roles: [ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH] },
  { label: 'Offres d\'emploi', route: '/offers', icon: '💼', roles: [ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH] },
  { label: 'Offres d\'embauche', route: '/employment-offers', icon: '📝', roles: [ROLES.CANDIDAT, ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH] },
  { label: 'Évaluation département', route: '/dept-review', icon: '📐', roles: [ROLES.RESPONSABLE_DEPT] },
  { label: 'Rapports', route: '/reports', icon: '📊', roles: [ROLES.RESPONSABLE_RH] },
  { label: 'Messagerie', route: '/messages', icon: '💬', roles: [ROLES.CANDIDAT, ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH] },
  { label: 'Notifications', route: '/notifications', icon: '🔔', roles: [ROLES.CANDIDAT, ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH, ROLES.RESPONSABLE_DEPT] },
  { label: 'Administration', route: '/admin', icon: '⚙️', roles: [ROLES.RESPONSABLE_RH] },
  { label: 'Assistant IA', route: '/chatbot', icon: '🤖', roles: [ROLES.CANDIDAT, ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH] }
];

export interface DashboardModule {
  title: string;
  description: string;
  route: string;
  icon: string;
  roles: string[];
}

export const DASHBOARD_MODULES: DashboardModule[] = [
  {
    title: 'Mes candidatures',
    description: 'Suivi de votre progression (lecture seule)',
    route: '/recruitment',
    icon: '📋',
    roles: [ROLES.CANDIDAT]
  },
  {
    title: 'Mon profil',
    description: 'CV, compétences et coordonnées',
    route: '/my-profile',
    icon: '👤',
    roles: [ROLES.CANDIDAT]
  },
  {
    title: 'Messagerie',
    description: 'Échanges avec le recruteur',
    route: '/messages',
    icon: '💬',
    roles: [ROLES.CANDIDAT]
  },
  {
    title: 'Notifications',
    description: 'Emails et alertes reçues',
    route: '/notifications',
    icon: '🔔',
    roles: [ROLES.CANDIDAT, ROLES.RESPONSABLE_DEPT]
  },
  {
    title: 'Offres d\'embauche',
    description: 'Consulter et répondre aux offres',
    route: '/employment-offers',
    icon: '📝',
    roles: [ROLES.CANDIDAT]
  },
  {
    title: 'Assistant IA',
    description: 'Statut candidature, entretiens, démarches',
    route: '/chatbot',
    icon: '🤖',
    roles: [ROLES.CANDIDAT, ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH]
  },
  {
    title: 'Pipeline',
    description: 'Kanban drag & drop entre les étapes',
    route: '/recruitment',
    icon: '🔄',
    roles: [ROLES.RECRUTEUR]
  },
  {
    title: 'Entretiens',
    description: 'Planifier et évaluer',
    route: '/interviews',
    icon: '📅',
    roles: [ROLES.RECRUTEUR]
  },
  {
    title: 'Candidats',
    description: 'Vivier de talents',
    route: '/candidates',
    icon: '👥',
    roles: [ROLES.RECRUTEUR]
  },
  {
    title: 'Offres d\'embauche',
    description: 'Générer et envoyer les offres',
    route: '/employment-offers',
    icon: '📝',
    roles: [ROLES.RECRUTEUR]
  },
  {
    title: 'Évaluation département',
    description: 'Tests techniques et entretiens finaux',
    route: '/dept-review',
    icon: '📐',
    roles: [ROLES.RESPONSABLE_DEPT]
  },
  {
    title: 'Rapports',
    description: 'Exports PDF / CSV et KPIs',
    route: '/reports',
    icon: '📊',
    roles: [ROLES.RESPONSABLE_RH]
  },
  {
    title: 'Administration',
    description: 'Utilisateurs, rôles et archivage',
    route: '/admin',
    icon: '⚙️',
    roles: [ROLES.RESPONSABLE_RH]
  },
  {
    title: 'Pipeline global',
    description: 'Vue complète du recrutement',
    route: '/recruitment',
    icon: '🔄',
    roles: [ROLES.RESPONSABLE_RH]
  },
  {
    title: 'Candidats',
    description: 'Tous les profils',
    route: '/candidates',
    icon: '👥',
    roles: [ROLES.RESPONSABLE_RH]
  },
  {
    title: 'Messagerie',
    description: 'Échanges internes',
    route: '/messages',
    icon: '💬',
    roles: [ROLES.RECRUTEUR, ROLES.RESPONSABLE_RH]
  }
];
