import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_ENTIDAD_CONVOCANTE_CONVERTER } from '@core/converters/csp/convocatoria-entidad-convocante.converter';
import { CONVOCATORIA_ENTIDAD_FINANCIADORA_CONVERTER } from '@core/converters/csp/convocatoria-entidad-financiadora.converter';
import { CONVOCATORIA_CONVERTER } from '@core/converters/csp/convocatoria.converter';
import { ESTADO_SOLICITUD_CONVERTER } from '@core/converters/csp/estado-solicitud.converter';
import { SOLICITUD_DOCUMENTO_CONVERTER } from '@core/converters/csp/solicitud-documento.converter';
import { SOLICITUD_MODALIDAD_CONVERTER } from '@core/converters/csp/solicitud-modalidad.converter';
import { SOLICITUD_PROYECTO_AREA_CONOCIMIENTO_CONVERTER } from '@core/converters/csp/solicitud-proyecto-area-conocimiento.converter';
import { SOLICITUD_PROYECTO_CLASIFICACION_CONVERTER } from '@core/converters/csp/solicitud-proyecto-clasificacion.converter';
import { SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_AJENA_CONVERTER } from '@core/converters/csp/solicitud-proyecto-entidad-financiadora-ajena.converter';
import { SOLICITUD_PROYECTO_EQUIPO_CONVERTER } from '@core/converters/csp/solicitud-proyecto-equipo.converter';
import { SOLICITUD_PROYECTO_PRESUPUESTO_CONVERTER } from '@core/converters/csp/solicitud-proyecto-presupuesto.converter';
import { SOLICITUD_PROYECTO_SOCIO_CONVERTER } from '@core/converters/csp/solicitud-proyecto-socio.converter';
import { SOLICITUD_PROYECTO_CONVERTER } from '@core/converters/csp/solicitud-proyecto.converter';
import { SOLICITUD_CONVERTER } from '@core/converters/csp/solicitud.converter';
import { IConvocatoriaBackend } from '@core/models/csp/backend/convocatoria-backend';
import { IConvocatoriaEntidadConvocanteBackend } from '@core/models/csp/backend/convocatoria-entidad-convocante-backend';
import { IConvocatoriaEntidadFinanciadoraBackend } from '@core/models/csp/backend/convocatoria-entidad-financiadora-backend';
import { IEstadoSolicitudBackend } from '@core/models/csp/backend/estado-solicitud-backend';
import { ISolicitudBackend } from '@core/models/csp/backend/solicitud-backend';
import { ISolicitudDocumentoBackend } from '@core/models/csp/backend/solicitud-documento-backend';
import { ISolicitudModalidadBackend } from '@core/models/csp/backend/solicitud-modalidad-backend';
import { ISolicitudProyectoAreaConocimientoBackend } from '@core/models/csp/backend/solicitud-proyecto-area-conocimiento-backend';
import { ISolicitudProyectoBackend } from '@core/models/csp/backend/solicitud-proyecto-backend';
import { ISolicitudProyectoClasificacionBackend } from '@core/models/csp/backend/solicitud-proyecto-clasificacion-backend';
import { ISolicitudProyectoEntidadFinanciadoraAjenaBackend } from '@core/models/csp/backend/solicitud-proyecto-entidad-financiadora-ajena-backend';
import { ISolicitudProyectoEquipoBackend } from '@core/models/csp/backend/solicitud-proyecto-equipo-backend';
import { ISolicitudProyectoPresupuestoBackend } from '@core/models/csp/backend/solicitud-proyecto-presupuesto-backend';
import { ISolicitudProyectoSocioBackend } from '@core/models/csp/backend/solicitud-proyecto-socio-backend';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { IEstadoSolicitud } from '@core/models/csp/estado-solicitud';
import { IGrupo } from '@core/models/csp/grupo';
import { IRequisitoEquipoNivelAcademico } from '@core/models/csp/requisito-equipo-nivel-academico';
import { IRequisitoIPCategoriaProfesional } from '@core/models/csp/requisito-ip-categoria-profesional';
import { IRequisitoIPNivelAcademico } from '@core/models/csp/requisito-ip-nivel-academico';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ISolicitudDocumento } from '@core/models/csp/solicitud-documento';
import { ISolicitudGrupo } from '@core/models/csp/solicitud-grupo';
import { ISolicitudHito } from '@core/models/csp/solicitud-hito';
import { ISolicitudModalidad } from '@core/models/csp/solicitud-modalidad';
import { ISolicitudPalabraClave } from '@core/models/csp/solicitud-palabra-clave';
import { ISolicitudProyecto } from '@core/models/csp/solicitud-proyecto';
import { ISolicitudProyectoAreaConocimiento } from '@core/models/csp/solicitud-proyecto-area-conocimiento';
import { ISolicitudProyectoClasificacion } from '@core/models/csp/solicitud-proyecto-clasificacion';
import { ISolicitudProyectoEntidad } from '@core/models/csp/solicitud-proyecto-entidad';
import { ISolicitudProyectoEntidadFinanciadoraAjena } from '@core/models/csp/solicitud-proyecto-entidad-financiadora-ajena';
import { ISolicitudProyectoEquipo } from '@core/models/csp/solicitud-proyecto-equipo';
import { ISolicitudProyectoPresupuesto } from '@core/models/csp/solicitud-proyecto-presupuesto';
import { ISolicitudProyectoPresupuestoTotalConceptoGasto } from '@core/models/csp/solicitud-proyecto-presupuesto-total-concepto-gasto';
import { ISolicitudProyectoPresupuestoTotales } from '@core/models/csp/solicitud-proyecto-presupuesto-totales';
import { ISolicitudProyectoResponsableEconomico } from '@core/models/csp/solicitud-proyecto-responsable-economico';
import { ISolicitudProyectoSocio } from '@core/models/csp/solicitud-proyecto-socio';
import { environment } from '@env';
import { SgiMutableRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { from, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap } from 'rxjs/operators';
import { PersonaService } from '../sgp/persona.service';
import { GRUPO_REQUEST_CONVERTER } from './grupo/grupo-request.converter';
import { IGrupoResponse } from './grupo/grupo-response';
import { GRUPO_RESPONSE_CONVERTER } from './grupo/grupo-response.converter';
import { IRequisitoEquipoNivelAcademicoResponse } from './requisito-equipo-nivel-academico/requisito-equipo-nivel-academico-response';
import { REQUISITO_EQUIPO_NIVELACADEMICO_RESPONSE_CONVERTER } from './requisito-equipo-nivel-academico/requisito-equipo-nivel-academico-response.converter';
import { IRequisitoIPCategoriaProfesionalResponse } from './requisito-ip-categoria-profesional/requisito-ip-categoria-profesional-response';
import { REQUISITOIP_CATEGORIA_PROFESIONAL_RESPONSE_CONVERTER } from './requisito-ip-categoria-profesional/requisito-ip-categoria-profesional-response.converter';
import { IRequisitoIPNivelAcademicoResponse } from './requisito-ip-nivel-academico/requisito-ip-nivel-academico-response';
import { REQUISITOIP_NIVELACADEMICO_RESPONSE_CONVERTER } from './requisito-ip-nivel-academico/requisito-ip-nivel-academico-response.converter';
import { ISolicitudHitoResponse } from './solicitud-hito/solicitud-hito-response';
import { SOLICITUD_HITO_RESPONSE_CONVERTER } from './solicitud-hito/solicitud-hito-response.converter';
import { ISolicitudGrupoResponse } from './solicitud-grupo/solicitud-grupo-response';
import { SOLICITUD_GRUPO_RESPONSE_CONVERTER } from './solicitud-grupo/solicitud-grupo-response-converter';
import { SolicitudModalidadService } from './solicitud-modalidad.service';
import { SOLICITUD_PALABRACLAVE_REQUEST_CONVERTER } from './solicitud-palabra-clave/solicitud-palabra-clave-request.converter';
import { ISolicitudPalabraClaveResponse } from './solicitud-palabra-clave/solicitud-palabra-clave-response';
import { SOLICITUD_PALABRACLAVE_RESPONSE_CONVERTER } from './solicitud-palabra-clave/solicitud-palabra-clave-response.converter';
import { ISolicitudProyectoEntidadResponse } from './solicitud-proyecto-entidad/solicitud-proyecto-entidad-response';
import { SOLICITUD_PROYECTO_ENTIDAD_RESPONSE_CONVERTER } from './solicitud-proyecto-entidad/solicitud-proyecto-entidad-response.converter';
import { ISolicitudProyectoResponsableEconomicoResponse } from './solicitud-proyecto-responsable-economico/solicitud-proyecto-responsable-economico-response';
import { SOLICITUD_PROYECTO_RESPONSABLE_ECONOMICO_RESPONSE_CONVERTER } from './solicitud-proyecto-responsable-economico/solicitud-proyecto-responsable-economico-response.converter';

@Injectable({
  providedIn: 'root'
})
export class SolicitudService extends SgiMutableRestService<number, ISolicitudBackend, ISolicitud> {
  private static readonly MAPPING = '/solicitudes';

  constructor(
    private readonly logger: NGXLogger,
    protected http: HttpClient,
    private personaService: PersonaService,
  ) {
    super(
      SolicitudService.name,
      `${environment.serviceServers.csp}${SolicitudService.MAPPING}`,
      http,
      SOLICITUD_CONVERTER
    );
  }

  /**
   * Desactiva una solicitud por su id
   *
   * @param id identificador de la solicitud.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined);
  }

  /**
   * Reactiva una solicitud por su id
   *
   * @param id identificador de la solicitud.
   */
  reactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined);
  }

  /**
   * Recupera todas las solicitudes activas e inactivas visibles por el usuario
   *
   * @param options opciones de busqueda
   * @returns observable con la lista de solicitudes
   */
  findAllTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ISolicitud>> {
    return this.find<ISolicitudBackend, ISolicitud>(`${this.endpointUrl}/todos`, options, SOLICITUD_CONVERTER);
  }

  /**
   * Recupera todas las modalidades de la solicitud
   *
   * @param solicitudId Id de la solicitud
   * @param options opciones de busqueda
   * @returns observable con la lista de modalidades de la solicitud
   */
  findAllSolicitudModalidades(solicitudId: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<ISolicitudModalidad>> {
    return this.find<ISolicitudModalidadBackend, ISolicitudModalidad>(
      `${this.endpointUrl}/${solicitudId}${SolicitudModalidadService.MAPPING}`,
      options,
      SOLICITUD_MODALIDAD_CONVERTER
    );
  }

  /**
   * Recupera listado de historico estado
   * @param id solicitud
   * @param options opciones de búsqueda.
   */
  findEstadoSolicitud(solicitudId: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IEstadoSolicitud>> {
    return this.find<IEstadoSolicitudBackend, IEstadoSolicitud>(
      `${this.endpointUrl}/${solicitudId}/estadosolicitudes`,
      options,
      ESTADO_SOLICITUD_CONVERTER
    );
  }

  /**
   * Recupera todos los documentos
   *
   * @param id Id de la solicitud
   * @param options Opciones de busqueda
   * @returns observable con la lista de documentos de la solicitud
   */
  findDocumentos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<ISolicitudDocumento>> {
    return this.find<ISolicitudDocumentoBackend, ISolicitudDocumento>(
      `${this.endpointUrl}/${id}/solicituddocumentos`,
      options,
      SOLICITUD_DOCUMENTO_CONVERTER
    );
  }

  /**
   * Recupera los hitos de la solicitud
   *
   * @param solicitudId Id de la solicitud
   * @param options opciones de busqueda
   * @returns observable con la lista de modalidades de la solicitud
   */
  findHitosSolicitud(solicitudId: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<ISolicitudHito>> {
    return this.find<ISolicitudHitoResponse, ISolicitudHito>(
      `${this.endpointUrl}/${solicitudId}/solicitudhitos`,
      options,
      SOLICITUD_HITO_RESPONSE_CONVERTER
    );
  }

  /**
   * Recupera las areas de conocimiento de la solicitud
   *
   * @param solicitudId Id de la solicitud
   * @param options opciones de busqueda
   * @returns observable con la lista de areas de conocimiento de la solicitud
   */
  findAllSolicitudProyectoAreaConocimiento(solicitudId: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<ISolicitudProyectoAreaConocimiento>> {
    return this.find<ISolicitudProyectoAreaConocimientoBackend, ISolicitudProyectoAreaConocimiento>(
      `${this.endpointUrl}/${solicitudId}/solicitud-proyecto-areas-conocimiento`,
      options,
      SOLICITUD_PROYECTO_AREA_CONOCIMIENTO_CONVERTER
    );
  }

  /**
   * Comprueba si una solicitud está asociada a una convocatoria SGI.
   *
   * @param id Id de la solicitud.
   */
  hasConvocatoriaSGI(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/convocatoria-sgi`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200)
    );
  }

  /**
   * Devuelve los datos del proyecto de una solicitud
   *
   * @param solicitudId Id de la solicitud
   */
  findSolicitudProyecto(solicitudId: number): Observable<ISolicitudProyecto> {
    return this.http.get<ISolicitudProyectoBackend>(
      `${this.endpointUrl}/${solicitudId}/solicitudproyecto`
    ).pipe(
      map(response => SOLICITUD_PROYECTO_CONVERTER.toTarget(response))
    );
  }

  /**
   * Devuelve los proyectoEquipos de una solicitud
   *
   * @param solicitudId Id de la solicitud
   */
  findAllSolicitudProyectoEquipo(solicitudId: number): Observable<SgiRestListResult<ISolicitudProyectoEquipo>> {
    return this.find<ISolicitudProyectoEquipoBackend, ISolicitudProyectoEquipo>(
      `${this.endpointUrl}/${solicitudId}/solicitudproyectoequipo`,
      undefined,
      SOLICITUD_PROYECTO_EQUIPO_CONVERTER
    ).pipe(
      switchMap(resultList =>
        from(resultList.items).pipe(
          mergeMap(wrapper => this.loadSolicitante(wrapper)),
          switchMap(() => of(resultList))
        )
      )
    );
  }

  private loadSolicitante(solicitudProyectoEquipo: ISolicitudProyectoEquipo): Observable<ISolicitudProyectoEquipo> {
    return this.personaService.findById(solicitudProyectoEquipo.persona.id).pipe(
      map(solicitante => {
        solicitudProyectoEquipo.persona = solicitante;
        return solicitudProyectoEquipo;
      }),
      catchError((err) => {
        this.logger.error(err);
        return of(solicitudProyectoEquipo);
      })
    );
  }

  /**
   * Comprueba si existe una solicitudProyectoDatos asociada a una solicitud
   *
   * @param id Id de la solicitud
   */
  existsSolictudProyecto(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/solicitudproyecto`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200)
    );
  }

  /**
   * Recupera los ISolicitudProyectoSocio de la solicitud
   *
   * @param id Id de la solicitud
   * @param options opciones de busqueda
   * @returns observable con la lista de ISolicitudProyectoSocio de la solicitud
   */
  findAllSolicitudProyectoSocio(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<ISolicitudProyectoSocio>> {
    return this.find<ISolicitudProyectoSocioBackend, ISolicitudProyectoSocio>(
      `${this.endpointUrl}/${id}/solicitudproyectosocio`,
      options,
      SOLICITUD_PROYECTO_SOCIO_CONVERTER
    );
  }

  /**
   * Recupera los ISolicitudProyectoSocio de la solicitud
   *
   * @param id Id de la solicitud
   * @param options opciones de busqueda
   * @returns observable con la lista de ISolicitudProyectoSocio de la solicitud
   */
  existSolicitanteInSolicitudProyectoEquipo(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/existssolicitante`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200)
    );
  }


  /**
   * Devuelve las entidades financiadoras de una solicitud
   *
   * @param solicitudId Id de la solicitud
   * @param options opciones de busqueda
   */
  findAllSolicitudProyectoEntidadFinanciadora(solicitudId: number, options?: SgiRestFindOptions)
    : Observable<SgiRestListResult<ISolicitudProyectoEntidadFinanciadoraAjena>> {
    return this.find<ISolicitudProyectoEntidadFinanciadoraAjenaBackend, ISolicitudProyectoEntidadFinanciadoraAjena>(
      `${this.endpointUrl}/${solicitudId}/solicitudproyectoentidadfinanciadoraajenas`,
      options,
      SOLICITUD_PROYECTO_ENTIDAD_FINANCIADORA_AJENA_CONVERTER
    );
  }

  /**
   * Recupera los ISolicitudProyectoPresupuesto de la solicitud
   *
   * @param id Id de la solicitud
   * @param options opciones de busqueda
   * @returns observable con la lista de ISolicitudProyectoPresupuesto de la solicitud
   */
  findAllSolicitudProyectoPresupuesto(id: number, options?: SgiRestFindOptions)
    : Observable<SgiRestListResult<ISolicitudProyectoPresupuesto>> {
    return this.find<ISolicitudProyectoPresupuestoBackend, ISolicitudProyectoPresupuesto>(
      `${this.endpointUrl}/${id}/solicitudproyectopresupuestos`,
      options,
      SOLICITUD_PROYECTO_PRESUPUESTO_CONVERTER
    );
  }

  /**
   * Comprueba la existencia de presupuestos de una solicitud, para una entidad concreta con relación ajena o no.
   *
   * @param id ID de la Solicitud
   * @param entidadRef Id de la Entidad
   * @param ajena Indica si es Ajena a la convocatoria
   * @returns **true** si existe alguna relación, **false** en cualquier otro caso
   */
  existsSolicitudProyectoPresupuesto(id: number, entidadRef: string, ajena: boolean): Observable<boolean> {
    return this.http.head(
      ajena
        ? `${this.endpointUrl}/${id}/solicitudproyectopresupuestos/entidadajena/${entidadRef}`
        : `${this.endpointUrl}/${id}/solicitudproyectopresupuestos/entidadconvocatoria/${entidadRef}`,
      { observe: 'response' }
    ).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Devuelve los datos del proyecto de una solicitud
   *
   * @param solicitudId Id de la solicitud
   */
  getSolicitudProyectoPresupuestoTotales(solicitudId: number): Observable<ISolicitudProyectoPresupuestoTotales> {
    return this.http.get<ISolicitudProyectoPresupuestoTotales>(`${this.endpointUrl}/${solicitudId}/solicitudproyectopresupuestos/totales`);
  }

  /**
   * Recupera los ISolicitudProyectoPresupuesto de la solicitud
   *
   * @param id Id de la solicitud
   * @param options opciones de busqueda
   * @returns observable con la lista de ISolicitudProyectoPresupuesto de la solicitud
   */
  findAllSolicitudProyectoPresupuestoTotalesConceptoGasto(id: number)
    : Observable<SgiRestListResult<ISolicitudProyectoPresupuestoTotalConceptoGasto>> {
    return this.find<ISolicitudProyectoPresupuestoTotalConceptoGasto, ISolicitudProyectoPresupuestoTotalConceptoGasto>(
      `${this.endpointUrl}/${id}/solicitudproyectopresupuestos/totalesconceptogasto`
    );
  }

  /**
   * Comprueba si se puede crear el proyecto a partir de la solicitud
   *
   * @param id Id de la solicitud
   */
  isPosibleCrearProyecto(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/crearproyecto`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Comprueba si tiene permisos de edición de la solicitud
   *
   * @param id Id de la solicitud
   */
  modificable(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/modificable`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Realiza el cambio de estado de estado de una solicitud.
   *
   * @param id identificador de la solicitud.
   * @param estadoSolicitud Nuevo estado de la solicitud.
   */

  cambiarEstado(id: number, estadoSolicitud: IEstadoSolicitud): Observable<IEstadoSolicitud> {
    return this.http.patch<IEstadoSolicitudBackend>(`${this.endpointUrl}/${id}/cambiar-estado`,
      ESTADO_SOLICITUD_CONVERTER.fromTarget(estadoSolicitud)
    ).pipe(
      map((response => ESTADO_SOLICITUD_CONVERTER.toTarget(response)))
    );
  }
  /**
   * Devuelve el listado de solicitudes que puede ver un investigador
   *
   * @param options opciones de búsqueda.
   */
  findAllInvestigador(options?: SgiRestFindOptions): Observable<SgiRestListResult<ISolicitud>> {
    return this.find<ISolicitudBackend, ISolicitud>(`${this.endpointUrl}/investigador`, options, SOLICITUD_CONVERTER);
  }

  /**
   * Recupera los ISolicitudProyectoClasificacion de la solicitud
   *
   * @param solicitudId Id de la solicitud
   * @param options opciones de busqueda
   * @returns observable con la lista de ISolicitudProyectoClasificacion de la solicitud
   */
  findAllClasificacionesSolicitud(solicitudId: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<ISolicitudProyectoClasificacion>> {
    return this.find<ISolicitudProyectoClasificacionBackend, ISolicitudProyectoClasificacion>(
      `${this.endpointUrl}/${solicitudId}/solicitud-proyecto-clasificaciones`,
      options,
      SOLICITUD_PROYECTO_CLASIFICACION_CONVERTER
    );
  }

  /**
   * Devuelve los responsables economicos de una solicitud
   *
   * @param solicitudId Id de la solicitud
   */
  findAllSolicitudProyectoResponsablesEconomicos(solicitudId: number):
    Observable<SgiRestListResult<ISolicitudProyectoResponsableEconomico>> {
    return this.find<ISolicitudProyectoResponsableEconomicoResponse, ISolicitudProyectoResponsableEconomico>(
      `${this.endpointUrl}/${solicitudId}/solicitudproyectoresponsableseconomicos`,
      undefined,
      SOLICITUD_PROYECTO_RESPONSABLE_ECONOMICO_RESPONSE_CONVERTER
    );
  }

  /**
   * Comprueba si existen SolicitudProyectoPresupuesto asociada a una solicitud
   *
   * @param id Id de la solicitud
   */
  existsSolictudProyectoPresupuesto(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/solicitudproyectopresupuestos`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200)
    );
  }

  /**
   * Comprueba si la solicitud proyecto de la solicitud es de tipo Global
   *
   * @param id Id de la Solicitud
   */
  hasSolicitudProyectoGlobal(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/solicitudproyecto-global`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200)
    );
  }

  /**
   * Devuelve las entidades financiadoras de la convocatoria de una solicitud
   *
   * @param solicitudId Id de la solicitud
   * @param options opciones de busqueda
   */
  findEntidadesFinanciadorasConvocatoriaSolicitud(
    solicitudId: number,
    options?: SgiRestFindOptions
  ): Observable<SgiRestListResult<IConvocatoriaEntidadFinanciadora>> {
    return this.find<IConvocatoriaEntidadFinanciadoraBackend, IConvocatoriaEntidadFinanciadora>(
      `${this.endpointUrl}/${solicitudId}/solicitudproyectoentidadfinanciadora`,
      options,
      CONVOCATORIA_ENTIDAD_FINANCIADORA_CONVERTER
    );
  }

  /**
   * Devuelve las entidades para un desglose de presupuesto de tipo mixto
   *
   * @param solicitudId Id de la solicitud
   * @param options opciones de busqueda
   */
  findAllSolicitudProyectoEntidadTipoPresupuestoMixto(
    solicitudId: number,
    options?: SgiRestFindOptions
  ): Observable<SgiRestListResult<ISolicitudProyectoEntidad>> {
    return this.find<ISolicitudProyectoEntidadResponse, ISolicitudProyectoEntidad>(
      `${this.endpointUrl}/${solicitudId}/solicitudproyectoentidad/tipopresupuestomixto`,
      options,
      SOLICITUD_PROYECTO_ENTIDAD_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve las entidades para un desglose de presupuesto de tipo por entidad
   *
   * @param solicitudId Id de la solicitud
   * @param options opciones de busqueda
   */
  findAllSolicitudProyectoEntidadTipoPresupuestoPorEntidades(
    solicitudId: number,
    options?: SgiRestFindOptions
  ): Observable<SgiRestListResult<ISolicitudProyectoEntidad>> {
    return this.find<ISolicitudProyectoEntidadResponse, ISolicitudProyectoEntidad>(
      `${this.endpointUrl}/${solicitudId}/solicitudproyectoentidad/tipopresupuestoporentidad`,
      options,
      SOLICITUD_PROYECTO_ENTIDAD_RESPONSE_CONVERTER
    );
  }

  public findIdsProyectosBySolicitudId(solicitudId: number): Observable<number[]> {
    return this.http.get<number[]>(`${this.endpointUrl}/${solicitudId}/proyectosids`);
  }

  /**
   * Recupera las Palabras Clave asociadas a la Solicitud con el id indicado.
   *
   * @param id de la Solicitud
   */
  findPalabrasClave(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<ISolicitudPalabraClave>> {
    return this.find<ISolicitudPalabraClaveResponse, ISolicitudPalabraClave>(
      `${this.endpointUrl}/${id}/palabrasclave`,
      options,
      SOLICITUD_PALABRACLAVE_RESPONSE_CONVERTER);
  }

  /**
   * Actualiza las Palabras Clave  asociadas a la Solicitud con el id indicado.
   *
   * @param id Identificador de la Solicitud
   * @param palabrasClave Palabras Clave a actualizar
   */
  updatePalabrasClave(id: number, palabrasClave: ISolicitudPalabraClave[]): Observable<ISolicitudPalabraClave[]> {
    return this.http.patch<ISolicitudPalabraClaveResponse[]>(`${this.endpointUrl}/${id}/palabrasclave`,
      SOLICITUD_PALABRACLAVE_REQUEST_CONVERTER.fromTargetArray(palabrasClave)
    ).pipe(
      map((response => SOLICITUD_PALABRACLAVE_RESPONSE_CONVERTER.toTargetArray(response)))
    );
  }

  /**
   * Devuelve los datos de la convocatoria de una solicitud
   *
   * @param solicitudId Id de la solicitud
   */
  findConvocatoria(solicitudId: number): Observable<IConvocatoria> {
    return this.http.get<IConvocatoriaBackend>(
      `${this.endpointUrl}/${solicitudId}/convocatoria`
    ).pipe(
      map(response => CONVOCATORIA_CONVERTER.toTarget(response))
    );
  }

  findAllConvocatoriaEntidadConvocantes(id: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IConvocatoriaEntidadConvocante>> {
    return this.find<IConvocatoriaEntidadConvocanteBackend, IConvocatoriaEntidadConvocante>(
      `${this.endpointUrl}/${id}/convocatoriaentidadconvocantes`,
      options,
      CONVOCATORIA_ENTIDAD_CONVOCANTE_CONVERTER
    );
  }

  findRequisitosIpCategoriasProfesionales(id: number): Observable<IRequisitoIPCategoriaProfesional[]> {
    const endpointUrl = `${this.endpointUrl}/${id}/categoriasprofesionalesrequisitosip`;
    return this.http.get<IRequisitoIPCategoriaProfesionalResponse[]>(endpointUrl)
      .pipe(
        map(r => {
          return REQUISITOIP_CATEGORIA_PROFESIONAL_RESPONSE_CONVERTER.toTargetArray(r);
        })
      );
  }

  findRequisitosEquipoNivelesAcademicos(id: number): Observable<IRequisitoEquipoNivelAcademico[]> {
    const endpointUrl = `${this.endpointUrl}/${id}/nivelesrequisitosequipo`;
    return this.http.get<IRequisitoEquipoNivelAcademicoResponse[]>(endpointUrl)
      .pipe(
        map(r => {
          return REQUISITO_EQUIPO_NIVELACADEMICO_RESPONSE_CONVERTER.toTargetArray(r);
        })
      );
  }

  findRequisitosIpNivelesAcademicos(id: number): Observable<IRequisitoIPNivelAcademico[]> {
    const endpointUrl = `${this.endpointUrl}/${id}/nivelesrequisitosip`;
    return this.http.get<IRequisitoIPNivelAcademicoResponse[]>(endpointUrl)
      .pipe(
        map(r => {
          return REQUISITOIP_NIVELACADEMICO_RESPONSE_CONVERTER.toTargetArray(r);
        })
      );
  }

  modificableEstadoAndDocumentosByInvestigador(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/modificableestadoanddocumentosbyinvestigador`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Devuelve el código de registro interno de la solicitud
   *
   * @param solicitudId Id de la solicitud
   */
  getCodigoRegistroInterno(solicitudId: number): Observable<string> {
    return this.http.get<string>(`${this.endpointUrl}/${solicitudId}/codigo-registro-interno`);
  }


  findSolicitudGrupo(id: number): Observable<ISolicitudGrupo> {
    const endpointUrl = `${this.endpointUrl}/${id}/solcitudgrupo`;
    return this.http.get<ISolicitudGrupoResponse>(endpointUrl)
      .pipe(
        map(response => {
          return SOLICITUD_GRUPO_RESPONSE_CONVERTER.toTarget(response);
        })
      );
  }
  /**
   * Se crea un grupo a partir de los datos de la solicitud
   *
   * @param id identificador de la solicitud a copiar
   */
  createGrupoBySolicitud(id: number, grupo: IGrupo): Observable<IGrupo> {
    return this.http.post<IGrupoResponse>(
      `${this.endpointUrl}/${id}/grupo`,
      GRUPO_REQUEST_CONVERTER.fromTarget(grupo)
    ).pipe(
      map(response => GRUPO_RESPONSE_CONVERTER.toTarget(response))
    );
  }

}
