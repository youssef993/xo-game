import { Component, OnInit } from '@angular/core';
import { PageHeaderComponent } from '../../shared/page-header.component';
import { BottomNavComponent } from '../../shared/bottom-nav.component';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [PageHeaderComponent, BottomNavComponent],
  templateUrl: './profile.component.html'
})
export class ProfileComponent implements OnInit{

  constructor(protected auth: AuthService){}
  ngOnInit(){}
}
