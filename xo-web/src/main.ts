import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { AuthService } from './app/core/auth/auth.service';

bootstrapApplication(AppComponent, appConfig)
  .catch(error => {
    console.error('Erreur au démarrage de l’application :', error);
  });
