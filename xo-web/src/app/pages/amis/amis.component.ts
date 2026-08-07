import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PageHeaderComponent } from '../../shared/page-header.component';
import { BottomNavComponent } from '../../shared/bottom-nav.component';
import {PlayerApiService} from '../../core/player/player-api.service';
import {FriendsApiService} from '../../core/friends/friends-api.service';

@Component({
  selector: 'app-amis',
  standalone: true,
  imports: [FormsModule, PageHeaderComponent, BottomNavComponent],
  templateUrl: './amis.component.html',
})
export class AmisComponent implements OnInit{

  draft = signal('');

  players = signal<any[]>([]);
  listAmis = signal<any[]>([])

  constructor(private playerService: PlayerApiService,
    private friendsService: FriendsApiService) {}

  ngOnInit(){
    this.friendsService.getFriends().subscribe(res=>{
      this.players.set(res)
      this.listAmis.set(res)
    })

  }
  search(): void {
      const text = this.draft().trim();
      this.playerService.findPlayers(text).subscribe(res=>{
        this.players.set(res);
        console.log('players updated')
        this.draft.set('');
      });
  }
  addFriend(id: string){
      this.friendsService.addFriend(id).subscribe(res=>{
          this.players.set(res)
      })
  }
  navigateToChat(id: string){
     console.log('id')
  }
}
