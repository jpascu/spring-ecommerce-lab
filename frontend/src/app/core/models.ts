/** Tipos que reflejan los DTOs de la API del backend. */

export interface TokenResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface Product {
  id: number;
  name: string;
  description: string | null;
  price: number;
  stock: number;
  category: string;
  createdAt: string;
  updatedAt: string;
}

export interface ProductRequest {
  name: string;
  description: string | null;
  price: number;
  stock: number;
  category: string;
}

export interface PageResult<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface QuoteRequest {
  tier: string;
  quantity: number;
  couponCode: string | null;
}

export interface PriceQuoteResponse {
  productId: number;
  unitPrice: number;
  quantity: number;
  subtotal: number;
  discount: number;
  total: number;
  appliedStrategy: string;
}

/** Formato de error estándar (ApiError) que devuelve el backend. */
export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  traceId: string | null;
  fieldErrors: { field: string; message: string }[];
}
