# Architecture PFA — Suivi des Candidats

## État du dépôt

Ce projet utilise une **architecture hybride** alignée sur votre cahier des charges :

| Service cible (spec) | Implémentation actuelle | Port | Eureka |
|---------------------|-------------------------|------|--------|
| auth-service | `backend/authentification` | 8081 | auth-service |
| candidate-service | `logique-metier` (package candidate) | 8083 | logique-metier-service |
| recruitment-service | `logique-metier` (package recruitment) | 8083 | logique-metier-service |
| interview-service | `logique-metier` (package interview) | 8083 | logique-metier-service |
| offer-service | `logique-metier` (package offer) | 8083 | logique-metier-service |
| report-service | `logique-metier` (package report) | 8083 | logique-metier-service |
| notification-service | `backend/notification-service` | 8085 | notification-service |
| chatbot-service | `backend/chatbotIA` | 8088 | chatbot-service |
| eureka-server | `backend/eureka-server` | 8761 | — |
| api-gateway | `backend/api-gateway` | 8080 | api-gateway |

Le module **logique-metier** regroupe le métier métier (équivalent à 5 microservices) le temps de la migration Feign complète.

## Démarrage local (sans Docker)

1. `docker compose up -d` (PostgreSQL 5433 + RabbitMQ)
2. Démarrer dans l'ordre :
   - Eureka → `8761`
   - auth-service → `8081`
   - logique-metier-service → `8083`
   - notification-service → `8085`
   - chatbot-service → `8088`
   - api-gateway → `8080`
3. Frontend : `cd frontend && npm start` (proxy → `http://localhost:8080`)

## API Gateway

- `POST /api/auth/login` et `/register` : publics
- Toutes les autres routes : JWT Bearer obligatoire
- Routage : voir `backend/api-gateway/src/main/resources/application.yml`

## RabbitMQ

- Exchange : `notification.exchange`
- Routing key : `notification.key`
- Queue : `notification.queue` (consommée par **notification-service** uniquement)

## Rôles frontend

- **CANDIDAT** : timeline lecture seule, messagerie, notifications, chatbot
- **RECRUTEUR** : pipeline Kanban (drag & drop), entretiens, offres
- **RESPONSABLE_DEPT** : `/dept-review` uniquement
- **RESPONSABLE_RH** : KPIs, admin, rapports PDF/CSV

## Prochaine étape (split microservices)

Extraire chaque package de `logique-metier` vers un JAR dédié (candidate-service, recruitment-service, …) avec Feign + bases dédiées si requis par le jury.
