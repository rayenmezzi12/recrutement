import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { ROLES } from '../../core/constants/roles';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./login.component.css']
})
export class RegisterComponent {
  form = {
    username: '',
    email: '',
    password: '',
    fullName: '',
    role: ROLES.CANDIDAT
  };

  errorMessage = '';
  successMessage = '';
  loading = false;

  constructor(private auth: AuthService, private router: Router) {}

  onSubmit() {
    if (!this.form.username?.trim() || !this.form.email || !this.form.password || !this.form.fullName) {
      this.errorMessage = 'Tous les champs sont obligatoires';
      return;
    }
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.auth
      .register({
        username: this.form.username.trim(),
        email: this.form.email.trim(),
        password: this.form.password,
        fullName: this.form.fullName.trim(),
        roles: [this.form.role]
      })
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: () => {
          this.successMessage = 'Compte créé. Vous pouvez vous connecter.';
          setTimeout(() => this.router.navigate(['/login']), 1500);
        },
        error: (err) => {
          this.errorMessage = err.error?.error ?? 'Impossible de créer le compte';
        }
      });
  }
}
