import { Component, ElementRef, ViewChild, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatbotService, ChatMessage } from './chatbot.service';

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chatbot.component.html',
  styleUrls: ['./chatbot.component.css']
})
export class ChatbotComponent implements AfterViewChecked {
  @ViewChild('scrollMe') private myScrollContainer!: ElementRef;

  messages: ChatMessage[] = [];
  userInput = '';
  isLoading = false;
  errorHint = '';
  showSuggestions = true;

  suggestedQuestions: { icon: string; text: string }[] = [
    { icon: '📝', text: 'Comment optimiser mon CV ?' },
    { icon: '🎯', text: 'Quelles sont les compétences les plus recherchées ?' },
    { icon: '🎤', text: 'Comment me préparer pour un entretien ?' },
    { icon: '📊', text: 'Quel est le processus de recrutement ?' },
    { icon: '💼', text: 'Comment suivre ma candidature ?' },
    { icon: '📧', text: 'Comment contacter le service RH ?' }
  ];

  constructor(private chatbotService: ChatbotService) {
    this.messages.push({
      text: "Bonjour ! Je suis votre assistant carrière IA. Comment puis-je vous aider aujourd'hui ?",
      sender: 'bot',
      timestamp: new Date()
    });
  }

  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  scrollToBottom(): void {
    try {
      const el = this.myScrollContainer?.nativeElement;
      if (el) {
        el.scrollTop = el.scrollHeight;
      }
    } catch {
      /* ignore scroll errors */
    }
  }

  sendSuggestedQuestion(question: string): void {
    this.userInput = question;
    this.showSuggestions = false;
    this.sendMessage();
  }

  sendMessage(): void {
    if (!this.userInput.trim() || this.isLoading) return;

    const userMsg = this.userInput.trim();
    this.messages.push({
      text: userMsg,
      sender: 'user',
      timestamp: new Date()
    });
    this.userInput = '';
    this.isLoading = true;
    this.errorHint = '';
    this.showSuggestions = false;

    this.chatbotService.askQuestion(userMsg).subscribe({
      next: (res) => {
        this.isLoading = false;
        const text = res?.response?.trim();
        this.messages.push({
          text: text || 'Réponse vide du serveur.',
          sender: 'bot',
          timestamp: new Date()
        });
      },
      error: (err) => {
        this.isLoading = false;
        const serverMsg =
          typeof err.error === 'string'
            ? err.error
            : err.error?.message ?? err.message;
        this.errorHint = serverMsg ? `Détail : ${serverMsg}` : '';
        this.messages.push({
          text: "Désolé, une erreur s'est produite lors de la connexion à l'assistant.",
          sender: 'bot',
          timestamp: new Date()
        });
      }
    });
  }
}
