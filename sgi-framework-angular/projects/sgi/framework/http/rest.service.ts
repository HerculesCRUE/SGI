import { HttpClient } from '@angular/common/http';
import { SgiNoConversionConverter } from '@sgi/framework/core';
import { deprecate } from 'util';
import { SgiMutableRestService } from './mutable.rest.service';

/**
 * Base service to consume REST endpoints without transformation
 *
 * Contains the common operations.
 *
 * @template K type of ID
 * @template T type of return element
 *
 * @deprecated Use mixings
 */
export class SgiRestService<K extends number | string, T> extends SgiMutableRestService<K, T, T> {
  constructor(serviceName: string, endpointUrl: string, http: HttpClient) {
    super(serviceName, endpointUrl, http, new SgiNoConversionConverter<T, T>());
  }
}