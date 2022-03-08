import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ICampoProduccionCientifica } from '@core/models/prc/campo-produccion-cientifica';
import { IProduccionCientifica } from '@core/models/prc/produccion-cientifica';
import { IValorCampo } from '@core/models/prc/valor-campo';
import { environment } from '@env';
import {
  FindAllCtor, FindByIdCtor,
  mixinFindAll, mixinFindById,
  RSQLSgiRestFilter, SgiRestBaseService,
  SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult,
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IValorCampoResponse } from '../valor-campo/valor-campo-response';
import { VALOR_CAMPO_RESPONSE_CONVERTER } from '../valor-campo/valor-campo-response.converter';
import { ICampoProduccionCientificaResponse } from './campo-produccion-cientifica-response';
import { CAMPO_PRODUCCION_CIENTIFICA_RESPONSE_CONVERTER } from './campo-produccion-cientifica-response.converter';

// tslint:disable-next-line: variable-name
const _CampoProduccionCientificaServiceMixinBase:
  FindAllCtor<ICampoProduccionCientifica, ICampoProduccionCientificaResponse> &
  FindByIdCtor<number, ICampoProduccionCientifica, ICampoProduccionCientificaResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      SgiRestBaseService,
      CAMPO_PRODUCCION_CIENTIFICA_RESPONSE_CONVERTER),
    CAMPO_PRODUCCION_CIENTIFICA_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class CampoProduccionCientificaService extends _CampoProduccionCientificaServiceMixinBase {

  private static readonly MAPPING = '/campos-producciones-cientificas';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.prc}${CampoProduccionCientificaService.MAPPING}`,
      http,
    );
  }

  findAllCamposProduccionCientifca(produccionCientifica: IProduccionCientifica): Observable<ICampoProduccionCientifica[]> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('produccionCientificaId', SgiRestFilterOperator.EQUALS, produccionCientifica.id?.toString())
    };
    return this.findAll(options).pipe(
      map(response => response.items)
    );
  }

  /**
   * Obtiene todos los Valores de un Campo de producción científica dado el id
   * @param id id del Campo de producción científica
   * @param options opciones de busqueda
   * @returns Valores de un Campo de producción científica
   */
  findValores(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IValorCampo>> {
    return this.find<IValorCampoResponse, IValorCampo>(
      `${this.endpointUrl}/${id}/valores`,
      options,
      VALOR_CAMPO_RESPONSE_CONVERTER);
  }
}
