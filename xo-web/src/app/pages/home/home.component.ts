import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { BottomNavComponent } from '../../shared/bottom-nav.component';
import { AuthService } from '../../core/auth/auth.service';

interface MenuItem {
  title: string;
  subtitle: string;
  icon: string;
  route: string;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, BottomNavComponent],
  templateUrl: './home.component.html',
})
export class HomeComponent {
  constructor(readonly auth: AuthService) {}
  readonly menuItems: MenuItem[] = [
    { title: 'Jouer maintenant', subtitle: 'Trouver un adversaire aléatoire', icon: '▶', route: '/matchmaking' },
    { title: 'Liste des amis', subtitle: 'Créer une partie privée', icon: '♟', route: '/amis' },
    { title: 'Classement', subtitle: 'Découvrir les meilleurs joueurs', icon: '🏆', route: '/leaderboard' }
  ];
}
