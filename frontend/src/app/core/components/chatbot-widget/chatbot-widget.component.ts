import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatbotService, ChatMessage } from '../../../modules/chatbot/chatbot.service';

@Component({
  selector: 'app-chatbot-widget',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chatbot-widget.component.html',
  styleUrls: ['./chatbot-widget.component.css']
})
export class ChatbotWidgetComponent {
  open = false;
  input = '';
  loading = false;
  messages: ChatMessage[] = [
    {
      text: 'Bonjour ! Posez-moi une question sur votre candidature, vos entretiens ou le processus.',
      sender: 'bot',
      timestamp: new Date()
    }
  ];

  readonly quickPrompts = [
    'Quel est le statut de ma candidature ?',
    'Quand est mon prochain entretien ?',
    'Comment soumettre mon CV ?',
    'Quelles sont les étapes du recrutement ?'
  ];

  constructor(private chatbot: ChatbotService) {}

  toggle(): void {
    this.open = !this.open;
  }

  send(): void {
    if (!this.input.trim() || this.loading) return;
    const question = this.input.trim();
    this.messages.push({ text: question, sender: 'user', timestamp: new Date() });
    this.input = '';
    this.loading = true;
    this.chatbot.askQuestion(question).subscribe({
      next: (res) => {
        this.messages.push({ text: res.response, sender: 'bot', timestamp: new Date() });
        this.loading = false;
      },
      error: () => {
        this.messages.push({
          text: 'Service indisponible. Vérifiez que chatbotIA (8084) est démarré.',
          sender: 'bot',
          timestamp: new Date()
        });
        this.loading = false;
      }
    });
  }

  usePrompt(text: string): void {
    this.input = text;
    this.send();
  }
}
