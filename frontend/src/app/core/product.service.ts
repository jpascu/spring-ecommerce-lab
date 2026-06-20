import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PageResult, PriceQuoteResponse, Product, ProductRequest, QuoteRequest } from './models';

/** Acceso a los endpoints de productos y presupuestos del backend. */
@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api/products';

  list(page = 0, size = 10, sort = 'id', direction = 'asc'): Observable<PageResult<Product>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', sort)
      .set('direction', direction);
    return this.http.get<PageResult<Product>>(this.base, { params });
  }

  get(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.base}/${id}`);
  }

  create(body: ProductRequest): Observable<Product> {
    return this.http.post<Product>(this.base, body);
  }

  update(id: number, body: ProductRequest): Observable<Product> {
    return this.http.put<Product>(`${this.base}/${id}`, body);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  quote(id: number, body: QuoteRequest): Observable<PriceQuoteResponse> {
    return this.http.post<PriceQuoteResponse>(`${this.base}/${id}/quote`, body);
  }
}
