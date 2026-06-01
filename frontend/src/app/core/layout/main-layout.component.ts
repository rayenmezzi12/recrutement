import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { RoleService } from '../services/role.service';
import { NAV_ITEMS, NavItem } from '../navigation/nav-items';
import { ChatbotWidgetComponent } from '../components/chatbot-widget/chatbot-widget.component';
import { NotificationApiService } from '../services/notification-api.service';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { SidebarComponent } from '../../shared/components/sidebar/sidebar.component';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, RouterOutlet, ChatbotWidgetComponent, NavbarComponent, SidebarComponent],
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.css']
})
export class MainLayoutComponent implements OnInit {
  username = '';
  roleLabel = '';
  navItems: NavItem[] = [];
  unreadNotifications = 0;

  constructor(
    private auth: AuthService,
    private roleService: RoleService,
    private router: Router,
    private notificationApi: NotificationApiService
  ) {}

  ngOnInit(): void {
    this.username = this.auth.getUsername() ?? '';
    this.roleLabel = this.roleService.primaryRoleLabel();
    this.navItems = NAV_ITEMS.filter((item) =>
      this.roleService.hasAnyRole(item.roles)
    );
    if (this.username) {
      this.notificationApi.getUnreadCount(this.username).subscribe({
        next: (r) => (this.unreadNotifications = r?.count ?? 0),
        error: () => (this.unreadNotifications = 0)
      });
    }
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
