export type PlayerStatus =
  | 'ONLINE'
  | 'OFFLINE'
  | 'IN_QUEUE'
  | 'IN_GAME';
export interface PlayerResponse {
  id: string;
  keycloakId: string;
  username: string;
  email: string;
  avatarUrl: string | null;
  status: PlayerStatus;
  gamesPlayed: number;
  wins: number;
  losses: number;
  draws: number;
  createdAt: string;
}
