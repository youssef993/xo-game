export interface FriendResponse {
  id: string;
  username: string;
  avatarUrl: string;
  status: string;
  wins: number;
  losses: number;
  draws: number;
}
export interface FriendshipRequest {
  recieverId: string;
  senderId: string;
}
