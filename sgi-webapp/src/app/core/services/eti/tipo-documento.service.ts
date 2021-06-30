import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FORMULARIO } from '@core/models/eti/formulario';
import { ITipoDocumento } from '@core/models/eti/tipo-documento';
import { environment } from '@env';
import { SgiReadOnlyRestService } from '@sgi/framework/http/';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class TipoDocumentoService extends SgiReadOnlyRestService<number, ITipoDocumento> {
  private static readonly MAPPING = '/tipodocumentos';

  constructor(protected http: HttpClient) {
    super(
      TipoDocumentoService.name,
      `${environment.serviceServers.eti}${TipoDocumentoService.MAPPING}`,
      http
    );
  }

  findByFormulario(formulario: FORMULARIO): Observable<ITipoDocumento[]> {
    return this.http.get<ITipoDocumento[]>(
      `${this.endpointUrl}/formulario/${formulario}`
    ).pipe(
      map(response => response)
    );
  }

}
