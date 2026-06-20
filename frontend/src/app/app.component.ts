import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from './core/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  template: `
    <header class="topbar">
      <a routerLink="/products" class="brand">spring-ecommerce-lab</a>
      <nav>
        @if (auth.isLoggedIn()) {
          <span class="user">
            {{ auth.username() }}
            @if (auth.isAdmin()) {
              <span class="badge">ADMIN</span>
            }
          </span>
          <button class="secondary" (click)="logout()">Salir</button>
        }
      </nav>
    </header>
    <main class="content">
      <router-outlet />
    </main>
  `,
  styles: [
    `
      .topbar {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 0.8rem 1.5rem;
        background: var(--surface);
        border-bottom: 1px solid var(--border);
        box-shadow: var(--shadow);
      }
      .brand {
        font-weight: 700;
        font-size: 1.1rem;
        color: var(--text);
      }
      nav {
        display: flex;
        align-items: center;
        gap: 0.8rem;
      }
      .user {
        font-size: 0.9rem;
        color: var(--muted);
        display: inline-flex;
        align-items: center;
        gap: 0.4rem;
      }
      .badge {
        background: var(--primary);
        color: #fff;
        font-size: 0.7rem;
        font-weight: 700;
        padding: 0.1rem 0.4rem;
        border-radius: 6px;
      }
      .content {
        max-width: 960px;
        margin: 0 auto;
        padding: 1.5rem;
      }
    `,
  ],
})
export class AppComponent {
  readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
