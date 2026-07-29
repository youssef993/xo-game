import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { roleGuard } from './core/auth/role.guard';

const playerAccess = {
  canActivate: [authGuard, roleGuard],
  data: { roles: ['PLAYER', 'ADMIN'] }
};

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'home',
    ...playerAccess,
    loadComponent: () => import('./pages/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'matchmaking',
    ...playerAccess,
    loadComponent: () => import('./pages/matchmaking/matchmaking.component').then(m => m.MatchmakingComponent)
  },
  {
    path: 'match-found',
    ...playerAccess,
    loadComponent: () => import('./pages/match-found/match-found.component').then(m => m.MatchFoundComponent)
  },
  {
    path: 'game/:gameId',
    ...playerAccess,
    loadComponent: () => import('./pages/game/game.component').then(m => m.GameComponent)
  },
  {
    path: 'result/:status',
    ...playerAccess,
    loadComponent: () => import('./pages/result/result.component').then(m => m.ResultComponent)
  },
  {
    path: 'chat',
    ...playerAccess,
    loadComponent: () => import('./pages/chat/chat.component').then(m => m.ChatComponent)
  },
  {
    path: 'profile',
    ...playerAccess,
    loadComponent: () => import('./pages/profile/profile.component').then(m => m.ProfileComponent)
  },
  {
    path: 'history',
    ...playerAccess,
    loadComponent: () => import('./pages/history/history.component').then(m => m.HistoryComponent)
  },
  {
    path: 'leaderboard',
    ...playerAccess,
    loadComponent: () => import('./pages/leaderboard/leaderboard.component').then(m => m.LeaderboardComponent)
  },
  {
    path: 'admin',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMIN'] },
    loadComponent: () => import('./pages/admin/admin.component').then(m => m.AdminComponent)
  },
  {
    path: 'forbidden',
    loadComponent: () => import('./pages/forbidden/forbidden.component').then(m => m.ForbiddenComponent)
  },
  { path: '**', redirectTo: '' }
];
