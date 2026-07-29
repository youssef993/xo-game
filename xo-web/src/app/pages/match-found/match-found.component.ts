import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PageHeaderComponent } from '../../shared/page-header.component';

@Component({
  selector: 'app-match-found',
  standalone: true,
  imports: [RouterLink, PageHeaderComponent],
  templateUrl: './match-found.component.html'
})
export class MatchFoundComponent {}
