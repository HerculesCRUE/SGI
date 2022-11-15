import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IPrograma } from '@core/models/csp/programa';
import { environment } from '@env';
import { SgiRestBaseService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http/';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProgramaPublicService extends SgiRestBaseService {

  private static readonly MAPPING = '/programas';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProgramaPublicService.PUBLIC_PREFIX}${ProgramaPublicService.MAPPING}`,
      http
    );
  }

  findAllHijosPrograma(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IPrograma>> {
    return this.find<IPrograma, IPrograma>(`${this.endpointUrl}/${id}/hijos`, options);
  }

}
