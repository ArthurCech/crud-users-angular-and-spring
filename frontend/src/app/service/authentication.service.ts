import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { User } from '../model/user.model';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  public api = environment.api;
  private accessToken: string;
  private loggedInUsername: string;
  private jwtHelper = new JwtHelperService();

  constructor(private http: HttpClient) {}

  public login(user: User): Observable<HttpResponse<User>> {
    return this.http.post<User>(`${this.api}/users/login`, user, {
      observe: 'response',
    });
  }

  public register(user: User): Observable<User> {
    return this.http.post<User>(`${this.api}/users/register`, user);
  }

  public logOut(): void {
    this.accessToken = null;
    this.loggedInUsername = null;
    localStorage.removeItem('user');
    localStorage.removeItem('accessToken');
    localStorage.removeItem('users');
  }

  public saveToken(accessToken: string): void {
    this.accessToken = accessToken;
    localStorage.setItem('accessToken', accessToken);
  }

  public addUserToLocalStorage(user: User): void {
    localStorage.setItem('user', JSON.stringify(user));
  }

  public getUserFromLocalStorage(): User {
    return JSON.parse(localStorage.getItem('user'));
  }

  public loadTokenFromLocalStorage(): void {
    this.accessToken = localStorage.getItem('accessToken');
  }

  public getToken(): string {
    return this.accessToken;
  }

  public isUserLoggedIn(): boolean {
    this.loadTokenFromLocalStorage();
    if (
      this.accessToken != null &&
      this.accessToken !== '' &&
      this.jwtHelper.decodeToken(this.accessToken).sub != null &&
      this.jwtHelper.decodeToken(this.accessToken).sub !== '' &&
      !this.isTokenExpired()
    ) {
      this.loggedInUsername = this.jwtHelper.decodeToken(this.accessToken).sub;
      return true;
    } else {
      this.logOut();
      return false;
    }
  }

  private isTokenExpired(): boolean {
    return this.jwtHelper.isTokenExpired(this.accessToken);
  }
}
