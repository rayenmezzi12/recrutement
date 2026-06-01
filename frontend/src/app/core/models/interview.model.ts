export interface Interview {
  id?: number;
  candidateId: number;
  applicationId: number;
  interviewerName?: string;
  interviewDate?: string;
  type?: string;
  status?: string;
  feedback?: string;
  location?: string;
}

export interface Evaluation {
  id?: number;
  interviewId: number;
  technicalRating?: number;
  communicationRating?: number;
  generalComments?: string;
  recommendation?: string;
  globalScore?: number;
}

export const INTERVIEW_TYPE_LABELS: Record<string, string> = {
  ONLINE: 'En ligne',
  IN_PERSON: 'Sur site'
};

export const INTERVIEW_STATUS_LABELS: Record<string, string> = {
  SCHEDULED: 'À venir',
  COMPLETED: 'Terminé',
  CANCELLED: 'Annulé'
};
