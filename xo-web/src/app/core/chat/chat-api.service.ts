import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {ConversationResponse, MessageResponse, SendMessageRequest} from './chat.model';

@Injectable({ providedIn: 'root' })
export class ChatApiService {
  private readonly baseUrl = 'http://localhost:8084/api/conversation';

  constructor(private readonly http: HttpClient) {
  }

  getMyConversations(): Observable<ConversationResponse[]> {
    return this.http.get<ConversationResponse[]>(`${this.baseUrl}`);
  }

  getOrCreateConversation(friendId: string): Observable<ConversationResponse> {
    return this.http.post<ConversationResponse>(`${this.baseUrl}/direct/${friendId}`, {});
  }

  getMessages(conversationId: string): Observable<MessageResponse[]>{
    return this.http.get<MessageResponse[]>(`${this.baseUrl}/${conversationId}/messages`)
  }

  sendMessage(message: SendMessageRequest, conversationId: string): Observable<MessageResponse[]>{
    return this.http.post<MessageResponse[]>(`${this.baseUrl}/${conversationId}/messages`, message);
  }
}
