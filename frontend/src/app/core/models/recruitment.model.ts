export type RecruitmentStep =
  | 'PRE_SELECTION'
  | 'ENTRETIEN_RH'
  | 'TEST_TECHNIQUE'
  | 'ENTRETIEN_FINAL'
  | 'OFFRE_EMBAUCHE';

export type ApplicationStatus = 'EN_ATTENTE' | 'SELECTIONNE' | 'REJETE' | 'EMBAUCHE';

export interface Job {
  id?: number;
  title: string;
  department?: string;
  description?: string;
  location?: string;
  status?: string;
  createdDate?: string;
}

export interface Application {
  id?: number;
  jobId: number;
  candidateId: number;
  currentStep?: RecruitmentStep;
  status?: ApplicationStatus;
  appliedDate?: string;
  notes?: string;
  recruiterUsername?: string;
  archived?: boolean;
  globalScore?: number;
}

export const RECRUITMENT_STEPS: RecruitmentStep[] = [
  'PRE_SELECTION',
  'ENTRETIEN_RH',
  'TEST_TECHNIQUE',
  'ENTRETIEN_FINAL',
  'OFFRE_EMBAUCHE'
];

export const STEP_LABELS: Record<RecruitmentStep, string> = {
  PRE_SELECTION: 'Pré-sélection',
  ENTRETIEN_RH: 'Entretien RH',
  TEST_TECHNIQUE: 'Test technique',
  ENTRETIEN_FINAL: 'Entretien final',
  OFFRE_EMBAUCHE: "Offre d'embauche"
};

export const STATUS_LABELS: Record<ApplicationStatus, string> = {
  EN_ATTENTE: 'En attente',
  SELECTIONNE: 'Sélectionné',
  REJETE: 'Rejeté',
  EMBAUCHE: 'Embauché'
};

/** Anciennes valeurs en base → étapes du pipeline actuel */
export function normalizeRecruitmentStep(step?: string | null): RecruitmentStep {
  if (!step) return 'PRE_SELECTION';
  const legacy: Record<string, RecruitmentStep> = {
    ENTRETIEN: 'ENTRETIEN_RH',
    TEST: 'TEST_TECHNIQUE',
    OFFRE: 'OFFRE_EMBAUCHE'
  };
  if (legacy[step]) return legacy[step];
  if (RECRUITMENT_STEPS.includes(step as RecruitmentStep)) {
    return step as RecruitmentStep;
  }
  return 'PRE_SELECTION';
}
