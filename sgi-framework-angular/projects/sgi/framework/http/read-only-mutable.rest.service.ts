import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams, HttpResponse } from '@angular/common/http';
import { SgiConverter } from '@sgi/framework/core';
import { Observable, of, throwError } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { SgiRestFilter } from './filter';
import { SgiRestSort } from './sort';
import { SgiRestFindOptions, SgiRestListResult, SgiRestPageRequest } from './types';


/**
 * Base service to consume REST endpoints of read only entities with support for transformation
 *
 * Contains the common operations.
 *
 * @template K type of ID
 * @template S type of rest response
 * @template T type of return element
 *
 * @deprecated Use mixings
 */
export abstract class SgiReadOnlyMutableRestService<K extends number | string, S, T> {
  /** The HttpClient to use in request */
  protected readonly http: HttpClient;
  /** The REST Endpoint URL common for all service operations */
  protected readonly endpointUrl: string;
  /** The converter for requests */
  protected readonly converter: SgiConverter<S, T>;
  /** The Service Name to log */
  protected readonly serviceName: string;

  /**
   *
   * @param serviceName The service name to appear in log
   * @param endpointRelativePath The endpoint relative URL path
   * @param http The HttpClient to use
   * @param converter The converter to use in transformations between rest response and returned type
   */
  constructor(serviceName: string, endpointUrl: string, http: HttpClient, converter: SgiConverter<S, T>) {
    this.serviceName = serviceName;
    this.endpointUrl = endpointUrl;
    this.http = http;
    this.converter = converter;
  }

  /**
   * Find an element by their ID
   *
   * @param id The ID of the element
   */
  // TODO: Manage 404 (NotFound) and return an empty element?
  public findById(id: K): Observable<T> {
    return this.http.get<S>(`${this.endpointUrl}/${id}`).pipe(
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
   * Find a list of elements. Optionally, the elements can be requested combining pagination, sorting and filtering
   *
   * @param options The options to apply
   */
  public findAll(options?: SgiRestFindOptions): Observable<SgiRestListResult<T>> {
    return this.find<S, T>(this.endpointUrl, options, this.converter);
  }

  /**
   * Find a list of elements on a endpoint. Optionally, the elements can be requested combining pagination, sorting and filtering
   *
   * @param endpointUrl The url of the endpoint
   * @param options The options to apply
   * @param converter The converter to use
   */
  protected find<U, V>(endpointUrl: string, options?: SgiRestFindOptions, converter?: SgiConverter<U, V>):
    Observable<SgiRestListResult<V>> {
    return this.http.get<U[]>(endpointUrl, this.buildHttpClientOptions(options))
      .pipe(
        // TODO: Explore the use a global HttpInterceptor with or without a custom error
        catchError((error: HttpErrorResponse) => {
          // Pass the error to subscribers. Anyway they would decide what to do with the error.
          return throwError(error);
        }),
        switchMap(r => {
          return this.toSgiRestListResult<U, V>(r, converter);
        })
      );
  }

  private getCommonHeaders(): HttpHeaders {
    return new HttpHeaders().set('Accept', 'application/json');
  }

  /**
   * Build the request headers to use
   * @param pageRequest Optional page request to use
   */
  private getRequestHeaders(pageRequest?: SgiRestPageRequest): HttpHeaders {
    let headers = this.getCommonHeaders();
    if (pageRequest) {
      if (pageRequest.size) {
        headers = headers.set('X-Page-Size', pageRequest.size.toString());
        headers = headers.set('X-Page', pageRequest.index ? pageRequest.index.toString() : '0');
      }
    }
    return headers;
  }

  private getSearchParam(sort: SgiRestSort, filter: SgiRestFilter): HttpParams {
    let param = new HttpParams();
    if (filter) {
      const filterString = filter.toString();
      if (filterString.length) {
        param = param.append('q', filter.toString());
      }
    }
    if (sort) {
      const sortString = sort.toString();
      if (sortString.length) {
        param = param.append('s', sort.toString());
      }
    }

    return param;
  }

  /**
   * Builds options for a HttpClient to make a find by request
   *
   * @param options SgiRestOptions to apply
   */
  private buildHttpClientOptions(options?: SgiRestFindOptions): {
    headers?: HttpHeaders | {
      [header: string]: string | string[];
    };
    observe: 'response';
    params?: HttpParams | {
      [param: string]: string | string[];
    };
  } {
    return {
      headers: this.getRequestHeaders(options?.page),
      params: this.getSearchParam(options?.sort, options?.filter),
      observe: 'response'
    };
  }

  /**
   * Convert a findAll http response to a list result
   *
   * @param response The response to convert
   * @param converter The converter to use
   */
  private toSgiRestListResult<U, V>(response: HttpResponse<U[]>, converter?: SgiConverter<U, V>): Observable<SgiRestListResult<V>> {
    let items = response.body;
    if (!items) {
      items = [];
    }
    const xPage = response.headers.get('X-Page');
    const xPageSize = response.headers.get('X-Page-Size');
    const xPageCount = response.headers.get('X-Page-Count');
    const xPageTotalCount = response.headers.get('X-Page-Total-Count');
    const xTotalCount = response.headers.get('X-Total-Count');
    return of({
      page: {
        index: xPage ? Number(xPage) : 0,
        size: xPageSize ? Number(xPageSize) : 0,
        count: xPageCount ? Number(xPageCount) : 0,
        total: xPageTotalCount ? Number(xPageTotalCount) : 0,
      },
      total: xTotalCount ? Number(xTotalCount) : 0,
      items: converter ? converter.toTargetArray(items) : items as unknown as V[]
    });
  }
}
