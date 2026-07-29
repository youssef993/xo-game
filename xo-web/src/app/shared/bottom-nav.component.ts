import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-bottom-nav',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  template: `
    <nav class="bottom-nav">
      <a routerLink="/home" routerLinkActive="active">⌂<span>Accueil</span></a>
      <a routerLink="/history" routerLinkActive="active">◷<span>Historique</span></a>
      <a routerLink="/chat" routerLinkActive="active">✉<span>Chat</span></a>
      <a routerLink="/profile" routerLinkActive="active">◉<span>Profil</span></a>
    </nav>
  `
})
export class BottomNavComponent {}
