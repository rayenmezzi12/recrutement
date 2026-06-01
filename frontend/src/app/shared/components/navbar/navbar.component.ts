import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { RoleService } from '../../../core/services/role.service';
import { NotificationApiService, AppNotification } from '../../../core/services/notification-api.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  userMenuOpen = false;
  notificationsOpen = false;
  username = '';
  userRole = '';
  notifications: AppNotification[] = [];
  unreadCount = 0;

  constructor(
    private router: Router, 
    private authService: AuthService,
    private roleService: RoleService,
    private notificationService: NotificationApiService
  ) {}

  ngOnInit(): void {
    this.username = this.authService.getUsername() ?? 'Utilisateur';
    this.userRole = this.roleService.primaryRoleLabel();
    this.loadNotifications();
  }

  loadNotifications() {
    if (this.username && this.username !== 'Utilisateur') {
      this.notificationService.getForUser(this.username).subscribe({
        next: (n) => {
          this.notifications = n.slice(0, 5);
          this.unreadCount = n.filter(x => !x.isRead).length;
        }
      });
    }
  }

  toggleUserMenu() {
    this.userMenuOpen = !this.userMenuOpen;
    if (this.notificationsOpen) this.notificationsOpen = false;
  }

  toggleNotifications() {
    this.notificationsOpen = !this.notificationsOpen;
    if (this.userMenuOpen) this.userMenuOpen = false;
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
