import { Component } from '@angular/core';
import { PageHeaderComponent } from '../../shared/page-header.component';
import { BottomNavComponent } from '../../shared/bottom-nav.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [PageHeaderComponent, BottomNavComponent],
  templateUrl: './profile.component.html'
})
export class ProfileComponent {}
