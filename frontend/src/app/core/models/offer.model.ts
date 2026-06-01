export type OfferStatus = 'EN_ATTENTE' | 'ENVOYEE' | 'ACCEPTEE' | 'REFUSEE';

export interface EmploymentOffer {
  id?: number;
  candidateId: number;
  applicationId: number;
  salaryOffer?: number;
  positionTitle?: string;
  offerLetterContent?: string;
  status?: OfferStatus | string;
  sentDate?: string;
  expirationDate?: string;
}

export const OFFER_STATUS_LABELS: Record<string, string> = {
  EN_ATTENTE: 'En attente',
  ENVOYEE: 'Envoyée',
  ACCEPTEE: 'Acceptée',
  REFUSEE: 'Refusée',
  GENERATED: 'En attente',
  SENT: 'Envoyée',
  ACCEPTED: 'Acceptée',
  REJECTED: 'Refusée'
};

export function normalizeOfferStatus(status?: string | null): string {
  if (!status) return 'EN_ATTENTE';
  const map: Record<string, string> = {
    GENERATED: 'EN_ATTENTE',
    SENT: 'ENVOYEE',
    ACCEPTED: 'ACCEPTEE',
    REJECTED: 'REFUSEE'
  };
  return map[status] ?? status;
}

export function offerCanSend(status?: string | null): boolean {
  const s = normalizeOfferStatus(status);
  return s === 'EN_ATTENTE';
}

export function offerCanRespond(status?: string | null): boolean {
  return normalizeOfferStatus(status) === 'ENVOYEE';
}
