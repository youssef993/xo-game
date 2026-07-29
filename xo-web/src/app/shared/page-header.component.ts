import { Component, input } from '@angular/core';
import { Location } from '@angular/common';

@Component({
  selector: 'app-page-header',
  standalone: true,
  template: `
    <header class="page-header">
      <button class="icon-button" type="button" (click)="back()" aria-label="Retour">←</button>
      <h1>{{ title() }}</h1>
      <span class="header-space"></span>
    </header>
  `
})
export class PageHeaderComponent {
  readonly title = input.required<string>();

  constructor(private readonly location: Location) {}

  back(): void {
    this.location.back();
  }
}
