import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Estado, ESTADO_MAP } from '@core/models/csp/convocatoria';
import { IConvocatoriaEnlace } from '@core/models/csp/convocatoria-enlace';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiGroupReport } from '@core/models/rep/sgi-group.report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { AbstractTableExportService, IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { ReportService } from '@core/services/rep/report.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, zip } from 'rxjs';
import { map, switchMap, takeLast } from 'rxjs/operators';
import { IConvocatoriaListado } from './convocatoria-listado/convocatoria-listado.component';


interface IConvocatoriaReportData extends IConvocatoriaListado {
  enlaces: IConvocatoriaEnlace[];
}
const TITLE_KEY = marker('csp.convocatoria.titulo');
const REFERENCIA_KEY = marker('csp.convocatoria.referencia');
const FECHA_INICIO_KEY = marker('csp.convocatoria.solicitud.fecha-inicio');
const FECHA_FIN_KEY = marker('csp.convocatoria.solicitud.fecha-fin');
const ENTIDAD_CONVOCANTE_KEY = marker('csp.convocatoria-entidad-convocante.programa');
const PLAN_INVESTIGACION_KEY = marker('csp.convocatoria.plan-investigacion');
const ENTIDAD_FINANCIADORA_KEY = marker('csp.entidad-financiadora.empresa-economica');
const FUENTE_FINANCIACION_KEY = marker('csp.convocatoria.fuente-financiacion');
const ESTADO_KEY = marker('csp.convocatoria.estado');

@Injectable()
export class ConvocatoriaListadoService extends AbstractTableExportService<IConvocatoriaReportData, IReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    protected readonly translate: TranslateService,
    private readonly convocatoriaService: ConvocatoriaService,
    protected reportService: ReportService,
    private empresaService: EmpresaService
  ) {
    super(reportService);
  }

  protected getRows(convocatorias: IConvocatoriaReportData[], reportConfig: IReportConfig<IReportOptions>): Observable<ISgiRowReport[]> {
    const requestsRow: Observable<ISgiRowReport>[] = [];

    convocatorias.forEach(convocatoria => {
      requestsRow.push(this.getRowsInner(convocatoria, reportConfig));
    });
    return zip(...requestsRow);
  }

  private getRowsInner(convocatoria: IConvocatoriaReportData, reportConfig: IReportConfig<IReportOptions>): Observable<ISgiRowReport> {
    const rowReport: ISgiRowReport = {
      elements: []
    };

    return of(rowReport).pipe(
      map((row) => {
        row.elements.push(convocatoria.convocatoria?.titulo);
        row.elements.push(convocatoria.convocatoria?.codigo);
        row.elements.push(LuxonUtils.toBackend(convocatoria.fase?.fechaInicio));
        row.elements.push(LuxonUtils.toBackend(convocatoria.fase?.fechaFin));
        row.elements.push(convocatoria.entidadConvocanteEmpresa?.nombre);
        row.elements.push(convocatoria.entidadConvocante.programa?.nombre);
        row.elements.push(convocatoria.entidadFinanciadoraEmpresa?.nombre);
        row.elements.push(convocatoria.entidadFinanciadora.fuenteFinanciacion?.nombre);
        return row;
      }),
      switchMap((row) => {
        return this.getEstadoByMap(convocatoria.convocatoria?.estado).pipe(
          map(estadoTranslate => {
            row.elements.push(estadoTranslate);
            return row;
          })
        );
      }),
    );
  }

  private getEstadoByMap(estado: Estado): Observable<string> {
    return this.translate.get(ESTADO_MAP.get(estado));
  }

  protected getDataReport(reportConfig: IReportConfig<IReportOptions>): Observable<IConvocatoriaReportData[]> {
    const findOptions = reportConfig.reportOptions?.findOptions;
    findOptions.page.index = 0;
    findOptions.page.size = undefined;

    return this.convocatoriaService.findAllTodosRestringidos(findOptions).pipe(
      map((convocatorias) => {
        return convocatorias.items.map((convocatoria) => {
          return {
            convocatoria,
            entidadConvocante: {} as IConvocatoriaEntidadConvocante,
            entidadConvocanteEmpresa: {} as IEmpresa,
            entidadFinanciadora: {} as IConvocatoriaEntidadFinanciadora,
            entidadFinanciadoraEmpresa: {} as IEmpresa,
            fase: {} as IConvocatoriaFase
          } as IConvocatoriaReportData;
        });
      }),
      switchMap((convocatoriasReportData) => {
        const requestsConvocatoria: Observable<IConvocatoriaReportData>[] = [];

        convocatoriasReportData.forEach(convocatoria => {
          requestsConvocatoria.push(this.getDataReportInner(convocatoria, reportConfig.reportOptions));
        });
        return zip(...requestsConvocatoria);
      }),
      takeLast(1)
    );
  }

  // tslint:disable-next-line: max-line-length
  private getDataReportInner(convocatoriaListado: IConvocatoriaReportData, reportOptions: IReportOptions): Observable<IConvocatoriaReportData> {
    return of(convocatoriaListado).pipe(
      switchMap(() => this.convocatoriaService.findEntidadesFinanciadoras(convocatoriaListado.convocatoria.id).pipe(
        map(entidadFinanciadora => {
          if (entidadFinanciadora.items.length > 0) {
            convocatoriaListado.entidadFinanciadora = entidadFinanciadora.items[0];
          }
          return convocatoriaListado;
        }),
        switchMap(() => {
          if (convocatoriaListado.entidadFinanciadora.id) {
            return this.empresaService.findById(convocatoriaListado.entidadFinanciadora.empresa.id).pipe(
              map(empresa => {
                convocatoriaListado.entidadFinanciadoraEmpresa = empresa;
                return convocatoriaListado;
              }),
            );
          }
          return of(convocatoriaListado);
        }),
        switchMap(() => {
          return this.convocatoriaService.findAllConvocatoriaFases(convocatoriaListado.convocatoria.id).pipe(
            map(convocatoriaFase => {
              if (convocatoriaFase.items.length > 0) {
                convocatoriaListado.fase = convocatoriaFase.items[0];
              }
              return convocatoriaListado;
            })
          );
        }),
        switchMap(() => {
          return this.convocatoriaService.findAllConvocatoriaEntidadConvocantes(convocatoriaListado.convocatoria.id).pipe(
            map(convocatoriaEntidadConvocante => {
              if (convocatoriaEntidadConvocante.items.length > 0) {
                convocatoriaListado.entidadConvocante = convocatoriaEntidadConvocante.items[0];
              }
              return convocatoriaListado;
            }),
            switchMap(() => {
              if (convocatoriaListado.entidadConvocante.id) {
                return this.empresaService.findById(convocatoriaListado.entidadConvocante.entidad.id).pipe(
                  map(empresa => {
                    convocatoriaListado.entidadConvocanteEmpresa = empresa;
                    return convocatoriaListado;
                  }),
                );
              }
              return of(convocatoriaListado);
            }),
          );
        })
      )
      )
    );
  }

  protected getColumns(resultados: IConvocatoriaReportData[], reportConfig: IReportConfig<IReportOptions>):
    Observable<ISgiColumnReport[]> {
    const columnsReport: ISgiColumnReport[] = [
      {
        title: this.translate.instant(TITLE_KEY),
        name: 'titulo',
        type: ColumnType.STRING,
      },
      {
        title: this.translate.instant(REFERENCIA_KEY),
        name: 'codigo',
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
        title: this.translate.instant(ENTIDAD_CONVOCANTE_KEY),
        name: 'entidadConvocante',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(PLAN_INVESTIGACION_KEY),
        name: 'planInvestigacion',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(ENTIDAD_FINANCIADORA_KEY),
        name: 'entidadFinanciadora',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(FUENTE_FINANCIACION_KEY),
        name: 'fuenteFinanciacion',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(ESTADO_KEY),
        name: 'estado',
        type: ColumnType.STRING
      }

    ];
    return of(columnsReport);
  }

  protected getGroupBy(): ISgiGroupReport {
    const groupBy: ISgiGroupReport = {
      name: 'titulo',
      visible: true
    };
    return groupBy;
  }

}
