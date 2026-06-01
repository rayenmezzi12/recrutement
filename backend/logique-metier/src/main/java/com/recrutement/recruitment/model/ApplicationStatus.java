package com.recrutement.recruitment.model;

public enum ApplicationStatus {
    EN_ATTENTE,
    SELECTIONNE,
    REJETE,
    EMBAUCHE,
    /** Valeurs historiques — migrées au démarrage */
    PENDING,
    SELECTED,
    APPROVED,
    REJECTED,
    REFUSED
}
