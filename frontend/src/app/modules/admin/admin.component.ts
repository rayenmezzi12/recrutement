import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { UserDto } from '../../core/models/auth.model';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {
  currentUser: UserDto | null = null;
  registeredUsers: UserDto[] = [];
  loading = true;
  error = '';
  success = '';
  showForm = false;

  form = {
    username: '',
    email: '',
    password: '',
    fullName: '',
    role: 'RECRUTEUR'
  };

  readonly roleOptions = ['CANDIDAT', 'RECRUTEUR', 'RESPONSABLE_RH', 'RESPONSABLE_DEPT'];

  constructor(private authService: AuthService) {}

  ngOnInit() {
    this.loadCurrentUser();
  }

  loadCurrentUser() {
    const username = this.authService.getUsername();
    if (!username) {
      this.loading = false;
      return;
    }
    this.authService.getUser(username).subscribe({
      next: (user) => {
        this.currentUser = user;
        this.registeredUsers = [user, ...this.registeredUsers.filter((u) => u.id !== user.id)];
        this.loading = false;
      },
      error: () => {
        this.error = 'Impossible de charger le profil utilisateur';
        this.loading = false;
      }
    });
  }

  openCreateForm() {
    this.form = { username: '', email: '', password: '', fullName: '', role: 'RECRUTEUR' };
    this.showForm = true;
    this.success = '';
  }

  closeForm() {
    this.showForm = false;
  }

  createUser() {
    if (!this.form.username || !this.form.email || !this.form.password || !this.form.fullName) {
      this.error = 'Tous les champs sont obligatoires';
      return;
    }
    this.authService.register({
      username: this.form.username,
      email: this.form.email,
      password: this.form.password,
      fullName: this.form.fullName,
      roles: [this.form.role]
    }).subscribe({
      next: (user) => {
        this.registeredUsers = [...this.registeredUsers, user];
        this.success = `Utilisateur ${user.username} créé avec succès`;
        this.closeForm();
        this.error = '';
      },
      error: () => {
        this.error = 'Erreur lors de la création de l\'utilisateur';
      }
    });
  }

  getRoleLabel(roles?: string[]): string {
    return roles?.join(', ') ?? '—';
  }
}
