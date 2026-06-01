# Liste complète de tous les fichiers à créer

Cette liste détaille la structure des fichiers qui seront générés pour l'application de recrutement.

## 1. BACKEND - POM Parent & Configuration globale
- `backend/pom.xml` (POM parent pour gérer les versions des dépendances de tous les services)

## 2. EUREKA SERVER
- `backend/eureka-server/pom.xml`
- `backend/eureka-server/src/main/resources/application.yml`
- `backend/eureka-server/src/main/java/com/recrutement/eureka/EurekaServerApplication.java`

## 3. API GATEWAY
- `backend/api-gateway/pom.xml`
- `backend/api-gateway/src/main/resources/application.yml`
- `backend/api-gateway/src/main/java/com/recrutement/gateway/ApiGatewayApplication.java`
- `backend/api-gateway/src/main/java/com/recrutement/gateway/config/CorsConfig.java`
- `backend/api-gateway/src/main/java/com/recrutement/gateway/config/JwtAuthenticationFilter.java`

## 4. AUTH SERVICE
- `backend/auth-service/pom.xml`
- `backend/auth-service/src/main/resources/application.yml`
- `backend/auth-service/src/main/resources/data.sql`
- `backend/auth-service/src/main/java/com/recrutement/auth/AuthServiceApplication.java`
- `backend/auth-service/src/main/java/com/recrutement/auth/model/User.java`
- `backend/auth-service/src/main/java/com/recrutement/auth/model/Role.java`
- `backend/auth-service/src/main/java/com/recrutement/auth/model/UserRole.java`
- `backend/auth-service/src/main/java/com/recrutement/auth/repository/UserRepository.java`
- `backend/auth-service/src/main/java/com/recrutement/auth/dto/AuthRequest.java`
- `backend/auth-service/src/main/java/com/recrutement/auth/dto/AuthResponse.java`
- `backend/auth-service/src/main/java/com/recrutement/auth/dto/RegisterRequest.java`
- `backend/auth-service/src/main/java/com/recrutement/auth/dto/UserDto.java`
- `backend/auth-service/src/main/java/com/recrutement/auth/config/SecurityConfig.java`
- `backend/auth-service/src/main/java/com/recrutement/auth/config/JwtUtils.java`
- `backend/auth-service/src/main/java/com/recrutement/auth/service/AuthService.java`
- `backend/auth-service/src/main/java/com/recrutement/auth/controller/AuthController.java`
- `backend/auth-service/src/main/java/com/recrutement/auth/exception/GlobalExceptionHandler.java`

## 5. CANDIDATE SERVICE
- `backend/candidate-service/pom.xml`
- `backend/candidate-service/src/main/resources/application.yml`
- `backend/candidate-service/src/main/resources/data.sql`
- `backend/candidate-service/src/main/java/com/recrutement/candidate/CandidateServiceApplication.java`
- `backend/candidate-service/src/main/java/com/recrutement/candidate/model/Candidate.java`
- `backend/candidate-service/src/main/java/com/recrutement/candidate/model/Experience.java`
- `backend/candidate-service/src/main/java/com/recrutement/candidate/model/Skill.java`
- `backend/candidate-service/src/main/java/com/recrutement/candidate/repository/CandidateRepository.java`
- `backend/candidate-service/src/main/java/com/recrutement/candidate/dto/CandidateDto.java`
- `backend/candidate-service/src/main/java/com/recrutement/candidate/service/CandidateService.java`
- `backend/candidate-service/src/main/java/com/recrutement/candidate/controller/CandidateController.java`

## 6. RECRUITMENT SERVICE
- `backend/recruitment-service/pom.xml`
- `backend/recruitment-service/src/main/resources/application.yml`
- `backend/recruitment-service/src/main/resources/data.sql`
- `backend/recruitment-service/src/main/java/com/recrutement/recruitment/RecruitmentServiceApplication.java`
- `backend/recruitment-service/src/main/java/com/recrutement/recruitment/model/Job.java`
- `backend/recruitment-service/src/main/java/com/recrutement/recruitment/model/Application.java`
- `backend/recruitment-service/src/main/java/com/recrutement/recruitment/model/RecruitmentStep.java`
- `backend/recruitment-service/src/main/java/com/recrutement/recruitment/repository/JobRepository.java`
- `backend/recruitment-service/src/main/java/com/recrutement/recruitment/repository/ApplicationRepository.java`
- `backend/recruitment-service/src/main/java/com/recrutement/recruitment/dto/ApplicationDto.java`
- `backend/recruitment-service/src/main/java/com/recrutement/recruitment/dto/JobDto.java`
- `backend/recruitment-service/src/main/java/com/recrutement/recruitment/service/RecruitmentService.java`
- `backend/recruitment-service/src/main/java/com/recrutement/recruitment/controller/RecruitmentController.java`
- `backend/recruitment-service/src/main/java/com/recrutement/recruitment/client/CandidateClient.java`

## 7. INTERVIEW SERVICE
- `backend/interview-service/pom.xml`
- `backend/interview-service/src/main/resources/application.yml`
- `backend/interview-service/src/main/resources/data.sql`
- `backend/interview-service/src/main/java/com/recrutement/interview/InterviewServiceApplication.java`
- `backend/interview-service/src/main/java/com/recrutement/interview/model/Interview.java`
- `backend/interview-service/src/main/java/com/recrutement/interview/model/Evaluation.java`
- `backend/interview-service/src/main/java/com/recrutement/interview/repository/InterviewRepository.java`
- `backend/interview-service/src/main/java/com/recrutement/interview/service/InterviewService.java`
- `backend/interview-service/src/main/java/com/recrutement/interview/controller/InterviewController.java`

## 8. NOTIFICATION SERVICE
- `backend/notification-service/pom.xml`
- `backend/notification-service/src/main/resources/application.yml`
- `backend/notification-service/src/main/java/com/recrutement/notification/NotificationServiceApplication.java`
- `backend/notification-service/src/main/java/com/recrutement/notification/config/RabbitMqConfig.java`
- `backend/notification-service/src/main/java/com/recrutement/notification/dto/NotificationEvent.java`
- `backend/notification-service/src/main/java/com/recrutement/notification/service/NotificationConsumer.java`
- `backend/notification-service/src/main/java/com/recrutement/notification/service/EmailService.java`

## 9. OFFER SERVICE
- `backend/offer-service/pom.xml`
- `backend/offer-service/src/main/resources/application.yml`
- `backend/offer-service/src/main/java/com/recrutement/offer/OfferServiceApplication.java`
- `backend/offer-service/src/main/java/com/recrutement/offer/model/Offer.java`
- `backend/offer-service/src/main/java/com/recrutement/offer/repository/OfferRepository.java`
- `backend/offer-service/src/main/java/com/recrutement/offer/service/OfferService.java`
- `backend/offer-service/src/main/java/com/recrutement/offer/controller/OfferController.java`

## 10. REPORT SERVICE
- `backend/report-service/pom.xml`
- `backend/report-service/src/main/resources/application.yml`
- `backend/report-service/src/main/java/com/recrutement/report/ReportServiceApplication.java`
- `backend/report-service/src/main/java/com/recrutement/report/service/ReportService.java`
- `backend/report-service/src/main/java/com/recrutement/report/controller/ReportController.java`

## 11. CHATBOT SERVICE
- `backend/chatbot-service/pom.xml`
- `backend/chatbot-service/src/main/resources/application.yml`
- `backend/chatbot-service/src/main/java/com/recrutement/chatbot/ChatbotServiceApplication.java`
- `backend/chatbot-service/src/main/java/com/recrutement/chatbot/dto/ChatMessage.java`
- `backend/chatbot-service/src/main/java/com/recrutement/chatbot/dto/ChatResponse.java`
- `backend/chatbot-service/src/main/java/com/recrutement/chatbot/service/ChatbotService.java`
- `backend/chatbot-service/src/main/java/com/recrutement/chatbot/controller/ChatbotController.java`
- `backend/chatbot-service/src/main/java/com/recrutement/chatbot/client/ApplicationClient.java`
- `backend/chatbot-service/src/main/java/com/recrutement/chatbot/client/InterviewClient.java`

## 12. FRONTEND ANGULAR (Standalone Architecture)
- `frontend/package.json`
- `frontend/angular.json`
- `frontend/tailwind.config.js`
- `frontend/src/index.html`
- `frontend/src/main.ts`
- `frontend/src/styles.css`
- `frontend/src/app/app.config.ts`
- `frontend/src/app/app.routes.ts`
- `frontend/src/app/app.component.ts`
- `frontend/src/app/core/interceptors/jwt.interceptor.ts`
- `frontend/src/app/core/guards/auth.guard.ts`
- `frontend/src/app/core/guards/role.guard.ts`
- `frontend/src/app/core/services/auth.service.ts`
- `frontend/src/app/core/services/api.service.ts`
- `frontend/src/app/core/models/user.model.ts`
- `frontend/src/app/shared/components/navbar/navbar.component.ts`
- `frontend/src/app/shared/components/sidebar/sidebar.component.ts`
- `frontend/src/app/modules/auth/login/login.component.ts`
- `frontend/src/app/modules/auth/register/register.component.ts`
- `frontend/src/app/modules/dashboard/dashboard.component.ts`
- `frontend/src/app/modules/candidat/candidat-list/candidat-list.component.ts`
- `frontend/src/app/modules/candidat/candidat-detail/candidat-detail.component.ts`
- `frontend/src/app/modules/candidat/candidat-form/candidat-form.component.ts`
- `frontend/src/app/modules/recruitment/kanban/kanban.component.ts`
- `frontend/src/app/modules/recruitment/job-list/job-list.component.ts`
- `frontend/src/app/modules/interview/interview-calendar/interview-calendar.component.ts`
- `frontend/src/app/modules/offer/offer-list/offer-list.component.ts`
- `frontend/src/app/modules/chatbot/chatbot-bubble/chatbot-bubble.component.ts`
- `frontend/src/app/modules/report/report-dashboard/report-dashboard.component.ts`

## 13. DOCKER & DEVOPS
- `backend/docker-compose.yml`
- `backend/prometheus.yml`
- `backend/nginx.conf`
- `Jenkinsfile`
- `k8s/deployment.yaml`
- `k8s/service.yaml`
- `k8s/configmap.yaml`
- `k8s/ingress.yaml`
- Dockerfiles pour chaque dossier backend et frontend.
