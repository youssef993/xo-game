import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription, finalize } from 'rxjs';
import { MatchmakingApiService } from '../../core/matchmaking/matchmaking-api.service';
import { MatchFoundEvent } from '../../core/matchmaking/matchmaking.models';
import { MatchmakingWebSocketService } from '../../core/matchmaking/matchmaking-websocket.service';
import { PageHeaderComponent } from '../../shared/page-header.component';

@Component({
  selector: 'app-matchmaking',
  standalone: true,
  imports: [PageHeaderComponent],
  templateUrl: './matchmaking.component.html'
})
export class MatchmakingComponent
  implements OnInit, OnDestroy {

  readonly error = signal<string | null>(null);
  readonly cancelling = signal(false);

  private matchFoundSubscription?: Subscription;
  private searchSubscription?: Subscription;
  private cancelSubscription?: Subscription;
  private navigatingToGame = false;

  constructor(
    private readonly matchmakingApi: MatchmakingApiService,
    private readonly matchmakingWebSocket: MatchmakingWebSocketService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    void this.startMatchmaking();
  }

  ngOnDestroy(): void {
    this.matchFoundSubscription?.unsubscribe();
    this.searchSubscription?.unsubscribe();
    this.cancelSubscription?.unsubscribe();

    void this.matchmakingWebSocket.disconnect();
  }

  cancelSearch(): void {
    if (this.cancelling() || this.navigatingToGame) {
      return;
    }

    this.cancelling.set(true);
    this.error.set(null);

    this.cancelSubscription?.unsubscribe();
    this.cancelSubscription =
      this.matchmakingApi
        .cancel()
        .pipe(
          finalize(() => {
            this.cancelling.set(false);
          })
        )
        .subscribe({
          next: () => {
            void this.matchmakingWebSocket
              .disconnect();

            void this.router.navigate(['/home']);
          },

          error: error => {
            this.error.set(
              error?.error?.detail ??
              'Impossible d’annuler la recherche.'
            );
          }
        });
  }

  private async startMatchmaking(): Promise<void> {
    this.error.set(null);

    try {
      // L’abonnement doit être prêt avant POST /search.
      await this.matchmakingWebSocket.connect();

      this.matchFoundSubscription?.unsubscribe();
      this.matchFoundSubscription =
        this.matchmakingWebSocket
          .watchMatchFound()
          .subscribe({
            next: event => {
              this.handleMatchFound(event);
            },

            error: error => {
              console.error(
                'Erreur abonnement matchmaking',
                error
              );

              this.error.set(
                'La connexion temps réel au matchmaking a été interrompue.'
              );
            }
          });

      this.searchSubscription?.unsubscribe();
      this.searchSubscription =
        this.matchmakingApi
          .search()
          .subscribe({
            next: response => {
              // Un adversaire pouvait déjà être en attente.
              if (
                response.status === 'MATCHED' &&
                response.gameId
              ) {
                this.navigateToGame(response.gameId);
              }
            },

            error: error => {
              console.error(
                'Erreur recherche matchmaking',
                error
              );

              this.error.set(
                error?.error?.detail ??
                'Impossible de lancer le matchmaking.'
              );
            }
          });
    } catch (error) {
      console.error(
        'Connexion matchmaking impossible',
        error
      );

      this.error.set(
        'Impossible de se connecter au service de matchmaking.'
      );
    }
  }

  private handleMatchFound(
    event: MatchFoundEvent
  ): void {
    if (
      event.type !== 'MATCH_FOUND' ||
      !event.gameId
    ) {
      return;
    }

    this.navigateToGame(event.gameId);
  }

  private navigateToGame(gameId: string): void {
    // La réponse REST et le WebSocket peuvent arriver ensemble.
    if (this.navigatingToGame) {
      return;
    }

    this.navigatingToGame = true;

    void this.router.navigate([
      '/game',
      gameId
    ]);
  }
}
