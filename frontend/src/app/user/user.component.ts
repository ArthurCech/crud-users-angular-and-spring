import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { BehaviorSubject, Subscription } from 'rxjs';
import { NotificationType } from '../enum/notification-type.enum';
import { User } from '../model/user.model';
import { NotificationService } from '../service/notification.service';
import { UserService } from '../service/user.service';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  private titleSubject = new BehaviorSubject<string>('Users');
  public titleAction$ = this.titleSubject.asObservable();

  public isLoading: boolean;

  public users: User[];

  constructor(private userService: UserService,
    private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.getUsers(true);
  }

  public changeTitle(title: string): void {
    this.titleSubject.next(title);
  }

  public getUsers(notification: boolean): void {
    this.isLoading = true;
    this.subscriptions.push(
      this.userService.getUsers().subscribe(
        {
          next: (users: User[]) => {
            this.userService.addUsersToLocalCache(users);
            this.users = users;
            if (notification) {
              this.sendNotification(NotificationType.SUCCESS,
                `${users.length} user(s) loaded successfully`);
            }
          },
          error: (err: HttpErrorResponse) => {
            this.sendNotification(NotificationType.ERROR, err.error.message);
          },
          complete: () => {
            this.isLoading = false;
          }
        }
      )
    );

  }

  private sendNotification(type: NotificationType, message: string): void {
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
