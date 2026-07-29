import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GameResponse } from './game.models';

@Injectable({ providedIn: 'root' })
export class GameApiService {
  private readonly baseUrl = 'http://localhost:8082/api/games';

  constructor(private readonly http: HttpClient) {}

  createGame(): Observable<GameResponse> {
    return this.http.post<GameResponse>(this.baseUrl, {});
  }

  findWaitingGames(): Observable<GameResponse[]> {
    return this.http.get<GameResponse[]>(`${this.baseUrl}/waiting`);
  }

  joinGame(gameId: string): Observable<GameResponse> {
    return this.http.post<GameResponse>(`${this.baseUrl}/${gameId}/join`, {});
  }

  getGame(gameId: string): Observable<GameResponse> {
    return this.http.get<GameResponse>(`${this.baseUrl}/${gameId}`);
  }

  playMove(gameId: string, cellIndex: number): Observable<GameResponse> {
    return this.http.post<GameResponse>(`${this.baseUrl}/${gameId}/moves`, { cellIndex });
  }

  abandon(gameId: string): Observable<GameResponse> {
    return this.http.post<GameResponse>(`${this.baseUrl}/${gameId}/abandon`, {});
  }
}
