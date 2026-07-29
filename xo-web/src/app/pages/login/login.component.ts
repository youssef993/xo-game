import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './login.component.html'
})
export class LoginComponent {

  constructor(
      private readonly authService: AuthService
    ) {}

    login(): void {
      void this.authService.login();
    }

    loginWithGoogle(): void {
      void this.authService.loginWithGoogle();
    }

    loginWithFacebook(): void {
      void this.authService.loginWithFacebook();
    }

    register(): void {
      void this.authService.register();
    }


  }
