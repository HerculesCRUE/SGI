import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { LanguageService } from '@core/services/language.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { IProyectoReportData, IProyectoReportOptions } from './proyecto-listado-export.service';

const CONVOCATORIA_KEY = 'csp.convocatoria';
const CONVOCATORIA_TITULO_KEY = 'csp.convocatoria.titulo';
const CONVOCATORIA_FIELD = 'convocatoria';
const CONVOCATORIA_IDENTIFICACION_KEY = marker('csp.convocatoria.codigo-externo');
const CONVOCATORIA_IDENTIFICACION_FIELD = 'identificacionConvocatoria';
const CONVOCATORIA_FECHA_PUBLICACION_KEY = marker('csp.convocatoria.fecha-publicacion');
const CONVOCATORIA_FECHA_PUBLICACION_FIELD = 'fechaPublicacionConvocatoria';
const CONVOCATORIA_FECHA_PROVISIONAL_KEY = marker('csp.convocatoria.fecha-provisional');
const CONVOCATORIA_FECHA_PROVISIONAL_FIELD = 'fechaProvisionalConvocatoria';
const CONVOCATORIA_FECHA_CONCESION_KEY = marker('csp.convocatoria.fecha-concesion');
const CONVOCATORIA_FECHA_CONCESION_FIELD = 'fechaConcesionConvocatoria';
const CONVOCATORIA_REGIMEN_KEY = marker('csp.convocatoria.regimen-concurrencia');
const CONVOCATORIA_REGIMEN_FIELD = 'regimenConvocatoria';

const COLUMN_VALUE_PREFIX = ': ';
@Injectable()
export class ProyectoConvocatoriaListadoExportService extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private convocatoriaService: ConvocatoriaService,
    private languageService: LanguageService
  ) {
    super(translate);
  }

  public getData(proyectoData: IProyectoReportData): Observable<IProyectoReportData> {
    if (proyectoData.convocatoriaId) {
      return this.convocatoriaService.findById(proyectoData.convocatoriaId).pipe(
        map(responseConvocatoria => {
          proyectoData.convocatoria = responseConvocatoria;
          return proyectoData;
        })
      );
    } else {
      return of(proyectoData);
    }
  }

  public fillColumns(
    proyectos: IProyectoReportData[],
    reportConfig: IReportConfig<IProyectoReportOptions>
  ): ISgiColumnReport[] {

    if (this.isExcelOrCsv(reportConfig.outputType)) {
      const prefixTitleConvocatoria = this.translate.instant(CONVOCATORIA_KEY, MSG_PARAMS.CARDINALIRY.SINGULAR) + ': ';

      return this.getColumnsConvocatoria(prefixTitleConvocatoria, false);
    }
  }

  private getColumnsConvocatoria(prefix: string, allString: boolean): ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [];

    const columnNombreConvocatoria: ISgiColumnReport = {
      name: CONVOCATORIA_FIELD,
      title: prefix + this.translate.instant(CONVOCATORIA_TITULO_KEY) + this.getValuePrefix(prefix),
      type: ColumnType.STRING,
    };
    columns.push(columnNombreConvocatoria);

    const columnIdentificacionConvocatoria: ISgiColumnReport = {
      name: CONVOCATORIA_IDENTIFICACION_FIELD,
      title: prefix + this.translate.instant(CONVOCATORIA_IDENTIFICACION_KEY) + this.getValuePrefix(prefix),
      type: ColumnType.STRING,
    };
    columns.push(columnIdentificacionConvocatoria);

    const columnFechaPublicacionConvocatoria: ISgiColumnReport = {
      name: CONVOCATORIA_FECHA_PUBLICACION_FIELD,
      title: prefix + this.translate.instant(CONVOCATORIA_FECHA_PUBLICACION_KEY) + this.getValuePrefix(prefix),
      type: allString ? ColumnType.STRING : ColumnType.DATE,
    };
    columns.push(columnFechaPublicacionConvocatoria);

    const columnFechaProvisionalConvocatoria: ISgiColumnReport = {
      name: CONVOCATORIA_FECHA_PROVISIONAL_FIELD,
      title: prefix + this.translate.instant(CONVOCATORIA_FECHA_PROVISIONAL_KEY) + this.getValuePrefix(prefix),
      type: allString ? ColumnType.STRING : ColumnType.DATE,
    };
    columns.push(columnFechaProvisionalConvocatoria);

    const columnFechaConcesionConvocatoria: ISgiColumnReport = {
      name: CONVOCATORIA_FECHA_CONCESION_FIELD,
      title: prefix + this.translate.instant(CONVOCATORIA_FECHA_CONCESION_KEY) + this.getValuePrefix(prefix),
      type: allString ? ColumnType.STRING : ColumnType.DATE,
    };
    columns.push(columnFechaConcesionConvocatoria);

    const columnRegimenConvocatoria: ISgiColumnReport = {
      name: CONVOCATORIA_REGIMEN_FIELD,
      title: prefix + this.translate.instant(CONVOCATORIA_REGIMEN_KEY) + this.getValuePrefix(prefix),
      type: ColumnType.STRING,
    };
    columns.push(columnRegimenConvocatoria);

    return columns;
  }

  public fillRows(proyectos: IProyectoReportData[], index: number, reportConfig: IReportConfig<IProyectoReportOptions>): any[] {

    const proyecto = proyectos[index];
    const elementsRow: any[] = [];
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      this.fillRowsConvocatoriaExcel(elementsRow, proyecto.convocatoria);
    }
    return elementsRow;
  }

  private fillRowsConvocatoriaExcel(elementsRow: any[], convocatoria: IConvocatoria) {
    if (convocatoria) {
      elementsRow.push(this.languageService.getFieldValue(convocatoria.titulo));
      elementsRow.push(convocatoria.codigo ?? '');
      elementsRow.push(LuxonUtils.toBackend(convocatoria.fechaPublicacion) ?? '');
      elementsRow.push(LuxonUtils.toBackend(convocatoria.fechaProvisional) ?? '');
      elementsRow.push(LuxonUtils.toBackend(convocatoria.fechaConcesion) ?? '');
      elementsRow.push(this.languageService.getFieldValue(convocatoria.regimenConcurrencia?.nombre));
    } else {
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
      elementsRow.push('');
    }
  }

  private getValuePrefix(prefix: string): string {
    if (!prefix) {
      return COLUMN_VALUE_PREFIX;
    }
    return '';
  }
}
