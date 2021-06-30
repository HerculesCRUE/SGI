import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_DOCUMENTO_CONVERTER } from '@core/converters/csp/proyecto-documento.converter';
import { IProyectoDocumentoBackend } from '@core/models/csp/backend/proyecto-documento-backend';
import { IProyectoDocumento } from '@core/models/csp/proyecto-documento';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProyectoDocumentoService extends SgiMutableRestService<number, IProyectoDocumentoBackend, IProyectoDocumento> {
  private static readonly MAPPING = '/proyectodocumentos';

  constructor(protected http: HttpClient) {
    super(
      ProyectoDocumentoService.name,
      `${environment.serviceServers.csp}${ProyectoDocumentoService.MAPPING}`,
      http,
      PROYECTO_DOCUMENTO_CONVERTER
    );
  }

  updateList(proyectoId: number, entities: IProyectoDocumento[]):
    Observable<IProyectoDocumento[]> {
    return this.http.patch<IProyectoDocumentoBackend[]>(
      `${this.endpointUrl}/${proyectoId}`,
      this.converter.fromTargetArray(entities)
    ).pipe(
      map(response => this.converter.toTargetArray(response))
    );
  }
}
