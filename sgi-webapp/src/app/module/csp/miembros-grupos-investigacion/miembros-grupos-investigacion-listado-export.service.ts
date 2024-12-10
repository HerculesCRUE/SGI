import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DEDICACION_MAP } from '@core/models/csp/grupo-equipo';
import { IGrupoLineaInvestigacion } from '@core/models/csp/grupo-linea-investigacion';
import { IGrupoLineaInvestigador } from '@core/models/csp/grupo-linea-investigador';
import { TIPO_MAP } from '@core/models/csp/grupo-tipo';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { GrupoLineaInvestigacionService } from '@core/services/csp/grupo-linea-investigacion/grupo-linea-investigacion.service';
import { GrupoLineaInvestigadorService } from '@core/services/csp/grupo-linea-investigador/grupo-linea-investigador.service';
import { LineaInvestigacionService } from '@core/services/csp/linea-investigacion/linea-investigacion.service';
import { AbstractTableExportService, IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { forkJoin, Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IMiembroGrupoInvestigacionListadoData } from '../miembros-grupos-investigacion/miembros-grupos-investigacion-listado/miembros-grupos-investigacion-listado.component';

const ACTIVO_KEY = marker('csp.miembros-grupos-investigacion.activo-grupo');
const APELLIDOS_KEY = marker('csp.miembros-grupos-investigacion.apellidos');
const DEDICACION_KEY = marker('csp.miembros-grupos-investigacion.dedicacion');
const EMAIL_KEY = marker('csp.miembros-grupos-investigacion.email');
const FECHA_FIN_PARTICIPACION_KEY = marker('csp.miembros-grupos-investigacion.fecha-fin-participacion');
const FECHA_FIN_VINCULACION_KEY = marker('csp.miembros-grupos-investigacion.linea-investigacion.fecha-fin-vinculacion');
const FECHA_INICIO_PARTICIPACION_KEY = marker('csp.miembros-grupos-investigacion.fecha-inicio-participacion');
const FECHA_INICIO_VINCULACION_KEY = marker('csp.miembros-grupos-investigacion.linea-investigacion.fecha-inicio-vinculacion');
const GRUPO_CODIGO_KEY = marker('csp.miembros-grupos-investigacion.grupo.codigo');
const GRUPO_ESPECIAL_INVESTIGACION_KEY = marker('csp.miembros-grupos-investigacion.grupo.especial-investigacion');
const GRUPO_FECHA_FIN_KEY = marker('csp.miembros-grupos-investigacion.grupo.fecha-fin');
const GRUPO_FECHA_INICIO_KEY = marker('csp.miembros-grupos-investigacion.grupo.fecha-inicio');
const GRUPO_NOMBRE_KEY = marker('csp.miembros-grupos-investigacion.grupo.nombre');
const GRUPO_PROYECTO_SGE_REF_KEY = marker('csp.miembros-grupos-investigacion.grupo.proyectoSgeRef-export');
const GRUPO_TIPO_KEY = marker('csp.miembros-grupos-investigacion.grupo.tipo');
const LINEA_INVESTIGACION_KEY = marker('csp.miembros-grupos-investigacion.linea-investigacion');
const NOMBRE_KEY = marker('csp.miembros-grupos-investigacion.nombre');
const PARTICIPACION_KEY = marker('csp.miembros-grupos-investigacion.participacion');
const ROL_KEY = marker('csp.miembros-grupos-investigacion.rol');

const MSG_TRUE = marker('label.si');
const MSG_FALSE = marker('label.no');

export interface IMiembroGrupoInvestigacionListadoDataReport extends IMiembroGrupoInvestigacionListadoData {
  lineasInvestigacionGrupo: IGrupoLineaInvestigacionParticipacionDataReport[];
}

export interface IGrupoLineaInvestigacionParticipacionDataReport extends IGrupoLineaInvestigacion {
  participaciones: IGrupoLineaInvestigador[];
}

export interface IMiembroGrupoInvestigacionListadoReportOptions extends IReportOptions {
  miembrosGruposInvestigacion: IMiembroGrupoInvestigacionListadoData[];
}

@Injectable()
export class MiembrosGruposInvestigacionListadoExportService extends AbstractTableExportService<IMiembroGrupoInvestigacionListadoDataReport, IReportOptions> {

  private batchSize = 50;

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly grupoLineaInvestigadorService: GrupoLineaInvestigadorService,
    private readonly grupoLineaInvestigacionService: GrupoLineaInvestigacionService,
    private readonly lineaInvestigacionService: LineaInvestigacionService,
    protected reportService: ReportService
  ) {
    super(reportService);
  }

  protected getDataReport(reportConfig: IReportConfig<IMiembroGrupoInvestigacionListadoReportOptions>): Observable<IMiembroGrupoInvestigacionListadoDataReport[]> {
    return of(reportConfig.reportOptions.miembrosGruposInvestigacion as IMiembroGrupoInvestigacionListadoDataReport[]).pipe(
      switchMap(miembrosGruposInvestigacion => this.getMiembrosLineasInvestigacion(miembrosGruposInvestigacion.map(m => m.persona.id)).pipe(
        switchMap(miembrosLineasInvestigacion => forkJoin({
          lineasInvestigacion: this.lineaInvestigacionService.findTodos().pipe(map(response => response.items)),
          lineasInvestigacionGrupos: this.getLineasInvestigacionGrupos(miembrosLineasInvestigacion.map(m => m.grupoLineaInvestigacion.id))
        }).pipe(
          map(({ lineasInvestigacion, lineasInvestigacionGrupos }) => {

            return miembrosLineasInvestigacion.map(miembroLinea => {
              miembroLinea.grupoLineaInvestigacion = lineasInvestigacionGrupos.find(lineaGrupo => lineaGrupo.id === miembroLinea.grupoLineaInvestigacion.id);
              miembroLinea.grupoLineaInvestigacion.lineaInvestigacion = lineasInvestigacion.find(linea => linea.id === miembroLinea.grupoLineaInvestigacion.lineaInvestigacion.id);
              return miembroLinea;
            });
          })
        )),
        map(miembrosLineasInvestigacion => {
          miembrosGruposInvestigacion.map(miembroGrupo => {
            const lineasInvestigadorGrupo = miembrosLineasInvestigacion.filter(miembroLinea => miembroLinea.persona.id === miembroGrupo.persona.id
              && miembroLinea.grupoLineaInvestigacion.grupo.id === miembroGrupo.grupo.id
              && this.checkFechasSolapadas(
                miembroGrupo.fechaInicioParticipacion,
                miembroGrupo.fechaFinParticipacion,
                miembroLinea.fechaInicio ?? miembroLinea.grupoLineaInvestigacion.fechaInicio,
                miembroLinea.fechaFin ?? miembroLinea.grupoLineaInvestigacion.fechaFin
              ));

            miembroGrupo.lineasInvestigacionGrupo = lineasInvestigadorGrupo.reduce((acc, lineaInvestigadorGrupo) => {
              let lineaInvestigacion = acc.find(item => item.lineaInvestigacion.id === lineaInvestigadorGrupo.grupoLineaInvestigacion.lineaInvestigacion.id);

              if (!lineaInvestigacion) {
                lineaInvestigacion = {
                  ...lineaInvestigadorGrupo.grupoLineaInvestigacion,
                  participaciones: []
                };
                acc.push(lineaInvestigacion);
              }

              const participacion: IGrupoLineaInvestigador = {
                ...lineaInvestigadorGrupo,
                fechaInicio: lineaInvestigadorGrupo.fechaInicio ?? lineaInvestigadorGrupo.grupoLineaInvestigacion.fechaInicio,
                fechaFin: lineaInvestigadorGrupo.fechaFin ?? lineaInvestigadorGrupo.grupoLineaInvestigacion.fechaFin
              };

              lineaInvestigacion.participaciones.push(participacion);

              return acc;
            }, [] as IGrupoLineaInvestigacionParticipacionDataReport[])

            return miembroGrupo;
          });

          return miembrosGruposInvestigacion;
        })
      ))
    );
  }

  protected getRows(miembrosGruposInvestigacion: IMiembroGrupoInvestigacionListadoDataReport[], reportConfig: IReportConfig<IReportOptions>): Observable<ISgiRowReport[]> {
    const rowsReport = miembrosGruposInvestigacion.map(miembroGrupoInvestigacion => {
      const rowReport: ISgiRowReport = {
        elements: [
          miembroGrupoInvestigacion.persona.nombre,
          miembroGrupoInvestigacion.persona.apellidos,
          miembroGrupoInvestigacion.persona.emails?.map(e => e.email).join(', '),
          miembroGrupoInvestigacion.rol.nombre,
          this.getI18nBooleanYesNo(miembroGrupoInvestigacion.activo),
          LuxonUtils.toBackend(miembroGrupoInvestigacion.fechaInicioParticipacion),
          LuxonUtils.toBackend(miembroGrupoInvestigacion.fechaFinParticipacion),
          miembroGrupoInvestigacion.dedicacion ? this.translate.instant(DEDICACION_MAP.get(miembroGrupoInvestigacion.dedicacion)) : '',
          miembroGrupoInvestigacion.participacion ?? 0
        ]
      }

      const maxNumLineasInvestigacion = Math.max(...miembrosGruposInvestigacion.map(m => m.lineasInvestigacionGrupo?.length ?? 0));

      for (let i = 0; i < maxNumLineasInvestigacion; i++) {
        const lineaX = miembroGrupoInvestigacion.lineasInvestigacionGrupo[i];
        rowReport.elements.push(lineaX?.lineaInvestigacion?.nombre ?? '');

        const maxNumParticipacionesLineaX = Math.max(...miembrosGruposInvestigacion.map(m => {
          return m.lineasInvestigacionGrupo[i]?.participaciones?.length ?? 0;
        }));

        for (let j = 0; j < maxNumParticipacionesLineaX; j++) {
          const vinculacionLineaX = lineaX?.participaciones[j];
          rowReport.elements.push(LuxonUtils.toBackend(vinculacionLineaX?.fechaInicio) ?? '');
          rowReport.elements.push(LuxonUtils.toBackend(vinculacionLineaX?.fechaFin) ?? '');
        }
      }

      rowReport.elements.push(...[
        miembroGrupoInvestigacion.grupo.nombre ?? '',
        miembroGrupoInvestigacion.grupo.codigo ?? '',
        miembroGrupoInvestigacion.grupo.proyectoSge?.id ?? '',
        miembroGrupoInvestigacion.grupo.tipo ? this.translate.instant(TIPO_MAP.get(miembroGrupoInvestigacion.grupo.tipo)) : '',
        this.getI18nBooleanYesNo(miembroGrupoInvestigacion.grupo.especialInvestigacion),
        LuxonUtils.toBackend(miembroGrupoInvestigacion.grupo.fechaInicio),
        LuxonUtils.toBackend(miembroGrupoInvestigacion.grupo.fechaFin)
      ]);

      return rowReport;
    })

    return of(rowsReport);
  }

  protected getColumns(miembrosGruposInvestigacion: IMiembroGrupoInvestigacionListadoDataReport[], reportConfig: IReportConfig<IReportOptions>): Observable<ISgiColumnReport[]> {
    const columns: ISgiColumnReport[] = [
      {
        title: this.translate.instant(NOMBRE_KEY),
        name: 'nombre',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(APELLIDOS_KEY),
        name: 'apellidos',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(EMAIL_KEY),
        name: 'email',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(ROL_KEY),
        name: 'rol',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(ACTIVO_KEY),
        name: 'activo',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(FECHA_INICIO_PARTICIPACION_KEY),
        name: 'fechaInicioParticipacion',
        type: ColumnType.DATE
      },
      {
        title: this.translate.instant(FECHA_FIN_PARTICIPACION_KEY),
        name: 'fechaFinParticipacion',
        type: ColumnType.DATE
      },
      {
        title: this.translate.instant(DEDICACION_KEY),
        name: 'dedicacion',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(PARTICIPACION_KEY),
        name: 'participacion',
        type: ColumnType.NUMBER
      }
    ];

    const maxNumLineasInvestigacion = Math.max(...miembrosGruposInvestigacion.map(m => m.lineasInvestigacionGrupo?.length ?? 0));
    const titleLineaInvestigacion = this.translate.instant(LINEA_INVESTIGACION_KEY);

    for (let i = 0; i < maxNumLineasInvestigacion; i++) {
      const lineaInvestigacionIndex: string = String(i + 1);
      const columnLineaInvestigacion: ISgiColumnReport = {
        name: 'lineaInvestigacion' + lineaInvestigacionIndex,
        title: titleLineaInvestigacion + ' ' + lineaInvestigacionIndex,
        type: ColumnType.STRING
      };
      columns.push(columnLineaInvestigacion);

      const maxNumParticipacionesLineaX = Math.max(...miembrosGruposInvestigacion.map(m => {
        return m.lineasInvestigacionGrupo[i]?.participaciones?.length ?? 0;
      }));

      for (let j = 0; j < maxNumParticipacionesLineaX; j++) {
        const vinculacionIndex: string = String(j + 1);
        const columnFechaInicioVinculacion: ISgiColumnReport = {
          name: 'lineaInvestigacion' + lineaInvestigacionIndex + 'Participacion' + vinculacionIndex + 'fechaInicio',
          title: this.translate.instant(FECHA_INICIO_VINCULACION_KEY, { indexLinea: lineaInvestigacionIndex, indexVinculacion: vinculacionIndex }),
          type: ColumnType.DATE
        };
        columns.push(columnFechaInicioVinculacion);

        const columnFechaFinVinculacion: ISgiColumnReport = {
          name: 'lineaInvestigacion' + lineaInvestigacionIndex + 'Participacion' + vinculacionIndex + 'fechaFin',
          title: this.translate.instant(FECHA_FIN_VINCULACION_KEY, { indexLinea: lineaInvestigacionIndex, indexVinculacion: vinculacionIndex }),
          type: ColumnType.DATE
        };
        columns.push(columnFechaFinVinculacion);
      }
    }

    columns.push(...[
      {
        title: this.translate.instant(GRUPO_NOMBRE_KEY),
        name: 'grupoNombre',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(GRUPO_CODIGO_KEY),
        name: 'grupoCodigo',
        type: ColumnType.STRING,
        format: '#'
      },
      {
        title: this.translate.instant(GRUPO_PROYECTO_SGE_REF_KEY),
        name: 'grupoProyectoSgeRef',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(GRUPO_TIPO_KEY),
        name: 'grupoTipo',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(GRUPO_ESPECIAL_INVESTIGACION_KEY),
        name: 'grupoEspecialInvestigacion',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(GRUPO_FECHA_INICIO_KEY),
        name: 'grupoFechaInicio',
        type: ColumnType.DATE
      },
      {
        title: this.translate.instant(GRUPO_FECHA_FIN_KEY),
        name: 'grupoFechaFin',
        type: ColumnType.DATE
      }
    ]);

    return of(columns);
  }

  protected getGroupBy(): ISgiGroupReport {
    const groupBy: ISgiGroupReport = {
      name: 'header',
      visible: true
    };
    return groupBy;
  }

  protected getI18nBooleanYesNo(field: boolean): string {
    return field ? this.translate.instant(MSG_TRUE) : this.translate.instant(MSG_FALSE);
  }

  private getMiembrosLineasInvestigacion(personaRefs: string[]): Observable<IGrupoLineaInvestigador[]> {
    const personaRefsUnicos = [...new Set<string>(personaRefs)];
    return this.grupoLineaInvestigadorService.findAllInBactchesByPersonaRefIn(personaRefsUnicos, this.batchSize);
  }

  private getLineasInvestigacionGrupos(grupoLineaInvestigacionIds: number[]): Observable<IGrupoLineaInvestigacion[]> {
    const grupoLineaInvestigacionIdsUnicos = [...new Set<number>(grupoLineaInvestigacionIds)];
    return this.grupoLineaInvestigacionService.findAllInBactchesByIdIn(grupoLineaInvestigacionIdsUnicos, this.batchSize);
  }

  private checkFechasSolapadas(
    fechaInicioA: DateTime,
    fechaFinA: DateTime,
    fechaInicioB: DateTime,
    fechaFinB: DateTime
  ): boolean {
    const fechaInicioMillisA = fechaInicioA?.toMillis() ?? Number.NEGATIVE_INFINITY;
    const fechaFinMillisA = fechaFinA?.toMillis() ?? Number.POSITIVE_INFINITY;
    const fechaInicioMillisB = fechaInicioB?.toMillis() ?? Number.NEGATIVE_INFINITY;
    const fechaFinMillisB = fechaFinB?.toMillis() ?? Number.POSITIVE_INFINITY;

    return fechaInicioMillisA <= fechaFinMillisB && fechaInicioMillisB <= fechaFinMillisA;
  }

}
