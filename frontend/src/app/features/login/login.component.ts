import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <div class="login-wrap">
      <div class="card login-card">
        <h1>Iniciar sesión</h1>
        <p class="muted">Accede para gestionar el catálogo de productos.</p>

        @if (error()) {
          <div class="banner-error">{{ error() }}</div>
        }

        <form [formGroup]="form" (ngSubmit)="submit()">
          <div class="field">
            <label for="username">Usuario</label>
            <input id="username" type="text" formControlName="username" autocomplete="username" />
          </div>
          <div class="field">
            <label for="password">Contraseña</label>
            <input
              id="password"
              type="password"
              formControlName="password"
              autocomplete="current-password"
            />
          </div>
          <button type="submit" [disabled]="form.invalid || loading()">
            {{ loading() ? 'Entrando…' : 'Entrar' }}
          </button>
        </form>

        <p class="hint muted">
          Usuarios de ejemplo: <code>admin / password</code> · <code>user / password</code>
        </p>
      </div>
    </div>
  `,
  styles: [
    `
      .login-wrap {
        display: flex;
        justify-content: center;
        padding-top: 3rem;
      }
      .login-card {
        width: 100%;
        max-width: 380px;
      }
      .hint {
        margin-top: 1rem;
        font-size: 0.8rem;
      }
      code {
        background: var(--surface-2);
        padding: 0.1rem 0.3rem;
        border-radius: 4px;
      }
    `,
  ],
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  submit(): void {
    if (this.form.invalid) {
      return;
    }
    this.loading.set(true);
    this.error.set(null);
    const { username, password } = this.form.getRawValue();
    this.auth.login(username, password).subscribe({
      next: () => this.router.navigate(['/products']),
      error: (err) => {
        this.error.set(
          err.status === 401 ? 'Credenciales inválidas' : 'No se pudo conectar con el servidor',
        );
        this.loading.set(false);
      },
    });
  }
}
