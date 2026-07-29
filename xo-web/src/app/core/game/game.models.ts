export type PlayerSymbol = 'X' | 'O';
export type GameStatus =
  | 'WAITING_FOR_PLAYER'
  | 'IN_PROGRESS'
  | 'X_WON'
  | 'O_WON'
  | 'DRAW'
  | 'ABANDONED';

export interface GameMove {
  id: string;
  playerId: string;
  symbol: PlayerSymbol;
  cellIndex: number;
  moveNumber: number;
  playedAt: string;
}

export interface GameResponse {
  id: string;
  playerXId: string;
  playerOId: string | null;
  board: Array<PlayerSymbol | null>;
  currentTurn: PlayerSymbol | null;
  status: GameStatus;
  winnerId: string | null;
  version: number;
  createdAt: string;
  startedAt: string | null;
  finishedAt: string | null;
  moves: GameMove[];
}

export interface ApiProblem {
  title?: string;
  detail?: string;
  status?: number;
  errors?: Record<string, string>;
}
