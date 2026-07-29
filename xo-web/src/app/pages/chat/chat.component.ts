import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PageHeaderComponent } from '../../shared/page-header.component';
import { BottomNavComponent } from '../../shared/bottom-nav.component';

interface Message {
  author: 'me' | 'other';
  text: string;
  time: string;
}

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [FormsModule, PageHeaderComponent, BottomNavComponent],
  templateUrl: './chat.component.html',
})
export class ChatComponent {
  draft = '';

  readonly messages = signal<Message[]>([
    { author: 'other', text: 'Bonne chance ! 🙂', time: '14:32' },
    { author: 'me', text: 'Merci, à toi aussi !', time: '14:32' },
    { author: 'other', text: 'Beau coup !', time: '14:34' }
  ]);

  send(): void {
    const text = this.draft.trim();
    if (!text) return;

    this.messages.update(messages => [
      ...messages,
      { author: 'me', text, time: new Date().toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' }) }
    ]);
    this.draft = '';
  }
}
