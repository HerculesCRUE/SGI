import { HttpClient } from '@angular/common/http';
import { SgiNoConversionConverter } from '@sgi/framework/core';
import { SgiReadOnlyMutableRestService } from './read-only-mutable.rest.service';

/**
 * Base service to consume REST endpoints of read only entites without transformation
 *
 * Contains the common operations.
 *
 * @template K type of ID
 * @template T type of return element
 *
 * @deprecated Use mixings
 */
export class SgiReadOnlyRestService<K extends number | string, T> extends SgiReadOnlyMutableRestService<K, T, T> {
  constructor(serviceName: string, endpointUrl: string, http: HttpClient) {
    super(serviceName, endpointUrl, http, new SgiNoConversionConverter<T, T>());
  }
}