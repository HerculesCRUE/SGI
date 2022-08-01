import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ESTADO_PROYECTO_CONVERTER } from '@core/converters/csp/estado-proyecto.converter';
import { PROYECTO_AREA_CONOCIMIENTO_CONVERTER } from '@core/converters/csp/proyecto-area-conocimiento.converter';
import { PROYECTO_CLASIFICACION_CONVERTER } from '@core/converters/csp/proyecto-clasificacion.converter';
import { PROYECTO_CONCEPTO_GASTO_CONVERTER } from '@core/converters/csp/proyecto-concepto-gasto.converter';
import { PROYECTO_CONTEXTO_CONVERTER } from '@core/converters/csp/proyecto-contexto.converter';
import { PROYECTO_DOCUMENTO_CONVERTER } from '@core/converters/csp/proyecto-documento.converter';
import { PROYECTO_ENTIDAD_CONVOCANTE_CONVERTER } from '@core/converters/csp/proyecto-entidad-convocante.converter';
import { PROYECTO_ENTIDAD_FINANCIADORA_CONVERTER } from '@core/converters/csp/proyecto-entidad-financiadora.converter';
import { PROYECTO_ENTIDAD_GESTORA_CONVERTER } from '@core/converters/csp/proyecto-entidad-gestora.converter';
import { PROYECTO_EQUIPO_CONVERTER } from '@core/converters/csp/proyecto-equipo.converter';
import { PROYECTO_IVA_CONVERTER } from '@core/converters/csp/proyecto-iva.converter';
import { PROYECTO_PAQUETE_TRABAJO_CONVERTER } from '@core/converters/csp/proyecto-paquete-trabajo.converter';
import { PROYECTO_PERIODO_SEGUIMIENTO_CONVERTER } from '@core/converters/csp/proyecto-periodo-seguimiento.converter';
import { PROYECTO_PRORROGA_CONVERTER } from '@core/converters/csp/proyecto-prorroga.converter';
import { PROYECTO_PROYECTO_SGE_CONVERTER } from '@core/converters/csp/proyecto-proyecto-sge.converter';
import { PROYECTO_SOCIO_CONVERTER } from '@core/converters/csp/proyecto-socio.converter';
import { PROYECTO_CONVERTER } from '@core/converters/csp/proyecto.converter';
import { IAnualidadGasto } from '@core/models/csp/anualidad-gasto';
import { IEstadoProyectoBackend } from '@core/models/csp/backend/estado-proyecto-backend';
import { IProyectoAreaConocimientoBackend } from '@core/models/csp/backend/proyecto-area-conocimiento-backend';
import { IProyectoBackend } from '@core/models/csp/backend/proyecto-backend';
import { IProyectoClasificacionBackend } from '@core/models/csp/backend/proyecto-clasificacion-backend';
import { IProyectoConceptoGastoBackend } from '@core/models/csp/backend/proyecto-concepto-gasto-backend';
import { IProyectoContextoBackend } from '@core/models/csp/backend/proyecto-contexto-backend';
import { IProyectoDocumentoBackend } from '@core/models/csp/backend/proyecto-documento-backend';
import { IProyectoEntidadConvocanteBackend } from '@core/models/csp/backend/proyecto-entidad-convocante-backend';
import { IProyectoEntidadFinanciadoraBackend } from '@core/models/csp/backend/proyecto-entidad-financiadora-backend';
import { IProyectoEntidadGestoraBackend } from '@core/models/csp/backend/proyecto-entidad-gestora-backend';
import { IProyectoEquipoBackend } from '@core/models/csp/backend/proyecto-equipo-backend';
import { IProyectoIVABackend } from '@core/models/csp/backend/proyecto-iva-backend';
import { IProyectoPaqueteTrabajoBackend } from '@core/models/csp/backend/proyecto-paquete-trabajo-backend';
import { IProyectoPeriodoSeguimientoBackend } from '@core/models/csp/backend/proyecto-periodo-seguimiento-backend';
import { IProyectoProrrogaBackend } from '@core/models/csp/backend/proyecto-prorroga-backend';
import { IProyectoProyectoSgeBackend } from '@core/models/csp/backend/proyecto-proyecto-sge-backend';
import { IProyectoSocioBackend } from '@core/models/csp/backend/proyecto-socio-backend';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IEstadoProyecto } from '@core/models/csp/estado-proyecto';
import { IPrograma } from '@core/models/csp/programa';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoAgrupacionGasto } from '@core/models/csp/proyecto-agrupacion-gasto';
import { IProyectoAnualidad } from '@core/models/csp/proyecto-anualidad';
import { IProyectoAnualidadResumen } from '@core/models/csp/proyecto-anualidad-resumen';
import { IProyectoAreaConocimiento } from '@core/models/csp/proyecto-area-conocimiento';
import { IProyectoClasificacion } from '@core/models/csp/proyecto-clasificacion';
import { IProyectoConceptoGasto } from '@core/models/csp/proyecto-concepto-gasto';
import { IProyectoContexto } from '@core/models/csp/proyecto-contexto';
import { IProyectoDocumento } from '@core/models/csp/proyecto-documento';
import { IProyectoEntidadConvocante } from '@core/models/csp/proyecto-entidad-convocante';
import { IProyectoEntidadFinanciadora } from '@core/models/csp/proyecto-entidad-financiadora';
import { IProyectoEntidadGestora } from '@core/models/csp/proyecto-entidad-gestora';
import { IProyectoEquipo } from '@core/models/csp/proyecto-equipo';
import { IProyectoFacturacion } from '@core/models/csp/proyecto-facturacion';
import { IProyectoHito } from '@core/models/csp/proyecto-hito';
import { IProyectoIVA } from '@core/models/csp/proyecto-iva';
import { IProyectoPalabraClave } from '@core/models/csp/proyecto-palabra-clave';
import { IProyectoPaqueteTrabajo } from '@core/models/csp/proyecto-paquete-trabajo';
import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { IProyectoPeriodoJustificacion } from '@core/models/csp/proyecto-periodo-justificacion';
import { IProyectoPeriodoSeguimiento } from '@core/models/csp/proyecto-periodo-seguimiento';
import { IProyectoFase } from '@core/models/csp/proyecto-fase';
import { IProyectoPresupuestoTotales } from '@core/models/csp/proyecto-presupuesto-totales';
import { IProyectoProrroga } from '@core/models/csp/proyecto-prorroga';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { IProyectoResponsableEconomico } from '@core/models/csp/proyecto-responsable-economico';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { IProyectosCompetitivosPersonas } from '@core/models/csp/proyectos-competitivos-personas';
import { environment } from '@env';
import { FormlyFieldConfig } from '@ngx-formly/core';
import {
  RSQLSgiRestFilter,
  SgiMutableRestService,
  SgiRestFilterOperator,
  SgiRestFindOptions,
  SgiRestListResult
} from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IAnualidadGastoResponse } from './anualidad-gasto/anualidad-gasto-response';
import { ANUALIDAD_GASTO_RESPONSE_CONVERTER } from './anualidad-gasto/anualidad-gasto-response.converter';
import { IConvocatoriaTituloResponse } from './convocatoria/convocatoria-titulo-response';
import { CONVOCATORIA_TITULO_RESPONSE_CONVERTER } from './convocatoria/convocatoria-titulo-response.converter';
import { IProyectoAgrupacionGastoResponse } from './proyecto-agrupacion-gasto/proyecto-agrupacion-gasto-response';
import { PROYECTO_AGRUPACION_GASTO_RESPONSE_CONVERTER } from './proyecto-agrupacion-gasto/proyecto-agrupacion-gasto-response.converter';
import { IProyectoAnualidadResponse } from './proyecto-anualidad/proyecto-anualidad-response';
import { PROYECTO_ANUALIDAD_RESPONSE_CONVERTER } from './proyecto-anualidad/proyecto-anualidad-response.converter';
import { IProyectoAnualidadResumenResponse } from './proyecto-anualidad/proyecto-anualidad-resumen-response';
import { PROYECTO_ANUALIDAD_RESUMEN_RESPONSE_CONVERTER } from './proyecto-anualidad/proyecto-anualidad-resumen-response.converter';
import { IProyectoFacturacionResponse } from './proyecto-facturacion/proyecto-facturacion-response';
import { PROYECTO_FACTURACION_RESPONSE_CONVERTER } from './proyecto-facturacion/proyecto-facturacion-response.converter';
import { IProyectoHitoResponse } from './proyecto-hito/proyecto-hito-response';
import { PROYECTO_HITO_RESPONSE_CONVERTER } from './proyecto-hito/proyecto-hito-response.converter';
import { PROYECTO_PALABRACLAVE_REQUEST_CONVERTER } from './proyecto-palabra-clave/proyecto-palabra-clave-request.converter';
import { IProyectoPalabraClaveResponse } from './proyecto-palabra-clave/proyecto-palabra-clave-response';
import { PROYECTO_PALABRACLAVE_RESPONSE_CONVERTER } from './proyecto-palabra-clave/proyecto-palabra-clave-response.converter';
import { IProyectoPeriodoJustificacionResponse } from './proyecto-periodo-justificacion/proyecto-periodo-justificacion-response';
import { PROYECTO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER } from './proyecto-periodo-justificacion/proyecto-periodo-justificacion-response.converter';
import { IProyectoResponsableEconomicoResponse } from './proyecto-responsable-economico/proyecto-responsable-economico-response';
import { PROYECTO_RESPONSABLE_ECONOMICO_RESPONSE_CONVERTER } from './proyecto-responsable-economico/proyecto-responsable-economico-response.converter';
import { IProyectosCompetitivosPersonasResponse } from './proyectos-competitivos-personas/proyectos-competitivos-personas-response';
import { PROYECTOS_COMPETITIVOS_PERSONAS_RESPONSE_CONVERTER } from './proyectos-competitivos-personas/proyectos-competitivos-personas-response.converter';
import { PROYECTO_FASE_RESPONSE_CONVERTER } from './proyecto-fase/proyecto-fase-response.converter';
import { IProyectoFaseResponse } from './proyecto-fase/proyecto-fase-response';

@Injectable({
  providedIn: 'root'
})
export class ProyectoService extends SgiMutableRestService<number, IProyectoBackend, IProyecto> {

  private static readonly MAPPING = '/proyectos';
  private static readonly ENTIDAD_CONVOCANTES_MAPPING = 'entidadconvocantes';

  constructor(readonly logger: NGXLogger, protected http: HttpClient) {
    super(
      ProyectoService.name,
      `${environment.serviceServers.csp}${ProyectoService.MAPPING}`,
      http,
      PROYECTO_CONVERTER
    );
  }

  /**
   * Devuelve todos que estén asociadas a
   * las unidades de gestión a las que esté vinculado el usuario
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyecto>> {
    return this.find<IProyectoBackend, IProyecto>(`${this.endpointUrl}/todos`, options, PROYECTO_CONVERTER);
  }

  /**
   * Devuelve una lista paginada y filtrada de todos los proyectos activos, que no estén en estado borrador
   * y en los que participe dentro del equipo el usuario logueado que se encuentren dentro de la unidad de gestión
   * @param options opciones de búsqueda.
   */
  findAllInvestigador(options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyecto>> {
    return this.find<IProyectoBackend, IProyecto>(`${this.endpointUrl}/investigador`, options, PROYECTO_CONVERTER);
  }

  /**
   * Recupera los paquete trabajo de un proyecto
   * @param idProyecto Identificador del proyecto.
   * @returns Listado de paquete trabajo.
   */
  findPaqueteTrabajoProyecto(idProyecto: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoPaqueteTrabajo>> {
    return this.find<IProyectoPaqueteTrabajoBackend, IProyectoPaqueteTrabajo>(
      `${this.endpointUrl}/${idProyecto}/proyectopaquetetrabajos`,
      options,
      PROYECTO_PAQUETE_TRABAJO_CONVERTER
    );
  }

  /**
   * Recupera los plazos de un proyecto
   * @param idProyecto Identificador del proyecto.
   * @returns Listado de plazos.
   */
  findPlazosProyecto(idProyecto: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoFase>> {
    return this.find<IProyectoFaseResponse, IProyectoFase>(
      `${this.endpointUrl}/${idProyecto}/proyectofases`,
      options,
      PROYECTO_FASE_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve los datos del proyecto contexto
   *
   * @param proyectoID Id del proyecto
   */
  findProyectoContexto(proyectoID: number): Observable<IProyectoContexto> {
    return this.http.get<IProyectoContextoBackend>(
      `${this.endpointUrl}/${proyectoID}/proyecto-contextoproyectos`
    ).pipe(
      map(response => PROYECTO_CONTEXTO_CONVERTER.toTarget(response))
    );
  }

  /**
   * Desactivar proyecto
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined);
  }

  /**
   * Reactivar proyecto
   * @param options opciones de búsqueda.
   */
  reactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined);
  }

  findEntidadesFinanciadoras(id: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IProyectoEntidadFinanciadora>> {
    return this.find<IProyectoEntidadFinanciadoraBackend, IProyectoEntidadFinanciadora>(
      `${this.endpointUrl}/${id}/proyectoentidadfinanciadoras`,
      options,
      PROYECTO_ENTIDAD_FINANCIADORA_CONVERTER
    );
  }

  private findEntidadesFinanciadorasFilterAjenas(id: number, ajenas: boolean, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IProyectoEntidadFinanciadora>> {
    let queryOptions: SgiRestFindOptions = options;
    if (!queryOptions) {
      queryOptions = {};
    }
    if (queryOptions.filter) {
      queryOptions.filter.remove('ajena');
      queryOptions.filter.and('ajena', SgiRestFilterOperator.EQUALS, `${ajenas}`);
    }
    else {
      queryOptions.filter = new RSQLSgiRestFilter('ajena', SgiRestFilterOperator.EQUALS, `${ajenas}`);
    }
    return this.findEntidadesFinanciadoras(id, queryOptions);
  }

  /**
   * Obtiene el listado de entidades financiadores asociadas al proyecto que NO son ajenas a la convocatoria
   * @param id Identificador del proyecto
   * @param options Opciones de filtrado/ordenación
   */
  findEntidadesFinanciadorasPropias(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoEntidadFinanciadora>> {
    return this.findEntidadesFinanciadorasFilterAjenas(id, false, options);
  }

  /**
   * Obtiene el listado de entidades financiadores asociadas al proyecto que son ajenas a la convocatoria
   * @param id Identificador del proyecto
   * @param options Opciones de filtrado/ordenación
   */
  findEntidadesFinanciadorasAjenas(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoEntidadFinanciadora>> {
    return this.findEntidadesFinanciadorasFilterAjenas(id, true, options);
  }

  /**
   * Recupera los hitos de un proyecto
   * @param idProyecto Identificador del proyecto.
   * @returns Listado de hitos.
   */
  findHitosProyecto(idProyecto: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoHito>> {
    return this.find<IProyectoHitoResponse, IProyectoHito>(
      `${this.endpointUrl}/${idProyecto}/proyectohitos`,
      options,
      PROYECTO_HITO_RESPONSE_CONVERTER
    );
  }

  /**
   * Recupera las entidades gestoras de un proyecto
   * @param idProyecto Identificador del proyecto.
   * @returns entidaded gestora.
   */
  findEntidadGestora(id: number, options?:
    SgiRestFindOptions): Observable<SgiRestListResult<IProyectoEntidadGestora>> {
    return this.find<IProyectoEntidadGestoraBackend, IProyectoEntidadGestora>(
      `${this.endpointUrl}/${id}/proyectoentidadgestoras`,
      options,
      PROYECTO_ENTIDAD_GESTORA_CONVERTER
    );
  }

  /**
   * Recupera los IProyectoSocio del proyecto
   *
   * @param id Id del proyecto
   * @param options opciones de busqueda
   * @returns observable con la lista de IProyectoSocio del proyecto
   */
  findAllProyectoSocioProyecto(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoSocio>> {
    return this.find<IProyectoSocioBackend, IProyectoSocio>(
      `${this.endpointUrl}/${id}/proyectosocios`,
      options,
      PROYECTO_SOCIO_CONVERTER
    );
  }

  /**
   * Devuelve el listado de IProyectoEquipo de un IProyecto
   *
   * @param id Id del IProyecto
   */
  findAllProyectoEquipo(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoEquipo>> {
    return this.find<IProyectoEquipoBackend, IProyectoEquipo>(
      `${this.endpointUrl}/${id}/proyectoequipos`,
      options,
      PROYECTO_EQUIPO_CONVERTER
    );
  }

  /**
   * Recupera los ProyectoEntidadConvocante de un proyecto
   * @param id Identificador del proyecto.
   * @returns Listado de ProyectoEntidadConvocante.
   */
  findAllEntidadConvocantes(idProyecto: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoEntidadConvocante>> {
    return this.find<IProyectoEntidadConvocanteBackend, IProyectoEntidadConvocante>(
      `${this.endpointUrl}/${idProyecto}/${ProyectoService.ENTIDAD_CONVOCANTES_MAPPING}`,
      options,
      PROYECTO_ENTIDAD_CONVOCANTE_CONVERTER
    );
  }

  public createEntidadConvocante(idProyecto: number, element: IProyectoEntidadConvocante): Observable<IProyectoEntidadConvocante> {
    return this.http.post<IProyectoEntidadConvocanteBackend>(
      `${this.endpointUrl}/${idProyecto}/${ProyectoService.ENTIDAD_CONVOCANTES_MAPPING}`,
      PROYECTO_ENTIDAD_CONVOCANTE_CONVERTER.fromTarget(element)
    ).pipe(
      map(response => PROYECTO_ENTIDAD_CONVOCANTE_CONVERTER.toTarget(response))
    );
  }

  public deleteEntidadConvocanteById(idProyecto: number, id: number): Observable<void> {
    const endpointUrl = `${this.endpointUrl}/${idProyecto}/${ProyectoService.ENTIDAD_CONVOCANTES_MAPPING}`;
    return this.http.delete<void>(`${endpointUrl}/${id}`);
  }

  setEntidadConvocantePrograma(idProyecto: number, id: number, programa: IPrograma): Observable<IProyectoEntidadConvocante> {
    const endpointUrl = `${this.endpointUrl}/${idProyecto}/${ProyectoService.ENTIDAD_CONVOCANTES_MAPPING}`;
    return this.http.patch<IProyectoEntidadConvocanteBackend>(
      `${endpointUrl}/${id}/programa`,
      programa
    ).pipe(
      map(response => PROYECTO_ENTIDAD_CONVOCANTE_CONVERTER.toTarget(response))
    );
  }

  /**
   * Recupera los IProyectoPeriodoSeguimiento del proyecto
   *
   * @param id Id del proyecto
   * @param options opciones de busqueda
   * @returns observable con la lista de IProyectoPeriodoSeguimiento del proyecto
   */
  findAllProyectoPeriodoSeguimientoProyecto(id: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IProyectoPeriodoSeguimiento>> {
    return this.find<IProyectoPeriodoSeguimientoBackend, IProyectoPeriodoSeguimiento>(
      `${this.endpointUrl}/${id}/proyectoperiodoseguimientos`,
      options,
      PROYECTO_PERIODO_SEGUIMIENTO_CONVERTER
    );
  }

  /**
   * Recupera los IProyectoProrroga del proyecto
   *
   * @param id Id del proyecto
   * @param options opciones de busqueda
   * @returns observable con la lista de IProyectoProrroga del proyecto
   */
  findAllProyectoProrrogaProyecto(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoProrroga>> {
    return this.find<IProyectoProrrogaBackend, IProyectoProrroga>(
      `${this.endpointUrl}/${id}/proyecto-prorrogas`,
      options,
      PROYECTO_PRORROGA_CONVERTER
    );
  }

  /**
   * Recupera listado de historico estado
   * @param id proyecto
   * @param options opciones de búsqueda.
   */
  findEstadoProyecto(proyectoId: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IEstadoProyecto>> {
    return this.find<IEstadoProyectoBackend, IEstadoProyecto>(
      `${this.endpointUrl}/${proyectoId}/estadoproyectos`,
      options,
      ESTADO_PROYECTO_CONVERTER
    );
  }

  /**
   * Recupera listado de proyectoIVA
   * @param id proyecto
   * @param options opciones de búsqueda.
   */
  findProyectoIVA(proyectoId: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoIVA>> {
    return this.find<IProyectoIVABackend, IProyectoIVA>(
      `${this.endpointUrl}/${proyectoId}/proyectoiva`,
      options,
      PROYECTO_IVA_CONVERTER
    );
  }

  /**
   * Recupera todos los documentos de un proyecto
   * @param id Identificador del proyecto.
   * @param options opciones de búsqueda
   */
  findAllProyectoDocumentos(idProyecto: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoDocumento>> {
    return this.find<IProyectoDocumentoBackend, IProyectoDocumento>(
      `${this.endpointUrl}/${idProyecto}/documentos`,
      options,
      PROYECTO_DOCUMENTO_CONVERTER
    );
  }

  /**
   * Recupera las areas de conocimiento del proyecto
   *
   * @param proyectoId Id del proyecto
   * @param options opciones de busqueda
   * @returns observable con la lista de areas de conocimiento del poryecto
   */
  findAllProyectoAreaConocimiento(proyectoId: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IProyectoAreaConocimiento>> {
    return this.find<IProyectoAreaConocimientoBackend, IProyectoAreaConocimiento>(
      `${this.endpointUrl}/${proyectoId}/proyecto-areas-conocimiento`,
      options,
      PROYECTO_AREA_CONOCIMIENTO_CONVERTER
    );
  }

  /**
   * Se crea un proyecto a partir de los datos de la solicitud
   *
   * @param id identificador de la solicitud a copiar
   */
  crearProyectoBySolicitud(id: number, proyecto: IProyecto): Observable<IProyecto> {
    return this.http.post<IProyectoBackend>(
      `${this.endpointUrl}/${id}/solicitud`,
      this.converter.fromTarget(proyecto)
    ).pipe(
      map(response => this.converter.toTarget(response))
    );
  }

  /**
   * Comprueba si Proyecto tiene ProyectoDocumento relacionado
   *
   * @param id Proyecto
   */
  hasProyectoDocumentos(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/documentos`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Comprueba si Proyecto tiene ProyectoFase relacionado
   *
   * @param id Proyecto
   */
  hasProyectoFases(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/proyectofases`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Comprueba si Proyecto tiene ProyectoHito relacionado
   *
   * @param id Proyecto
   */
  hasProyectoHitos(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/proyectohitos`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Comprueba si Proyecto tiene ProyectosSGE relacionados
   *
   * @param id Proyecto
   */
  hasProyectosSGE(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/proyectossge`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Recupera los IProyectoClasificacion del proyecto
   *
   * @param proyectoId Id del proyecto
   * @param options opciones de busqueda
   * @returns observable con la lista de IProyectoClasificacion del proyecto
   */
  findAllClasificacionesProyecto(proyectoId: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IProyectoClasificacion>> {
    return this.find<IProyectoClasificacionBackend, IProyectoClasificacion>(
      `${this.endpointUrl}/${proyectoId}/proyecto-clasificaciones`,
      options,
      PROYECTO_CLASIFICACION_CONVERTER
    );
  }

  /**
   * Recupera los IProyectoProyectoSge del proyecto
   *
   * @param proyectoId Id del proyecto
   * @param options opciones de busqueda
   * @returns observable con la lista de IProyectoProyectoSge del proyecto
   */
  findAllProyectosSgeProyecto(proyectoId: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IProyectoProyectoSge>> {
    return this.find<IProyectoProyectoSgeBackend, IProyectoProyectoSge>(
      `${this.endpointUrl}/${proyectoId}/proyectossge`,
      options,
      PROYECTO_PROYECTO_SGE_CONVERTER
    );
  }

  /**
   * Comprueba si Proyecto tiene ProyectoProrroga relacionado
   *
   * @param id Proyecto
   */
  hasProyectoProrrogas(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/proyecto-prorrogas`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Recupera los IProyectoPartida del proyecto
   *
   * @param proyectoId Id del proyecto
   * @param options opciones de busqueda
   * @returns observable con la lista de IProyectoPartida del proyecto
   */
  findAllProyectoPartidas(proyectoId: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IProyectoPartida>> {
    return this.find<IProyectoPartida, IProyectoPartida>(
      `${this.endpointUrl}/${proyectoId}/proyecto-partidas`,
      options
    );
  }

  /**
   * Recupera listado de conceptos de gasto permitidos.
   * @param id Id del proyecto
   * @param options opciones de búsqueda.
   */
  findAllProyectoConceptosGastoPermitidos(id: number): Observable<SgiRestListResult<IProyectoConceptoGasto>> {
    return this.find<IProyectoConceptoGastoBackend, IProyectoConceptoGasto>(
      `${this.endpointUrl}/${id}/proyectoconceptosgasto/permitidos`,
      undefined,
      PROYECTO_CONCEPTO_GASTO_CONVERTER
    );
  }

  /**
   * Recupera listado de conceptos de gasto NO permitidos.
   * @param id Id del proyecto
   * @param options opciones de búsqueda.
   */
  findAllProyectoConceptosGastoNoPermitidos(id: number): Observable<SgiRestListResult<IProyectoConceptoGasto>> {
    return this.find<IProyectoConceptoGastoBackend, IProyectoConceptoGasto>(
      `${this.endpointUrl}/${id}/proyectoconceptosgasto/nopermitidos`,
      undefined,
      PROYECTO_CONCEPTO_GASTO_CONVERTER
    );
  }

  /**
   * Realiza el cambio de estado de estado de un proyecto.
   *
   * @param id identificador del proyecto.
   * @param estadoProyecto Nuevo estado del proyecto.
   */
  cambiarEstado(id: number, estadoProyecto: IEstadoProyecto): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/cambiar-estado`, estadoProyecto);
  }

  hasAnyProyectoSocio(proyectoId: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${proyectoId}/proyectosocios`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  hasPeriodosPago(proyectoId: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${proyectoId}/proyectosocios/periodospago`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  hasPeriodosJustificacion(proyectoId: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${proyectoId}/proyectosocios/periodosjustificacion`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  hasAnyProyectoSocioWithRolCoordinador(proyectoId: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${proyectoId}/proyectosocios/coordinador`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  findAllProyectoAnualidades(proyectoId: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IProyectoAnualidadResumen>> {
    return this.find<IProyectoAnualidadResumenResponse, IProyectoAnualidadResumen>(
      `${this.endpointUrl}/${proyectoId}/anualidades`,
      options,
      PROYECTO_ANUALIDAD_RESUMEN_RESPONSE_CONVERTER
    );
  }

  findAllProyectoAnualidadesGasto(proyectoId: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IAnualidadGasto>> {
    return this.find<IAnualidadGastoResponse, IAnualidadGasto>(
      `${this.endpointUrl}/${proyectoId}/anualidadesgasto`,
      options,
      ANUALIDAD_GASTO_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve los totales de los importes de un presupuesto de proyecto
   *
   * @param proyectoId Id del proyecto
   */
  getProyectoPresupuestoTotales(proyectoId: number): Observable<IProyectoPresupuestoTotales> {
    return this.http.get<IProyectoPresupuestoTotales>(`${this.endpointUrl}/${proyectoId}/presupuesto-totales`);
  }
  /**
   * Recupera las agrupaciones de gasto del proyecto.
   *
   * @param proyectoId Id del proyecto
   * @param options opciones de busqueda
   * @returns observable con laagrupaciones de gasto del poryecto
   */
  findAllAgrupacionesGasto(proyectoId: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IProyectoAgrupacionGasto>> {
    return this.find<IProyectoAgrupacionGastoResponse, IProyectoAgrupacionGasto>(
      `${this.endpointUrl}/${proyectoId}/proyectoagrupaciongasto`,
      options,
      PROYECTO_AGRUPACION_GASTO_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve el listado de IProyectoPeriodoJustificacion de un IProyecto
   *
   * @param id Id del IProyecto
   */
  findAllPeriodoJustificacion(proyectoId: number, options?: SgiRestFindOptions)
    : Observable<SgiRestListResult<IProyectoPeriodoJustificacion>> {
    return this.find<IProyectoPeriodoJustificacionResponse, IProyectoPeriodoJustificacion>(
      `${this.endpointUrl}/${proyectoId}/proyectoperiodojustificacion`,
      options,
      PROYECTO_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve los responsables economicos de un proyecto
   *
   * @param proyectoId Id del proyecto
   */
  findAllProyectoResponsablesEconomicos(proyectoId: number, findOptions?: SgiRestFindOptions):
    Observable<SgiRestListResult<IProyectoResponsableEconomico>> {
    return this.find<IProyectoResponsableEconomicoResponse, IProyectoResponsableEconomico>(
      `${this.endpointUrl}/${proyectoId}/proyectoresponsableseconomicos`,
      findOptions,
      PROYECTO_RESPONSABLE_ECONOMICO_RESPONSE_CONVERTER
    );
  }

  /**
   * Busca todos los proyectos que tengan la convocatoria asociada.
   *
   * @param id identificador de la convocatoria
   * @returns la lista de Proyectos
   */
  findAllProyectosByConvocatoria(id: number): Observable<SgiRestListResult<IProyecto>> {
    let options: SgiRestFindOptions;
    options = {
      filter: new RSQLSgiRestFilter('convocatoria.id', SgiRestFilterOperator.EQUALS, id.toString())
    };
    return this.findTodos(options);
  }

  createProyecto(model: any): Observable<void> {
    return this.http.post<void>(`${this.endpointUrl}`, model);
  }

  getFormlyCreate(): Observable<FormlyFieldConfig[]> {
    return this.http.get<FormlyFieldConfig[]>(`${this.endpointUrl}/formly/create`);
  }

  /**
   * Comprueba si tiene permisos de edición del proyecto
   *
   * @param id Id del proyecto
   */
  modificable(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/modificable`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Devuelve los objetos {@link IProyectoAnualidad} asociados a un {@link IProyecto}
   *
   * @param id Id del proyecto
   */
  findAllProyectoAnualidadesByProyectoId(proyectoId: number):
    Observable<SgiRestListResult<IProyectoAnualidad>> {
    return this.find<IProyectoAnualidadResponse, IProyectoAnualidad>(
      `${this.endpointUrl}/${proyectoId}/proyectoanualidades`,
      {},
      PROYECTO_ANUALIDAD_RESPONSE_CONVERTER
    );
  }

  /**
   * Devuelve los objetos {@link IAnualidadGastoWithProyectoAgrupacionGasto} asociados a un {@link IProyecto}
   *
   * @param id Id del proyecto
   */
  findAnualidadesGastosByProyectoId(proyectoId: number):
    Observable<SgiRestListResult<IAnualidadGasto>> {
    return this.find<IAnualidadGastoResponse, IAnualidadGasto>(
      `${this.endpointUrl}/${proyectoId}/anualidadesgastos`,
      {},
      ANUALIDAD_GASTO_RESPONSE_CONVERTER);
  }

  /*
   * Devuelve el listado de {@link IProyectoFacturacion} de un {@link IProyecto}
   *
   * @param proyectoId Id del {@link IProyecto}
   */
  findProyectosFacturacionByProyectoId(
    proyectoId: number, options?: SgiRestFindOptions
  ): Observable<SgiRestListResult<IProyectoFacturacion>> {

    return this.find<IProyectoFacturacionResponse, IProyectoFacturacion>(
      `${this.endpointUrl}/${proyectoId}/proyectosfacturacion`, options, PROYECTO_FACTURACION_RESPONSE_CONVERTER);
  }

  /**
   * Recupera las Palabras Clave asociadas al Proyecto con el id indicado.
   *
   * @param id del Proyecto
   */
  findPalabrasClave(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoPalabraClave>> {
    return this.find<IProyectoPalabraClaveResponse, IProyectoPalabraClave>(
      `${this.endpointUrl}/${id}/palabrasclave`,
      options,
      PROYECTO_PALABRACLAVE_RESPONSE_CONVERTER);
  }

  /**
   * Actualiza las Palabras Clave  asociadas al Proyecto con el id indicado.
   *
   * @param id Identificador del Proyecto
   * @param palabrasClave Palabras Clave a actualizar
   */
  updatePalabrasClave(id: number, palabrasClave: IProyectoPalabraClave[]): Observable<IProyectoPalabraClave[]> {
    return this.http.patch<IProyectoPalabraClaveResponse[]>(`${this.endpointUrl}/${id}/palabrasclave`,
      PROYECTO_PALABRACLAVE_REQUEST_CONVERTER.fromTargetArray(palabrasClave)
    ).pipe(
      map((response => PROYECTO_PALABRACLAVE_RESPONSE_CONVERTER.toTargetArray(response)))
    );
  }

  /**
   * Devuelve los datos de la convocatoria asociada a un proyecto
   * para usuarios con perfil investigador
   * @param id Id del proyecto
   */
  findConvocatoria(id: number): Observable<IConvocatoria> {
    return this.http.get<IConvocatoriaTituloResponse>(
      `${this.endpointUrl}/${id}/convocatoria`
    ).pipe(
      map(response => CONVOCATORIA_TITULO_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  /**
   * ProyectoEquipo que son investigador o investigadores principales del
   * proyecto con el id indicado.
   * Se considera investiador principal a la ProyectoEquipo que a fecha actual
   * tiene el rol Proyecto con el flag "principal" a
   * true. En caso de que varios coincidan se devuelven todos los que coincidan.
   *
   * @param id identificador del proyecto.
   * @return la lista de personaRef de los investigadores principales del
   *         proyecto en el momento actual.
   */
  findPersonaRefInvestigadoresPrincipales(id: number): Observable<string[]> {
    return this.http.get<string[]>(`${this.endpointUrl}/${id}/investigadoresprincipales`);
  }

  /**
   * Devuelve los datos de la convocatoria asociada a un proyecto
   * para usuarios con perfil investigador
   * @param id Id del proyecto
   */
  getProyectoCompetitivosPersona(
    personasRef: string | string[],
    onlyAsRolPrincipal = false,
    exludedProyectoId?: number
  ): Observable<IProyectosCompetitivosPersonas> {
    const url = `${this.endpointUrl}/competitivos-personas`;

    let params = new HttpParams();
    if (Array.isArray(personasRef)) {
      personasRef.forEach(personaRef =>
        params = params.append('personasRef', personaRef)
      );
    } else {
      params = params.append('personasRef', personasRef);
    }

    if (exludedProyectoId) {
      params = params.append('exludedProyectoId', exludedProyectoId.toString());
    }

    params = params.append('onlyAsRolPrincipal', onlyAsRolPrincipal.toString());

    return this.http.get<IProyectosCompetitivosPersonasResponse>(url, { params }).pipe(
      map(response => PROYECTOS_COMPETITIVOS_PERSONAS_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  /**
   * Comprueba si Proyecto tiene AnualidadGastos relacionado
   *
   * @param id Proyecto
   */
  hasAnualidadGastos(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/anualidad-gastos`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Comprueba si Proyecto tiene AnualidadIngresos relacionado
   *
   * @param id Proyecto
   */
  hasAnualidadIngresos(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/anualidad-ingresos`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Comprueba si Proyecto tiene GastosProyecto relacionado
   *
   * @param id Proyecto
   */
  hasGastosProyecto(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/gastos-proyecto`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

}
