import { Component, OnDestroy, OnInit, computed, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Subscription, catchError, finalize, of } from 'rxjs';
import { AuthService } from '../../core/auth/auth.service';
import { GameApiService } from '../../core/game/game-api.service';
import { ApiProblem, GameResponse, PlayerSymbol } from '../../core/game/game.models';
import { GameUpdatedEvent, GameWebSocketService } from '../../core/game-websocket.service';
import { PlayerWebSocketService } from '../../core/player-websocket.service';
import { PageHeaderComponent } from '../../shared/page-header.component';

@Component({
  selector: 'app-game',
  standalone: true,
  imports: [
    RouterLink,
    PageHeaderComponent
  ],
  templateUrl: './game.component.html'
})
export class GameComponent implements OnInit, OnDestroy {

  readonly game = signal<GameResponse | null>(null);
  readonly loading = signal(true);
  readonly actionPending = signal(false);
  readonly error = signal<string | null>(null);

  readonly webSocketConnected = computed(
    () => {
      this.gameWebSocket.connected();
      this.playerWebSocket.connected();
      }
  );

  readonly board = computed(
    () =>
      this.game()?.board ??
      Array<PlayerSymbol | null>(9).fill(null)
  );

  readonly finished = computed(() => {
    const status = this.game()?.status;

    return status
      ? [
          'X_WON',
          'O_WON',
          'DRAW',
          'ABANDONED'
        ].includes(status)
      : false;
  });

  readonly waiting = computed(
    () =>
      this.game()?.status ===
      'WAITING_FOR_PLAYER'
  );

  readonly currentPlayer = computed(
    () => this.game()?.currentTurn ?? '-'
  );

  readonly mySymbol =
    computed<PlayerSymbol | null>(() => {
      const game = this.game();
      const userId = this.auth.user()?.keycloakId;

      if (!game || !userId) {
        return null;
      }

      if (game.playerXId === userId) {
        return 'X';
      }

      if (game.playerOId === userId) {
        return 'O';
      }

      return null;
    });

  readonly isMyTurn = computed(
    () =>
      this.game()?.status === 'IN_PROGRESS' &&
      this.game()?.currentTurn ===
        this.mySymbol()
  );

  private gameId = '';

  private gameWebSocketSubscription?: Subscription;
  private playerWebSocketSubscription?: Subscription;

  private initialGameSubscription?:Subscription;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly gameApi: GameApiService,
    private readonly gameWebSocket: GameWebSocketService,
    private readonly playerWebSocket: PlayerWebSocketService,
    readonly auth: AuthService
  ) {}

  ngOnInit(): void {
    this.gameId =
      this.route.snapshot.paramMap.get(
        'gameId'
      ) ?? '';

    if (!this.gameId) {
      void this.router.navigate(['/home']);
      return;
    }

    this.loadInitialGame();
  }

  ngOnDestroy(): void {
    this.initialGameSubscription?.unsubscribe();

    this.gameWebSocketSubscription?.unsubscribe();
    //this.playerWebSocketSubscription?.unsubscribe();

    void this.gameWebSocket.disconnect();
    //void this.playerWebSocket.disconnect();
  }

  play(index: number): void {
    if (
      !this.isMyTurn() ||
      this.board()[index] ||
      this.actionPending()
    ) {
      return;
    }

    this.actionPending.set(true);
    this.error.set(null);

    this.gameApi
      .playMove(this.gameId, index)
      .pipe(
        finalize(() => {
          this.actionPending.set(false);
        })
      )
      .subscribe({
        next: game => {
          /*
           * On met à jour immédiatement l'écran.
           * Le WebSocket recevra également l'événement
           * MOVE_PLAYED pour synchroniser les autres joueurs.
           */
          this.updateGame(game);
        },

        error: error => {
          this.error.set(
            this.problemMessage(error?.error)
          );
        }
      });
  }

  abandon(): void {
    if (
      this.finished() ||
      this.actionPending()
    ) {
      return;
    }

    this.actionPending.set(true);
    this.error.set(null);

    this.gameApi
      .abandon(this.gameId)
      .pipe(
        finalize(() => {
          this.actionPending.set(false);
        })
      )
      .subscribe({
        next: game => {
          this.updateGame(game);
        },

        error: error => {
          this.error.set(
            this.problemMessage(error?.error)
          );
        }
      });
  }

  copyGameId(): void {
    void navigator.clipboard.writeText(
      this.gameId
    );
  }

  private loadInitialGame(): void {
    this.loading.set(true);
    this.error.set(null);

    this.initialGameSubscription =
      this.gameApi
        .getGame(this.gameId)
        .pipe(
          catchError(error => {
            this.error.set(
              this.problemMessage(error?.error)
            );

            return of(null);
          }),

          finalize(() => {
            this.loading.set(false);
          })
        )
        .subscribe(game => {
          if (!game) {
            return;
          }

          this.updateGame(game);

          if (!this.finished()) {
            void this.connectWebSocket();
          }
        });
  }

  private async connectWebSocket():
    Promise<void> {
    try {
      await this.gameWebSocket.connect();
      await this.playerWebSocket.connect();

      this.gameWebSocketSubscription?.unsubscribe();
      this.playerWebSocketSubscription?.unsubscribe();

      this.gameWebSocketSubscription =
        this.gameWebSocket.watchGame(this.gameId).subscribe({
            next: event => {
              this.handleGameEvent(event);
            }, error: error => {
              console.error(
                'Erreur abonnement WebSocket',
                error
              );
              this.error.set(
                'La connexion temps réel a été interrompue.'
              );
            }
          });
      this.playerWebSocketSubscription =
        this.playerWebSocket.watchScore().subscribe({
            next: event => {
              console.log(event)
            }, error: error => {
              console.error(
                'Erreur abonnement WebSocket',
                error
              );
              this.error.set(
                'La connexion temps réel a été interrompue.'
              );
            }
        });
    } catch (error) {
      console.error(
        'Connexion WebSocket impossible',
        error
      );

      this.error.set(
        'Impossible d’activer la synchronisation en temps réel.'
      );
    }
  }

  private handleGameEvent(
    event: GameUpdatedEvent
  ): void {
    if (event.gameId !== this.gameId) {
      return;
    }

    console.log(
      'Événement de partie reçu :',
      event.type
    );

    switch (event.type) {
      case 'GAME_CREATED':
      case 'PLAYER_JOINED':
      case 'MOVE_PLAYED':
      case 'GAME_FINISHED':
      case 'GAME_ABANDONED':
        this.updateGame(event.game);
        break;
    }
  }

  private updateGame(game: GameResponse): void {
    this.game.set(game);
    console.log(this.game()?.currentTurn)
    if (this.isFinishedGame(game)) {
      this.navigateToResult(game);
    }
  }

  private isFinishedGame(
    game: GameResponse
  ): boolean {
    return [
      'X_WON',
      'O_WON',
      'DRAW',
      'ABANDONED'
    ].includes(game.status);
  }

  private navigateToResult(
    game: GameResponse
  ): void {
    const userId = this.auth.user()?.keycloakId;

    let result:
      | 'win'
      | 'loss'
      | 'draw' = 'draw';

    if (game.status !== 'DRAW') {
      result =
        game.winnerId &&
        game.winnerId === userId
          ? 'win'
          : 'loss';
    }

    void this.router.navigate(
      ['/result', result],
      {
        queryParams: {
          gameId: game.id
        }
      }
    );
  }

  private problemMessage(
    problem?: ApiProblem
  ): string {
    return (
      problem?.detail ??
      'Une erreur est survenue pendant la partie.'
    );
  }
}
