import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { SgiConverter } from '@sgi/framework/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { SgiReadOnlyMutableRestService } from './read-only-mutable.rest.service';


/**
 * Base service to consume REST endpoints with support for transformation
 *
 * Contains the common operations.
 *
 * @template K type of ID
 * @template S type of rest response
 * @template T type of return element
 *
 * @deprecated Use mixings
 */
export abstract class SgiMutableRestService<K extends number | string, S, T> extends SgiReadOnlyMutableRestService<K, S, T> {

  /**
   *
   * @param serviceName The service name to appear in log
   * @param endpointRelativePath The endpoint relative URL path
   * @param http The HttpClient to use
   * @param converter The converter to use in transformations between rest response and returned type
   */
  constructor(serviceName: string, endpointUrl: string, http: HttpClient, converter: SgiConverter<S, T>) {
    super(serviceName, endpointUrl, http, converter);
  }

  /**
   * Create the element and return the persisted value
   *
   * @param element The element to create
   */
  public create(element: T): Observable<T> {
    return this.http.post<S>(this.endpointUrl, this.converter.fromTarget(element)).pipe(
      // TODO: Explore the use a global HttpInterceptor with or without a custom error
      catchError((error: HttpErrorResponse) => {
        // Log the error
        console.error(JSON.stringify(error));
        // Pass the error to subscribers. Anyway they would decide what to do with the error.
        return throwError(error);
      }),
      map(response => {
        return this.converter.toTarget(response);
      })
    );
  }

  /**
   * Update an element and return the persisted value
   *
   * @param id The ID of the element
   * @param element The element to update
   */
  public update(id: K, element: T): Observable<T> {
    return this.http.put<S>(`${this.endpointUrl}/${id}`, this.converter.fromTarget(element)).pipe(
      // TODO: Explore the use a global HttpInterceptor with or without a custom error
      catchError((error: HttpErrorResponse) => {
        // Pass the error to subscribers. Anyway they would decide what to do with the error.
        return throwError(error);
      }),
      map(response => {
        return this.converter.toTarget(response);
      })
    );
  }

  /**
   * Delete an element by their ID
   *
   * @param id The ID of the element
   */
  public deleteById(id: K) {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`).pipe(
      // TODO: Explore the use a global HttpInterceptor with or without a custom error
      catchError((error: HttpErrorResponse) => {
        // Pass the error to subscribers. Anyway they would decide what to do with the error.
        return throwError(error);
      })
    );
  }

  /**
   * Delete all elements
   */
  public deleteAll() {
    return this.http.delete<void>(`${this.endpointUrl}`).pipe(
      // TODO: Explore the use a global HttpInterceptor with or without a custom error
      catchError((error: HttpErrorResponse) => {
        // Pass the error to subscribers. Anyway they would decide what to do with the error.
        return throwError(error);
      })
    );
  }
}
