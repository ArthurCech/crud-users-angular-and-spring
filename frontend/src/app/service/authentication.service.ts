import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { User } from '../model/user.model';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private api = environment.api;
  private accessToken: string;
  private loggedInUsername: string;

  constructor(private http: HttpClient, private jwtHelper: JwtHelperService) { }

  public login(user: User): Observable<HttpResponse<User>> {
    return this.http.post<User>(`${this.api}/users/login`,
      user, { observe: 'response' });
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
    this.accessToken = localStorage.getItem('token');
  }

  public getToken(): string {
    return this.accessToken;
  }

  public isUserLoggedIn(): boolean {
    this.loadTokenFromLocalStorage();

    const sub = this.jwtHelper.decodeToken(this.accessToken).sub;

    if (
      this.accessToken != null && this.accessToken !== '' &&
      sub != null && sub !== '' && !this.isTokenExpired()
    ) {
      this.loggedInUsername = sub;
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
