import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
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

  public users: User[] = [];
  public selectedUser: User;

  public fileName: string;
  public profileImage: File;

  public editUser: User = new User();
  public currentUsername: string;

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

  public onSelectUser(user: User): void {
    this.selectedUser = user;
    document.getElementById('openUserInfo').click();
  }

  public onProfileImageChange(event: Event): void {
    const profileImage = (<HTMLInputElement>event.target).files[0];
    this.fileName = profileImage.name;
  }

  public onSaveUser(): void {
    document.getElementById('new-user-save').click();
  }

  public saveUser(userForm: NgForm): void {
    const formData = this.userService.createUserFormDate(null, userForm.value, this.profileImage);
    this.subscriptions.push(
      this.userService.addUser(formData).subscribe(
        {
          next: (user: User) => {
            document.getElementById('new-user-close').click();
            this.getUsers(false);
            userForm.reset();
            this.sendNotification(NotificationType.SUCCESS,
              `${user.firstName} ${user.lastName} added successfully`);
          },
          error: (err: HttpErrorResponse) => {
            this.sendNotification(NotificationType.ERROR, err.error.message);
          },
          complete: () => {
            this.profileImage = null;
            this.fileName = null;
          }
        }
      )
    );
  }

  public searchUsers(searchTerm: string): void {
    const results: User[] = [];
    for (const user of this.userService.getUsersFromLocalStorage()) {
      if (user.firstName.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
        user.lastName.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
        user.username.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
        user.userId.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1) {
        results.push(user);
      }
    }
    this.users = results;
    if (results.length === 0 || !searchTerm) {
      this.users = this.userService.getUsersFromLocalStorage();
    }
  }

  public onEdit(user: User): void {
    this.editUser = user;
    this.currentUsername = user.username;
    document.getElementById('openUserEdit').click();
  }

  public onUpdateUser(): void {
    const formData = this.userService.createUserFormDate(this.currentUsername, this.editUser, this.profileImage);
    this.subscriptions.push(
      this.userService.updateUser(formData).subscribe({
        next: (user: User) => {
          document.getElementById('closeEditUserModalButton').click();
          this.getUsers(false);
          this.sendNotification(NotificationType.SUCCESS,
            `${user.firstName} ${user.lastName} updated successfully`);
        },
        error: (err: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, err.error.message);
        },
        complete: () => {
          this.profileImage = null;
          this.fileName = null;
        }
      })
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
