import { PercentPipe } from '@angular/common';
import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { CLASIFICACION_CVN_MAP } from '@core/enums/clasificacion-cvn';
import { ESTADO_MAP } from '@core/models/csp/estado-proyecto';
import { CAUSA_EXENCION_MAP } from '@core/models/csp/proyecto';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { AreaTematicaService } from '@core/services/csp/area-tematica.service';
import { ContextoProyectoService } from '@core/services/csp/contexto-proyecto.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IProyectoReportData, IProyectoReportOptions } from './proyecto-listado-export.service';

const ID_KEY = marker('csp.proyecto.id-interno');
const ID_SGE_KEY = marker('sge.proyecto.identificador');
const TITULO_KEY = marker('csp.proyecto.titulo');
const CODIGO_EXTERNO_KEY = marker('csp.proyecto-relacion.ref-entidad-convocante');
const CODIGO_INTERNO_KEY = marker('csp.proyecto.codigo-interno');
const ESTADO_KEY = marker('csp.proyecto.estado');
const ACRONIMO_KEY = marker('csp.proyecto.acronimo');
const FECHA_ESTADO_KEY = marker('csp.estado-validacion-ip.fecha-estado');
const AMBITO_GEOGRAFICO_KEY = marker('csp.fuente-financiacion.ambito-geografico');
const UNIDAD_GESTION_KEY = marker('csp.modelo-ejecucion-unidad-gestion');
const MODELO_EJECUCION_KEY = marker('csp.proyecto.modelo-ejecucion');
const FINALIDAD_KEY = marker('csp.convocatoria.finalidad');
const FECHA_INICIO_KEY = marker('csp.proyecto.fecha-inicio');
const FECHA_FIN_KEY = marker('csp.proyecto.fecha-fin');
const FECHA_FIN_DEFINITIVA_KEY = marker('csp.proyecto.fecha-fin-definitiva');
const CONFIDENCIAL_KEY = marker('csp.proyecto.confidencial');
const CLASIFICACION_CVN_KEY = marker('csp.convocatoria.clasificacion-produccion-cientifica');
const COORDINADO_KEY = marker('csp.proyecto.proyecto-coordinado');
const COORDINADOR_EXTERNO_KEY = marker('csp.proyecto.coordinador-externo');
const PROYECTO_COLABORATIVO_KEY = marker('csp.proyecto.proyecto-colaborativo');
const PORCENTAJE_IVA_KEY = marker('csp.ejecucion-economica.iva');
const CAUSA_EXENCION_IVA_KEY = marker('csp.proyecto.causa-exencion');
const AREA_TEMATICA_KEY = marker('csp.area-tematica.nombre');

@Injectable()
export class ProyectoGeneralListadoExportService extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly proyectoService: ProyectoService,
    private readonly contextoProyectoService: ContextoProyectoService,
    private readonly unidadGestionService: UnidadGestionService,
    private readonly areaTematicaService: AreaTematicaService,
    private readonly percentPipe: PercentPipe,
  ) {
    super(translate);
  }

  public getData(proyectoData: IProyectoReportData): Observable<IProyectoReportData> {
    return this.proyectoService.hasProyectoProrrogas(proyectoData.id).pipe(
      map(value => {
        proyectoData.prorrogado = value;
        return proyectoData;
      }),
      switchMap(() => this.proyectoService.findAllProyectosSgeProyecto(proyectoData.id).pipe(
        map(value => {
          proyectoData.proyectosSGE = value.items.map(element => element.proyectoSge.id).join(', ');
          return proyectoData;
        }))
      ),
      switchMap((row) => {
        return this.contextoProyectoService.findById(proyectoData.id).pipe(
          map(contextoProyecto => {
            proyectoData.contextoProyecto = contextoProyecto;
            return proyectoData;
          })
        );
      }),
      switchMap(() => {
        if (proyectoData.unidadGestion?.id) {
          return this.unidadGestionService.findById(proyectoData.unidadGestion?.id).pipe(
            map(unidadGestion => {
              proyectoData.unidadGestion = unidadGestion;
              return proyectoData;
            })
          );
        } else {
          return of(proyectoData);
        }
      }),
      switchMap(() => {
        if (proyectoData.contextoProyecto?.areaTematica?.id) {
          return this.areaTematicaService.findById(proyectoData.contextoProyecto?.areaTematica?.id).pipe(
            map(areaTematica => {
              proyectoData.contextoProyecto.areaTematica = areaTematica;
              return proyectoData;
            })
          );
        } else {
          return of(proyectoData);
        }
      })
    );
  }

  public fillColumns(
    proyectos: IProyectoReportData[],
    reportConfig: IReportConfig<IProyectoReportOptions>
  ): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [
      {
        title: this.translate.instant(ID_KEY),
        name: 'id',
        type: ColumnType.NUMBER,
        format: '#'
      },
      {
        title: this.translate.instant(ID_SGE_KEY),
        name: 'idSGE',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(TITULO_KEY),
        name: 'titulo',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(ACRONIMO_KEY),
        name: 'acronimo',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(CODIGO_EXTERNO_KEY),
        name: 'codigoExterno',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(CODIGO_INTERNO_KEY),
        name: 'codigoInterno',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(ESTADO_KEY),
        name: 'estado',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(FECHA_ESTADO_KEY),
        name: 'fechaEstado',
        type: ColumnType.DATE
      },
      {
        title: this.translate.instant(AMBITO_GEOGRAFICO_KEY),
        name: 'ambitoGeografico',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(UNIDAD_GESTION_KEY),
        name: 'unidadGestion',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(MODELO_EJECUCION_KEY),
        name: 'modeloEjecucion',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(FINALIDAD_KEY),
        name: 'finalidad',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(FECHA_INICIO_KEY),
        name: 'fechaInicio',
        type: ColumnType.DATE
      },
      {
        title: this.translate.instant(FECHA_FIN_KEY),
        name: 'fechaFin',
        type: ColumnType.DATE
      },
      {
        title: this.translate.instant(FECHA_FIN_DEFINITIVA_KEY),
        name: 'fechaFinDefinitiva',
        type: ColumnType.DATE
      },
      {
        title: this.translate.instant(CONFIDENCIAL_KEY),
        name: 'confidencial',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(CLASIFICACION_CVN_KEY),
        name: 'clasificacionCVN',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(COORDINADO_KEY),
        name: 'coordinado',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(COORDINADOR_EXTERNO_KEY),
        name: 'coordinadorExterno',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(PROYECTO_COLABORATIVO_KEY),
        name: 'proyectoColaborativo',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(PORCENTAJE_IVA_KEY),
        name: 'porcentajeIVA',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(CAUSA_EXENCION_IVA_KEY),
        name: 'causaExencionIVA',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(AREA_TEMATICA_KEY),
        name: 'areaTematica',
        type: ColumnType.STRING
      },
    ];

    return columns;
  }

  public fillRows(resultados: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {
    const proyecto = resultados[index];

    const elementsRow: any[] = [];
    elementsRow.push(proyecto.id);
    elementsRow.push(proyecto.proyectosSGE);
    elementsRow.push(proyecto.titulo);
    elementsRow.push(proyecto.acronimo);
    elementsRow.push(proyecto.codigoExterno);
    elementsRow.push(proyecto.codigoInterno);
    elementsRow.push(proyecto.estado?.estado ? this.translate.instant(ESTADO_MAP.get(proyecto.estado?.estado)) : '');
    elementsRow.push(LuxonUtils.toBackend(proyecto.estado?.fechaEstado));
    elementsRow.push(proyecto.ambitoGeografico?.nombre);
    elementsRow.push(proyecto.unidadGestion?.nombre);
    elementsRow.push(proyecto.modeloEjecucion?.nombre);
    elementsRow.push(proyecto.finalidad?.nombre);
    elementsRow.push(LuxonUtils.toBackend(proyecto.fechaInicio));
    elementsRow.push(LuxonUtils.toBackend(proyecto.fechaFin));
    elementsRow.push(LuxonUtils.toBackend(proyecto.fechaFinDefinitiva));
    elementsRow.push(this.notIsNullAndNotUndefined(proyecto.confidencial) ? this.getI18nBooleanYesNo(proyecto.confidencial) : '');
    elementsRow.push(proyecto.clasificacionCVN ? this.translate.instant(CLASIFICACION_CVN_MAP.get(proyecto.clasificacionCVN)) : '');
    elementsRow.push(this.notIsNullAndNotUndefined(proyecto.coordinado) ? this.getI18nBooleanYesNo(proyecto.coordinado) : '');
    elementsRow.push(this.notIsNullAndNotUndefined(proyecto.coordinadorExterno) ? this.getI18nBooleanYesNo(proyecto.coordinadorExterno) : '');
    elementsRow.push(this.notIsNullAndNotUndefined(proyecto.colaborativo) ? this.getI18nBooleanYesNo(proyecto.colaborativo) : '');
    elementsRow.push(this.percentPipe.transform(proyecto.iva?.iva / 100));
    elementsRow.push(proyecto.causaExencion ? this.translate.instant(CAUSA_EXENCION_MAP.get(proyecto.causaExencion)) : '');
    elementsRow.push(proyecto.contextoProyecto?.areaTematica?.nombre);
    return elementsRow;
  }
}
