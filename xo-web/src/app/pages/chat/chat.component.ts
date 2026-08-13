import {Component, OnInit, signal} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PageHeaderComponent } from '../../shared/page-header.component';
import { BottomNavComponent } from '../../shared/bottom-nav.component';
import {ActivatedRoute} from '@angular/router';
import {PlayerResponse} from '../../core/player/player.model';
import {PlayerApiService} from '../../core/player/player-api.service';
import {Location} from '@angular/common';
import {ChatApiService} from '../../core/chat/chat-api.service';
import {MessageResponse} from '../../core/chat/chat.model';

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
export class ChatComponent implements OnInit {

  userFriend= signal<PlayerResponse | null>(null)
  messages = signal<MessageResponse[]>([]);

  constructor(private activatedRoute: ActivatedRoute,
              private playerService: PlayerApiService,
              private chatService: ChatApiService,
              private readonly location: Location) {
  }

  ngOnInit(): void {
    const idDest = this.activatedRoute.snapshot.paramMap.get('userId')
    idDest === null ? this.location.back() :
    this.playerService.findPlayersByAuthId(idDest).subscribe(
      res => {
        this.userFriend.set(res);
        this.chatService.getOrCreateConversation(idDest).subscribe(
          conversation=>{
            this.chatService.getMessages(conversation.id).subscribe(
              msgs =>{
                this.messages.set(msgs);
              }
            )
          }
        )
      }
    )
  }
  draft = '';



  send(): void {
    const text = this.draft.trim();
    if (!text) return;

    this.messages.update(messages => [
      ...messages,
      //{ author: 'me', text, time: new Date().toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' }) }
    ]);
    this.draft = '';
  }
}
