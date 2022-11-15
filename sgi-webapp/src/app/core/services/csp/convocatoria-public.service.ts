import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONVOCATORIA_AREA_TEMATICA_CONVERTER } from '@core/converters/csp/convocatoria-area-tematica.converter';
import { CONVOCATORIA_CONCEPTO_GASTO_CONVERTER } from '@core/converters/csp/convocatoria-concepto-gasto.converter';
import { CONVOCATORIA_DOCUMENTO_CONVERTER } from '@core/converters/csp/convocatoria-documento.converter';
import { CONVOCATORIA_ENLACE_CONVERTER } from '@core/converters/csp/convocatoria-enlace.converter';
import { CONVOCATORIA_ENTIDAD_CONVOCANTE_CONVERTER } from '@core/converters/csp/convocatoria-entidad-convocante.converter';
import { CONVOCATORIA_ENTIDAD_FINANCIADORA_CONVERTER } from '@core/converters/csp/convocatoria-entidad-financiadora.converter';
import { CONVOCATORIA_ENTIDAD_GESTORA_CONVERTER } from '@core/converters/csp/convocatoria-entidad-gestora.converter';
import { CONVOCATORIA_PARTIDA_PRESUPUESTARIA_CONVERTER } from '@core/converters/csp/convocatoria-partida.converter';
import { CONVOCATORIA_PERIODO_JUSTIFICACION_CONVERTER } from '@core/converters/csp/convocatoria-periodo-justificacion.converter';
import { CONVOCATORIA_PERIODO_SEGUIMIENTO_CIENTIFICO_CONVERTER } from '@core/converters/csp/convocatoria-periodo-seguimiento-cientifico.converter';
import { CONVOCATORIA_CONVERTER } from '@core/converters/csp/convocatoria.converter';
import { IConvocatoriaAreaTematicaBackend } from '@core/models/csp/backend/convocatoria-area-tematica-backend';
import { IConvocatoriaBackend } from '@core/models/csp/backend/convocatoria-backend';
import { IConvocatoriaConceptoGastoBackend } from '@core/models/csp/backend/convocatoria-concepto-gasto-backend';
import { IConvocatoriaDocumentoBackend } from '@core/models/csp/backend/convocatoria-documento-backend';
import { IConvocatoriaEnlaceBackend } from '@core/models/csp/backend/convocatoria-enlace-backend';
import { IConvocatoriaEntidadConvocanteBackend } from '@core/models/csp/backend/convocatoria-entidad-convocante-backend';
import { IConvocatoriaEntidadFinanciadoraBackend } from '@core/models/csp/backend/convocatoria-entidad-financiadora-backend';
import { IConvocatoriaEntidadGestoraBackend } from '@core/models/csp/backend/convocatoria-entidad-gestora-backend';
import { IConvocatoriaPartidaPresupuestariaBackend } from '@core/models/csp/backend/convocatoria-partida-backend';
import { IConvocatoriaPeriodoJustificacionBackend } from '@core/models/csp/backend/convocatoria-periodo-justificacion-backend';
import { IConvocatoriaPeriodoSeguimientoCientificoBackend } from '@core/models/csp/backend/convocatoria-periodo-seguimiento-cientifico-backend';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaAreaTematica } from '@core/models/csp/convocatoria-area-tematica';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { IConvocatoriaDocumento } from '@core/models/csp/convocatoria-documento';
import { IConvocatoriaEnlace } from '@core/models/csp/convocatoria-enlace';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { IConvocatoriaEntidadGestora } from '@core/models/csp/convocatoria-entidad-gestora';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { IConvocatoriaHito } from '@core/models/csp/convocatoria-hito';
import { IConvocatoriaPalabraClave } from '@core/models/csp/convocatoria-palabra-clave';
import { IConvocatoriaPartidaPresupuestaria } from '@core/models/csp/convocatoria-partida-presupuestaria';
import { IConvocatoriaPeriodoJustificacion } from '@core/models/csp/convocatoria-periodo-justificacion';
import { IConvocatoriaPeriodoSeguimientoCientifico } from '@core/models/csp/convocatoria-periodo-seguimiento-cientifico';
import { IRequisitoEquipoCategoriaProfesional } from '@core/models/csp/requisito-equipo-categoria-profesional';
import { IRequisitoEquipoNivelAcademico } from '@core/models/csp/requisito-equipo-nivel-academico';
import { IRequisitoIPCategoriaProfesional } from '@core/models/csp/requisito-ip-categoria-profesional';
import { IRequisitoIPNivelAcademico } from '@core/models/csp/requisito-ip-nivel-academico';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http/';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IConvocatoriaFaseResponse } from './convocatoria-fase/convocatoria-fase-response';
import { CONVOCATORIA_FASE_RESPONSE_CONVERTER } from './convocatoria-fase/convocatoria-fase-response.converter';
import { IConvocatoriaHitoResponse } from './convocatoria-hito/convocatoria-hito-response';
import { CONVOCATORIA_HITO_RESPONSE_CONVERTER } from './convocatoria-hito/convocatoria-hito-response.converter';
import { IConvocatoriaPalabraClaveResponse } from './convocatoria-palabra-clave/convocatoria-palabra-clave-response';
import { CONVOCATORIA_PALABRACLAVE_RESPONSE_CONVERTER } from './convocatoria-palabra-clave/convocatoria-palabra-clave-response.converter';
import { IRequisitoEquipoCategoriaProfesionalResponse } from './requisito-equipo-categoria-profesional/requisito-equipo-categoria-profesional-response';
import { REQUISITO_EQUIPO_CATEGORIA_PROFESIONAL_RESPONSE_CONVERTER } from './requisito-equipo-categoria-profesional/requisito-equipo-categoria-profesional-response.converter';
import { IRequisitoEquipoNivelAcademicoResponse } from './requisito-equipo-nivel-academico/requisito-equipo-nivel-academico-response';
import { REQUISITO_EQUIPO_NIVELACADEMICO_RESPONSE_CONVERTER } from './requisito-equipo-nivel-academico/requisito-equipo-nivel-academico-response.converter';
import { IRequisitoIPCategoriaProfesionalResponse } from './requisito-ip-categoria-profesional/requisito-ip-categoria-profesional-response';
import { REQUISITOIP_CATEGORIA_PROFESIONAL_RESPONSE_CONVERTER } from './requisito-ip-categoria-profesional/requisito-ip-categoria-profesional-response.converter';
import { IRequisitoIPNivelAcademicoResponse } from './requisito-ip-nivel-academico/requisito-ip-nivel-academico-response';
import { REQUISITOIP_NIVELACADEMICO_RESPONSE_CONVERTER } from './requisito-ip-nivel-academico/requisito-ip-nivel-academico-response.converter';

// tslint:disable-next-line: variable-name
const _ConvocatoriaMixinBase:
  FindByIdCtor<number, IConvocatoria, IConvocatoriaBackend> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    CONVOCATORIA_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaPublicService extends _ConvocatoriaMixinBase {

  private static readonly MAPPING = '/convocatorias';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ConvocatoriaPublicService.PUBLIC_PREFIX}${ConvocatoriaPublicService.MAPPING}`,
      http
    );
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

  findAllInvestigador(options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoria>> {
    return this.find<IConvocatoriaBackend, IConvocatoria>(
      `${this.endpointUrl}/investigador`,
      options,
      CONVOCATORIA_CONVERTER
    );
  }

  tramitable(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/tramitable`;
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
    return this.find<IConvocatoriaFaseResponse, IConvocatoriaFase>(
      `${this.endpointUrl}/${id}/convocatoriafases`,
      options,
      CONVOCATORIA_FASE_RESPONSE_CONVERTER
    );
  }

  findEntidadesFinanciadoras(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaEntidadFinanciadora>> {
    return this.find<IConvocatoriaEntidadFinanciadoraBackend, IConvocatoriaEntidadFinanciadora>(
      `${this.endpointUrl}/${id}/convocatoriaentidadfinanciadoras`,
      options,
      CONVOCATORIA_ENTIDAD_FINANCIADORA_CONVERTER
    );
  }

  getPeriodosJustificacion(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaPeriodoJustificacion>> {
    return this.find<IConvocatoriaPeriodoJustificacionBackend, IConvocatoriaPeriodoJustificacion>(
      `${this.endpointUrl}/${id}/convocatoriaperiodojustificaciones`,
      options,
      CONVOCATORIA_PERIODO_JUSTIFICACION_CONVERTER
    );
  }

  findHitosConvocatoria(idConvocatoria: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaHito>> {
    return this.find<IConvocatoriaHitoResponse, IConvocatoriaHito>(
      `${this.endpointUrl}/${idConvocatoria}/convocatoriahitos`,
      options,
      CONVOCATORIA_HITO_RESPONSE_CONVERTER
    );
  }

  findDocumentos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaDocumento>> {
    return this.find<IConvocatoriaDocumentoBackend, IConvocatoriaDocumento>(
      `${this.endpointUrl}/${id}/convocatoriadocumentos`,
      options,
      CONVOCATORIA_DOCUMENTO_CONVERTER
    );
  }

  findSeguimientosCientificos(id: number, options?: SgiRestFindOptions)
    : Observable<SgiRestListResult<IConvocatoriaPeriodoSeguimientoCientifico>> {
    return this.find<IConvocatoriaPeriodoSeguimientoCientificoBackend, IConvocatoriaPeriodoSeguimientoCientifico>(
      `${this.endpointUrl}/${id}/convocatoriaperiodoseguimientocientificos`,
      options,
      CONVOCATORIA_PERIODO_SEGUIMIENTO_CIENTIFICO_CONVERTER
    );
  }

  getEnlaces(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaEnlace>> {
    return this.find<IConvocatoriaEnlaceBackend, IConvocatoriaEnlace>(
      `${this.endpointUrl}/${id}/convocatoriaenlaces`,
      options,
      CONVOCATORIA_ENLACE_CONVERTER
    );
  }

  findPartidasPresupuestarias(id: number, options?: SgiRestFindOptions)
    : Observable<SgiRestListResult<IConvocatoriaPartidaPresupuestaria>> {
    return this.find<IConvocatoriaPartidaPresupuestariaBackend, IConvocatoriaPartidaPresupuestaria>(
      `${this.endpointUrl}/${id}/convocatoria-partidas-presupuestarias`,
      options,
      CONVOCATORIA_PARTIDA_PRESUPUESTARIA_CONVERTER
    );
  }

  findAllConvocatoriaConceptoGastosNoPermitidos(id: number): Observable<SgiRestListResult<IConvocatoriaConceptoGasto>> {
    return this.find<IConvocatoriaConceptoGastoBackend, IConvocatoriaConceptoGasto>(
      `${this.endpointUrl}/${id}/convocatoriagastos/nopermitidos`,
      undefined,
      CONVOCATORIA_CONCEPTO_GASTO_CONVERTER
    );
  }

  findAllConvocatoriaConceptoGastosPermitidos(id: number): Observable<SgiRestListResult<IConvocatoriaConceptoGasto>> {
    return this.find<IConvocatoriaConceptoGastoBackend, IConvocatoriaConceptoGasto>(
      `${this.endpointUrl}/${id}/convocatoriagastos/permitidos`,
      undefined,
      CONVOCATORIA_CONCEPTO_GASTO_CONVERTER
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

  findPalabrasClave(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaPalabraClave>> {
    return this.find<IConvocatoriaPalabraClaveResponse, IConvocatoriaPalabraClave>(
      `${this.endpointUrl}/${id}/palabrasclave`,
      options,
      CONVOCATORIA_PALABRACLAVE_RESPONSE_CONVERTER);
  }

  findAreaTematicas(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaAreaTematica>> {
    return this.find<IConvocatoriaAreaTematicaBackend, IConvocatoriaAreaTematica>(
      `${this.endpointUrl}/${id}/convocatoriaareatematicas`,
      options,
      CONVOCATORIA_AREA_TEMATICA_CONVERTER
    );
  }

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

}
