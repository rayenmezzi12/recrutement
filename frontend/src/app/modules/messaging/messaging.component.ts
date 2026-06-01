import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MessageApiService, ChatMessage } from '../../core/services/message-api.service';
import { CandidateService } from '../../core/services/candidate.service';
import { AuthService } from '../../core/services/auth.service';
import { RoleService } from '../../core/services/role.service';
import { Candidate } from '../../core/models/candidate.model';

interface Contact extends Candidate {
  unread: number;
}

@Component({
  selector: 'app-messaging',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './messaging.component.html',
  styleUrls: ['./messaging.component.css']
})
export class MessagingComponent implements OnInit {
  contacts: Contact[] = [];
  selectedContact: Contact | null = null;
  messages: ChatMessage[] = [];
  newMessage = '';
  searchTerm = '';
  loading = true;
  myUsername = '';

  constructor(
    private messageApi: MessageApiService,
    private candidateService: CandidateService,
    private auth: AuthService,
    public roleService: RoleService
  ) {}

  ngOnInit() {
    this.myUsername = this.auth.getUsername() ?? '';
    if (this.roleService.isStaff()) {
      this.candidateService.getAll().subscribe({
        next: (list) => {
          this.contacts = list.map((c) => ({ ...c, unread: 0 }));
          if (this.contacts.length) this.selectContact(this.contacts[0]);
          this.loading = false;
        },
        error: () => (this.loading = false)
      });
    } else {
      this.contacts = [{ id: 0, firstName: 'Recruteur', lastName: 'Principal', email: 'recruteur@recrutement.com', unread: 0 }];
      this.selectContact(this.contacts[0]);
      this.loading = false;
    }
  }

  get filteredContacts(): Contact[] {
    const term = this.searchTerm.toLowerCase();
    if (!term) return this.contacts;
    return this.contacts.filter((c) =>
      `${c.firstName} ${c.lastName}`.toLowerCase().includes(term)
    );
  }

  peerUsername(c: Contact): string {
    return c.username ?? 'recruteur';
  }

  selectContact(contact: Contact) {
    this.selectedContact = contact;
    const peer = this.roleService.isStaff() ? this.peerUsername(contact) : 'recruteur';
    this.messageApi.getConversation(this.myUsername, peer).subscribe({
      next: (msgs) => (this.messages = msgs),
      error: () => (this.messages = [])
    });
  }

  sendMessage() {
    if (!this.newMessage.trim() || !this.selectedContact) return;
    const peer = this.roleService.isStaff() ? this.peerUsername(this.selectedContact) : 'recruteur';
    this.messageApi.send(this.myUsername, peer, this.newMessage.trim()).subscribe({
      next: (msg) => {
        this.messages = [...this.messages, msg];
        this.newMessage = '';
      }
    });
  }

  getContactName(c: Contact): string {
    return `${c.firstName} ${c.lastName}`;
  }

  isSent(msg: ChatMessage): boolean {
    return msg.senderUsername === this.myUsername;
  }
}
