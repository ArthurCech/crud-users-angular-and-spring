import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { BehaviorSubject, Subscription } from 'rxjs';
import { NotificationType } from '../enum/notification-type.enum';
import { CustomHttpResponse } from '../model/custom-http-response.model';
import { User } from '../model/user.model';
import { AuthenticationService } from '../service/authentication.service';
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
  public user: User;
  public selectedUser: User;

  public fileName: string;
  public profileImage: File;

  public editUser: User = new User();
  public currentUsername: string;

  constructor(private userService: UserService,
    private notificationService: NotificationService,
    private authService: AuthenticationService,
    private router: Router) { }

  ngOnInit(): void {
    this.getUsers(true);
    this.user = this.authService.getUserFromLocalStorage();
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

  public onDelete(username: string): void {
    this.subscriptions.push(
      this.userService.deleteUser(username).subscribe({
        next: (res: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, res.message);
          this.getUsers(false);
        },
        error: (err: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, err.error.message);
        }
      })
    );
  }

  public onResetPassword(form: NgForm): void {
    this.isLoading = true;
    const emailAddress = form.value['reset-password-email'];
    this.subscriptions.push(
      this.userService.resetPassword(emailAddress).subscribe({
        next: (res: CustomHttpResponse) => {
          this.sendNotification(NotificationType.SUCCESS, res.message);
          this.isLoading = false;
          form.reset();
        },
        error: (err: HttpErrorResponse) => {
          this.sendNotification(NotificationType.WARNING, err.error.message);
          this.isLoading = false;
        }
      })
    );
  }

  public onUpdateCurrentUser(user: User): void {
    this.isLoading = true;
    this.currentUsername = this.authService.getUserFromLocalStorage().username;
    const formData = this.userService
      .createUserFormDate(this.currentUsername, user, this.profileImage);
    this.subscriptions.push(
      this.userService.updateUser(formData).subscribe({
        next: (user: User) => {
          this.isLoading = false;
          this.authService.addUserToLocalStorage(user);
          this.getUsers(false);
          this.fileName = null;
          this.profileImage = null;
          this.sendNotification(NotificationType.SUCCESS,
            `${user.firstName} ${user.lastName} updated successfully`);
        },
        error: (err: HttpErrorResponse) => {
          this.isLoading = false;
          this.sendNotification(NotificationType.ERROR, err.error.message);
          this.fileName = null;
          this.profileImage = null;
        }
      })
    );
  }

  public onLogOut(): void {
    this.authService.logOut();
    this.router.navigate(['/login']);
    this.sendNotification(NotificationType.SUCCESS,
      `You've been successfully logged out`);
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
