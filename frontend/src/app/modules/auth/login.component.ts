import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { ROLES } from '../../core/constants/roles';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username = '';
  password = '';
  errorMessage = '';
  loading = false;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    if (!this.username?.trim() || !this.password) {
      this.errorMessage = 'Veuillez saisir identifiant et mot de passe';
      return;
    }
    this.loading = true;
    this.errorMessage = '';
    this.authService
      .login({ username: this.username.trim(), password: this.password })
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: () => this.router.navigateByUrl(this.dashboardForRole()),
        error: (err) => {
          this.errorMessage =
            err.error?.error ?? 'Identifiants invalides. Vérifiez votre identifiant et mot de passe.';
        }
      });
  }

  private dashboardForRole(): string {
    const roles = this.authService.getRoles();
    if (roles.includes(ROLES.RESPONSABLE_RH)) return '/dashboard';
    if (roles.includes(ROLES.RESPONSABLE_DEPT)) return '/dashboard';
    if (roles.includes(ROLES.RECRUTEUR)) return '/dashboard';
    return '/dashboard';
  }
}
