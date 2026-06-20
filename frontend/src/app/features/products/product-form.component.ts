import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ProductService } from '../../core/product.service';
import { ApiError, ProductRequest } from '../../core/models';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  template: `
    <div class="header-row">
      <h1>{{ id() ? 'Editar producto' : 'Nuevo producto' }}</h1>
      <button class="secondary" routerLink="/products">Volver</button>
    </div>

    @if (error()) {
      <div class="banner-error">{{ error() }}</div>
    }

    <div class="card form-card">
      <form [formGroup]="form" (ngSubmit)="submit()">
        <div class="field">
          <label for="name">Nombre</label>
          <input id="name" type="text" formControlName="name" />
          @if (invalid('name')) {
            <div class="error-text">El nombre es obligatorio.</div>
          }
        </div>

        <div class="field">
          <label for="description">Descripción</label>
          <input id="description" type="text" formControlName="description" />
        </div>

        <div class="grid-2">
          <div class="field">
            <label for="price">Precio (€)</label>
            <input id="price" type="number" step="0.01" min="0" formControlName="price" />
            @if (invalid('price')) {
              <div class="error-text">Precio obligatorio y no negativo.</div>
            }
          </div>
          <div class="field">
            <label for="stock">Stock</label>
            <input id="stock" type="number" min="0" formControlName="stock" />
            @if (invalid('stock')) {
              <div class="error-text">Stock obligatorio y no negativo.</div>
            }
          </div>
        </div>

        <div class="field">
          <label for="category">Categoría</label>
          <input id="category" type="text" formControlName="category" />
          @if (invalid('category')) {
            <div class="error-text">La categoría es obligatoria.</div>
          }
        </div>

        <div class="actions-row">
          <button type="submit" [disabled]="form.invalid || saving()">
            {{ saving() ? 'Guardando…' : 'Guardar' }}
          </button>
          <button type="button" class="secondary" routerLink="/products">Cancelar</button>
        </div>
      </form>
    </div>
  `,
  styles: [
    `
      .header-row {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 1rem;
      }
      .form-card {
        max-width: 560px;
      }
      .grid-2 {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 1rem;
      }
      .actions-row {
        display: flex;
        gap: 0.6rem;
        margin-top: 0.5rem;
      }
    `,
  ],
})
export class ProductFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(ProductService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly id = signal<number | null>(null);
  readonly saving = signal(false);
  readonly error = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    description: [''],
    price: [0, [Validators.required, Validators.min(0)]],
    stock: [0, [Validators.required, Validators.min(0)]],
    category: ['', Validators.required],
  });

  ngOnInit(): void {
    const param = this.route.snapshot.paramMap.get('id');
    if (param) {
      const productId = Number(param);
      this.id.set(productId);
      this.service.get(productId).subscribe({
        next: (product) =>
          this.form.patchValue({
            name: product.name,
            description: product.description ?? '',
            price: product.price,
            stock: product.stock,
            category: product.category,
          }),
        error: () => this.error.set('No se pudo cargar el producto.'),
      });
    }
  }

  invalid(control: string): boolean {
    const c = this.form.get(control);
    return !!c && c.invalid && (c.dirty || c.touched);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    this.error.set(null);
    const body = this.form.getRawValue() as ProductRequest;
    const request$ = this.id()
      ? this.service.update(this.id()!, body)
      : this.service.create(body);

    request$.subscribe({
      next: () => this.router.navigate(['/products']),
      error: (err) => {
        this.error.set(this.extractMessage(err.error));
        this.saving.set(false);
      },
    });
  }

  private extractMessage(apiError: ApiError | null): string {
    if (apiError?.fieldErrors?.length) {
      return apiError.fieldErrors.map((f) => `${f.field}: ${f.message}`).join(' · ');
    }
    return apiError?.message ?? 'No se pudo guardar el producto.';
  }
}
