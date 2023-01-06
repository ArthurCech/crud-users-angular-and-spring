import { HttpClient, HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CustomHttpResponse } from '../model/custom-http-response.model';
import { User } from '../model/user.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private api = environment.api;

  constructor(private http: HttpClient) {}

  public getUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.api}/users/list`);
  }

  public addUser(formData: FormData): Observable<User> {
    return this.http.post<User>(`${this.api}/users/add`, formData);
  }

  public updateUser(formData: FormData): Observable<User> {
    return this.http.put<User>(`${this.api}/users/update`, formData);
  }

  public resetPassword(email: string): Observable<CustomHttpResponse> {
    return this.http.get<CustomHttpResponse>(
      `${this.api}/users/resetpassword/${email}`
    );
  }

  public updateProfileImage(formData: FormData): Observable<HttpEvent<any>> {
    return this.http.post<any>(
      `${this.api}/users/update-profile-image`,
      formData,
      {
        reportProgress: true,
        observe: 'events',
      }
    );
  }

  public deleteUser(username: string): Observable<CustomHttpResponse> {
    return this.http.delete<CustomHttpResponse>(
      `${this.api}/users/delete/${username}`
    );
  }

  public addUsersToLocalCache(users: User[]): void {
    localStorage.setItem('users', JSON.stringify(users));
  }

  public getUsersFromLocalStorage(): User[] {
    if (localStorage.getItem('users'))
      return JSON.parse(localStorage.getItem('users'));
    return null;
  }

  public createUserFormDate(
    loggedInUsername: string,
    user: User,
    profileImage: File
  ): FormData {
    const formData = new FormData();
    formData.append('currentUsername', loggedInUsername);
    formData.append('firstName', user.firstName);
    formData.append('lastName', user.lastName);
    formData.append('username', user.username);
    formData.append('email', user.email);
    formData.append('role', user.role);
    formData.append('profileImage', profileImage);
    formData.append('isActive', JSON.stringify(user.active));
    formData.append('isNonLocked', JSON.stringify(user.notLocked));
    return formData;
  }
}
