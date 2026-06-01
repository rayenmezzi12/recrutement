import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationApiService, AppNotification } from '../../core/services/notification-api.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.css']
})
export class NotificationComponent implements OnInit {
  notifications: AppNotification[] = [];
  loading = true;

  constructor(private api: NotificationApiService, private auth: AuthService) {}

  ngOnInit() {
    const username = this.auth.getUsername();
    if (!username) {
      this.loading = false;
      return;
    }
    this.api.getForUser(username).subscribe({
      next: (n) => {
        this.notifications = n;
        this.loading = false;
      },
      error: () => {
        this.notifications = [];
        this.loading = false;
      }
    });
  }

  markAllRead() {
    const username = this.auth.getUsername();
    if (!username) return;
    this.api.markAllRead(username).subscribe({
      next: () => {
        this.notifications = this.notifications.map((n) => ({ ...n, isRead: true }));
      }
    });
  }
}
