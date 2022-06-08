import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { IInvencionReportData, IInvencionReportOptions } from './invencion-listado-export.service';

const NUMERO_INVENCION_KEY = marker('pii.invencion.numero-invencion');
const TITULO_INVENCION_KEY = marker('pii.invencion.titulo');
const FECHA_COMUNICACION_KEY = marker('pii.invencion.fecha-comunicacion');
const TIPO_PROTECCION_KEY = marker('pii.invencion.tipo-proteccion');

@Injectable()
export class InvencionGeneralListadoExportService extends
  AbstractTableExportFillService<IInvencionReportData, IInvencionReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly invencionService: InvencionService,
    private readonly personaService: PersonaService,
    private readonly vinculacionService: VinculacionService
  ) {
    super(translate);
  }

  public getData(invencionData: IInvencionReportData): Observable<IInvencionReportData> {
    return of(invencionData);
  }

  public fillColumns(
    invenciones: IInvencionReportData[],
    reportConfig: IReportConfig<IInvencionReportOptions>
  ): ISgiColumnReport[] {

    const columns: ISgiColumnReport[] = [];

    return [
      {
        title: this.translate.instant(NUMERO_INVENCION_KEY),
        name: 'numeroInvencion',
        type: ColumnType.NUMBER,
        format: '#'
      }, {
        title: this.translate.instant(TITULO_INVENCION_KEY),
        name: 'tituloInvencion',
        type: ColumnType.STRING,
        format: '#'
      }, {
        title: this.translate.instant(FECHA_COMUNICACION_KEY),
        name: 'fechaComunicacion',
        type: ColumnType.DATE
      }, {
        title: this.translate.instant(TIPO_PROTECCION_KEY),
        name: 'tipoProteccion',
        type: ColumnType.STRING
      }
    ] as ISgiColumnReport[];

  }

  public fillRows(invenciones: IInvencionReportData[], index: number, reportConfig: IReportConfig<IInvencionReportOptions>): any[] {

    const invencionData = invenciones[index];

    return [
      invencionData.id,
      invencionData.titulo,
      LuxonUtils.toBackend(invencionData.fechaComunicacion),
      invencionData.tipoProteccion.nombre
    ];
  }
}
