import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {FriendResponse, FriendshipRequest} from './friends.model';

@Injectable({ providedIn: 'root' })
export class FriendsApiService {
  private readonly baseUrl = 'http://localhost:8081/api/friends';

  constructor(private readonly http: HttpClient) {
  }

  getFriends(): Observable<FriendResponse[]> {
    return this.http.get<FriendResponse[]>(`${this.baseUrl}`);
  }

  addFriend(requestedId: string): Observable<FriendResponse[]> {
    let friends: FriendshipRequest = {
      recieverId: requestedId,
      senderId: ''
    };
    return this.http.post<FriendResponse[]>(`${this.baseUrl}`, friends);
  }
}
