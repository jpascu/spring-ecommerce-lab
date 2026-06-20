import { Routes } from '@angular/router';
import { authGuard } from './core/auth.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'products' },
  {
    path: 'login',
    loadComponent: () => import('./features/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'products',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/products/product-list.component').then((m) => m.ProductListComponent),
  },
  {
    path: 'products/new',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/products/product-form.component').then((m) => m.ProductFormComponent),
  },
  {
    path: 'products/:id/edit',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/products/product-form.component').then((m) => m.ProductFormComponent),
  },
  {
    path: 'products/:id/quote',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/products/quote.component').then((m) => m.QuoteComponent),
  },
  { path: '**', redirectTo: 'products' },
];
