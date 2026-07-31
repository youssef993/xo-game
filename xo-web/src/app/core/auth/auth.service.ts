import { Injectable, signal } from '@angular/core';
import { keycloak } from './keycloak';
import {PlayerApiService} from '../player/player-api.service';
import {PlayerResponse} from '../player/player.model';

export interface AuthenticatedUser {
  id: string;
  username: string;
  email?: string;
  firstName?: string;
  lastName?: string;
  roles: string[];
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private readonly playerService: PlayerApiService) {
  }
  readonly initialized = signal(false);
  readonly authenticated = signal(false);
  readonly user = signal<PlayerResponse | null>(null);

  async initialize(): Promise<void> {
    try {
      const authenticated = await keycloak.init({
        onLoad: 'check-sso',
        pkceMethod: 'S256',
        checkLoginIframe: false
      });

      this.authenticated.set(authenticated);

      if (authenticated) {
        this.loadUserFromToken();
        this.installTokenRefresh();
      }
    } finally {
      this.initialized.set(true);
    }
  }

  async login(): Promise<void> {
    await keycloak.login({
      redirectUri: `${window.location.origin}/home`
    });
  }

  async loginWithGoogle(): Promise<void> {
    await keycloak.login({
      idpHint: 'google',
      redirectUri: `${window.location.origin}/home`
    });
  }

  async loginWithFacebook(): Promise<void> {
    await keycloak.login({
      idpHint: 'facebook',
      redirectUri: `${window.location.origin}/home`
    });
  }

  async register(): Promise<void> {
    await keycloak.register({
      redirectUri: `${window.location.origin}/home`
    });
  }

  async logout(): Promise<void> {
    await keycloak.logout({
      redirectUri: window.location.origin
    });

    this.authenticated.set(false);
    this.user.set(null);
  }

  async getValidToken(): Promise<string | undefined> {
    if (!keycloak.authenticated) {
      return undefined;
    }

    try {
      await keycloak.updateToken(30);
      return keycloak.token;
    } catch {
      await this.logout();
      return undefined;
    }
  }

  hasRole(role: string): boolean {
    return keycloak.hasRealmRole(role);
  }

  hasAnyRole(roles: string[]): boolean {
    return roles.some(role => this.hasRole(role));
  }

  private loadUserFromToken(): void {
    const token = keycloak.tokenParsed;

    if (!token?.sub) {
      this.user.set(null);
      return;
    }

    const realmAccess = token['realm_access'] as
      | { roles?: string[] }
      | undefined;

    this.playerService.getOrRegisterPlayer().subscribe(res=>{
      this.user.set(res);
    },error => {
      console.error("cant get or register user")
    })
  }

  private installTokenRefresh(): void {
    keycloak.onTokenExpired = () => {
      void keycloak.updateToken(30).catch(() => this.logout());
    };
  }
}
