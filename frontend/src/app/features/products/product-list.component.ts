import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { ProductService } from '../../core/product.service';
import { PageResult, Product } from '../../core/models';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [RouterLink, CurrencyPipe],
  template: `
    <div class="header-row">
      <h1>Productos</h1>
      @if (auth.isAdmin()) {
        <button routerLink="/products/new">+ Nuevo producto</button>
      }
    </div>

    @if (error()) {
      <div class="banner-error">{{ error() }}</div>
    }

    @if (loading()) {
      <p class="muted">Cargando…</p>
    } @else if (page(); as p) {
      @if (p.content.length === 0) {
        <div class="card">No hay productos todavía.</div>
      } @else {
        <div class="card table-card">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Categoría</th>
                <th class="num">Precio</th>
                <th class="num">Stock</th>
                <th class="actions">Acciones</th>
              </tr>
            </thead>
            <tbody>
              @for (product of p.content; track product.id) {
                <tr>
                  <td>{{ product.id }}</td>
                  <td>{{ product.name }}</td>
                  <td>{{ product.category }}</td>
                  <td class="num">{{ product.price | currency: 'EUR' }}</td>
                  <td class="num">{{ product.stock }}</td>
                  <td class="actions">
                    <button class="secondary sm" [routerLink]="['/products', product.id, 'quote']">
                      Presupuesto
                    </button>
                    @if (auth.isAdmin()) {
                      <button class="secondary sm" [routerLink]="['/products', product.id, 'edit']">
                        Editar
                      </button>
                      <button class="danger sm" (click)="remove(product)">Borrar</button>
                    }
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>

        <div class="pager">
          <button class="secondary" [disabled]="p.page === 0" (click)="go(p.page - 1)">
            ◀ Anterior
          </button>
          <span class="muted">Página {{ p.page + 1 }} de {{ p.totalPages || 1 }}</span>
          <button
            class="secondary"
            [disabled]="p.page + 1 >= p.totalPages"
            (click)="go(p.page + 1)"
          >
            Siguiente ▶
          </button>
        </div>
      }
    }
  `,
  styles: [
    `
      .header-row {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 1rem;
      }
      .table-card {
        padding: 0;
        overflow: hidden;
      }
      table {
        width: 100%;
        border-collapse: collapse;
      }
      th,
      td {
        padding: 0.7rem 1rem;
        text-align: left;
        border-bottom: 1px solid var(--border);
      }
      th {
        background: var(--surface-2);
        font-size: 0.8rem;
        text-transform: uppercase;
        color: var(--muted);
      }
      tr:last-child td {
        border-bottom: none;
      }
      .num {
        text-align: right;
      }
      .actions {
        text-align: right;
        white-space: nowrap;
      }
      .sm {
        padding: 0.35rem 0.6rem;
        font-size: 0.8rem;
        margin-left: 0.35rem;
      }
      .pager {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 1rem;
        margin-top: 1rem;
      }
    `,
  ],
})
export class ProductListComponent implements OnInit {
  readonly auth = inject(AuthService);
  private readonly service = inject(ProductService);

  readonly page = signal<PageResult<Product> | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  private readonly pageSize = 10;

  ngOnInit(): void {
    this.go(0);
  }

  go(pageIndex: number): void {
    this.loading.set(true);
    this.error.set(null);
    this.service.list(pageIndex, this.pageSize).subscribe({
      next: (result) => {
        this.page.set(result);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar el listado de productos.');
        this.loading.set(false);
      },
    });
  }

  remove(product: Product): void {
    if (!confirm(`¿Borrar "${product.name}"?`)) {
      return;
    }
    this.service.delete(product.id).subscribe({
      next: () => this.go(this.page()?.page ?? 0),
      error: () => this.error.set('No se pudo borrar el producto.'),
    });
  }
}
