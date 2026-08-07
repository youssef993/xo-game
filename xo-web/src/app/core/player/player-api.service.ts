import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {PlayerResponse} from './player.model';

@Injectable({ providedIn: 'root' })
export class PlayerApiService {
  private readonly baseUrl = 'http://localhost:8081/api/players';

  constructor(private readonly http: HttpClient) {
  }

  getOrRegisterPlayer(): Observable<PlayerResponse> {
    return this.http.post<PlayerResponse>(`${this.baseUrl}/me/register`, {});
  }

  findPlayers(search: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}?search=${search}`);
  }
}
