import {
  HttpErrorResponse,
  HttpEvent,
  HttpEventType,
} from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { BehaviorSubject, Subscription } from 'rxjs';
import { NotificationType } from '../enum/notification-type.enum';
import { CustomHttpResponse } from '../model/custom-http-response.model';
import { FileUploadStatus } from '../model/file-upload.status';
import { User } from '../model/user.model';
import { AuthenticationService } from '../service/authentication.service';
import { NotificationService } from '../service/notification.service';
import { UserService } from '../service/user.service';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css'],
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

  public fileStatus = new FileUploadStatus();

  constructor(
    private userService: UserService,
    private notificationService: NotificationService,
    private authService: AuthenticationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.user = this.authService.getUserFromLocalStorage();
    this.getUsers(true);
  }

  public changeTitle(title: string): void {
    this.titleSubject.next(title);
  }

  public getUsers(notification: boolean): void {
    this.isLoading = true;
    this.subscriptions.push(
      this.userService.getUsers().subscribe({
        next: (users: User[]) => {
          this.userService.addUsersToLocalStorage(users);
          this.users = users;
          this.isLoading = false;
          if (notification) {
            this.sendNotification(
              NotificationType.SUCCESS,
              `${users.length} user(s) loaded successfully`
            );
          }
        },
        error: (err: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, err.error.message);
          this.isLoading = false;
        },
      })
    );
  }

  public onSelectUser(user: User): void {
    this.selectedUser = user;
    document.getElementById('openUserInfo').click();
  }

  public onProfileImageChange(event: Event): void {
    this.profileImage = (<HTMLInputElement>event.target).files[0];
    this.fileName = this.profileImage.name;
  }

  public onSaveUser(): void {
    document.getElementById('new-user-save').click();
  }

  public saveUser(userForm: NgForm): void {
    const formData = this.userService.createUserFormDate(
      null,
      userForm.value,
      this.profileImage
    );
    this.subscriptions.push(
      this.userService.addUser(formData).subscribe({
        next: (user: User) => {
          this.profileImage = null;
          this.fileName = null;
          document.getElementById('new-user-close').click();
          this.getUsers(false);
          userForm.reset();
          this.sendNotification(
            NotificationType.SUCCESS,
            `${user.firstName} ${user.lastName} added successfully`
          );
        },
        error: (err: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, err.error.message);
          this.profileImage = null;
        },
      })
    );
  }

  public searchUsers(searchTerm: string): void {
    const results: User[] = [];
    for (const user of this.userService.getUsersFromLocalStorage()) {
      if (
        user.firstName.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
        user.lastName.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
        user.username.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1 ||
        user.userId.toLowerCase().indexOf(searchTerm.toLowerCase()) !== -1
      ) {
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
    const formData = this.userService.createUserFormDate(
      this.currentUsername,
      this.editUser,
      this.profileImage
    );
    this.subscriptions.push(
      this.userService.updateUser(formData).subscribe({
        next: (user: User) => {
          this.profileImage = null;
          this.fileName = null;
          document.getElementById('closeEditUserModalButton').click();
          this.getUsers(false);
          this.sendNotification(
            NotificationType.SUCCESS,
            `${user.firstName} ${user.lastName} updated successfully`
          );
        },
        error: (err: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, err.error.message);
          this.profileImage = null;
        },
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
        },
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
          form.reset();
        },
      })
    );
  }

  public onUpdateCurrentUser(user: User): void {
    this.isLoading = true;
    this.currentUsername = this.authService.getUserFromLocalStorage().username;
    const formData = this.userService.createUserFormDate(
      this.currentUsername,
      user,
      this.profileImage
    );
    this.subscriptions.push(
      this.userService.updateUser(formData).subscribe({
        next: (user: User) => {
          this.isLoading = false;
          this.authService.addUserToLocalStorage(user);
          this.getUsers(false);
          this.fileName = null;
          this.profileImage = null;
          this.sendNotification(
            NotificationType.SUCCESS,
            `${user.firstName} ${user.lastName} updated successfully`
          );
        },
        error: (err: HttpErrorResponse) => {
          this.isLoading = false;
          this.profileImage = null;
          this.sendNotification(NotificationType.ERROR, err.error.message);
        },
      })
    );
  }

  public onLogOut(): void {
    this.authService.logOut();
    this.router.navigate(['/login']);
    this.sendNotification(
      NotificationType.SUCCESS,
      `You've been successfully logged out`
    );
  }

  public updateProfileImage(): void {
    document.getElementById('profile-image-input').click();
  }

  public onUpdateProfileImage(): void {
    const formData = new FormData();
    formData.append('username', this.user.username);
    formData.append('profileImage', this.profileImage);
    this.subscriptions.push(
      this.userService.updateProfileImage(formData).subscribe({
        next: (event: HttpEvent<any>) => {
          this.reportUploadProgress(event);
        },
        error: (err: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, err.error.message);
          this.fileStatus.status = 'done';
        },
      })
    );
  }

  private reportUploadProgress(event: HttpEvent<any>): void {
    switch (event.type) {
      case HttpEventType.UploadProgress:
        this.fileStatus.percentage = Math.round(
          (100 * event.loaded) / event.total
        );
        this.fileStatus.status = 'progress';
        break;
      case HttpEventType.Response:
        if (event.status === 200) {
          this.user.profileImageUrl = `${
            event.body.profileImageUrl
          }?time=${new Date().getTime()}`;
          this.sendNotification(
            NotificationType.SUCCESS,
            `${event.body.firstName}\'s profile image updated successfully`
          );
          this.fileStatus.status = 'done';
          break;
        } else {
          this.sendNotification(
            NotificationType.ERROR,
            `Unable to upload image. Please try again`
          );
          break;
        }
      default:
        `Finished all processes`;
    }
  }

  private sendNotification(type: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(type, message);
    } else {
      this.notificationService.notify(
        type,
        'An error occurred. Please, try again'
      );
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }
}
