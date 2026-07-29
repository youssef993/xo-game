import {
  DestroyRef,
  Injectable,
  inject,
  signal
} from '@angular/core';
import {
  Client,
  Frame,
  IMessage,
  StompSubscription
} from '@stomp/stompjs';
import { Observable, Subject } from 'rxjs';

import { AuthService } from './auth/auth.service';
import { GameResponse } from './game/game.models';

export interface GameUpdatedEvent {
  type:
    | 'GAME_CREATED'
    | 'PLAYER_JOINED'
    | 'MOVE_PLAYED'
    | 'GAME_FINISHED'
    | 'GAME_ABANDONED';

  gameId: string;
  game: GameResponse;
  occurredAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class GameWebSocketService {
  private readonly authService = inject(AuthService);
  private readonly destroyRef = inject(DestroyRef);

  private client?: Client;
  private connectionPromise?: Promise<void>;
  private gameSubscription?: StompSubscription;
  private watchedGameId?: string;
  private readonly gameEvents = new Subject<GameUpdatedEvent>();

  readonly connected = signal(false);

  constructor() {
    this.destroyRef.onDestroy(() => {
      void this.disconnect();
    });
  }

  connect(): Promise<void> {
    if (this.client?.connected) {
      return Promise.resolve();
    }

    if (this.connectionPromise) {
      return this.connectionPromise;
    }

    this.connectionPromise = this.createAndActivateClient()
      .finally(() => {
        this.connectionPromise = undefined;
      });

    return this.connectionPromise;
  }

  watchGame(gameId: string): Observable<GameUpdatedEvent> {
    if (!this.client?.connected) {
      throw new Error('WebSocket non connecté');
    }

    this.watchedGameId = gameId;
    this.subscribeToCurrentGame();

    return this.gameEvents.asObservable();
  }

  async disconnect(): Promise<void> {
    this.watchedGameId = undefined;
    this.unsubscribeFromGame();

    const client = this.client;
    this.client = undefined;
    this.connectionPromise = undefined;
    this.connected.set(false);

    if (client?.active) {
      await client.deactivate();
    }
  }

  private async createAndActivateClient(): Promise<void> {
    const token = await this.authService.getValidToken();

    if (!token) {
      throw new Error(
        'Impossible de connecter le WebSocket sans JWT'
      );
    }

    return new Promise<void>((resolve, reject) => {
      let settled = false;

      const resolveOnce = (): void => {
        if (!settled) {
          settled = true;
          resolve();
        }
      };

      const rejectOnce = (error: unknown): void => {
        if (!settled) {
          settled = true;
          reject(error);
        }
      };

      const client = new Client({
        brokerURL: 'ws://localhost:8082/ws/game',
        connectHeaders: {
          Authorization: `Bearer ${token}`
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 10000,
        heartbeatOutgoing: 10000,

        beforeConnect: async () => {
          const refreshedToken =
            await this.authService.getValidToken();

          if (!refreshedToken) {
            throw new Error('JWT indisponible');
          }

          client.connectHeaders = {
            Authorization: `Bearer ${refreshedToken}`
          };
        },

        onConnect: (frame: Frame) => {
          console.info('Connexion STOMP établie', frame.headers);
          this.connected.set(true);
          this.subscribeToCurrentGame();
          resolveOnce();
        },

        onDisconnect: () => {
          this.connected.set(false);
        },

        onWebSocketClose: event => {
          this.connected.set(false);
          this.gameSubscription = undefined;
          console.warn(
            'WebSocket fermé',
            event.code,
            event.reason
          );
        },

        onStompError: frame => {
          this.connected.set(false);

          const error = new Error(
            frame.headers['message'] ||
            frame.body ||
            'Erreur STOMP'
          );

          console.error(
            'Erreur STOMP',
            frame.headers,
            frame.body
          );
          rejectOnce(error);
        },

        onWebSocketError: event => {
          this.connected.set(false);
          console.error('Erreur WebSocket', event);
          rejectOnce(
            new Error('Échec de la connexion WebSocket')
          );
        }
      });

      this.client = client;
      client.activate();
    });
  }

  private subscribeToCurrentGame(): void {
    if (
      !this.client?.connected ||
      !this.watchedGameId
    ) {
      return;
    }

    this.unsubscribeFromGame();

    const destination =
      `/topic/games/${this.watchedGameId}`;

    this.gameSubscription = this.client.subscribe(
      destination,
      (message: IMessage) => {
        try {
          const event = JSON.parse(
            message.body
          ) as GameUpdatedEvent;

          this.gameEvents.next(event);
        } catch (error) {
          console.error(
            'Message WebSocket invalide',
            message.body,
            error
          );
        }
      }
    );

    console.info('Abonnement STOMP actif :', destination);
  }

  private unsubscribeFromGame(): void {
    this.gameSubscription?.unsubscribe();
    this.gameSubscription = undefined;
  }
}
