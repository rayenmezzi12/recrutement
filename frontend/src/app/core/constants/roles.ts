export const ROLES = {
  CANDIDAT: 'CANDIDAT',
  RECRUTEUR: 'RECRUTEUR',
  RESPONSABLE_RH: 'RESPONSABLE_RH',
  RESPONSABLE_DEPT: 'RESPONSABLE_DEPT',
  ADMIN: 'ADMIN'
} as const;

export type AppRole = (typeof ROLES)[keyof typeof ROLES];

/** Recruteur, RH et responsable département */
export const STAFF_ROLES: AppRole[] = [
  ROLES.RECRUTEUR,
  ROLES.RESPONSABLE_RH,
  ROLES.RESPONSABLE_DEPT,
  ROLES.ADMIN
];

export const ALL_ROLES: AppRole[] = [ROLES.CANDIDAT, ...STAFF_ROLES];
