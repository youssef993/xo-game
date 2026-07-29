export type MatchmakingStatus =
  | 'SEARCHING'
  | 'MATCHED'
  | 'CANCELLED';

export interface MatchmakingResponse {
  ticketId: string;
  status: MatchmakingStatus;
  playerId: string;
  opponentId: string | null;
  gameId: string | null;
  searchingSince: string;
  matchedAt: string | null;
}

export interface MatchFoundEvent {
  type: 'MATCH_FOUND';
  playerId: string;
  opponentId: string;
  gameId: string;
  occurredAt: string;
}
