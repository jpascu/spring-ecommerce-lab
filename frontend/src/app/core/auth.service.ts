import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { TokenResponse } from './models';

const TOKEN_KEY = 'ecommerce.jwt';

interface JwtPayload {
  sub?: string;
  roles?: string[];
  exp?: number;
}

/**
 * Gestiona la autenticación: login contra /api/auth/login, almacenamiento del JWT
 * y exposición reactiva (signals) del estado de sesión y los roles.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  private readonly token = signal<string | null>(localStorage.getItem(TOKEN_KEY));

  readonly isLoggedIn = computed(() => this.token() !== null);
  readonly username = computed(() => this.decode()?.sub ?? null);
  readonly roles = computed(() => this.decode()?.roles ?? []);
  readonly isAdmin = computed(() => this.roles().includes('ROLE_ADMIN'));

  login(username: string, password: string): Observable<TokenResponse> {
    return this.http
      .post<TokenResponse>('/api/auth/login', { username, password })
      .pipe(tap((res) => this.setToken(res.accessToken)));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    this.token.set(null);
  }

  getToken(): string | null {
    return this.token();
  }

  private setToken(value: string): void {
    localStorage.setItem(TOKEN_KEY, value);
    this.token.set(value);
  }

  private decode(): JwtPayload | null {
    const raw = this.token();
    if (!raw) {
      return null;
    }
    try {
      const payload = raw.split('.')[1];
      const json = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
      return JSON.parse(json) as JwtPayload;
    } catch {
      return null;
    }
  }
}
