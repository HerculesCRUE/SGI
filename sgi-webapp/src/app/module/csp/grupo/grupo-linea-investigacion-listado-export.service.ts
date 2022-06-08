import { PercentPipe } from '@angular/common';
import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupoLineaEquipoInstrumental } from '@core/models/csp/grupo-linea-equipo-instrumental';
import { IGrupoLineaInvestigacion } from '@core/models/csp/grupo-linea-investigacion';
import { IGrupoLineaInvestigador } from '@core/models/csp/grupo-linea-investigador';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { IPersona } from '@core/models/sgp/persona';
import { GrupoEquipoInstrumentalService } from '@core/services/csp/grupo-equipo-instrumental/grupo-equipo-instrumental.service';
import { GrupoLineaInvestigacionService } from '@core/services/csp/grupo-linea-investigacion/grupo-linea-investigacion.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { LineaInvestigacionService } from '@core/services/csp/linea-investigacion/linea-investigacion.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { ClasificacionService } from '@core/services/sgo/clasificacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { LuxonDatePipe } from '@shared/luxon-date-pipe';
import { NGXLogger } from 'ngx-logger';
import { from, merge, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast } from 'rxjs/operators';
import { GrupoLineaClasificacionListado } from '../grupo-linea-investigacion/grupo-linea-investigacion-formulario/grupo-linea-clasificaciones/grupo-linea-clasificaciones.fragment';
import { IGrupoReportData, IGrupoReportOptions } from '../grupo/grupo-listado-export.service';

const LINEA_KEY = marker('csp.grupo-linea-investigacion.linea');
const LINEA_INVESTIGACION_KEY = marker('csp.grupo-linea-investigacion');
const LINEA_INVESTIGACION_NOMBRE_KEY = marker('csp.grupo-linea-investigacion.nombre');
const LINEA_INVESTIGACION_FECHA_INICIO_KEY = marker('csp.grupo-linea-investigacion.fecha-inicio');
const LINEA_INVESTIGACION_FECHA_FIN_KEY = marker('csp.grupo-linea-investigacion.fecha-fin');
const LINEA_INVESTIGACION_MIEMBROS_ADSCRITOS_KEY = marker('csp.grupo-linea-investigador');
const LINEA_INVESTIGACION_MIEMBRO_KEY = marker('csp.grupo-linea-investigador.miembro');
const LINEA_INVESTIGACION_CLASIFICACIONES_KEY = marker('csp.grupo-linea-clasificacion');
const LINEA_INVESTIGACION_EQUIPO_KEY = marker('csp.grupo-linea-equipo-instrumental');
const LINEA_INVESTIGACION_MIEMBRO_APELLIDOS_KEY = marker('csp.grupo-linea-investigador.apellidos');
const LINEA_INVESTIGACION_MIEMBRO_EMAIL_KEY = marker('csp.grupo-linea-investigador.email');
const LINEA_INVESTIGACION_CLASIFICACION_CODIGO_KEY = marker('csp.grupo-linea-clasificacion.codigo');

const LINEA_INVESTIGACION_FIELD = 'lineaInvestigacion';
const LINEA_INVESTIGACION_NOMBRE_FIELD = 'lineaInvestigacionNombreInvestigador';
const LINEA_INVESTIGACION_FECHA_INICIO_FIELD = 'lineaInvestigacionFechaInicio';
const LINEA_INVESTIGACION_FECHA_FIN_FIELD = 'lineaInvestigacionFechaFin';
const LINEA_INVESTIGACION_MIEMBRO_NOMBRE_FIELD = 'lineaInvestigacionNombreMiembrosAdscritos';
const LINEA_INVESTIGACION_MIEMBRO_APELLIDOS_FIELD = 'lineaInvestigacionApellidosMiembrosAdscritos';
const LINEA_INVESTIGACION_MIEMBRO_EMAIL_FIELD = 'lineaInvestigacionEmailMiembrosAdscritos';
const LINEA_INVESTIGACION_MIEMBRO_FECHA_INICIO_FIELD = 'lineaInvestigacionFechaInicioMiembrosAdscritos';
const LINEA_INVESTIGACION_MIEMBRO_FECHA_FIN_FIELD = 'lineaInvestigacionFechaFinMiembrosAdscritos';
const LINEA_INVESTIGACION_CLASIFICACION_FIELD = 'lineaInvestigacionClasificacion';
const LINEA_INVESTIGACION_CLASIFICACION_CODIGO_FIELD = 'lineaInvestigacionClasificacionCodigo';
const LINEA_INVESTIGACION_EQUIPO_INSTRUMENTAL_NOMBRE_FIELD = 'lineaInvestigacionEquipoInstrumentalNombre';

@Injectable()
export class GrupoLineaInvestigacionListadoExportService extends AbstractTableExportFillService<IGrupoReportData, IGrupoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private luxonDatePipe: LuxonDatePipe,
    private readonly percentPipe: PercentPipe,
    private readonly grupoService: GrupoService,
    private personaService: PersonaService,
    private grupoLineaInvestigacionService: GrupoLineaInvestigacionService,
    private lineaInvestigacionService: LineaInvestigacionService,
    private clasificacionService: ClasificacionService,
    private grupoEquipoInstrumentalService: GrupoEquipoInstrumentalService
  ) {
    super(translate);
  }

  public getData(grupoData: IGrupoReportData): Observable<IGrupoReportData> {
    const findOptions: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('id', SgiRestSortDirection.ASC)
    };
    return this.grupoService.findLineasInvestigacion(grupoData.id, findOptions).pipe(
      map(response => {
        grupoData.lineasInvestigacion = response.items;
        return response.items.map(item => item);
      }),
      switchMap(response => {
        return this.fillLineas(grupoData, response);
      }));
  }

  private fillLineas(
    grupoData: IGrupoReportData,
    responseLineasInvestigacion: IGrupoLineaInvestigacion[]
  ): Observable<IGrupoReportData> {
    if (responseLineasInvestigacion.length === 0) {
      return of(grupoData);
    }
    if (!grupoData.lineasInvestigacion) {
      grupoData.lineasInvestigacion = [];
    }

    grupoData.lineasInvestigador = [];
    grupoData.clasificaciones = [];
    grupoData.lineasEquiposInstrumentales = [];

    return from(responseLineasInvestigacion).pipe(
      mergeMap(grupoLineaInvestigacion => {
        return this.lineaInvestigacionService.findById(grupoLineaInvestigacion.lineaInvestigacion.id).pipe(
          map(lineaInvestigacion => {
            grupoLineaInvestigacion.lineaInvestigacion = lineaInvestigacion;
            return grupoLineaInvestigacion;
          }),
          switchMap(() => {
            return merge(
              this.getMiembrosAdscritos(grupoLineaInvestigacion, grupoData),
              this.getClasificaciones(grupoLineaInvestigacion, grupoData),
              this.getEquiposInstrumentales(grupoLineaInvestigacion, grupoData)
            ).pipe(
              takeLast(1),
              catchError((err) => {
                this.logger.error(err);
                throw err;
              }));
          })
        );
      }
      ),
      map(() => grupoData)
    );
  }

  private getMiembrosAdscritos(grupoLineaInvestigacion: IGrupoLineaInvestigacion, grupoData: IGrupoReportData):
    Observable<IGrupoLineaInvestigacion> {
    return this.grupoLineaInvestigacionService.findLineasInvestigadores(grupoLineaInvestigacion.id).pipe(
      map((responseLineaInvestigador) => {
        grupoData.lineasInvestigador.push(...responseLineaInvestigador.items);
        grupoData.lineasInvestigador.map(lineaInvestigador => lineaInvestigador.grupoLineaInvestigacion = grupoLineaInvestigacion);
        return responseLineaInvestigador;
      }),
      switchMap(responseLineaInvestigador => {
        return from(responseLineaInvestigador.items).pipe(
          mergeMap(element => {
            return this.personaService.findById(element.persona.id).pipe(
              map(persona => {
                element.persona = persona;
              })
            );
          }),
          map(() => grupoLineaInvestigacion)
        );
      })
    );
  }

  private getClasificaciones(grupoLineaInvestigacion: IGrupoLineaInvestigacion, grupoData: IGrupoReportData):
    Observable<IGrupoLineaInvestigacion> {
    return this.grupoLineaInvestigacionService.findClasificaciones(grupoLineaInvestigacion.id).pipe(
      map(response => response.items.map(grupoLineaClasificacion => {
        const clasificacionListado: GrupoLineaClasificacionListado = {
          id: grupoLineaClasificacion.id,
          grupoLineaInvestigacionId: grupoLineaInvestigacion.id,
          clasificacion: undefined,
          nivelSeleccionado: grupoLineaClasificacion.clasificacion,
          niveles: undefined,
          nivelesTexto: ''
        };
        return clasificacionListado;
      })),
      switchMap((result) => {
        grupoData.clasificaciones.push(...result);
        return from(result).pipe(
          mergeMap((grupoLineaClasificacion) => {

            return this.clasificacionService.findById(grupoLineaClasificacion.nivelSeleccionado.id).pipe(
              map((clasificacion) => {
                grupoData.clasificaciones.filter(c => c.id === grupoLineaClasificacion.id).map(gc => {
                  gc.nivelSeleccionado = clasificacion;
                  gc.niveles = [clasificacion];
                });
                grupoLineaClasificacion.nivelSeleccionado = clasificacion;
                grupoLineaClasificacion.niveles = [clasificacion];
              }),
              switchMap(() => {
                return this.getNiveles(grupoLineaClasificacion);
              })
            );
          }),
          map((grupoLineaClasificacion) => {
            grupoData.clasificaciones.filter(c => c.id === grupoLineaClasificacion.id).map(gc => {
              gc.clasificacion = grupoLineaClasificacion.niveles[grupoLineaClasificacion.niveles.length - 1];
              gc.nivelesTexto = grupoLineaClasificacion.niveles
                .slice(0, grupoLineaClasificacion.niveles.length)
                .reverse()
                .map(clasificacion => clasificacion.nombre).join(' - ');
            });
            return grupoLineaInvestigacion;
          })
        );
      })
    );
  }

  private getEquiposInstrumentales(grupoLineaInvestigacion: IGrupoLineaInvestigacion, grupoData: IGrupoReportData):
    Observable<IGrupoLineaInvestigacion> {
    return this.grupoLineaInvestigacionService.findLineasEquiposInstrumentales(grupoLineaInvestigacion.id).pipe(
      map((responseLineaEquipoInstrumental) => {
        grupoData.lineasEquiposInstrumentales.push(...responseLineaEquipoInstrumental.items);
        grupoData.lineasEquiposInstrumentales.map(lineaEquipoInstrumental =>
          lineaEquipoInstrumental.grupoLineaInvestigacion = grupoLineaInvestigacion);
        return responseLineaEquipoInstrumental;
      }),
      switchMap(result => {
        return from(result.items).pipe(
          mergeMap(element => {
            return this.grupoEquipoInstrumentalService.findById(element.grupoEquipoInstrumental.id).pipe(
              map(equipoInstrumental => {
                element.grupoEquipoInstrumental = equipoInstrumental;
                return element as IGrupoLineaEquipoInstrumental;
              })
            );
          }),
          map(() => result)
        );
      }),
      map(lineaEquiposInstrumentales => {
        grupoData.lineasEquiposInstrumentales = lineaEquiposInstrumentales.items;
        return lineaEquiposInstrumentales.items.map(lineaEquipoInstrumental => {
          lineaEquipoInstrumental.grupoLineaInvestigacion = grupoLineaInvestigacion;
        });
      }),
      map(() => grupoLineaInvestigacion)
    );
  }

  private getNiveles(grupoLineaClasificacion: GrupoLineaClasificacionListado): Observable<GrupoLineaClasificacionListado> {
    const lastLevel = grupoLineaClasificacion.niveles[grupoLineaClasificacion.niveles.length - 1];
    if (!lastLevel.padreId) {
      return of(grupoLineaClasificacion);
    }

    return this.clasificacionService.findById(lastLevel.padreId).pipe(
      switchMap(clasificacion => {
        grupoLineaClasificacion.niveles.push(clasificacion);
        return this.getNiveles(grupoLineaClasificacion);
      })
    );
  }

  public fillColumns(
    grupos: IGrupoReportData[],
    reportConfig: IReportConfig<IGrupoReportOptions>
  ): ISgiColumnReport[] {

    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsLineaNotExcel();
    } else {
      return this.getColumnsLineaExcel(grupos);
    }
  }

  private getColumnsLineaNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: LINEA_INVESTIGACION_FIELD,
      title: this.translate.instant(LINEA_KEY),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(LINEA_INVESTIGACION_KEY, MSG_PARAMS.CARDINALIRY.PLURAL) +
      ' (' + this.translate.instant(LINEA_INVESTIGACION_NOMBRE_KEY) +
      ' - ' + this.translate.instant(LINEA_INVESTIGACION_FECHA_INICIO_KEY) +
      ' - ' + this.translate.instant(LINEA_INVESTIGACION_FECHA_FIN_KEY) +
      ' - ' + this.translate.instant(LINEA_INVESTIGACION_MIEMBROS_ADSCRITOS_KEY, MSG_PARAMS.CARDINALIRY.PLURAL) +
      ' - ' + this.translate.instant(LINEA_INVESTIGACION_CLASIFICACIONES_KEY, MSG_PARAMS.CARDINALIRY.PLURAL) +
      ' - ' + this.translate.instant(LINEA_INVESTIGACION_EQUIPO_KEY, MSG_PARAMS.CARDINALIRY.PLURAL) +
      ')';
    const columnEquipo: ISgiColumnReport = {
      name: LINEA_INVESTIGACION_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEquipo];
  }

  private getColumnsLineaExcel(grupos: IGrupoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumLineas = Math.max(...grupos.map(g => g.lineasInvestigacion?.length));
    const maxNumMiembros = Math.max(...grupos.map(g => g.lineasInvestigador?.length));
    const maxClasificaciones = Math.max(...grupos.map(g => g.clasificaciones?.length));
    const maxEquiposInstrumentales = Math.max(...grupos.map(g => g.lineasEquiposInstrumentales?.length));

    const titleLinea = this.translate.instant(LINEA_KEY);
    for (let i = 0; i < maxNumLineas; i++) {
      const idLinea: string = String(i + 1);
      const columnNombreLinea: ISgiColumnReport = {
        name: LINEA_INVESTIGACION_NOMBRE_FIELD + idLinea,
        title: titleLinea + idLinea + ': ' + this.translate.instant(LINEA_INVESTIGACION_NOMBRE_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnNombreLinea);
      const columnFechaInicioLinea: ISgiColumnReport = {
        name: LINEA_INVESTIGACION_FECHA_INICIO_FIELD + idLinea,
        title: titleLinea + idLinea + ': ' + this.translate.instant(LINEA_INVESTIGACION_FECHA_INICIO_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaInicioLinea);
      const columnFechaFinLinea: ISgiColumnReport = {
        name: LINEA_INVESTIGACION_FECHA_FIN_FIELD + idLinea,
        title: titleLinea + idLinea + ': ' + this.translate.instant(LINEA_INVESTIGACION_FECHA_FIN_KEY),
        type: ColumnType.DATE,
      };
      columns.push(columnFechaFinLinea);

      const titleMiembro = this.translate.instant(LINEA_INVESTIGACION_MIEMBRO_KEY);
      for (let m = 0; m < maxNumMiembros; m++) {
        const idMiembro: string = String(m + 1);
        const columnNombreMiembro: ISgiColumnReport = {
          name: LINEA_INVESTIGACION_MIEMBRO_NOMBRE_FIELD + idLinea + '_' + idMiembro,
          title: titleLinea + idLinea + ': ' + titleMiembro + idMiembro + ': ' + this.translate.instant(LINEA_INVESTIGACION_NOMBRE_KEY),
          type: ColumnType.STRING,
        };
        columns.push(columnNombreMiembro);
        const columnApellidosMiembro: ISgiColumnReport = {
          name: LINEA_INVESTIGACION_MIEMBRO_APELLIDOS_FIELD + idLinea + '_' + idMiembro,
          title: titleLinea + idLinea + ': ' + titleMiembro + idMiembro + ': ' + this.translate.instant(LINEA_INVESTIGACION_MIEMBRO_APELLIDOS_KEY),
          type: ColumnType.STRING,
        };
        columns.push(columnApellidosMiembro);
        const columnEmailMiembro: ISgiColumnReport = {
          name: LINEA_INVESTIGACION_MIEMBRO_EMAIL_FIELD + idLinea + '_' + idMiembro,
          title: titleLinea + idLinea + ': ' + titleMiembro + idMiembro + ': ' + this.translate.instant(LINEA_INVESTIGACION_MIEMBRO_EMAIL_KEY),
          type: ColumnType.STRING,
        };
        columns.push(columnEmailMiembro);
        const columnFechaInicioMiembro: ISgiColumnReport = {
          name: LINEA_INVESTIGACION_MIEMBRO_FECHA_INICIO_FIELD + idLinea + '_' + idMiembro,
          title: titleLinea + idLinea + ': ' + titleMiembro + idMiembro + ': ' + this.translate.instant(LINEA_INVESTIGACION_FECHA_INICIO_KEY),
          type: ColumnType.DATE,
        };
        columns.push(columnFechaInicioMiembro);
        const columnFechaFin: ISgiColumnReport = {
          name: LINEA_INVESTIGACION_MIEMBRO_FECHA_FIN_FIELD + idLinea + '_' + idMiembro,
          title: titleLinea + idLinea + ': ' + titleMiembro + idMiembro + ': ' + this.translate.instant(LINEA_INVESTIGACION_FECHA_FIN_KEY),
          type: ColumnType.DATE,
        };
        columns.push(columnFechaFin);
      }

      const titleClasificacion = this.translate.instant(LINEA_INVESTIGACION_CLASIFICACIONES_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR);
      for (let c = 0; c < maxClasificaciones; c++) {
        const idClasificacion: string = String(c + 1);
        const columnClasificacion: ISgiColumnReport = {
          name: LINEA_INVESTIGACION_CLASIFICACION_FIELD + idLinea + '_' + idClasificacion,
          title: titleLinea + idLinea + ': ' + titleClasificacion + idClasificacion,
          type: ColumnType.STRING,
        };
        columns.push(columnClasificacion);
        const columnCodigoClasificacion: ISgiColumnReport = {
          name: LINEA_INVESTIGACION_CLASIFICACION_CODIGO_FIELD + idLinea + '_' + idClasificacion,
          title: titleLinea + idLinea + ': ' + titleClasificacion + idClasificacion + ': ' + this.translate.instant(LINEA_INVESTIGACION_CLASIFICACION_CODIGO_KEY),
          type: ColumnType.STRING,
        };
        columns.push(columnCodigoClasificacion);
      }

      const titleEquipoInstrumental = this.translate.instant(LINEA_INVESTIGACION_EQUIPO_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR);
      for (let e = 0; e < maxEquiposInstrumentales; e++) {
        const idEquipo: string = String(e + 1);
        const columnNombreEquipo: ISgiColumnReport = {
          name: LINEA_INVESTIGACION_EQUIPO_INSTRUMENTAL_NOMBRE_FIELD + idLinea + '_' + idEquipo,
          title: titleLinea + idLinea + ': ' + titleEquipoInstrumental + idEquipo + ': ' + this.translate.instant(LINEA_INVESTIGACION_NOMBRE_KEY),
          type: ColumnType.STRING,
        };
        columns.push(columnNombreEquipo);
      }
    }

    return columns;
  }

  public fillRows(grupos: IGrupoReportData[], index: number, reportConfig: IReportConfig<IGrupoReportOptions>): any[] {
    const grupo = grupos[index];
    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsLineaNotExcel(grupo, elementsRow);
    } else {
      const maxNumLineas = Math.max(...grupos.map(g => g.lineasInvestigacion?.length));
      const maxNumMiembros = Math.max(...grupos.map(g => g.lineasInvestigador?.length));
      const maxNumClasificaciones = Math.max(...grupos.map(g => g.clasificaciones?.length));
      const maxNumEquipoInstrumental = Math.max(...grupos.map(g => g.lineasEquiposInstrumentales?.length));
      for (let i = 0; i < maxNumLineas; i++) {
        const lineaInvestigacion = grupo.lineasInvestigacion[i] ?? null;
        const miembrosAdscritos = lineaInvestigacion ? grupo.lineasInvestigador.filter(lineaInvestigador => lineaInvestigador.grupoLineaInvestigacion.id === lineaInvestigacion.id) : null;
        const clasificaciones = lineaInvestigacion ? grupo.clasificaciones.filter(clasificacion => clasificacion.grupoLineaInvestigacionId === lineaInvestigacion.id) : null;
        const equiposInstrumentales = lineaInvestigacion ? grupo.lineasEquiposInstrumentales.filter(equipoInstrumental => equipoInstrumental.grupoLineaInvestigacion.id === lineaInvestigacion.id) : null;
        this.fillRowsLineaExcel(elementsRow, lineaInvestigacion, miembrosAdscritos, maxNumMiembros, clasificaciones, maxNumClasificaciones, equiposInstrumentales, maxNumEquipoInstrumental);
      }
    }
    return elementsRow;
  }

  private fillRowsLineaNotExcel(grupo: IGrupoReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    grupo.lineasInvestigacion?.forEach(grupoLineaInvestigacion => {
      const equipoElementsRow: any[] = [];

      const miembrosAdscritos = grupo.lineasInvestigador?.filter(grupoLineaInvestigador =>
        grupoLineaInvestigador.grupoLineaInvestigacion.id === grupoLineaInvestigacion.id)
        .map(grupoLineaInvestigador => {
          return grupoLineaInvestigador.persona.nombre + ' ' + grupoLineaInvestigador.persona.apellidos + ' '
            + this.translate.instant(LINEA_INVESTIGACION_FECHA_INICIO_KEY) + ': ' + this.luxonDatePipe.transform(LuxonUtils.toBackend(grupoLineaInvestigador?.fechaInicio, true), 'shortDate')
            + this.translate.instant(LINEA_INVESTIGACION_FECHA_FIN_KEY) + ': ' + (this.luxonDatePipe.transform(LuxonUtils.toBackend(grupoLineaInvestigador?.fechaFin, true), 'shortDate') ?? ' - ');
        }).join('; ');

      const clasificaciones =
        grupo.clasificaciones?.filter(grupoLineaClasificacion =>
          grupoLineaClasificacion.grupoLineaInvestigacionId === grupoLineaInvestigacion.id)
          .map(grupoLineaClasificacion => {
            return grupoLineaClasificacion.clasificacion?.nombre ?? '';
          }).join('; ');

      const equiposInstrumentales =
        grupo.lineasEquiposInstrumentales?.filter(grupoLineaEquipoInstrumental =>
          grupoLineaEquipoInstrumental.grupoLineaInvestigacion.id === grupoLineaInvestigacion.id)
          .map(grupoLineaEquipoInstrumental => {
            return grupoLineaEquipoInstrumental.grupoEquipoInstrumental?.nombre ?? '';
          }).join('; ');


      let grupoLineaInvestigacionTable = grupoLineaInvestigacion?.lineaInvestigacion?.nombre;
      grupoLineaInvestigacionTable += '\n';
      grupoLineaInvestigacionTable += this.luxonDatePipe.transform(LuxonUtils.toBackend(grupoLineaInvestigacion?.fechaInicio, true), 'shortDate') ?? '';
      grupoLineaInvestigacionTable += '\n';
      grupoLineaInvestigacionTable += this.luxonDatePipe.transform(LuxonUtils.toBackend(grupoLineaInvestigacion?.fechaFin, true), 'shortDate') ?? '';
      grupoLineaInvestigacionTable += '\n';
      grupoLineaInvestigacionTable += miembrosAdscritos;
      grupoLineaInvestigacionTable += '\n';
      grupoLineaInvestigacionTable += clasificaciones;
      grupoLineaInvestigacionTable += '\n';
      grupoLineaInvestigacionTable += equiposInstrumentales;

      equipoElementsRow.push(grupoLineaInvestigacionTable);

      const rowReport: ISgiRowReport = {
        elements: equipoElementsRow
      };
      rowsReport.push(rowReport);
    });

    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsLineaExcel(elementsRow: any[], grupoLineaInvestigacion: IGrupoLineaInvestigacion,
    grupoLineasInvestigador: IGrupoLineaInvestigador[], maxNumMiembros: number,
    grupoLineasClasificacion: GrupoLineaClasificacionListado[], maxNumClasificaciones: number,
    grupoLineasEquipoInstrumental: IGrupoLineaEquipoInstrumental[], maxNumEquipoInstrumental: number) {
    if (grupoLineaInvestigacion) {
      elementsRow.push(grupoLineaInvestigacion?.lineaInvestigacion?.nombre ?? '');
      elementsRow.push(LuxonUtils.toBackend(grupoLineaInvestigacion.fechaInicio) ?? '');
      elementsRow.push(LuxonUtils.toBackend(grupoLineaInvestigacion.fechaFin) ?? '');

      for (let i = 0; i < maxNumMiembros; i++) {
        const grupoLineaInvestigador = grupoLineasInvestigador[i];
        if (grupoLineaInvestigador) {
          elementsRow.push(grupoLineaInvestigador.persona?.nombre ?? '');
          elementsRow.push(grupoLineaInvestigador.persona?.apellidos ?? '');
          elementsRow.push(this.getEmailPrincipal(grupoLineaInvestigador.persona));
          elementsRow.push(LuxonUtils.toBackend(grupoLineaInvestigador.fechaInicio) ?? '');
          elementsRow.push(LuxonUtils.toBackend(grupoLineaInvestigador.fechaFin) ?? '');
        } else {
          elementsRow.push('');
          elementsRow.push('');
          elementsRow.push('');
          elementsRow.push('');
          elementsRow.push('');
        }
      }

      for (let k = 0; k < maxNumClasificaciones; k++) {
        const grupoLineaClasificacion = grupoLineasClasificacion[k];
        if (grupoLineaClasificacion) {
          elementsRow.push(grupoLineaClasificacion.nivelesTexto ?? '');
          elementsRow.push(grupoLineaClasificacion.clasificacion?.codigo ?? '');
        } else {
          elementsRow.push('');
          elementsRow.push('');
        }
      }

      for (let j = 0; j < maxNumEquipoInstrumental; j++) {
        const grupoLineaEquipoInstrumental = grupoLineasEquipoInstrumental[j];
        if (grupoLineaEquipoInstrumental) {
          elementsRow.push(grupoLineaEquipoInstrumental.grupoEquipoInstrumental?.nombre ?? '');
        } else {
          elementsRow.push('');
        }
      }

    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      for (let i = 0; i < maxNumMiembros; i++) {
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
        elementsRow.push('');
      }
      for (let k = 0; k < maxNumClasificaciones; k++) {
        elementsRow.push('');
        elementsRow.push('');
      }
      for (let j = 0; j < maxNumEquipoInstrumental; j++) {
        elementsRow.push('');
      }
    }
  }


  private getEmailPrincipal({ emails }: IPersona): string {
    if (!emails) {
      return '';
    }
    const emailDataPrincipal = emails.find(emailData => emailData.principal);
    return emailDataPrincipal?.email ?? '';
  }
}
