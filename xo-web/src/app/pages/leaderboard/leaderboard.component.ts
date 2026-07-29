import { Component } from '@angular/core';
import { PageHeaderComponent } from '../../shared/page-header.component';
import { BottomNavComponent } from '../../shared/bottom-nav.component';

@Component({
  selector: 'app-leaderboard',
  standalone: true,
  imports: [PageHeaderComponent, BottomNavComponent],
  templateUrl: './leaderboard.component.html',
})
export class LeaderboardComponent {
  readonly players = [
    { rank: 4, name: 'Emma_88', points: 2100 },
    { rank: 5, name: 'Lucas_23', points: 2050 },
    { rank: 6, name: 'Julie_15', points: 1980 }
  ];
}
