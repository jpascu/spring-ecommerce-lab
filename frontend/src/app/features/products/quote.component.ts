import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ProductService } from '../../core/product.service';
import { PriceQuoteResponse, Product, QuoteRequest } from '../../core/models';

@Component({
  selector: 'app-quote',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, CurrencyPipe],
  template: `
    <div class="header-row">
      <h1>Presupuesto</h1>
      <button class="secondary" routerLink="/products">Volver</button>
    </div>

    @if (product(); as p) {
      <p class="muted">
        {{ p.name }} — precio unitario {{ p.price | currency: 'EUR' }} · stock {{ p.stock }}
      </p>
    }

    @if (error()) {
      <div class="banner-error">{{ error() }}</div>
    }

    <div class="layout">
      <div class="card form-card">
        <form [formGroup]="form" (ngSubmit)="submit()">
          <div class="field">
            <label for="tier">Nivel de cliente</label>
            <select id="tier" formControlName="tier">
              <option value="STANDARD">STANDARD</option>
              <option value="PREMIUM">PREMIUM (8%)</option>
              <option value="VIP">VIP (15%)</option>
            </select>
          </div>
          <div class="field">
            <label for="quantity">Cantidad</label>
            <input id="quantity" type="number" min="1" formControlName="quantity" />
          </div>
          <div class="field">
            <label for="couponCode">Cupón (opcional)</label>
            <input id="couponCode" type="text" formControlName="couponCode" placeholder="SAVE10, SAVE20, HALF" />
          </div>
          <button type="submit" [disabled]="form.invalid || loading()">
            {{ loading() ? 'Calculando…' : 'Calcular' }}
          </button>
        </form>
      </div>

      @if (result(); as r) {
        <div class="card result-card">
          <h3>Resultado</h3>
          <div class="line"><span>Precio unitario</span><strong>{{ r.unitPrice | currency: 'EUR' }}</strong></div>
          <div class="line"><span>Cantidad</span><strong>{{ r.quantity }}</strong></div>
          <div class="line"><span>Subtotal</span><strong>{{ r.subtotal | currency: 'EUR' }}</strong></div>
          <div class="line discount"><span>Descuento</span><strong>-{{ r.discount | currency: 'EUR' }}</strong></div>
          <div class="line total"><span>Total</span><strong>{{ r.total | currency: 'EUR' }}</strong></div>
          <div class="line"><span>Estrategia</span><span class="badge">{{ r.appliedStrategy }}</span></div>
        </div>
      }
    </div>
  `,
  styles: [
    `
      .header-row {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 0.5rem;
      }
      .layout {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 1rem;
        margin-top: 1rem;
      }
      .line {
        display: flex;
        justify-content: space-between;
        padding: 0.45rem 0;
        border-bottom: 1px dashed var(--border);
      }
      .line.total {
        border-bottom: none;
        font-size: 1.15rem;
        margin-top: 0.3rem;
      }
      .line.discount strong {
        color: var(--success);
      }
      .badge {
        background: var(--surface-2);
        padding: 0.15rem 0.5rem;
        border-radius: 6px;
        font-size: 0.8rem;
      }
      @media (max-width: 640px) {
        .layout {
          grid-template-columns: 1fr;
        }
      }
    `,
  ],
})
export class QuoteComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(ProductService);
  private readonly route = inject(ActivatedRoute);

  private productId!: number;

  readonly product = signal<Product | null>(null);
  readonly result = signal<PriceQuoteResponse | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    tier: ['STANDARD', Validators.required],
    quantity: [1, [Validators.required, Validators.min(1)]],
    couponCode: [''],
  });

  ngOnInit(): void {
    this.productId = Number(this.route.snapshot.paramMap.get('id'));
    this.service.get(this.productId).subscribe({
      next: (p) => this.product.set(p),
      error: () => this.error.set('No se pudo cargar el producto.'),
    });
  }

  submit(): void {
    if (this.form.invalid) {
      return;
    }
    this.loading.set(true);
    this.error.set(null);
    const raw = this.form.getRawValue();
    const body: QuoteRequest = {
      tier: raw.tier,
      quantity: raw.quantity,
      couponCode: raw.couponCode?.trim() ? raw.couponCode.trim() : null,
    };
    this.service.quote(this.productId, body).subscribe({
      next: (r) => {
        this.result.set(r);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudo calcular el presupuesto.');
        this.loading.set(false);
      },
    });
  }
}
