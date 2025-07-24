import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IComite } from '@core/models/eti/comite';
import { IMemoria } from '@core/models/eti/memoria';
import { IMemoriaResponse } from '@core/services/eti/memoria/memoria-response';
import { MEMORIA_RESPONSE_CONVERTER } from '@core/services/eti/memoria/memoria-response.converter';
import { environment } from '@env';
import { FindAllCtor, FindByIdCtor, mixinFindAll, mixinFindById, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { COMITE_RESPONSE_CONVERTER } from './comite/comite-response.converter';


// tslint:disable-next-line: variable-name
const _ComiteServiceMixinBase:
  FindByIdCtor<number, IComite, IComite> &
  FindAllCtor<IComite, IComite> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      SgiRestBaseService,
      COMITE_RESPONSE_CONVERTER
    ),
    COMITE_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root',
})
export class ComiteService extends _ComiteServiceMixinBase {
  private static readonly MAPPING = '/comites';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.eti}${ComiteService.MAPPING}`,
      http
    );
  }

  /**
   * Recupera la lista paginada de las  memorias en función del comité recibido.
   * @param idComite Identificador del comité.
   * @param idPeticionEvaluacion Identificador de la petición de evaluación.
   * @param options Opciones de búsqueda.
   */
  findMemoriasComitePeticionEvaluacion(idComite: number, idPeticionEvaluacion: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IMemoria>> {
    return this.find<IMemoriaResponse, IMemoria>(
      `${this.endpointUrl}/${idComite}/memorias-peticion-evaluacion/${idPeticionEvaluacion}`,
      options,
      MEMORIA_RESPONSE_CONVERTER
    );
  }
}
