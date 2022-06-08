import { DecimalPipe } from '@angular/common';
import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IDetalleProduccionInvestigador } from '@core/models/prc/detalle-produccion-investigador';
import { IResumenPuntuacionGrupoAnio } from '@core/models/prc/resumen-puntuacion-grupo-anio';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { OutputReport } from '@core/models/rep/output-report.enum';
import { ElementAlignmentType } from '@core/models/rep/sgi-column-horizontal-options-report';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiDynamicReport } from '@core/models/rep/sgi-dynamic-report';
import { ISgiFilterReport } from '@core/models/rep/sgi-filter-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ReportPrcService } from '@core/services/rep/report-prc/report-prc.service';
import { ReportService } from '@core/services/rep/report.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { BaremacionService } from '../baremacion/baremacion.service';

const RESUMEN_PUNTUACION_GRUPOS_CONVOCATORIA_KEY = marker('prc.report.resumen-puntuacion-grupos.convocatoria');
const RESUMEN_PUNTUACION_GRUPOS_GRUPO_KEY = marker('prc.report.resumen-puntuacion-grupos.grupo');
const RESUMEN_PUNTUACION_GRUPOS_PERSONA_RESPONSABLE_KEY = marker('prc.report.resumen-puntuacion-grupos.personaResponsable');
const RESUMEN_PUNTUACION_GRUPOS_PUNTOS_CI_KEY = marker('prc.report.resumen-puntuacion-grupos.puntos-costes-indirectos');
const RESUMEN_PUNTUACION_GRUPOS_PUNTOS_SEXENIOS_KEY = marker('prc.report.resumen-puntuacion-grupos.puntos-sexenios');
const RESUMEN_PUNTUACION_GRUPOS_PUNTOS_PROD_KEY = marker('prc.report.resumen-puntuacion-grupos.puntos-produccion');
const RESUMEN_PUNTUACION_GRUPOS_TITLE_KEY = marker('prc.report.resumen-puntuacion-grupos.title');

const RESUMEN_DETALLE_PROD_INV_INVESTIGADOR_KEY = marker('prc.report.detalle-prod-inv.investigador');
const RESUMEN_DETALLE_PROD_INV_PUNTOS_KEY = marker('prc.report.detalle-prod-inv.puntos');
const RESUMEN_DETALLE_PROD_INV_TITULO_KEY = marker('prc.report.detalle-prod-inv.titulo');
const RESUMEN_DETALLE_PROD_INV_TITLE_KEY = marker('prc.report.detalle-prod-inv.title');

@Injectable()
export class PrcReportService {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    protected readonly translate: TranslateService,
    private readonly baremacionService: BaremacionService,
    protected reportService: ReportService,
    protected reportPrcService: ReportPrcService,
    private readonly decimalPipe: DecimalPipe
  ) {
  }

  /**
   * Visualiza el informe de detalle de grupo.
   * @param anio Año de la convocatoria.
   * @param grupoId identificador del grupo.
   */
  getInformeDetalleGrupo(anio: number, grupoId: number): Observable<Blob> {
    return this.reportPrcService.getInformeDetalleGrupo(anio, grupoId);
  }

  /**
   * Visualiza el informe de resumen de puntuacion de grupos.
   * @param anio Año de la convocatoria
   */
  getInformeResumenPuntuacionGrupos(anio: number): Observable<Blob> {

    const report: ISgiDynamicReport = {
      outputType: OutputReport.PDF,
      fieldOrientation: FieldOrientation.HORIZONTAL,
      customWidth: 630,
      title: this.translate.instant(RESUMEN_PUNTUACION_GRUPOS_TITLE_KEY),
      filters: [{
        name: this.translate.instant(RESUMEN_PUNTUACION_GRUPOS_CONVOCATORIA_KEY),
        filter: anio.toString()
      }],
      groupBy: {
        name: 'grupo',
        visible: true
      },
      columns: [
        {
          name: 'grupo',
          title: this.translate.instant(RESUMEN_PUNTUACION_GRUPOS_GRUPO_KEY),
          type: ColumnType.STRING,
          horizontalOptions: {
            customWidth: 30
          }
        }, {
          name: 'personaResponsable',
          title: this.translate.instant(RESUMEN_PUNTUACION_GRUPOS_PERSONA_RESPONSABLE_KEY),
          type: ColumnType.STRING,
          horizontalOptions: {
            customWidth: 32
          }
        }, {
          name: 'puntosSexenios',
          title: this.translate.instant(RESUMEN_PUNTUACION_GRUPOS_PUNTOS_SEXENIOS_KEY),
          type: ColumnType.STRING,
          horizontalOptions: {
            customWidth: 14
          }
        }, {
          name: 'puntosCostesIndirectos',
          title: this.translate.instant(RESUMEN_PUNTUACION_GRUPOS_PUNTOS_CI_KEY),
          type: ColumnType.STRING,
          horizontalOptions: {
            customWidth: 14
          }
        }, {
          name: 'puntosProduccion',
          title: this.translate.instant(RESUMEN_PUNTUACION_GRUPOS_PUNTOS_PROD_KEY),
          type: ColumnType.STRING,
          horizontalOptions: {
            customWidth: 16
          }
        }],
      rows: []
    };

    return this.baremacionService.resumenPuntuacionGrupos(anio).pipe(
      switchMap((reportData: IResumenPuntuacionGrupoAnio) => {
        const rowsReport: ISgiRowReport[] = [];

        reportData?.puntuacionesGrupos?.forEach(resumenPuntuacionGrupo => {
          const puntGrupoElementsRow: any[] = [];

          puntGrupoElementsRow.push(resumenPuntuacionGrupo.grupo);
          puntGrupoElementsRow.push(resumenPuntuacionGrupo.personaResponsable);
          puntGrupoElementsRow.push(this.decimalPipe.transform(resumenPuntuacionGrupo.puntosSexenios, '.2-2') ?? '');
          puntGrupoElementsRow.push(this.decimalPipe.transform(resumenPuntuacionGrupo.puntosCostesIndirectos, '.2-2') ?? '');
          puntGrupoElementsRow.push(this.decimalPipe.transform(resumenPuntuacionGrupo.puntosProduccion, '.2-2') ?? '');

          const rowReport: ISgiRowReport = {
            elements: puntGrupoElementsRow
          };
          rowsReport.push(rowReport);
        });

        report.rows = rowsReport;
        return of(report);
      }),
      switchMap((dynamicReport: ISgiDynamicReport) => {
        return this.reportService.downloadDynamicReport(dynamicReport);
      })
    );
  }

  /**
   * Visualiza el informe de Detalle producción investigador.
   * @param anio Año de la convocatoria
   * @param personaRef Id de la persona
   */
  getInformeDetalleProduccionInvestigador(anio: number, personaRef: string): Observable<Blob> {

    const report: ISgiDynamicReport = {
      outputType: OutputReport.PDF,
      fieldOrientation: FieldOrientation.VERTICAL,
      title: this.translate.instant(RESUMEN_DETALLE_PROD_INV_TITLE_KEY),
      filters: [],
      groupBy: {
        name: 'titulo',
        additionalGroupNames: ['puntos'],
        visible: true
      },
      columns: [],
      rows: []
    };

    return this.baremacionService.detalleProduccionInvestigador(anio, personaRef).pipe(
      switchMap((reportData: IDetalleProduccionInvestigador) => {

        report.filters = this.getFiltersDetalleProduccionInvestigador(reportData);

        if (reportData?.tipos?.length !== 0) {

          const columnsReport: ISgiColumnReport[] = [];
          const rowsReport: ISgiRowReport[] = [];

          columnsReport.push(... this.getHeaderColumnsDetalleProduccionInvestigador('titulo'));
          const columnsSubReport: ISgiColumnReport[] = [];

          reportData.tipos[0].produccionesCientificas?.forEach((tipoSubReport, indexSubReport) => {
            columnsSubReport.push(this.getHeaderSubReportColumnsDetalleProduccionInvestigador(
              tipoSubReport.titulo, indexSubReport.toString(), tipoSubReport.puntos));
          });
          columnsReport.push(...columnsSubReport);

          reportData.tipos?.forEach(tipo => {
            const detalleProduccionElementsRow: any[] = [];

            detalleProduccionElementsRow.push(tipo.titulo);
            detalleProduccionElementsRow.push(this.decimalPipe.transform(tipo.puntos, '.2-2') ?? '');

            columnsSubReport.forEach((columnSubReport, indexColumnSubReport) => {

              const rowsSubReport: ISgiRowReport[] = [];
              if (tipo.produccionesCientificas?.length > indexColumnSubReport) {

                tipo.produccionesCientificas[indexColumnSubReport].produccionesCientificas?.forEach(tipoSubReport => {
                  const detalleProduccionSubReportElementsRow: any[] = [];
                  detalleProduccionSubReportElementsRow.push(tipoSubReport.titulo);
                  detalleProduccionSubReportElementsRow.push(this.decimalPipe.transform(tipoSubReport.puntos, '.2-2') ?? '');
                  const rowSubReport: ISgiRowReport = {
                    elements: detalleProduccionSubReportElementsRow
                  };
                  rowsSubReport.push(rowSubReport);
                });
              }
              detalleProduccionElementsRow.push({
                rows: rowsSubReport
              });
            });

            const rowReport: ISgiRowReport = {
              elements: detalleProduccionElementsRow
            };
            rowsReport.push(rowReport);
          });

          report.columns = columnsReport;
          report.rows = rowsReport;
        }

        return of(report);
      }),
      switchMap((dynamicReport: ISgiDynamicReport) => {
        return this.reportService.downloadDynamicReport(dynamicReport);
      })
    );
  }

  private getFiltersDetalleProduccionInvestigador(reportData: IDetalleProduccionInvestigador): ISgiFilterReport[] {
    const filtersReport: ISgiFilterReport[] = [{
      name: this.translate.instant(RESUMEN_DETALLE_PROD_INV_INVESTIGADOR_KEY),
      filter: reportData.investigador
    }, {
      name: this.translate.instant(RESUMEN_PUNTUACION_GRUPOS_CONVOCATORIA_KEY),
      filter: reportData.anio.toString()
    }];
    return filtersReport;
  }

  private getHeaderColumnsDetalleProduccionInvestigador(headerTitle: string): ISgiColumnReport[] {
    const columnsReport: ISgiColumnReport[] = [];

    const titleColumnReport: ISgiColumnReport = {
      title: headerTitle,
      name: 'titulo',
      type: ColumnType.STRING,
      visible: false
    };
    columnsReport.push(titleColumnReport);

    const puntosColumnReport: ISgiColumnReport = {
      title: this.translate.instant(RESUMEN_DETALLE_PROD_INV_PUNTOS_KEY),
      name: 'puntos',
      type: ColumnType.STRING,
      visible: false,
      horizontalOptions: {
        elementAlignmentType: ElementAlignmentType.RIGHT
      }
    };
    columnsReport.push(puntosColumnReport);

    return columnsReport;
  }

  private getHeaderSubReportColumnsDetalleProduccionInvestigador(headerTitle: string, headerName: string, puntos: number)
    : ISgiColumnReport {

    const puntosFormat = this.decimalPipe.transform(puntos, '.2-2') ?? '';
    const columnSubReport: ISgiColumnReport = {
      title: headerTitle,
      name: 'name_' + headerName,
      additionalTitle: [puntosFormat],
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.HORIZONTAL,
      visibleIfSubReportEmpty: false,
      columns: [
        {
          name: 'titulo_' + headerName,
          title: this.translate.instant(RESUMEN_DETALLE_PROD_INV_TITULO_KEY),
          type: ColumnType.STRING,
        },
        {
          name: 'puntos_' + headerName,
          title: this.translate.instant(RESUMEN_DETALLE_PROD_INV_PUNTOS_KEY),
          type: ColumnType.STRING,
          horizontalOptions: {
            elementAlignmentType: ElementAlignmentType.RIGHT
          }
        }
      ]
    };

    return columnSubReport;
  }

}
