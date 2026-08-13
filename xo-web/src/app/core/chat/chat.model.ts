export type ConversationType = 'DIRECT' | 'GAME';
export interface ConversationResponse {
  id: string;
  conversationType: ConversationType;
  gameId: string;
  createdAt: string;
  updatedAt: string;
}
export interface MessageResponse {
  id: string;
  conversationId: string;
  senderId: string;
  content: string;
  sentAt: string;
  editedAt: string;
}
export interface SendMessageRequest {
  content: string;
}
