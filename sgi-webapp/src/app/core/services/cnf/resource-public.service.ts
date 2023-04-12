import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '@env';
import { SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ResourcePublicService extends SgiRestBaseService {
  private static readonly MAPPING = '/resources';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.cnf}${ResourcePublicService.PUBLIC_PREFIX}${ResourcePublicService.MAPPING}`,
      http
    );
  }

  /**
   * Genera la url para acceder al recurso
   * 
   * @param id identificador del recurso.
   */
  getUrlResource(id: string): string {
    return `${this.endpointUrl}/${id}`;
  }

  /**
   * Descarga el fichero.
   * 
   * @param id identificador del recurso.
   */
  download(id: string): Observable<Blob> {
    return this.http.get(`${this.endpointUrl}/${id}`, {
      headers: new HttpHeaders().set('Accept', 'application/octet-stream'), responseType: 'blob'
    });
  }

}
