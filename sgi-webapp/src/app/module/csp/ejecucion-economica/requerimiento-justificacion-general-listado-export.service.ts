import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IProyectoEntidadFinanciadora } from '@core/models/csp/proyecto-entidad-financiadora';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ProyectoPeriodoJustificacionService } from '@core/services/csp/proyecto-periodo-justificacion/proyecto-periodo-justificacion.service';
import { ProyectoProyectoSgeService } from '@core/services/csp/proyecto-proyecto-sge.service';
import { ProyectoSeguimientoEjecucionEconomicaService } from '@core/services/csp/proyecto-seguimiento-ejecucion-economica/proyecto-seguimiento-ejecucion-economica.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { RequerimientoJustificacionService } from '@core/services/csp/requerimiento-justificacion/requerimiento-justificacion.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { NGXLogger } from 'ngx-logger';
import { concat, from, Observable, of } from 'rxjs';
import { concatMap, map, mergeMap, switchMap, toArray } from 'rxjs/operators';
import { IRequerimientoJustificacionInforme, IRequerimientoJustificacionReportData, IRequerimientoJustificacionReportOptions } from './requerimiento-justificacion-listado-export.service';

const REQUERIMIENTO_JUSTIFICACION_CODIGO_PROYECTO_SGE_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.codigo-sge');
const REQUERIMIENTO_JUSTIFICACION_CODIGO_PROYECTO_SGI_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.gastos.proyecto-sgi-id');
const REQUERIMIENTO_JUSTIFICACION_PROYECTO_CODIGO_EXTERNO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.ref-entidad-convocante');
const REQUERIMIENTO_JUSTIFICACION_PROYECTO_TITULO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.titulo-proyecto');
const REQUERIMIENTO_JUSTIFICACION_PROYECTO_FECHA_INICIO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.fecha-inicio');
const REQUERIMIENTO_JUSTIFICACION_PROYECTO_FECHA_FIN_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.fecha-fin');
const REQUERIMIENTO_JUSTIFICACION_PROYECTO_FECHA_FIN_DEFINITIVA_KEY = marker('csp.proyecto.fecha-fin-definitiva');

const REQUERIMIENTO_JUSTIFICACION_CODIGO_PROYECTO_SGE_FIELD = 'codigoProyectoSGE';
const REQUERIMIENTO_JUSTIFICACION_CODIGO_PROYECTO_SGI_FIELD = 'idProyectoSGI';
const REQUERIMIENTO_JUSTIFICACION_PROYECTO_CODIGO_EXTERNO_FIELD = 'proyectoCodigoExterno';
const REQUERIMIENTO_JUSTIFICACION_PROYECTO_TITULO_FIELD = 'proyectoTitulo';
const REQUERIMIENTO_JUSTIFICACION_PROYECTO_FECHA_INICIO_FIELD = 'proyectoFechaInicio';
const REQUERIMIENTO_JUSTIFICACION_PROYECTO_FECHA_FIN_FIELD = 'proyectoFechaFin';
const REQUERIMIENTO_JUSTIFICACION_PROYECTO_FECHA_FIN_DEFINITIVA_FIELD = 'proyectoFechaFinDefinitiva';

const REQUERIMIENTO_JUSTIFICACION_RESPONSABLE_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.responsable');
const REQUERIMIENTO_JUSTIFICACION_RESPONSABLE_NOMBRE_KEY = marker('sgp.nombre');
const REQUERIMIENTO_JUSTIFICACION_RESPONSABLE_APELLIDOS_KEY = marker('sgp.apellidos');
const REQUERIMIENTO_JUSTIFICACION_RESPONSABLE_EMAIL_KEY = marker('sgp.email');
const REQUERIMIENTO_JUSTIFICACION_TITULO_CONVOCATORIA_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.titulo-convocatoria');

const REQUERIMIENTO_JUSTIFICACION_RESPONSABLE_NOMBRE_FIELD = 'responsableNombre';
const REQUERIMIENTO_JUSTIFICACION_RESPONSABLE_APELLIDOS_FIELD = 'responsableApellidos';
const REQUERIMIENTO_JUSTIFICACION_RESPONSABLE_EMAIL_FIELD = 'responsableEmail';
const REQUERIMIENTO_JUSTIFICACION_TITULO_CONVOCATORIA_FIELD = 'responsableTituloConvocatoria';

const REQUERIMIENTO_JUSTIFICACION_ENTIDAD_FINANCIADORA_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.entidad-financiadora');
const REQUERIMIENTO_JUSTIFICACION_ENTIDAD_FINANCIADORA_NOMBRE_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.entidad-financiadora.nombre');
const REQUERIMIENTO_JUSTIFICACION_ENTIDAD_FINANCIADORA_NUMERO_IDENTIFICACION_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.entidad-financiadora.numero-identificacion');

const REQUERIMIENTO_JUSTIFICACION_ENTIDAD_FINANCIADORA_NOMBRE_FIELD = 'entidadFinanciadoraNombre';
const REQUERIMIENTO_JUSTIFICACION_ENTIDAD_FINANCIADORA_NUMERO_IDENTIFICACION_FIELD = 'entidadFinanciadoraNumeroId';

const REQUERIMIENTO_JUSTIFICACION_IMPORTE_CONCEDIDO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.importe-concedido');
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_CONCEDIDO_CD_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.importe-concedido-directos');
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_CONCEDIDO_CI_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.resumen.importe-concedido-indirectos');

const REQUERIMIENTO_JUSTIFICACION_IMPORTE_CONCEDIDO_FIELD = 'entidadFinanciadoraImporteConcedido';
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_CONCEDIDO_CD_FIELD = 'entidadFinanciadoraImporteConcedidoCD';
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_CONCEDIDO_CI_FIELD = 'entidadFinanciadoraImporteConcedidoCI';

const REQUERIMIENTO_JUSTIFICACION_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento');
const REQUERIMIENTO_JUSTIFICACION_NUM_REQUERIMIENTO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.num-requerimiento');
const REQUERIMIENTO_JUSTIFICACION_TIPO_REQUERIMIENTO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.tipo-requerimiento');
const REQUERIMIENTO_JUSTIFICACION_PERIODO_JUSTIFICACION_KEY = marker('csp.proyecto-periodo-justificacion');
const REQUERIMIENTO_JUSTIFICACION_REQ_PREVIO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.requerimiento-previo');
const REQUERIMIENTO_JUSTIFICACION_FECHA_NOTIFICACION_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimientos.fecha-notificacion');
const REQUERIMIENTO_JUSTIFICACION_FECHA_FIN_ALEGACION_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimientos.fecha-fin-alegacion');
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_ACEPTADO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.importe-aceptado');
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_ACEPTADO_CD_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.importe-aceptado-cd');
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_ACEPTADO_CI_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.importe-aceptado-ci');
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_RECHAZADO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.importe-rechazado');
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_RECHAZADO_CD_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.importe-rechazado-cd');
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_RECHAZADO_CI_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.importe-rechazado-ci');
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRAR_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.importe-reintegrar');
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRAR_CD_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.importe-reintegrar-cd');
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRAR_CI_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.importe-reintegrar-ci');
const REQUERIMIENTO_JUSTIFICACION_SUBVENCION_JUSTIFICADA_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.subvencion-justificada');
const REQUERIMIENTO_JUSTIFICACION_DEFECTO_SUBVENCION_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.defecto-subvencion');
const REQUERIMIENTO_JUSTIFICACION_ANTICIPO_JUSTIFICADO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.anticipo-justificado');
const REQUERIMIENTO_JUSTIFICACION_DEFECTO_ANTICIPO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.defecto-anticipo');
const REQUERIMIENTO_JUSTIFICACION_RECURSO_ESTIMADO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.recurso-estimado');
const REQUERIMIENTO_JUSTIFICACION_FECHA_ALEGACION_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.fecha-alegacion');
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_ALEGADO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.respuesta-alegacion.importe-alegado');
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_ALEGADO_CD_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.respuesta-alegacion.importe-alegado-cd');
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_ALEGADO_CI_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.respuesta-alegacion.importe-alegado-ci');
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRADO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.respuesta-alegacion.importe-reintegrado');
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRADO_CD_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.respuesta-alegacion.importe-reintegrado-cd');
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRADO_CI_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.respuesta-alegacion.importe-reintegrado-ci');
const REQUERIMIENTO_JUSTIFICACION_INTERESES_REINTEGRADOS_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.respuesta-alegacion.intereses-reintegrados');
const REQUERIMIENTO_JUSTIFICACION_FECHA_REINTEGRO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.respuesta-alegacion.fecha-reintegro');
const REQUERIMIENTO_JUSTIFICACION_JUSTIFICANTE_REINTEGRO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.respuesta-alegacion.justificante-reintegro');

const REQUERIMIENTO_JUSTIFICACION_NUM_REQUERIMIENTO_FIELD = 'numRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_TIPO_REQUERIMIENTO_FIELD = 'tipoRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_PERIODO_JUSTIFICACION_FIELD = 'periodoJustificacionRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_REQ_PREVIO_FIELD = 'requerimientoPrevioRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_FECHA_NOTIFICACION_FIELD = 'fechaNotificacionRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_FECHA_FIN_ALEGACION_FIELD = 'fechaFinAlegacionRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_ACEPTADO_FIELD = 'importeAceptadoRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_ACEPTADO_CD_FIELD = 'importeAceptadoCDRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_ACEPTADO_CI_FIELD = 'importeAceptadoCIRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_RECHAZADO_FIELD = 'importeRechazadoRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_RECHAZADO_CD_FIELD = 'importeRechazadoCDRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_RECHAZADO_CI_FIELD = 'importeRechazadoCIRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRAR_FIELD = 'importeReintegrarRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRAR_CD_FIELD = 'importeReintegrarCDRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRAR_CI_FIELD = 'importeReintegrarCIRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_SUBVENCION_JUSTIFICADA_FIELD = 'subvencionJustificadaRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_DEFECTO_SUBVENCION_FIELD = 'defectoSubvencionRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_ANTICIPO_JUSTIFICADO_FIELD = 'anticipoJustificadoRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_DEFECTO_ANTICIPO_FIELD = 'defectoAnticipoRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_RECURSO_ESTIMADO_FIELD = 'recursoEstimadoRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_FECHA_ALEGACION_FIELD = 'fechaAlegacionRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_ALEGADO_FIELD = 'importAlegadoRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_ALEGADO_CD_FIELD = 'importeAlegadoCDRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_ALEGADO_CI_FIELD = 'importeAlegadoCIRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRADO_FIELD = 'importeReintegradoRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRADO_CD_FIELD = 'importeReintegradoCDRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRADO_CI_FIELD = 'importeReintegradoCIRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_INTERESES_REINTEGRADOS_FIELD = 'interesesReintegradosRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_FECHA_REINTEGRO_FIELD = 'fechaReintegroRequerimientoJustificacion';
const REQUERIMIENTO_JUSTIFICACION_JUSTIFICANTE_REINTEGRO_FIELD = 'justificanteReintegroRequerimientoJustificacion';

const COLUMN_VALUE_PREFIX = ': ';

@Injectable()
export class RequerimientoJustificacionGeneralListadoExportService extends
  AbstractTableExportFillService<IRequerimientoJustificacionReportData, IReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly proyectoSeguimientoEjecucionEconomicaService: ProyectoSeguimientoEjecucionEconomicaService,
    private readonly proyectoProyectoSgeService: ProyectoProyectoSgeService,
    private readonly proyectoPeriodoJustificacionService: ProyectoPeriodoJustificacionService,
    private readonly proyectoService: ProyectoService,
    private readonly convocatoriaService: ConvocatoriaService,
    private readonly empresaService: EmpresaService,
    private readonly requerimientoJustificacionService: RequerimientoJustificacionService,
    private luxonDatePipe: LuxonDatePipe,
  ) {
    super(translate);
  }

  public getData(requerimientoJustificacionData: IRequerimientoJustificacionReportData): Observable<IRequerimientoJustificacionReportData> {
    return concat(this.fillDatos(requerimientoJustificacionData),
      this.fillRequerimientosJustificacion(requerimientoJustificacionData)
    );
  }

  private fillRequerimientosJustificacion(requerimientoJustificacionData: IRequerimientoJustificacionReportData):
    Observable<IRequerimientoJustificacionReportData> {
    return this.proyectoSeguimientoEjecucionEconomicaService.findRequerimientosJustificacion(requerimientoJustificacionData.proyectoSge.id).pipe(
      map(({ items }) => items.map(item => {
        // De existir el requerimientoPrevio debe estar dentro del array
        item.requerimientoPrevio = this.findRequerimientoPrevio(item, items);
        return item;
      })),
      mergeMap(requerimientosJustificacion =>
        from(requerimientosJustificacion).pipe(
          mergeMap(requerimientoJustificacion => this.fillProyectoProyectoSge(requerimientoJustificacion)),
          mergeMap(requerimientoJustificacion => this.fillPeriodoJustificacion(requerimientoJustificacion)),
          mergeMap(requerimientoJustificacion => this.fillAlegacion(requerimientoJustificacion as IRequerimientoJustificacionInforme)),
          toArray(),
          map((reqs) => {
            requerimientoJustificacionData.requerimientosJustificacion = reqs as IRequerimientoJustificacionInforme[];
            return requerimientoJustificacionData;
          })
        )
      )
    )
  }

  private findRequerimientoPrevio(
    requerimientoJustificacion: IRequerimientoJustificacion,
    requerimientosJustificacionToFind: IRequerimientoJustificacion[]): IRequerimientoJustificacion | undefined {
    if (requerimientoJustificacion.requerimientoPrevio?.id) {
      return requerimientosJustificacionToFind.find(requerimientoJustificacionToFind =>
        requerimientoJustificacionToFind.id === requerimientoJustificacion.requerimientoPrevio.id);
    } else {
      return requerimientoJustificacion.requerimientoPrevio;
    }
  }

  private fillAlegacion(requerimientoJustificacion: IRequerimientoJustificacionInforme):
    Observable<IRequerimientoJustificacion> {
    if (requerimientoJustificacion?.id) {
      return this.requerimientoJustificacionService.findAlegacion(requerimientoJustificacion?.id).pipe(
        map(alegacion => {
          requerimientoJustificacion.alegacion = alegacion;
          return requerimientoJustificacion;
        }),
      );
    } else {
      return of(requerimientoJustificacion);
    }
  }

  private fillProyectoProyectoSge(requerimientoJustificacion: IRequerimientoJustificacion):
    Observable<IRequerimientoJustificacion> {
    if (requerimientoJustificacion.proyectoProyectoSge?.id) {
      return this.proyectoProyectoSgeService.findById(requerimientoJustificacion.proyectoProyectoSge.id).pipe(
        map(proyectoProyectoSge => {
          requerimientoJustificacion.proyectoProyectoSge = proyectoProyectoSge;
          return requerimientoJustificacion;
        }),
      );
    } else {
      return of(requerimientoJustificacion);
    }
  }

  private fillPeriodoJustificacion(requerimientoJustificacion: IRequerimientoJustificacion):
    Observable<IRequerimientoJustificacion> {
    if (requerimientoJustificacion.proyectoPeriodoJustificacion?.id) {
      return this.proyectoPeriodoJustificacionService.findById(requerimientoJustificacion.proyectoPeriodoJustificacion.id).pipe(
        map(proyectoPeriodoJustificacion => {
          requerimientoJustificacion.proyectoPeriodoJustificacion = proyectoPeriodoJustificacion;
          return requerimientoJustificacion;
        })
      );
    } else {
      return of(requerimientoJustificacion);
    }
  }

  private fillDatos(requerimientoJustificacionData: IRequerimientoJustificacionReportData):
    Observable<IRequerimientoJustificacionReportData> {
    if (requerimientoJustificacionData.id) {
      return this.proyectoService.findById(requerimientoJustificacionData.id).pipe(
        map(proyecto => {
          requerimientoJustificacionData.proyecto = proyecto;
          return requerimientoJustificacionData;
        }),
        switchMap(requerimientoJustificacionData => {
          if (requerimientoJustificacionData.proyecto.convocatoriaId) {
            return this.convocatoriaService.findById(requerimientoJustificacionData.proyecto.convocatoriaId).pipe(
              map(convocatoria => {
                requerimientoJustificacionData.tituloConvocatoria = convocatoria.titulo;
                return requerimientoJustificacionData;
              })
            );
          } else {
            return of(requerimientoJustificacionData);
          }
        }),
        switchMap(requerimientoJustificacionData => {
          return this.fillEntidadesFinanciadoras(requerimientoJustificacionData);
        }),
        switchMap(requerimientoJustificacionData => {
          return this.calculateImportesConcedidos(requerimientoJustificacionData);
        })
      )
    } else {
      return of(requerimientoJustificacionData);
    }
  }

  private fillEntidadesFinanciadoras(requerimientoJustificacionData: IRequerimientoJustificacionReportData):
    Observable<IRequerimientoJustificacionReportData> {
    return this.proyectoService.findEntidadesFinanciadoras(requerimientoJustificacionData.proyecto.id)
      .pipe(
        concatMap(({ items }) => this.fillEmpresa(items)),
        map(entidadesFinanciadoras => {
          requerimientoJustificacionData.entidadesFinanciadoras = entidadesFinanciadoras ?? [];
          return requerimientoJustificacionData;
        })
      );
  }

  private fillEmpresa(entidades: IProyectoEntidadFinanciadora[]): Observable<IProyectoEntidadFinanciadora[]> {
    return from(entidades).pipe(
      concatMap(entidad => this.empresaService.findById(entidad.empresa.id)
        .pipe(
          map(empresa => {
            entidad.empresa = empresa;
            return entidad;
          })
        )
      ),
      toArray()
    );
  }

  private calculateImportesConcedidos(requerimientoJustificacionData: IRequerimientoJustificacionReportData):
    Observable<IRequerimientoJustificacionReportData> {
    if (requerimientoJustificacionData.proyecto && (requerimientoJustificacionData.proyecto.importeConcedido == null || requerimientoJustificacionData.proyecto.importeConcedidoCostesIndirectos == null)) {
      return this.proyectoService.getProyectoPresupuestoTotales(requerimientoJustificacionData.proyecto.id).pipe(
        map(presupuestosTotales => {
          requerimientoJustificacionData.proyecto.importeConcedido = requerimientoJustificacionData.proyecto.importeConcedido ?? presupuestosTotales.importeTotalConcedidoUniversidad;
          requerimientoJustificacionData.proyecto.importeConcedidoCostesIndirectos = requerimientoJustificacionData.proyecto.importeConcedidoCostesIndirectos ??
            presupuestosTotales.importeTotalConcedidoUniversidadCostesIndirectos;
          return requerimientoJustificacionData;
        })
      );
    } else {
      return of(requerimientoJustificacionData);
    }
  }

  public fillColumns(
    requerimientos: IRequerimientoJustificacionReportData[],
    reportConfig: IReportConfig<IRequerimientoJustificacionReportOptions>
  ): ISgiColumnReport[] {

    const columns: ISgiColumnReport[] = [];
    const columnCodigoSGE: ISgiColumnReport = {
      name: REQUERIMIENTO_JUSTIFICACION_CODIGO_PROYECTO_SGE_FIELD,
      title: this.translate.instant(REQUERIMIENTO_JUSTIFICACION_CODIGO_PROYECTO_SGE_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnCodigoSGE);

    const columnCodigoSGI: ISgiColumnReport = {
      name: REQUERIMIENTO_JUSTIFICACION_CODIGO_PROYECTO_SGI_FIELD,
      title: this.translate.instant(REQUERIMIENTO_JUSTIFICACION_CODIGO_PROYECTO_SGI_KEY),
      type: ColumnType.NUMBER,
      format: '#'
    };
    columns.push(columnCodigoSGI);

    const columnCodigoExterno: ISgiColumnReport = {
      name: REQUERIMIENTO_JUSTIFICACION_PROYECTO_CODIGO_EXTERNO_FIELD,
      title: this.translate.instant(REQUERIMIENTO_JUSTIFICACION_PROYECTO_CODIGO_EXTERNO_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnCodigoExterno);

    const columnTituloProyecto: ISgiColumnReport = {
      name: REQUERIMIENTO_JUSTIFICACION_PROYECTO_TITULO_FIELD,
      title: this.translate.instant(REQUERIMIENTO_JUSTIFICACION_PROYECTO_TITULO_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnTituloProyecto);

    const columnFechaInicioProyecto: ISgiColumnReport = {
      name: REQUERIMIENTO_JUSTIFICACION_PROYECTO_FECHA_INICIO_FIELD,
      title: this.translate.instant(REQUERIMIENTO_JUSTIFICACION_PROYECTO_FECHA_INICIO_KEY),
      type: ColumnType.DATE,
    };
    columns.push(columnFechaInicioProyecto);

    const columnFechaFinProyecto: ISgiColumnReport = {
      name: REQUERIMIENTO_JUSTIFICACION_PROYECTO_FECHA_FIN_FIELD,
      title: this.translate.instant(REQUERIMIENTO_JUSTIFICACION_PROYECTO_FECHA_FIN_KEY),
      type: ColumnType.DATE,
    };
    columns.push(columnFechaFinProyecto);

    const columnFechaFinDefinitiva: ISgiColumnReport = {
      name: REQUERIMIENTO_JUSTIFICACION_PROYECTO_FECHA_FIN_DEFINITIVA_FIELD,
      title: this.translate.instant(REQUERIMIENTO_JUSTIFICACION_PROYECTO_FECHA_FIN_DEFINITIVA_KEY),
      type: ColumnType.DATE,
    };
    columns.push(columnFechaFinDefinitiva);


    const isRequerimientosNull = requerimientos === undefined || requerimientos === null;
    const maxNumResponsables = !isRequerimientosNull ? Math.max(...requerimientos.map(r => r.responsables ? r.responsables?.length : 0)) : 0;

    const titleResponsable = this.translate.instant(REQUERIMIENTO_JUSTIFICACION_RESPONSABLE_KEY);

    for (let i = 0; i < maxNumResponsables; i++) {
      const idResponsables: string = String(i + 1);
      const columnNombreResponsable: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_RESPONSABLE_NOMBRE_FIELD + idResponsables,
        title: titleResponsable + idResponsables + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_RESPONSABLE_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNombreResponsable);

      const columnApellidosResponsable: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_RESPONSABLE_APELLIDOS_FIELD + idResponsables,
        title: titleResponsable + idResponsables + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_RESPONSABLE_APELLIDOS_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnApellidosResponsable);

      const columnEmailResponsable: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_RESPONSABLE_EMAIL_FIELD + idResponsables,
        title: titleResponsable + idResponsables + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_RESPONSABLE_EMAIL_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnEmailResponsable);
    }

    const columnTituloConvocatoria: ISgiColumnReport = {
      name: REQUERIMIENTO_JUSTIFICACION_TITULO_CONVOCATORIA_FIELD,
      title: this.translate.instant(REQUERIMIENTO_JUSTIFICACION_TITULO_CONVOCATORIA_KEY),
      type: ColumnType.STRING,
    };
    columns.push(columnTituloConvocatoria);

    const maxNumEntidadesFinanciadoras = !isRequerimientosNull ? Math.max(...requerimientos.map(r => r.entidadesFinanciadoras ? r.entidadesFinanciadoras?.length : 0)) : 0;

    const titleEntidadFinanciadora = this.translate.instant(REQUERIMIENTO_JUSTIFICACION_ENTIDAD_FINANCIADORA_KEY);

    for (let i = 0; i < maxNumEntidadesFinanciadoras; i++) {
      const idEntidadesFinanciadoras: string = String(i + 1);
      const columnNombreResponsable: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_ENTIDAD_FINANCIADORA_NOMBRE_FIELD + idEntidadesFinanciadoras,
        title: titleEntidadFinanciadora + idEntidadesFinanciadoras + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_ENTIDAD_FINANCIADORA_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNombreResponsable);

      const columnApellidosResponsable: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_ENTIDAD_FINANCIADORA_NUMERO_IDENTIFICACION_FIELD + idEntidadesFinanciadoras,
        title: titleEntidadFinanciadora + idEntidadesFinanciadoras + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_ENTIDAD_FINANCIADORA_NUMERO_IDENTIFICACION_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnApellidosResponsable);
    }

    const columnImporteConcedido: ISgiColumnReport = {
      name: REQUERIMIENTO_JUSTIFICACION_IMPORTE_CONCEDIDO_FIELD,
      title: this.translate.instant(REQUERIMIENTO_JUSTIFICACION_IMPORTE_CONCEDIDO_KEY),
      type: ColumnType.NUMBER,
      format: null
    };
    columns.push(columnImporteConcedido);

    const columnImporteConcedidoCD: ISgiColumnReport = {
      name: REQUERIMIENTO_JUSTIFICACION_IMPORTE_CONCEDIDO_CD_FIELD,
      title: this.translate.instant(REQUERIMIENTO_JUSTIFICACION_IMPORTE_CONCEDIDO_CD_KEY),
      type: ColumnType.NUMBER,
      format: null
    };
    columns.push(columnImporteConcedidoCD);

    const columnImporteConcedidoCI: ISgiColumnReport = {
      name: REQUERIMIENTO_JUSTIFICACION_IMPORTE_CONCEDIDO_CI_FIELD,
      title: this.translate.instant(REQUERIMIENTO_JUSTIFICACION_IMPORTE_CONCEDIDO_CI_KEY),
      type: ColumnType.NUMBER,
      format: null
    };
    columns.push(columnImporteConcedidoCI);

    const maxNumRequerimientosJustificacion = !isRequerimientosNull ? Math.max(...requerimientos.map(r => r.requerimientosJustificacion ? r.requerimientosJustificacion?.length : 0)) : 0;

    const titleRequerimientos = this.translate.instant(REQUERIMIENTO_JUSTIFICACION_KEY);

    for (let i = 0; i < maxNumRequerimientosJustificacion; i++) {
      const idRequerimientosJustificacion: string = String(i + 1);
      const columnNumRequerimiento: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_NUM_REQUERIMIENTO_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_NUM_REQUERIMIENTO_KEY),
        type: ColumnType.NUMBER,
        format: '#'
      };
      columns.push(columnNumRequerimiento);

      const columnTipoRequerimiento: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_TIPO_REQUERIMIENTO_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_TIPO_REQUERIMIENTO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnTipoRequerimiento);

      const columnPeriodoJustificacion: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_PERIODO_JUSTIFICACION_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_PERIODO_JUSTIFICACION_KEY),
        type: ColumnType.NUMBER,
        format: null
      };
      columns.push(columnPeriodoJustificacion);

      const columnReqPrevio: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_REQ_PREVIO_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_REQ_PREVIO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnReqPrevio);

      const columnFechaNotificacion: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_FECHA_NOTIFICACION_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_FECHA_NOTIFICACION_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnFechaNotificacion);

      const columnFechaFinAlegacion: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_FECHA_FIN_ALEGACION_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_FECHA_FIN_ALEGACION_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnFechaFinAlegacion);

      const columnImporteAceptado: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_IMPORTE_ACEPTADO_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_IMPORTE_ACEPTADO_KEY),
        type: ColumnType.NUMBER,
        format: null
      };
      columns.push(columnImporteAceptado);

      const columnImporteAceptadoCD: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_IMPORTE_ACEPTADO_CD_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_IMPORTE_ACEPTADO_CD_KEY),
        type: ColumnType.NUMBER,
        format: null
      };
      columns.push(columnImporteAceptadoCD);

      const columnImporteAceptadoCI: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_IMPORTE_ACEPTADO_CI_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_IMPORTE_ACEPTADO_CI_KEY),
        type: ColumnType.NUMBER,
        format: null
      };
      columns.push(columnImporteAceptadoCI);

      const columnImporteRechazado: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_IMPORTE_RECHAZADO_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_IMPORTE_RECHAZADO_KEY),
        type: ColumnType.NUMBER,
        format: null
      };
      columns.push(columnImporteRechazado);

      const columnImporteRechazadoCD: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_IMPORTE_RECHAZADO_CD_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_IMPORTE_RECHAZADO_CD_KEY),
        type: ColumnType.NUMBER,
        format: null
      };
      columns.push(columnImporteRechazadoCD);

      const columnImporteRechazadoCI: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_IMPORTE_RECHAZADO_CI_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_IMPORTE_RECHAZADO_CI_KEY),
        type: ColumnType.NUMBER,
        format: null
      };
      columns.push(columnImporteRechazadoCI);

      const columnImporteReintegrar: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRAR_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRAR_KEY),
        type: ColumnType.NUMBER,
        format: null
      };
      columns.push(columnImporteReintegrar);

      const columnImporteReintegrarCD: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRAR_CD_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRAR_CD_KEY),
        type: ColumnType.NUMBER,
        format: null
      };
      columns.push(columnImporteReintegrarCD);

      const columnImporteReintegrarCI: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRAR_CI_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRAR_CI_KEY),
        type: ColumnType.NUMBER,
        format: null
      };
      columns.push(columnImporteReintegrarCI);

      const columnSubvencionJustificada: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_SUBVENCION_JUSTIFICADA_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_SUBVENCION_JUSTIFICADA_KEY),
        type: ColumnType.NUMBER,
        format: null
      };
      columns.push(columnSubvencionJustificada);

      const columnDefectoSubvencion: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_DEFECTO_SUBVENCION_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_DEFECTO_SUBVENCION_KEY),
        type: ColumnType.NUMBER,
        format: null
      };
      columns.push(columnDefectoSubvencion);

      const columnAnticipoJustificado: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_ANTICIPO_JUSTIFICADO_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_ANTICIPO_JUSTIFICADO_KEY),
        type: ColumnType.NUMBER,
        format: null
      };
      columns.push(columnAnticipoJustificado);

      const columnDefectoAnticipo: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_DEFECTO_ANTICIPO_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_DEFECTO_ANTICIPO_KEY),
        type: ColumnType.NUMBER,
        format: null
      };
      columns.push(columnDefectoAnticipo);

      const columnRecursoEstimado: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_RECURSO_ESTIMADO_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_RECURSO_ESTIMADO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnRecursoEstimado);

      const columnFechaAlegacion: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_FECHA_ALEGACION_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_FECHA_ALEGACION_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaAlegacion);

      const columnImporteAlegado: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_IMPORTE_ALEGADO_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_IMPORTE_ALEGADO_KEY),
        type: ColumnType.NUMBER,
        format: null
      };
      columns.push(columnImporteAlegado);

      const columnImporteAlegadoCD: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_IMPORTE_ALEGADO_CD_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_IMPORTE_ALEGADO_CD_KEY),
        type: ColumnType.NUMBER,
        format: null
      };
      columns.push(columnImporteAlegadoCD);

      const columnImporteAlegadoCI: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_IMPORTE_ALEGADO_CI_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_IMPORTE_ALEGADO_CI_KEY),
        type: ColumnType.NUMBER,
        format: null
      };
      columns.push(columnImporteAlegadoCI);

      const columnImporteReintegrado: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRADO_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRADO_KEY),
        type: ColumnType.NUMBER,
        format: null
      };
      columns.push(columnImporteReintegrado);

      const columnImporteReintegradoCD: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRADO_CD_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRADO_CD_KEY),
        type: ColumnType.NUMBER,
        format: null
      };
      columns.push(columnImporteReintegradoCD);

      const columnImporteReintegradoCI: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRADO_CI_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_IMPORTE_REINTEGRADO_CI_KEY),
        type: ColumnType.NUMBER,
        format: null
      };
      columns.push(columnImporteReintegradoCI);

      const columnInteresesReintegrados: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_INTERESES_REINTEGRADOS_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_INTERESES_REINTEGRADOS_KEY),
        type: ColumnType.NUMBER,
        format: '#'
      };
      columns.push(columnInteresesReintegrados);

      const columnFechaReintegro: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_FECHA_REINTEGRO_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_FECHA_REINTEGRO_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaReintegro);

      const columnJustificanteReintegro: ISgiColumnReport = {
        name: REQUERIMIENTO_JUSTIFICACION_JUSTIFICANTE_REINTEGRO_FIELD + idRequerimientosJustificacion,
        title: titleRequerimientos + idRequerimientosJustificacion + this.getValuePrefix('') + this.translate.instant(REQUERIMIENTO_JUSTIFICACION_JUSTIFICANTE_REINTEGRO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnJustificanteReintegro);
    }

    return columns;

  }

  public fillRows(requerimientos: IRequerimientoJustificacionReportData[],
    index: number, reportConfig: IReportConfig<IRequerimientoJustificacionReportOptions>): any[] {

    const requerimiento = requerimientos[index];
    const isRequerimientosNull = requerimientos === undefined || requerimientos === null;
    const maxNumResponsables = !isRequerimientosNull ? Math.max(...requerimientos.map(r => r.responsables ? r.responsables?.length : 0)) : 0;
    const maxNumEntidadesFinanciadoras = !isRequerimientosNull ? Math.max(...requerimientos.map(r => r.entidadesFinanciadoras ? r.entidadesFinanciadoras?.length : 0)) : 0;
    const maxNumRequerimientosJustificacion = !isRequerimientosNull ? Math.max(...requerimientos.map(r => r.requerimientosJustificacion ? r.requerimientosJustificacion?.length : 0)) : 0;

    const rows: any[] = [];

    rows.push(requerimiento?.proyectoSge?.id ?? '');
    rows.push(requerimiento?.proyecto?.id ?? '');
    rows.push(requerimiento?.proyecto?.codigoExterno ?? '');
    rows.push(requerimiento?.nombre ?? '');
    rows.push(LuxonUtils.toBackend(requerimiento?.fechaInicio) ?? '');
    rows.push(LuxonUtils.toBackend(requerimiento?.fechaFin) ?? '');
    rows.push(LuxonUtils.toBackend(requerimiento?.proyecto?.fechaFinDefinitiva) ?? '');

    for (let i = 0; i < maxNumResponsables; i++) {
      if (requerimiento.responsables && requerimiento.responsables[i]) {
        rows.push(requerimiento.responsables[i].nombre ?? '');
        rows.push(requerimiento.responsables[i].apellidos ?? '');
        requerimiento.responsables[i].emails.filter(e => e.principal).forEach(e => rows.push(e.email ?? ''));
      } else {
        rows.push('');
        rows.push('');
        rows.push('');
      }
    }
    rows.push(requerimiento.tituloConvocatoria ?? '');

    for (let i = 0; i < maxNumEntidadesFinanciadoras; i++) {
      if (requerimiento.entidadesFinanciadoras && requerimiento.entidadesFinanciadoras[i]) {
        rows.push(requerimiento.entidadesFinanciadoras[i].empresa?.nombre ?? '');
        rows.push(requerimiento.entidadesFinanciadoras[i].empresa?.numeroIdentificacion ?? '');
      } else {
        rows.push('');
        rows.push('');
      }
    }

    if (requerimiento.proyecto?.importeConcedido || requerimiento.proyecto?.importeConcedidoCostesIndirectos || requerimiento.proyecto?.importePresupuestoSocios ||
      requerimiento.proyecto?.importeConcedidoSocios || requerimiento.proyecto?.totalImportePresupuesto) {
      rows.push((requerimiento.proyecto?.importeConcedido ?? 0) + (requerimiento.proyecto?.importeConcedidoCostesIndirectos ?? 0));
    } else {
      rows.push(requerimiento.proyecto?.totalImporteConcedido ?? '');
    }
    rows.push(requerimiento.proyecto?.importeConcedido ?? '');
    rows.push(requerimiento.proyecto?.importeConcedidoCostesIndirectos ?? '');

    for (let i = 0; i < maxNumRequerimientosJustificacion; i++) {
      if (requerimiento.requerimientosJustificacion && requerimiento.requerimientosJustificacion[i]) {
        rows.push(requerimiento.requerimientosJustificacion[i].numRequerimiento ?? '');
        rows.push(requerimiento.requerimientosJustificacion[i].tipoRequerimiento?.nombre ?? '');
        rows.push(requerimiento.requerimientosJustificacion[i].proyectoPeriodoJustificacion?.numPeriodo ?? '');
        rows.push(requerimiento.requerimientosJustificacion[i].requerimientoPrevio?.numRequerimiento ? (requerimiento.requerimientosJustificacion[i].requerimientoPrevio?.numRequerimiento + ' - ' +
          requerimiento.requerimientosJustificacion[i].requerimientoPrevio?.tipoRequerimiento?.nombre ?? '') : '');
        rows.push(requerimiento.requerimientosJustificacion[i].fechaNotificacion ? this.luxonDatePipe.transform(LuxonUtils.toBackend(requerimiento.requerimientosJustificacion[i].fechaNotificacion, true), 'shortDate') : '');
        rows.push(requerimiento.requerimientosJustificacion[i].fechaFinAlegacion ? this.luxonDatePipe.transform(LuxonUtils.toBackend(requerimiento.requerimientosJustificacion[i].fechaFinAlegacion, true), 'short') : '');
        rows.push(requerimiento.requerimientosJustificacion[i].importeAceptado ?? '');
        rows.push(requerimiento.requerimientosJustificacion[i].importeAceptadoCd ?? '');
        rows.push(requerimiento.requerimientosJustificacion[i].importeAceptadoCi ?? '');
        rows.push(requerimiento.requerimientosJustificacion[i].importeRechazado ?? '');
        rows.push(requerimiento.requerimientosJustificacion[i].importeRechazadoCd ?? '');
        rows.push(requerimiento.requerimientosJustificacion[i].importeRechazadoCi ?? '');
        rows.push(requerimiento.requerimientosJustificacion[i].importeReintegrar ?? '');
        rows.push(requerimiento.requerimientosJustificacion[i].importeReintegrarCd ?? '');
        rows.push(requerimiento.requerimientosJustificacion[i].importeReintegrarCi ?? '');
        rows.push(requerimiento.requerimientosJustificacion[i].subvencionJustificada ?? '');
        rows.push(requerimiento.requerimientosJustificacion[i].defectoSubvencion ?? '');
        rows.push(requerimiento.requerimientosJustificacion[i].anticipoJustificado ?? '');
        rows.push(requerimiento.requerimientosJustificacion[i].defectoAnticipo ?? '');
        rows.push(this.notIsNullAndNotUndefined(requerimiento.requerimientosJustificacion[i].recursoEstimado)
          ? this.getI18nBooleanYesNo(requerimiento.requerimientosJustificacion[i].recursoEstimado) ?? '' : '');
        rows.push(requerimiento.requerimientosJustificacion[i].alegacion?.fechaAlegacion ? LuxonUtils.toBackend(requerimiento.requerimientosJustificacion[i].alegacion?.fechaAlegacion) : '');
        rows.push(requerimiento.requerimientosJustificacion[i].alegacion?.importeAlegado ?? '');
        rows.push(requerimiento.requerimientosJustificacion[i].alegacion?.importeAlegadoCd ?? '');
        rows.push(requerimiento.requerimientosJustificacion[i].alegacion?.importeAlegadoCi ?? '');
        rows.push(requerimiento.requerimientosJustificacion[i].alegacion?.importeReintegrado ?? '');
        rows.push(requerimiento.requerimientosJustificacion[i].alegacion?.importeReintegradoCd ?? '');
        rows.push(requerimiento.requerimientosJustificacion[i].alegacion?.importeReintegradoCi ?? '');
        rows.push(requerimiento.requerimientosJustificacion[i].alegacion?.interesesReintegrados ?? '');
        rows.push(requerimiento.requerimientosJustificacion[i].alegacion?.fechaReintegro ? LuxonUtils.toBackend(requerimiento.requerimientosJustificacion[i].alegacion?.fechaReintegro) : '');
        rows.push(requerimiento.requerimientosJustificacion[i].alegacion?.justificanteReintegro ?? '');
      } else {
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
        rows.push('');
      }
    }

    return rows;
  }

  private getValuePrefix(prefix: string): string {
    if (!prefix) {
      return COLUMN_VALUE_PREFIX;
    }
    return '';
  }
}
