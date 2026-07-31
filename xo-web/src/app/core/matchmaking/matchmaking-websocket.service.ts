import { DestroyRef, Injectable, inject, signal } from '@angular/core';
import { Client, Frame, IMessage, StompSubscription } from '@stomp/stompjs';
import { Observable, Subject } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { MatchFoundEvent } from './matchmaking.models';

@Injectable({
  providedIn: 'root'
})
export class MatchmakingWebSocketService {
  private readonly authService = inject(AuthService);
  private readonly destroyRef = inject(DestroyRef);

  private client?: Client;
  private connectionPromise?: Promise<void>;
  private matchSubscription?: StompSubscription;
  private watchingMatchmaking = false;
  private readonly matchEvents =
    new Subject<MatchFoundEvent>();

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

    this.connectionPromise =
      this.createAndActivateClient()
        .finally(() => {
          this.connectionPromise = undefined;
        });

    console.log(this.connectionPromise)
    return this.connectionPromise;
  }

  watchMatchFound(): Observable<MatchFoundEvent> {
    if (!this.client?.connected) {
      throw new Error(
        'WebSocket matchmaking non connecté'
      );
    }

    this.watchingMatchmaking = true;
    this.subscribeToMatchmaking();

    return this.matchEvents.asObservable();
  }

  async disconnect(): Promise<void> {
    this.watchingMatchmaking = false;
    this.unsubscribeFromMatchmaking();

    const client = this.client;
    this.client = undefined;
    this.connectionPromise = undefined;
    this.connected.set(false);

    if (client?.active) {
      await client.deactivate();
    }
  }

  private async createAndActivateClient():
    Promise<void> {
    const token =
      await this.authService.getValidToken();

    if (!token) {
      throw new Error(
        'Impossible de connecter le matchmaking sans JWT'
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
        brokerURL:
          'ws://localhost:8083/ws/matchmaking',

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
          console.info(
            'WebSocket matchmaking connecté',
            frame.headers
          );

          this.connected.set(true);
          this.subscribeToMatchmaking();
          resolveOnce();
        },

        onDisconnect: () => {
          this.connected.set(false);
        },

        onWebSocketClose: event => {
          this.connected.set(false);
          this.matchSubscription = undefined;

          console.warn(
            'WebSocket matchmaking fermé',
            event.code,
            event.reason
          );
        },

        onStompError: frame => {
          this.connected.set(false);

          const error = new Error(
            frame.headers['message'] ||
            frame.body ||
            'Erreur STOMP matchmaking'
          );

          console.error(
            'Erreur STOMP matchmaking',
            frame.headers,
            frame.body
          );

          rejectOnce(error);
        },

        onWebSocketError: event => {
          this.connected.set(false);
          console.error(
            'Erreur WebSocket matchmaking',
            event
          );

          rejectOnce(
            new Error(
              'Échec de la connexion WebSocket matchmaking'
            )
          );
        }
      });

      this.client = client;
      client.activate();
    });
  }

  private subscribeToMatchmaking(): void {
    if (
      !this.client?.connected ||
      !this.watchingMatchmaking
    ) {
      return;
    }

    this.unsubscribeFromMatchmaking();

    const destination =
      '/user/queue/matchmaking';

    this.matchSubscription =
      this.client.subscribe(
        destination,
        (message: IMessage) => {
          try {
            const event = JSON.parse(
              message.body
            ) as MatchFoundEvent;

            this.matchEvents.next(event);
          } catch (error) {
            console.error(
              'Événement matchmaking invalide',
              message.body,
              error
            );
          }
        }
      );

    console.info(
      'Abonnement matchmaking actif :',
      destination
    );
  }

  private unsubscribeFromMatchmaking(): void {
    this.matchSubscription?.unsubscribe();
    this.matchSubscription = undefined;
  }
}
