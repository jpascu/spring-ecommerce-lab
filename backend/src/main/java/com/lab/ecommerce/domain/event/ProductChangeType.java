package com.lab.ecommerce.domain.event;

/**
 * Tipo de cambio ocurrido sobre un producto. Concepto de dominio, agnostico a
 * cualquier tecnologia de mensajeria o almacenamiento.
 */
public enum ProductChangeType {
  CREATED,
  UPDATED
}
