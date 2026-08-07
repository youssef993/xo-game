import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PageHeaderComponent } from '../../shared/page-header.component';
import { BottomNavComponent } from '../../shared/bottom-nav.component';
import {PlayerApiService} from '../../core/player/player-api.service';
import {FriendsApiService} from '../../core/friends/friends-api.service';
import {PlayerResponse} from '../../core/player/player.model';
import {FriendResponse} from '../../core/friends/friends.model';
import {NgIf} from '@angular/common';
import {Router} from '@angular/router';

@Component({
  selector: 'app-amis',
  standalone: true,
  imports: [FormsModule, PageHeaderComponent, BottomNavComponent, NgIf],
  templateUrl: './amis.component.html',
})
export class AmisComponent implements OnInit{

  draft = signal('');

  players = signal<PlayerResponse[]>([]);
  listAmis = signal<FriendResponse[]>([])

  constructor(private playerService: PlayerApiService,
              private friendsService: FriendsApiService,
              private readonly router: Router) {}

  ngOnInit(){
    this.friendsService.getFriends().subscribe(res=>{
      this.listAmis.set(res)
    })

  }
  search(): void {
      const text = this.draft().trim();
      this.playerService.findPlayers(text).subscribe(res=>{
        let listIds = new Set(this.listAmis().map(a => a.id))
        this.players.set(
          res.filter(p => !listIds.has(p.id))
        )
        this.listAmis.set([])
        this.draft.set('');
      });
  }
  addFriend(id: string){
      this.friendsService.addFriend(id).subscribe(res=>{
          this.listAmis.set(res)
      })
  }
  navigateToChat(id: string){
    this.router.navigate([`/chat/${id}`])
  }
}
