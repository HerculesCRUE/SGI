import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoEnlace } from '@core/models/csp/grupo-enlace';
import { IGrupoEquipo } from '@core/models/csp/grupo-equipo';
import { IGrupoEquipoInstrumental } from '@core/models/csp/grupo-equipo-instrumental';
import { IGrupoEspecialInvestigacion } from '@core/models/csp/grupo-especial-investigacion';
import { IGrupoLineaInvestigacion } from '@core/models/csp/grupo-linea-investigacion';
import { IGrupoPalabraClave } from '@core/models/csp/grupo-palabra-clave';
import { IGrupoPersonaAutorizada } from '@core/models/csp/grupo-persona-autorizada';
import { IGrupoResponsableEconomico } from '@core/models/csp/grupo-responsable-economico';
import { IGrupoTipo } from '@core/models/csp/grupo-tipo';
import { ISolicitud } from '@core/models/csp/solicitud';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { environment } from '@env';
import {
  CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate,
  RSQLSgiRestFilter,
  SgiRestBaseService, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult, UpdateCtor
} from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { from, Observable } from 'rxjs';
import { map, mergeMap, reduce } from 'rxjs/operators';
import { IGrupoEnlaceResponse } from '../grupo-enlace/grupo-enlace-response';
import { GRUPO_ENLACE_RESPONSE_CONVERTER } from '../grupo-enlace/grupo-enlace-response.converter';
import { IGrupoEquipoInstrumentalResponse } from '../grupo-equipo-instrumental/grupo-equipo-instrumental-response';
import { GRUPO_EQUIPO_INSTRUMENTAL_RESPONSE_CONVERTER } from '../grupo-equipo-instrumental/grupo-equipo-instrumental-response.converter';
import { IGrupoEquipoResponse } from '../grupo-equipo/grupo-equipo-response';
import { GRUPO_EQUIPO_RESPONSE_CONVERTER } from '../grupo-equipo/grupo-equipo-response.converter';
import { IGrupoEspecialInvestigacionResponse } from '../grupo-especial-investigacion/grupo-especial-investigacion-response';
import { GRUPO_ESPECIAL_INVESTIGACION_RESPONSE_CONVERTER } from '../grupo-especial-investigacion/grupo-especial-investigacion-response.converter';
import { IGrupoLineaInvestigacionResponse } from '../grupo-linea-investigacion/grupo-linea-investigacion-response';
import { GRUPO_LINEA_INVESTIGACION_RESPONSE_CONVERTER } from '../grupo-linea-investigacion/grupo-linea-investigacion-response.converter';
import { GRUPO_PALABRACLAVE_REQUEST_CONVERTER } from '../grupo-palabra-clave/grupo-palabra-clave-request.converter';
import { IGrupoPalabraClaveResponse } from '../grupo-palabra-clave/grupo-palabra-clave-response';
import { GRUPO_PALABRACLAVE_RESPONSE_CONVERTER } from '../grupo-palabra-clave/grupo-palabra-clave-response.converter';
import { IGrupoPersonaAutorizadaResponse } from '../grupo-persona-autorizada/grupo-persona-autorizada-response';
import { GRUPO_PERSONA_AUTORIZADA_RESPONSE_CONVERTER } from '../grupo-persona-autorizada/grupo-persona-autorizada-response.converter';
import { IGrupoResponsableEconomicoResponse } from '../grupo-responsable-economico/grupo-responsable-economico-response';
import { GRUPO_RESPONSABLE_ECONOMICO_RESPONSE_CONVERTER } from '../grupo-responsable-economico/grupo-responsable-economico-response.converter';
import { IGrupoTipoResponse } from '../grupo-tipo/grupo-tipo-response';
import { GRUPO_TIPO_RESPONSE_CONVERTER } from '../grupo-tipo/grupo-tipo-response.converter';
import { ISolicitudResumenResponse } from '../solicitud/solicitud-resumen-response';
import { SOLICITUD_RESUMEN_RESPONSE_CONVERTER } from '../solicitud/solicitud-resumen-response.converter';
import { IGrupoRequest } from './grupo-request';
import { GRUPO_REQUEST_CONVERTER } from './grupo-request.converter';
import { IGrupoResponse } from './grupo-response';
import { GRUPO_RESPONSE_CONVERTER } from './grupo-response.converter';

// tslint:disable-next-line: variable-name
const _GrupoMixinBase:
  CreateCtor<IGrupo, IGrupo, IGrupoRequest, IGrupoResponse> &
  UpdateCtor<number, IGrupo, IGrupo, IGrupoRequest, IGrupoResponse> &
  FindByIdCtor<number, IGrupo, IGrupoResponse> &
  FindAllCtor<IGrupo, IGrupoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          GRUPO_REQUEST_CONVERTER,
          GRUPO_RESPONSE_CONVERTER
        ),
        GRUPO_REQUEST_CONVERTER,
        GRUPO_RESPONSE_CONVERTER
      ),
      GRUPO_RESPONSE_CONVERTER
    ),
    GRUPO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class GrupoService extends _GrupoMixinBase {
  private static readonly MAPPING = '/grupos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${GrupoService.MAPPING}`,
      http,
    );
  }

  /**
   * Busca todas las grupos que tengan alguno de los ids de la lista
   *
   * @param ids lista de identificadores de grupos
   * @returns la lista de grupos
   */
  findTodosByIdIn(ids: number[]): Observable<SgiRestListResult<IGrupo>> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('id', SgiRestFilterOperator.IN, ids.map(id => id.toString()))
    };

    return this.findTodos(options);
  }

  /**
   * Busca todos los grupos que tengan alguno de los ids de la lista,
   * dividiendo la lista de ids en lotes con el tamaño maximo de batchSize 
   * y haciendo tantas peticiones como lotes se generen para hacer la busqueda
   *
   * @param ids lista de identificadores de grupo
   * @param batchSize tamaño maximo de los lotes
   * @param maxConcurrentBatches número máximo de llamadas paralelas para recuperar los lotes (por defecto 10)
   * @returns la lista de grupos
   */
  findTodosInBactchesByIdIn(ids: number[], batchSize: number, maxConcurrentBatches: number = 10): Observable<IGrupo[]> {
    const batches: number[][] = [];
    for (let i = 0; i < ids.length; i += batchSize) {
      batches.push(ids.slice(i, i + batchSize));
    }

    return from(batches).pipe(
      mergeMap(batch =>
        this.findTodosByIdIn(batch).pipe(
          map(response => response.items)
        ),
        maxConcurrentBatches
      ),
      reduce((acc, items) => acc.concat(items), [] as IGrupo[])
    );
  }

  /**
   * Muestra activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IGrupo>> {
    return this.find<IGrupoResponse, IGrupo>(
      `${this.endpointUrl}/todos`,
      options,
      GRUPO_RESPONSE_CONVERTER
    );
  }

  /**
   * Muestra los grupos a los que pertenece el investigador actual
   *
   * @param options opciones de búsqueda.
   */
  findGruposInvestigador(options?: SgiRestFindOptions): Observable<SgiRestListResult<IGrupo>> {
    return this.find<IGrupoResponse, IGrupo>(
      `${this.endpointUrl}/investigador`,
      options,
      GRUPO_RESPONSE_CONVERTER
    );
  }

  /**
   * Comprueba si existe el grupo
   *
   * @param id Identificador del grupo
   */
  exists(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Activa el grupo
   *
   * @param id Identificador del grupo
   */
  activar(id: number): Observable<void> {
    const url = `${this.endpointUrl}/${id}/activar`;
    return this.http.patch<void>(url, { id });
  }

  /**
   * Desactiva el grupo
   *
   * @param id Identificador del grupo
   */
  desactivar(id: number): Observable<void> {
    const url = `${this.endpointUrl}/${id}/desactivar`;
    return this.http.patch<void>(url, { id });
  }

  /**
   * Recupera la lista de miembros del equipo del grupo
   *
   * @param id Identificador del grupo
   * @param options opciones de búsqueda.
   */
  findMiembrosEquipo(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IGrupoEquipo>> {
    return this.find<IGrupoEquipoResponse, IGrupoEquipo>(
      `${this.endpointUrl}/${id}/miembrosequipo`,
      options,
      GRUPO_EQUIPO_RESPONSE_CONVERTER
    );
  }

  /**
   * GrupoEquipo que son investigador o investigadores principales del
   * grupo con el id indicado.
   * Se considera investiador principal a la GrupoEquipo que a fecha actual
   * tiene el rol Proyecto con el flag "principal" a
   * true. En caso de existir mas de una GrupoEquipo principal, se
   * recupera el que tenga el mayor porcentaje de dedicación al grupo (campo
   * "participación").
   * Y en caso de que varios coincidan se devuelven todos los que coincidan.
   *
   * @param id identificador del grupo.
   * @return la lista de personaRef de los investigadores principales del
   *         grupo en el momento actual.
   */
  findPersonaRefInvestigadoresPrincipalesWithMaxParticipacion(id: number): Observable<string[]> {
    return this.get<string[]>(`${this.endpointUrl}/${id}/investigadoresprincipalesmaxparticipacion`);
  }

  /**
   * GrupoEquipo que son investigador o investigadores principales del
   * grupo con el id indicado.
   * Se considera investiador principal a la GrupoEquipo que a fecha actual
   * tiene el rol Proyecto con el flag "principal" a
   * true. En caso de que varios coincidan se devuelven todos los que coincidan.
   *
   * @param id identificador del grupo.
   * @return la lista de investigadores principales del grupo en el momento actual.
   */
  findInvestigadoresPrincipales(id: number): Observable<IGrupoEquipo[]> {
    return this.get<IGrupoEquipoResponse[]>(`${this.endpointUrl}/${id}/investigadoresprincipales`).pipe(
      map((response => GRUPO_EQUIPO_RESPONSE_CONVERTER.toTargetArray(response ?? [])))
    );
  }

  getNextCodigo(departamentoRef: string): Observable<string> {
    const url = `${this.endpointUrl}/nextcodigo`;
    let params = new HttpParams();
    params = params.append('departamentoRef', departamentoRef);
    return this.http.get<string>(url, { params });
  }

  /**
   * Comprueba si el codigo es valido
   *
   * @param codigo Codigo que se quiere comprobar
   * @param grupoId Identificador del grupo en el que se esta modificando el codigo
   */
  isDuplicatedCodigo(codigo: string, grupoId?: number): Observable<boolean> {
    const url = `${this.endpointUrl}/codigoduplicado`;
    let params = new HttpParams();
    params = params.append('codigo', codigo);

    if (grupoId) {
      params = params.append('grupoId', grupoId.toString());
    }

    return this.http.head(url, { params, observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Recupera las Palabras Clave asociadas al grupo con el id indicado.
   *
   * @param id del grupo
   */
  findPalabrasClave(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IGrupoPalabraClave>> {
    return this.find<IGrupoPalabraClaveResponse, IGrupoPalabraClave>(
      `${this.endpointUrl}/${id}/palabrasclave`,
      options,
      GRUPO_PALABRACLAVE_RESPONSE_CONVERTER);
  }

  /**
   * Actualiza las Palabras Clave  asociadas al grupo con el id indicado.
   *
   * @param id Identificador del grupo
   * @param palabrasClave Palabras Clave a actualizar
   */
  updatePalabrasClave(id: number, palabrasClave: IGrupoPalabraClave[]): Observable<IGrupoPalabraClave[]> {
    return this.http.patch<IGrupoPalabraClaveResponse[]>(`${this.endpointUrl}/${id}/palabrasclave`,
      GRUPO_PALABRACLAVE_REQUEST_CONVERTER.fromTargetArray(palabrasClave)
    ).pipe(
      map((response => GRUPO_PALABRACLAVE_RESPONSE_CONVERTER.toTargetArray(response)))
    );
  }

  /**
   * Recupera la lista de tipos del grupo
   *
   * @param id Identificador del grupo
   * @param options opciones de búsqueda.
   */
  findTipos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IGrupoTipo>> {
    return this.find<IGrupoTipoResponse, IGrupoTipo>(
      `${this.endpointUrl}/${id}/tipos`,
      options,
      GRUPO_TIPO_RESPONSE_CONVERTER
    );
  }

  /**
   * Recupera la lista de especiales de investigación
   *
   * @param id Identificador del grupo
   * @param options opciones de búsqueda.
   */
  findEspecialesInvestigacion(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IGrupoEspecialInvestigacion>> {
    return this.find<IGrupoEspecialInvestigacionResponse, IGrupoEspecialInvestigacion>(
      `${this.endpointUrl}/${id}/especiales-investigacion`,
      options,
      GRUPO_ESPECIAL_INVESTIGACION_RESPONSE_CONVERTER
    );
  }

  /**
   * Comprueba si tiene permisos de edición del grupo
   *
   * @param id Id del grupo
   */
  modificable(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/modificable`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Recupera la lista de equipos instrumentales
   *
   * @param id Identificador del grupo
   * @param options opciones de búsqueda.
   */
  findEquiposInstrumentales(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IGrupoEquipoInstrumental>> {
    return this.find<IGrupoEquipoInstrumentalResponse, IGrupoEquipoInstrumental>(
      `${this.endpointUrl}/${id}/equipos-instrumentales`,
      options,
      GRUPO_EQUIPO_INSTRUMENTAL_RESPONSE_CONVERTER
    );
  }

  /**
   * Recupera la lista de responsables económicos
   * @param id Identificador del grupo
   * @param options opciones de búsqueda.
   */
  findResponsablesEconomicos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IGrupoResponsableEconomico>> {
    return this.find<IGrupoResponsableEconomicoResponse, IGrupoResponsableEconomico>(
      `${this.endpointUrl}/${id}/responsables-economicos`,
      options,
      GRUPO_RESPONSABLE_ECONOMICO_RESPONSE_CONVERTER
    );
  }

  /**
   * Recupera la lista de enlaces
   * @param id Identificador del grupo
   * @param options opciones de búsqueda.
   */
  findEnlaces(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IGrupoEnlace>> {
    return this.find<IGrupoEnlaceResponse, IGrupoEnlace>(
      `${this.endpointUrl}/${id}/enlaces`,
      options,
      GRUPO_ENLACE_RESPONSE_CONVERTER
    );
  }

  /**
   * Recupera la lista de personas autorizadas
   * @param id Identificador del grupo
   * @param options opciones de búsqueda.
   */
  findPersonasAutorizadas(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IGrupoPersonaAutorizada>> {
    return this.find<IGrupoPersonaAutorizadaResponse, IGrupoPersonaAutorizada>(
      `${this.endpointUrl}/${id}/personas-autorizadas`,
      options,
      GRUPO_PERSONA_AUTORIZADA_RESPONSE_CONVERTER
    );
  }

  /**
   * Recupera la lista de líneas de investigación
   * @param id Identificador del grupo
   * @param options opciones de búsqueda.
   */
  findLineasInvestigacion(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IGrupoLineaInvestigacion>> {
    return this.find<IGrupoLineaInvestigacionResponse, IGrupoLineaInvestigacion>(
      `${this.endpointUrl}/${id}/lineas-investigacion`,
      options,
      GRUPO_LINEA_INVESTIGACION_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve los datos de la solicitud del grupo
   *
   * @param id Id del grupo
   */
  findSolicitud(id: number): Observable<ISolicitud> {
    return this.http.get<ISolicitudResumenResponse>(
      `${this.endpointUrl}/${id}/solicitud`
    ).pipe(
      map(response => SOLICITUD_RESUMEN_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  /**
 * Muestra los grupos a los que pertenece el investigador actual
 *
 * @param personaRef Identificador de la persona
 * @param fechaIinicio fecha de inicio de participacion de la persona
 * @param fechaFin fecha de fin de participacion de la persona
 */
  findGruposPersona(personaRef: string, fechaInicio?: DateTime, fechaFin?: DateTime): Observable<IGrupo[]> {
    let params = new HttpParams();
    params = params.append('personaRef', personaRef);

    if (fechaInicio) {
      params = params.append('fechaInicio', LuxonUtils.toBackend(fechaInicio));
    }

    if (fechaFin) {
      params = params.append('fechaFin', LuxonUtils.toBackend(fechaFin));
    }

    return this.http.get<IGrupoResponse[]>(
      `${this.endpointUrl}/persona`,
      { params }
    ).pipe(
      map(response => response ? GRUPO_RESPONSE_CONVERTER.toTargetArray(response) : [])
    );
  }

}
