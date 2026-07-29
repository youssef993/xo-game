import { Component } from '@angular/core';
import { PageHeaderComponent } from '../../shared/page-header.component';
import { BottomNavComponent } from '../../shared/bottom-nav.component';

interface MatchHistory {
  opponent: string;
  result: 'Victoire' | 'Défaite' | 'Nul';
  score: string;
  date: string;
}

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [PageHeaderComponent, BottomNavComponent],
  templateUrl: './history.component.html',
})
export class HistoryComponent {
  readonly matches: MatchHistory[] = [
    { opponent: 'Sarah_23', result: 'Victoire', score: '3 - 1', date: 'Il y a 2 h' },
    { opponent: 'Thomas_44', result: 'Défaite', score: '1 - 3', date: 'Il y a 5 h' },
    { opponent: 'Emma_88', result: 'Victoire', score: '3 - 0', date: 'Hier' },
    { opponent: 'Lucas_23', result: 'Nul', score: '2 - 2', date: 'Il y a 2 j' }
  ];
}
