import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { HeaderType } from '../enum/headers.enum';
import { NotificationType } from '../enum/notification-type.enum';
import { User } from '../model/user.model';
import { AuthenticationService } from '../service/authentication.service';
import { NotificationService } from '../service/notification.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {
  public isLoading: boolean;
  private subscriptions: Subscription[] = [];

  constructor(private router: Router, private authService: AuthenticationService,
    private notificationService: NotificationService) { }

  ngOnInit(): void {
    if (this.authService.isUserLoggedIn()) {
      this.router.navigate(['/users/management']);
    } else {
      this.router.navigate(['/login']);
    }
  }

  public onLogin(user: User): void {
    this.isLoading = true;
    this.subscriptions.push(
      this.authService.login(user).subscribe(
        {
          next: (res: HttpResponse<User>) => {
            const token = res.headers.get(HeaderType.TOKEN);
            this.authService.saveToken(token);
            this.authService.addUserToLocalStorage(res.body);
            this.router.navigate(['/users/management']);
            this.isLoading = false;
          },
          error: (err: HttpErrorResponse) => {
            this.sendErrorNotification(NotificationType.ERROR, err.error.message);
            this.isLoading = false;

          }
        }
      )
    );
  }

  private sendErrorNotification(type: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(type, message);
    } else {
      this.notificationService.notify(type, 'An error occurred. Please, try again');
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }
}
