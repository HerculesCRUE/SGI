import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IGrupo } from '@core/models/csp/grupo';
import { IProyecto } from '@core/models/csp/proyecto';
import { IInvencion } from '@core/models/pii/invencion';
import { IRelacion, TIPO_ENTIDAD_MAP, TipoEntidad } from '@core/models/rel/relacion';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { LanguageService } from '@core/services/language.service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { RelacionService } from '@core/services/rel/relaciones/relacion.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, from, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast } from 'rxjs/operators';
import { IGrupoWithTitulo } from './proyecto-formulario/proyecto-relaciones/proyecto-relaciones.fragment';
import { IProyectoReportData, IProyectoReportOptions } from './proyecto-listado-export.service';

const RELACION_KEY = marker('csp.proyecto-relacion');
const RELACION_TIPO_KEY = marker('label.csp.proyecto-relacion.tipo-relacion');
const RELACION_TITULO_KEY = marker('label.csp.proyecto-relacion.titulo');

const RELACION_TIPO_FIELD = 'relacionTipo';
const RELACION_TITULO_FIELD = 'relacionTitulo';

type EntidadRelacionada = IConvocatoria | IInvencion | IProyecto | IGrupoWithTitulo;

export interface ProyectoRelacionListadoExport {
  id: number;
  entidadRelacionada: EntidadRelacionada;
  tipoEntidadRelacionada: TipoEntidad;
  observaciones: string;
}

@Injectable()
export class ProyectoRelacionListadoExportService extends AbstractTableExportFillService<IProyectoReportData, IProyectoReportOptions> {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private relacionService: RelacionService,
    private convocatoriaService: ConvocatoriaService,
    private invencionService: InvencionService,
    private proyectoService: ProyectoService,
    private grupoService: GrupoService,
    private languageService: LanguageService
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
          observaciones: this.languageService.getFieldValue(proyectoRelacion.observaciones)
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
      case TipoEntidad.GRUPO:
        observable$ = this.grupoService.findById(proyectoRelacion.entidadRelacionada.id).pipe(map(grupo => this.createGrupoWithTitulo(grupo)));
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
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      return this.getColumnsRelacionExcel(proyectos);
    }
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
    if (this.isExcelOrCsv(reportConfig.outputType)) {
      const maxNumRelaciones = Math.max(...proyectos.map(p => p.relaciones?.length));
      for (let i = 0; i < maxNumRelaciones; i++) {
        const relacion = proyecto.relaciones[i] ?? null;
        this.fillRowsEntidadExcel(elementsRow, relacion);
      }
    }
    return elementsRow;
  }

  private fillRowsEntidadExcel(elementsRow: any[], proyectoRelacion: ProyectoRelacionListadoExport) {
    if (proyectoRelacion) {
      elementsRow.push(proyectoRelacion.tipoEntidadRelacionada ? this.translate.instant(TIPO_ENTIDAD_MAP.get(proyectoRelacion.tipoEntidadRelacionada)) : '');
      elementsRow.push(this.languageService.getFieldValue(proyectoRelacion.entidadRelacionada?.titulo));
    } else {
      elementsRow.push('');
      elementsRow.push('');
    }
  }

  private createGrupoWithTitulo(grupo: IGrupo): IGrupoWithTitulo {
    return {
      ...grupo,
      titulo: grupo.nombre
    };
  }
}
