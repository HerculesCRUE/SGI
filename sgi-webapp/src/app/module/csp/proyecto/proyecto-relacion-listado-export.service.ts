import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IProyecto } from '@core/models/csp/proyecto';
import { IInvencion } from '@core/models/pii/invencion';
import { IRelacion, TipoEntidad } from '@core/models/rel/relacion';
import { FieldOrientation } from '@core/models/rep/field-orientation.enum';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ISgiRowReport } from '@core/models/rep/sgi-row.report';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { RelacionService } from '@core/services/rel/relaciones/relacion.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast } from 'rxjs/operators';
import { IProyectoReportData, IProyectoReportOptions } from './proyecto-listado-export.service';

const RELACION_KEY = marker('csp.proyecto-relacion');
const RELACION_TIPO_KEY = marker('label.csp.proyecto-relacion.tipo-relacion');
const RELACION_TITULO_KEY = marker('label.csp.proyecto-relacion.titulo');

const RELACION_FIELD = 'relacion';
const RELACION_TIPO_FIELD = 'relacionTipo';
const RELACION_TITULO_FIELD = 'relacionTitulo';

type EntidadRelacionada = IConvocatoria | IInvencion | IProyecto;

export interface ProyectoRelacionListadoExport {
  id: number;
  entidadRelacionada: EntidadRelacionada;
  tipoEntidadRelacionada: TipoEntidad;
  observaciones: string;
}

@Injectable()
export class ProyectoRelacionListadoExportService extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private relacionService: RelacionService,
    private convocatoriaService: ConvocatoriaService,
    private invencionService: InvencionService,
    private proyectoService: ProyectoService
  ) {
    super(translate);
  }

  public getData(proyectoData: IProyectoReportData): Observable<IProyectoReportData> {
    return this.relacionService.findProyectoRelaciones(proyectoData.id).pipe(
      map(response => response.map(proyectoRelacion => {
        const isEntidadOrigenProyecto = this.isEntidadRelacionadaProyecto(proyectoRelacion, proyectoData.id);
        const relacionListado: ProyectoRelacionListadoExport = {
          id: proyectoRelacion.id,
          entidadRelacionada: isEntidadOrigenProyecto ? proyectoRelacion.entidadOrigen : proyectoRelacion.entidadDestino,
          tipoEntidadRelacionada: isEntidadOrigenProyecto ? proyectoRelacion.tipoEntidadOrigen : proyectoRelacion.tipoEntidadDestino,
          observaciones: proyectoRelacion.observaciones
        };
        return relacionListado;
      })),
      switchMap((responseRelacion) => {
        if (responseRelacion.length === 0) {
          return of(responseRelacion);
        }
        return from(responseRelacion).pipe(
          mergeMap((proyectoRelacion) => {
            return this.getRelacion(proyectoRelacion);
          }, this.DEFAULT_CONCURRENT),
          map(() => responseRelacion)
        );
      }),
      map(responseRelacion => {
        proyectoData.relaciones = responseRelacion;
        return proyectoData;
      }),
      takeLast(1)
    );
  }

  private isEntidadRelacionadaProyecto(relacion: IRelacion, idProyecto: number): boolean {
    return relacion.entidadOrigen.id === idProyecto ?
      relacion.tipoEntidadDestino === TipoEntidad.PROYECTO :
      relacion.tipoEntidadOrigen === TipoEntidad.PROYECTO;
  }

  private getRelacion(proyectoRelacion: ProyectoRelacionListadoExport): Observable<ProyectoRelacionListadoExport> {
    let observable$: Observable<EntidadRelacionada> = null;

    switch (proyectoRelacion.tipoEntidadRelacionada) {
      case TipoEntidad.CONVOCATORIA:
        observable$ = this.convocatoriaService.findById(proyectoRelacion.entidadRelacionada.id);
        break;
      case TipoEntidad.INVENCION:
        observable$ = this.invencionService.findById(proyectoRelacion.entidadRelacionada.id);
        break;
      case TipoEntidad.PROYECTO:
        observable$ = this.proyectoService.findById(proyectoRelacion.entidadRelacionada.id);
        break;
      default:
        this.logger.error('Entidad relacionada not found');
    }

    return observable$.pipe(
      map((entidadRelacionada) => {
        proyectoRelacion.entidadRelacionada = entidadRelacionada;
        return proyectoRelacion;
      })
    );
  }


  public fillColumns(
    proyectos: IProyectoReportData[],
    reportConfig: IReportConfig<IProyectoReportOptions>
  ): ISgiColumnReport[] {
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsRelacionNotExcel();
    } else {
      return this.getColumnsRelacionExcel(proyectos);
    }
  }

  private getColumnsRelacionNotExcel(): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];
    columns.push({
      name: RELACION_FIELD,
      title: this.translate.instant(RELACION_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR),
      type: ColumnType.STRING
    });
    const titleI18n = this.translate.instant(RELACION_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR) +
      ' (' + this.translate.instant(RELACION_TIPO_KEY) +
      ' - ' + this.translate.instant(RELACION_TITULO_KEY) +
      ')';
    const columnEntidad: ISgiColumnReport = {
      name: RELACION_FIELD,
      title: titleI18n,
      type: ColumnType.SUBREPORT,
      fieldOrientation: FieldOrientation.VERTICAL,
      columns
    };
    return [columnEntidad];
  }

  private getColumnsRelacionExcel(proyectos: IProyectoReportData[]): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const maxNumRelaciones = Math.max(...proyectos.map(p => p.relaciones?.length));
    const titleRelacion = this.translate.instant(RELACION_KEY, MSG_PARAMS.CARDINALIRY.PLURAL);

    for (let i = 0; i < maxNumRelaciones; i++) {
      const idRelacion: string = String(i + 1);
      const columnTipoRelacion: ISgiColumnReport = {
        name: RELACION_TIPO_FIELD + idRelacion,
        title: this.translate.instant(RELACION_TIPO_KEY) + idRelacion,
        type: ColumnType.STRING,
      };
      columns.push(columnTipoRelacion);

      const columnTitulo: ISgiColumnReport = {
        name: RELACION_TITULO_FIELD + idRelacion,
        title: titleRelacion + idRelacion + ': ' + this.translate.instant(RELACION_TITULO_KEY),
        type: ColumnType.STRING,
      };
      columns.push(columnTitulo);
    }

    return columns;
  }

  public fillRows(proyectos: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {
    const proyecto = proyectos[index];

    const elementsRow: any[] = [];
    if (!this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsRelacionNotExcel(proyecto, elementsRow);
    } else {
      const maxNumRelaciones = Math.max(...proyectos.map(p => p.relaciones?.length));
      for (let i = 0; i < maxNumRelaciones; i++) {
        const relacion = proyecto.relaciones[i] ?? null;
        this.fillRowsEntidadExcel(elementsRow, relacion);
      }
    }
    return elementsRow;
  }

  private fillRowsRelacionNotExcel(proyecto: IProyectoReportData, elementsRow: any[]) {
    const rowsReport: ISgiRowReport[] = [];

    proyecto.relaciones?.forEach(proyectoRelacion => {
      const relacionElementsRow: any[] = [];

      let relacionContent = proyectoRelacion?.tipoEntidadRelacionada ?? '';
      relacionContent += ' - ';
      relacionContent += proyectoRelacion?.entidadRelacionada?.titulo ?? '';

      relacionElementsRow.push(relacionContent);

      const rowReport: ISgiRowReport = {
        elements: relacionElementsRow
      };
      rowsReport.push(rowReport);
    });

    elementsRow.push({
      rows: rowsReport
    });
  }

  private fillRowsEntidadExcel(elementsRow: any[], proyectoRelacion: ProyectoRelacionListadoExport) {
    if (proyectoRelacion) {
      elementsRow.push(proyectoRelacion.tipoEntidadRelacionada ?? '');
      elementsRow.push(proyectoRelacion.entidadRelacionada?.titulo ?? '');
    } else {
      elementsRow.push('');
      elementsRow.push('');
    }
  }
}
