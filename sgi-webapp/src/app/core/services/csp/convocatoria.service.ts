import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_AREA_TEMATICA_CONVERTER } from '@core/converters/csp/convocatoria-area-tematica.converter';
import { CONVOCATORIA_CONCEPTO_GASTO_CODIGO_EC_CONVERTER } from '@core/converters/csp/convocatoria-concepto-gasto-codigo-ec.converter';
import { CONVOCATORIA_CONCEPTO_GASTO_CONVERTER } from '@core/converters/csp/convocatoria-concepto-gasto.converter';
import { CONVOCATORIA_DOCUMENTO_CONVERTER } from '@core/converters/csp/convocatoria-documento.converter';
import { CONVOCATORIA_ENLACE_CONVERTER } from '@core/converters/csp/convocatoria-enlace.converter';
import { CONVOCATORIA_ENTIDAD_CONVOCANTE_CONVERTER } from '@core/converters/csp/convocatoria-entidad-convocante.converter';
import { CONVOCATORIA_ENTIDAD_FINANCIADORA_CONVERTER } from '@core/converters/csp/convocatoria-entidad-financiadora.converter';
import { CONVOCATORIA_ENTIDAD_GESTORA_CONVERTER } from '@core/converters/csp/convocatoria-entidad-gestora.converter';
import { CONVOCATORIA_FASE_CONVERTER } from '@core/converters/csp/convocatoria-fase.converter';
import { CONVOCATORIA_HITO_CONVERTER } from '@core/converters/csp/convocatoria-hito.converter';
import { CONVOCATORIA_PARTIDA_PRESUPUESTARIA_CONVERTER } from '@core/converters/csp/convocatoria-partida.converter';
import { CONVOCATORIA_PERIODO_JUSTIFICACION_CONVERTER } from '@core/converters/csp/convocatoria-periodo-justificacion.converter';
import { CONVOCATORIA_PERIODO_SEGUIMIENTO_CIENTIFICO_CONVERTER } from '@core/converters/csp/convocatoria-periodo-seguimiento-cientifico.converter';
import { CONVOCATORIA_CONVERTER } from '@core/converters/csp/convocatoria.converter';
import { IConvocatoriaAreaTematicaBackend } from '@core/models/csp/backend/convocatoria-area-tematica-backend';
import { IConvocatoriaBackend } from '@core/models/csp/backend/convocatoria-backend';
import { IConvocatoriaConceptoGastoBackend } from '@core/models/csp/backend/convocatoria-concepto-gasto-backend';
import { IConvocatoriaConceptoGastoCodigoEcBackend } from '@core/models/csp/backend/convocatoria-concepto-gasto-codigo-ec-backend';
import { IConvocatoriaDocumentoBackend } from '@core/models/csp/backend/convocatoria-documento-backend';
import { IConvocatoriaEnlaceBackend } from '@core/models/csp/backend/convocatoria-enlace-backend';
import { IConvocatoriaEntidadConvocanteBackend } from '@core/models/csp/backend/convocatoria-entidad-convocante-backend';
import { IConvocatoriaEntidadFinanciadoraBackend } from '@core/models/csp/backend/convocatoria-entidad-financiadora-backend';
import { IConvocatoriaEntidadGestoraBackend } from '@core/models/csp/backend/convocatoria-entidad-gestora-backend';
import { IConvocatoriaFaseBackend } from '@core/models/csp/backend/convocatoria-fase-backend';
import { IConvocatoriaHitoBackend } from '@core/models/csp/backend/convocatoria-hito-backend';
import { IConvocatoriaPartidaPresupuestariaBackend } from '@core/models/csp/backend/convocatoria-partida-backend';
import { IConvocatoriaPeriodoJustificacionBackend } from '@core/models/csp/backend/convocatoria-periodo-justificacion-backend';
import { IConvocatoriaPeriodoSeguimientoCientificoBackend } from '@core/models/csp/backend/convocatoria-periodo-seguimiento-cientifico-backend';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaAreaTematica } from '@core/models/csp/convocatoria-area-tematica';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { IConvocatoriaDocumento } from '@core/models/csp/convocatoria-documento';
import { IConvocatoriaEnlace } from '@core/models/csp/convocatoria-enlace';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { IConvocatoriaEntidadGestora } from '@core/models/csp/convocatoria-entidad-gestora';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { IConvocatoriaHito } from '@core/models/csp/convocatoria-hito';
import { IConvocatoriaPartidaPresupuestaria } from '@core/models/csp/convocatoria-partida-presupuestaria';
import { IConvocatoriaPeriodoJustificacion } from '@core/models/csp/convocatoria-periodo-justificacion';
import { IConvocatoriaPeriodoSeguimientoCientifico } from '@core/models/csp/convocatoria-periodo-seguimiento-cientifico';
import { IRequisitoEquipoCategoriaProfesional } from '@core/models/csp/requisito-equipo-categoria-profesional';
import { IRequisitoEquipoNivelAcademico } from '@core/models/csp/requisito-equipo-nivel-academico';
import { IRequisitoIPCategoriaProfesional } from '@core/models/csp/requisito-ip-categoria-profesional';
import { IRequisitoIPNivelAcademico } from '@core/models/csp/requisito-ip-nivel-academico';
import { IModeloEjecucion } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { SgiMutableRestService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http/';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IRequisitoEquipoCategoriaProfesionalResponse } from './requisito-equipo-categoria-profesional/requisito-equipo-categoria-profesional-response';
import { REQUISITO_EQUIPO_CATEGORIA_PROFESIONAL_RESPONSE_CONVERTER } from './requisito-equipo-categoria-profesional/requisito-equipo-categoria-profesional-response.converter';
import { IRequisitoEquipoNivelAcademicoResponse } from './requisito-equipo-nivel-academico/requisito-equipo-nivel-academico-response';
import { REQUISITO_EQUIPO_NIVELACADEMICO_RESPONSE_CONVERTER } from './requisito-equipo-nivel-academico/requisito-equipo-nivel-academico-response.converter';
import { IRequisitoIPCategoriaProfesionalResponse } from './requisito-ip-categoria-profesional/requisito-ip-categoria-profesional-response';
import { REQUISITOIP_CATEGORIA_PROFESIONAL_RESPONSE_CONVERTER } from './requisito-ip-categoria-profesional/requisito-ip-categoria-profesional-response.converter';
import { IRequisitoIPNivelAcademicoResponse } from './requisito-ip-nivel-academico/requisito-ip-nivel-academico-response';
import { REQUISITOIP_NIVELACADEMICO_RESPONSE_CONVERTER } from './requisito-ip-nivel-academico/requisito-ip-nivel-academico-response.converter';

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaService extends SgiMutableRestService<number, IConvocatoriaBackend, IConvocatoria> {

  private static readonly MAPPING = '/convocatorias';

  constructor(protected http: HttpClient) {
    super(
      ConvocatoriaService.name,
      `${environment.serviceServers.csp}${ConvocatoriaService.MAPPING}`,
      http,
      CONVOCATORIA_CONVERTER
    );
  }

  /**
   * Recupera el listado de todas las convocatorias activas asociadas a las unidades de gestión del usuario logueado.
   * @param options Opciones de búsqueda
   * @returns listado de convocatorias
   */
  findAllRestringidos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoria>> {
    return this.find<IConvocatoriaBackend, IConvocatoria>(
      `${this.endpointUrl}/restringidos`,
      options,
      this.converter
    );
  }

  /**
   * Recupera el listado de todas las convocatorias asociadas a las unidades de gestión del usuario logueado.
   * @param options Opciones de búsqueda
   * @returns listado de convocatorias
   */
  findAllTodosRestringidos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoria>> {
    return this.find<IConvocatoriaBackend, IConvocatoria>(
      `${this.endpointUrl}/todos/restringidos`,
      options,
      this.converter
    );
  }

  /**
   * Recupera listado de periodos justificacion de una convocatoria.
   *
   * @param id Id de la convocatoria.
   * @param options opciones de búsqueda.
   * @returns periodos de justificacion de la convocatoria.
   */
  getPeriodosJustificacion(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaPeriodoJustificacion>> {
    return this.find<IConvocatoriaPeriodoJustificacionBackend, IConvocatoriaPeriodoJustificacion>(
      `${this.endpointUrl}/${id}/convocatoriaperiodojustificaciones`,
      options,
      CONVOCATORIA_PERIODO_JUSTIFICACION_CONVERTER
    );
  }

  /**
   * Recupera listado de enlaces.
   * @param id convocatoria
   * @param options opciones de búsqueda.
   */
  getEnlaces(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaEnlace>> {
    return this.find<IConvocatoriaEnlaceBackend, IConvocatoriaEnlace>(
      `${this.endpointUrl}/${id}/convocatoriaenlaces`,
      options,
      CONVOCATORIA_ENLACE_CONVERTER
    );
  }

  /**
   * Indica si la convocatoria tiene relacionados algún enlace
   *
   * @param id Id de la convocatoria
   * @returns **true** si tiene relaciones, **false** en cualquier otro caso
   */
  hasConvocatoriaEnlaces(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/convocatoriaenlaces`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Recupera listado de seguimiento científicos.
   * @param id seguimiento científicos
   * @param options opciones de búsqueda.
   */
  findSeguimientosCientificos(id: number, options?: SgiRestFindOptions)
    : Observable<SgiRestListResult<IConvocatoriaPeriodoSeguimientoCientifico>> {
    return this.find<IConvocatoriaPeriodoSeguimientoCientificoBackend, IConvocatoriaPeriodoSeguimientoCientifico>(
      `${this.endpointUrl}/${id}/convocatoriaperiodoseguimientocientificos`,
      options,
      CONVOCATORIA_PERIODO_SEGUIMIENTO_CIENTIFICO_CONVERTER
    );
  }

  findEntidadesFinanciadoras(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaEntidadFinanciadora>> {
    return this.find<IConvocatoriaEntidadFinanciadoraBackend, IConvocatoriaEntidadFinanciadora>(
      `${this.endpointUrl}/${id}/convocatoriaentidadfinanciadoras`,
      options,
      CONVOCATORIA_ENTIDAD_FINANCIADORA_CONVERTER
    );
  }

  /**
   * Recupera los hitos de una convocatoria
   * @param idConvocatoria Identificador de la convocatoria.
   * @returns Listado de hitos.
   */
  findHitosConvocatoria(idConvocatoria: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaHito>> {
    return this.find<IConvocatoriaHitoBackend, IConvocatoriaHito>(
      `${this.endpointUrl}/${idConvocatoria}/convocatoriahitos`,
      options,
      CONVOCATORIA_HITO_CONVERTER
    );
  }

  /**
   * Indica si la convocatoria tiene relacionados algún hito
   *
   * @param id Id de la convocatoria
   * @returns **true** si tiene relaciones, **false** en cualquier otro caso
   */
  hasConvocatoriaHitos(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/convocatoriahitos`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
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

  findAllConvocatoriaFases(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaFase>> {
    return this.find<IConvocatoriaFaseBackend, IConvocatoriaFase>(
      `${this.endpointUrl}/${id}/convocatoriafases`,
      options,
      CONVOCATORIA_FASE_CONVERTER
    );
  }

  /**
   * Indica si la convocatoria tiene relacionados algúna fase
   *
   * @param id Id de la convocatoria
   * @returns **true** si tiene relaciones, **false** en cualquier otro caso
   */
  hasConvocatoriaFases(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/convocatoriafases`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Recupera listado de partidas presupuestarias.
   * @param id partida presupuestaria
   * @param options opciones de búsqueda.
   */
  findPartidasPresupuestarias(id: number, options?: SgiRestFindOptions)
    : Observable<SgiRestListResult<IConvocatoriaPartidaPresupuestaria>> {
    return this.find<IConvocatoriaPartidaPresupuestariaBackend, IConvocatoriaPartidaPresupuestaria>(
      `${this.endpointUrl}/${id}/convocatoria-partidas-presupuestarias`,
      options,
      CONVOCATORIA_PARTIDA_PRESUPUESTARIA_CONVERTER
    );
  }

  findAllConvocatoriaEntidadGestora(id: number, options?:
    SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaEntidadGestora>> {
    return this.find<IConvocatoriaEntidadGestoraBackend, IConvocatoriaEntidadGestora>(
      `${this.endpointUrl}/${id}/convocatoriaentidadgestoras`,
      options,
      CONVOCATORIA_ENTIDAD_GESTORA_CONVERTER
    );
  }

  /**
   * Recupera listado mock de modelos de áreas temáticas.
   * @param idConvocatoria opciones de búsqueda.
   * @returns listado de modelos de áreas temáticas.
   */
  findAreaTematicas(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaAreaTematica>> {
    return this.find<IConvocatoriaAreaTematicaBackend, IConvocatoriaAreaTematica>(
      `${this.endpointUrl}/${id}/convocatoriaareatematicas`,
      options,
      CONVOCATORIA_AREA_TEMATICA_CONVERTER
    );
  }

  findDocumentos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaDocumento>> {
    return this.find<IConvocatoriaDocumentoBackend, IConvocatoriaDocumento>(
      `${this.endpointUrl}/${id}/convocatoriadocumentos`,
      options,
      CONVOCATORIA_DOCUMENTO_CONVERTER
    );
  }

  /**
   * Indica si la convocatoria tiene relacionados algún documento
   *
   * @param id Id de la convocatoria
   * @returns **true** si tiene relaciones, **false** en cualquier otro caso
   */
  hasConvocatoriaDocumentos(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/convocatoriadocumentos`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Recupera listado de convocatoria concepto gastos permitidos.
   * @param id convocatoria
   * @param options opciones de búsqueda.
   */
  findAllConvocatoriaConceptoGastosPermitidos(id: number): Observable<SgiRestListResult<IConvocatoriaConceptoGasto>> {
    return this.find<IConvocatoriaConceptoGastoBackend, IConvocatoriaConceptoGasto>(
      `${this.endpointUrl}/${id}/convocatoriagastos/permitidos`,
      undefined,
      CONVOCATORIA_CONCEPTO_GASTO_CONVERTER
    );
  }

  /**
   * Recupera listado de convocatoria concepto gastos NO permitidos.
   * @param id convocatoria
   * @param options opciones de búsqueda.
   */
  findAllConvocatoriaConceptoGastosNoPermitidos(id: number): Observable<SgiRestListResult<IConvocatoriaConceptoGasto>> {
    return this.find<IConvocatoriaConceptoGastoBackend, IConvocatoriaConceptoGasto>(
      `${this.endpointUrl}/${id}/convocatoriagastos/nopermitidos`,
      undefined,
      CONVOCATORIA_CONCEPTO_GASTO_CONVERTER
    );
  }

  /**
   * Recupera listado de convocatoria concepto gastos códigos económicos permitidos.
   * @param id convocatoria
   * @param options opciones de búsqueda.
   */
  findAllConvocatoriaConceptoGastoCodigoEcsPermitidos(id: number): Observable<SgiRestListResult<IConvocatoriaConceptoGastoCodigoEc>> {
    return this.find<IConvocatoriaConceptoGastoCodigoEcBackend, IConvocatoriaConceptoGastoCodigoEc>(
      `${this.endpointUrl}/${id}/convocatoriagastocodigoec/permitidos`,
      undefined,
      CONVOCATORIA_CONCEPTO_GASTO_CODIGO_EC_CONVERTER
    );
  }

  /**
   * Recupera listado de convocatoria concepto gasto códigos económicos NO permitidos.
   * @param id convocatoria
   * @param options opciones de búsqueda.
   */
  findAllConvocatoriaConceptoGastoCodigoEcsNoPermitidos(id: number): Observable<SgiRestListResult<IConvocatoriaConceptoGastoCodigoEc>> {
    return this.find<IConvocatoriaConceptoGastoCodigoEcBackend, IConvocatoriaConceptoGastoCodigoEc>(
      `${this.endpointUrl}/${id}/convocatoriagastocodigoec/nopermitidos`,
      undefined,
      CONVOCATORIA_CONCEPTO_GASTO_CODIGO_EC_CONVERTER);
  }

  /**
   * Reactivar convocatoria
   * @param options opciones de búsqueda.
   */
  reactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined);
  }

  /**
   * Desactivar convocatoria
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined);
  }

  /**
   * Acción de registro de una convocatoria
   * @param id identificador de la convocatoria a registrar
   */
  registrar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/registrar`, undefined);
  }

  /**
   * Comprueba si existe una convocatoria
   *
   * @param id Id de la convocatoria
   */
  exists(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Comprueba si tiene permisos de edición de la convocatoria
   *
   * @param id Id de la convocatoria
   */
  modificable(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/modificable`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  getUnidadGestionRef(id: number): Observable<string> {
    const url = `${this.endpointUrl}/${id}/unidadgestion`;
    return this.http.get(url, { responseType: 'text' });
  }

  getModeloEjecucion(id: number): Observable<IModeloEjecucion> {
    const url = `${this.endpointUrl}/${id}/modeloejecucion`;
    return this.http.get<IModeloEjecucion>(url);
  }

  registrable(id: number): Observable<boolean> {
    return this.http.head(`${this.endpointUrl}/${id}/registrable`, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Devuelve el listado de convocatorias que puede ver un investigador
   *
   * @param options opciones de búsqueda.
   */
  findAllInvestigador(options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoria>> {
    return this.find<IConvocatoriaBackend, IConvocatoria>(
      `${this.endpointUrl}/investigador`,
      options,
      this.converter
    );
  }

  /**
   * Comprueba si tiene permisos para tramitar la convocatoria
   *
   * @param id Id de la convocatoria
   */
  tramitable(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/tramitable`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  public hasSolicitudesReferenced(convocatoriaId: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${convocatoriaId}/solicitudesreferenced`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  public hasProyectosReferenced(convocatoriaId: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${convocatoriaId}/proyectosreferenced`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Recupera los niveles académicos asociados a los requisitos equipo de la Convocatoria con el id indicado
   * @param id Identificador de la Convocatoria
   */
  findRequisitosEquipoNivelesAcademicos(id: number): Observable<IRequisitoEquipoNivelAcademico[]> {
    const endpointUrl = `${this.endpointUrl}/${id}/nivelesrequisitosequipo`;
    const params = new HttpParams().set('id', id.toString());
    return this.http.get<IRequisitoEquipoNivelAcademicoResponse[]>(endpointUrl, { params })
      .pipe(
        map(r => {
          return REQUISITO_EQUIPO_NIVELACADEMICO_RESPONSE_CONVERTER.toTargetArray(r);
        })
      );
  }

  /**
   * Recupera los niveles académicos asociados a los requisitos ip de la Convocatoria con el id indicado
   * @param id Identificador de la Convocatoria
   */
  findRequisitosIpNivelesAcademicos(id: number): Observable<IRequisitoIPNivelAcademico[]> {
    const endpointUrl = `${this.endpointUrl}/${id}/nivelesrequisitosip`;
    const params = new HttpParams().set('id', id.toString());
    return this.http.get<IRequisitoIPNivelAcademicoResponse[]>(endpointUrl, { params })
      .pipe(
        map(r => {
          return REQUISITOIP_NIVELACADEMICO_RESPONSE_CONVERTER.toTargetArray(r);
        })
      );
  }

  /**
   * Recupera las categorías sociadas a los requisitos ip de la Convocatoria con el id indicado
   * @param id Identificador de la Convocatoria
   */
  findRequisitosIpCategoriasProfesionales(id: number): Observable<IRequisitoIPCategoriaProfesional[]> {
    const endpointUrl = `${this.endpointUrl}/${id}/categoriasprofesionalesrequisitosip`;
    const params = new HttpParams().set('id', id.toString());
    return this.http.get<IRequisitoIPCategoriaProfesionalResponse[]>(endpointUrl, { params })
      .pipe(
        map(r => {
          return REQUISITOIP_CATEGORIA_PROFESIONAL_RESPONSE_CONVERTER.toTargetArray(r);
        })
      );
  }

  /**
   * Recupera las categorías asociadas a los requisitos equipo de la Convocatoria con el id indicado
   * @param id Identificador de la Convocatoria
   */
  findRequisitosEquipoCategoriasProfesionales(id: number): Observable<IRequisitoEquipoCategoriaProfesional[]> {
    const endpointUrl = `${this.endpointUrl}/${id}/categoriasprofesionalesrequisitosequipo`;
    const params = new HttpParams().set('id', id.toString());
    return this.http.get<IRequisitoEquipoCategoriaProfesionalResponse[]>(endpointUrl, { params })
      .pipe(
        map(r => {
          return REQUISITO_EQUIPO_CATEGORIA_PROFESIONAL_RESPONSE_CONVERTER.toTargetArray(r);
        })
      );
  }

  public clone(convocatoriaId: number): Observable<number> {
    const url = `${this.endpointUrl}/${convocatoriaId}/clone`;
    return this.http.post<number>(url, {});
  }

}
