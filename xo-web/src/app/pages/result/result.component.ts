import { Component, computed, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

type ResultStatus = 'win' | 'loss' | 'draw';

@Component({
  selector: 'app-result',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './result.component.html'
})
export class ResultComponent {
  private readonly route = inject(ActivatedRoute);

  readonly status = computed<ResultStatus>(() => {
    const value = this.route.snapshot.paramMap.get('status');
    return value === 'loss' || value === 'draw' ? value : 'win';
  });

  readonly title = computed(() =>
    this.status() === 'win' ? 'Victoire !' :
    this.status() === 'loss' ? 'Défaite' : 'Match nul'
  );

  readonly subtitle = computed(() =>
    this.status() === 'win' ? 'Félicitations, tu as gagné !' :
    this.status() === 'loss' ? 'Bonne chance pour la prochaine partie.' :
    'Une partie très équilibrée.'
  );

  readonly points = computed(() =>
    this.status() === 'win' ? '+25 points' :
    this.status() === 'loss' ? '−15 points' : '+5 points'
  );

  readonly icon = computed(() =>
    this.status() === 'win' ? '🏆' :
    this.status() === 'loss' ? '💪' : '🤝'
  );

  readonly confettiPieces = Array.from({ length: 22 }, (_, index) => index + 1);
}
