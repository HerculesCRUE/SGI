import { HttpClient, HttpHeaders, HttpParams, HttpResponse } from '@angular/common/http';
import { SgiConverter } from '@sgi/framework/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SgiRestFilter } from './filter';
import { SgiRestSort } from './sort';
import { SgiRestFindOptions, SgiRestListResult, SgiRestPageRequest } from './types';

/**
 * Base Service template for Service implementations
 */
export class SgiRestBaseService {

  /**
   * @param endpointUrl Base URL of the service
   * @param http HTTP client
   */
  constructor(
    protected readonly endpointUrl: string,
    protected readonly http: HttpClient
  ) { }

  /**
   * POST an entity to an endpoint url and return the result
   *
   * @template I type of resquest
   * @template O type of response
   *
   * @param endpointUrl The url of the endpoint
   * @param entity The entity to post
   * @return The result of the post
   */
  protected post<I, O>(endpointUrl: string, entity: I): Observable<O> {
    return this.http.post<O>(endpointUrl, entity);
  }

  /**
   * PUT an entity to an endpoint url and return the result
   *
   * @template I type of resquest
   * @template O type of response
   *
   * @param endpointUrl The url of the endpoint
   * @param entity The entity to put
   * @return The result of the put
   */
  protected put<I, O>(endpointUrl: string, entity: I): Observable<O> {
    return this.http.put<O>(endpointUrl, entity);
  }

  /**
   * GET an entity from an endpoint
   *
   * @template O type of response
   *
   * @param endpointUrl The url of the endpoint
   * @return The result of the get
   */
  protected get<O>(endpointUrl: string): Observable<O> {
    return this.http.get<O>(endpointUrl);
  }

  /**
   * Find a list of entities from an endpoint url. Optionally, the entity can be requested combining pagination, sorting and filtering
   *
   * @template RO type of response
   * @template O type of return entity
   *
   * @param endpointUrl The url of the endpoint
   * @param options The options to apply
   * @param converter The converter to use. Optionally
   *
   * @returns SgiRestListResult with the content of the response
   */
  protected find<RO, O>(endpointUrl: string, options?: SgiRestFindOptions, converter?: SgiConverter<RO, O>):
    Observable<SgiRestListResult<O>> {
    return this.http.get<RO[]>(endpointUrl, this.buildHttpClientOptions(options))
      .pipe(
        map(r => {
          return this.toSgiRestListResult<RO, O>(r, converter);
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
  private toSgiRestListResult<U, V>(response: HttpResponse<U[]>, converter?: SgiConverter<U, V>): SgiRestListResult<V> {
    let items = response.body;
    if (!items) {
      items = [];
    }
    const xPage = response.headers.get('X-Page');
    const xPageSize = response.headers.get('X-Page-Size');
    const xPageCount = response.headers.get('X-Page-Count');
    const xPageTotalCount = response.headers.get('X-Page-Total-Count');
    const xTotalCount = response.headers.get('X-Total-Count');
    return {
      page: {
        index: xPage ? Number(xPage) : 0,
        size: xPageSize ? Number(xPageSize) : 0,
        count: xPageCount ? Number(xPageCount) : 0,
        total: xPageTotalCount ? Number(xPageTotalCount) : 0,
      },
      total: xTotalCount ? Number(xTotalCount) : 0,
      items: converter ? converter.toTargetArray(items) : items as unknown as V[]
    };
  }
}
