import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { MatchmakingResponse } from './matchmaking.models';

@Injectable({
  providedIn: 'root'
})
export class MatchmakingApiService {
  private readonly baseUrl =
    'http://localhost:8083/api/matchmaking';

  constructor(private readonly http: HttpClient) {}

  search(): Observable<MatchmakingResponse> {
    return this.http.post<MatchmakingResponse>(
      `${this.baseUrl}/search`,
      {}
    );
  }

  getStatus(): Observable<MatchmakingResponse> {
    return this.http.get<MatchmakingResponse>(
      `${this.baseUrl}/status`
    );
  }

  cancel(): Observable<MatchmakingResponse> {
    return this.http.delete<MatchmakingResponse>(
      `${this.baseUrl}/search`
    );
  }
}
