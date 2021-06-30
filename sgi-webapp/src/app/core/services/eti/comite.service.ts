import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MEMORIA_CONVERTER } from '@core/converters/eti/memoria.converter';
import { IMemoriaBackend } from '@core/models/eti/backend/memoria-backend';
import { IComite } from '@core/models/eti/comite';
import { IMemoria } from '@core/models/eti/memoria';
import { ITipoMemoria } from '@core/models/eti/tipo-memoria';
import { environment } from '@env';
import { SgiReadOnlyRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ComiteService extends SgiReadOnlyRestService<number, IComite> {
  private static readonly MAPPING = '/comites';

  constructor(protected http: HttpClient) {
    super(
      ComiteService.name,
      `${environment.serviceServers.eti}${ComiteService.MAPPING}`,
      http
    );
  }

  /**
   * Recupera la lista paginada de los tipos de memoria en función del comité recibido.
   * @param id Identificador del comité.
   * @param options Opciones de búsqueda.
   */
  findTipoMemoria(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoMemoria>> {
    return this.find<ITipoMemoria, ITipoMemoria>(`${this.endpointUrl}/${id}/tipo-memorias`, options);
  }

  /**
   * Recupera la lista paginada de las  memorias en función del comité recibido.
   * @param id Identificador del comité.
   * @param options Opciones de búsqueda.
   */
  findMemorias(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IMemoria>> {
    return this.find<IMemoriaBackend, IMemoria>(
      `${this.endpointUrl}/${id}/memorias`,
      options,
      MEMORIA_CONVERTER
    );
  }
}
