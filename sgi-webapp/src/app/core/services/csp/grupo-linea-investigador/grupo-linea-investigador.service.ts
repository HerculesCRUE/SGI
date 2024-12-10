import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IGrupoLineaInvestigador } from '@core/models/csp/grupo-linea-investigador';
import { environment } from '@env';
import { FindAllCtor, FindByIdCtor, mixinFindAll, mixinFindById, RSQLSgiRestFilter, SgiRestBaseService, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { from, Observable } from 'rxjs';
import { map, mergeMap, reduce } from 'rxjs/operators';
import { GRUPO_LINEA_INVESTIGADOR_REQUEST_CONVERTER } from './grupo-linea-investigador-request.converter';
import { IGrupoLineaInvestigadorResponse } from './grupo-linea-investigador-response';
import { GRUPO_LINEA_INVESTIGADOR_RESPONSE_CONVERTER } from './grupo-linea-investigador-response.converter';

// tslint:disable-next-line: variable-name
const _GrupoLineaInvestigadorMixinBase:
  FindByIdCtor<number, IGrupoLineaInvestigador, IGrupoLineaInvestigadorResponse> &
  FindAllCtor<IGrupoLineaInvestigador, IGrupoLineaInvestigadorResponse> &
  typeof SgiRestBaseService = mixinFindById(
    mixinFindAll(
      SgiRestBaseService,
      GRUPO_LINEA_INVESTIGADOR_RESPONSE_CONVERTER
    ),
    GRUPO_LINEA_INVESTIGADOR_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class GrupoLineaInvestigadorService extends _GrupoLineaInvestigadorMixinBase {
  private static readonly MAPPING = '/gruposlineasinvestigadores';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${GrupoLineaInvestigadorService.MAPPING}`,
      http,
    );
  }

  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

  /**
   * Actualiza el listado de GrupoLineaInvestigador asociados a un grupo
   *
   * @param id Id del IGrupoLineaInvestigador
   * @param entities Listado de IGrupoLineaInvestigador
   */
  updateList(id: number, entities: IGrupoLineaInvestigador[]): Observable<IGrupoLineaInvestigador[]> {
    return this.http.patch<IGrupoLineaInvestigadorResponse[]>(
      `${this.endpointUrl}/${id}`,
      GRUPO_LINEA_INVESTIGADOR_REQUEST_CONVERTER.fromTargetArray(entities)
    ).pipe(
      map(response => GRUPO_LINEA_INVESTIGADOR_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }

  /**
   * Busca todos las participaciones de los investigadores en las lineas de los grupos
   * 
   * @param personaRefs lista de identificadores de personas
   * @returns la lista de investigadores
   */
  findAllByPersonaRefIn(personaRefs: string[]): Observable<SgiRestListResult<IGrupoLineaInvestigador>> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('personaRef', SgiRestFilterOperator.IN, personaRefs)
    };

    return this.findAll(options);
  }

  /**
   * Busca todos las participaciones de los investigadores en las lineas de los grupos
   * dividiendo la lista de ids en lotes con el tamaño maximo de batchSize 
   * y haciendo tantas peticiones como lotes se generen para hacer la busqueda
   *
   * @param personaRefs lista de identificadores de persona
   * @param batchSize tamaño maximo de los lotes
   * @param maxConcurrentBatches número máximo de llamadas paralelas para recuperar los lotes (por defecto 10)
   * @returns la lista de investigadores
   */
  findAllInBactchesByPersonaRefIn(personaRefs: string[], batchSize: number, maxConcurrentBatches: number = 10): Observable<IGrupoLineaInvestigador[]> {
    const batches: string[][] = [];
    for (let i = 0; i < personaRefs.length; i += batchSize) {
      batches.push(personaRefs.slice(i, i + batchSize));
    }

    return from(batches).pipe(
      mergeMap(batch =>
        this.findAllByPersonaRefIn(batch).pipe(
          map(response => response.items)
        ),
        maxConcurrentBatches
      ),
      reduce((acc, items) => acc.concat(items), [] as IGrupoLineaInvestigador[])
    );
  }

}
