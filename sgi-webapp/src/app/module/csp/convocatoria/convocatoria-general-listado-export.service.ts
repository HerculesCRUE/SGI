import { Injectable } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FORMULARIO_SOLICITUD_MAP } from '@core/enums/formulario-solicitud';
import { ESTADO_MAP } from '@core/models/csp/convocatoria';
import { ColumnType, ISgiColumnReport } from '@core/models/rep/sgi-column-report';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { AbstractTableExportFillService } from '@core/services/rep/abstract-table-export-fill.service';
import { IReportConfig, IReportOptions } from '@core/services/rep/abstract-table-export.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IConvocatoriaReportData, IConvocatoriaReportOptions } from './convocatoria-listado-export.service';

const TITLE_KEY = marker('csp.convocatoria.titulo');
const REFERENCIA_KEY = marker('csp.convocatoria.referencia');
const ESTADO_KEY = marker('csp.convocatoria.estado');
const UNIDAD_GESTION_KEY = marker('csp.convocatoria.unidad-gestion');
const MODELO_EJECUCION_KEY = marker('csp.convocatoria.modelo-ejecucion');
const FINDALIDAD_KEY = marker('csp.convocatoria.finalidad');
const TIPO_SOLICITUD_SGI_KEY = marker('csp.convocatoria.tipo-formulario');
const FECHA_PUBLICACION_KEY = marker('csp.convocatoria.fecha-publicacion');
const FECHA_PROVISIONAL_KEY = marker('csp.convocatoria.fecha-provisional');
const FECHA_CONCESION_KEY = marker('csp.convocatoria.fecha-concesion');
const DURACION_KEY = marker('csp.convocatoria.duracion');
const AMBITO_GEOGRAFICO_KEY = marker('csp.convocatoria.ambito-geografico');
const REGIMEN_CONCURRENCIA_KEY = marker('csp.convocatoria.regimen-concurrencia');
const ENTIDAD_GESTORA_KEY = marker('csp.convocatoria.entidad-gestora-nombre-numero-identificacion');

@Injectable()
export class ConvocatoriaGeneralListadoExportService
  extends AbstractTableExportFillService<IConvocatoriaReportData, IConvocatoriaReportOptions>{

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly translate: TranslateService,
    private readonly convocatoriaService: ConvocatoriaService,
    private empresaService: EmpresaService,
    private unidadGestionService: UnidadGestionService
  ) {
    super(translate);
  }

  public getData(convocatoriaData: IConvocatoriaReportData): Observable<IConvocatoriaReportData> {
    return of(convocatoriaData).pipe(
      switchMap(() => this.convocatoriaService.findAllConvocatoriaEntidadGestora(convocatoriaData.convocatoria.id).pipe(
        map(entidadGestora => {
          if (entidadGestora.items.length > 0) {
            convocatoriaData.entidadGestora = entidadGestora.items[0];
          }
          return convocatoriaData;
        }),
        switchMap(() => {
          if (convocatoriaData.entidadGestora?.empresa?.id) {
            return this.empresaService.findById(convocatoriaData.entidadGestora.empresa.id).pipe(
              map(empresa => {
                convocatoriaData.entidadGestora.empresa = empresa;
                return convocatoriaData;
              }),
            );
          } else {
            return of(convocatoriaData);
          }
        }),
        switchMap(() => {
          if (convocatoriaData.convocatoria.unidadGestion?.id) {
            return this.unidadGestionService.findById(convocatoriaData.convocatoria.unidadGestion.id).pipe(
              map(unidadGestion => {
                convocatoriaData.convocatoria.unidadGestion = unidadGestion;
                return convocatoriaData;
              })
            );
          } else {
            of(convocatoriaData);
          }
        })
      )
      )
    );
  }

  public fillColumns(resultados: IConvocatoriaReportData[], reportConfig: IReportConfig<IReportOptions>):
    ISgiColumnReport[] {
    const columns: ISgiColumnReport[] = [
      {
        title: this.translate.instant(TITLE_KEY),
        name: 'titulo',
        type: ColumnType.STRING,
      },
      {
        title: this.translate.instant(ESTADO_KEY),
        name: 'estado',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(TIPO_SOLICITUD_SGI_KEY),
        name: 'tipoSolicitudSGI',
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
        title: this.translate.instant(FINDALIDAD_KEY),
        name: 'finalidad',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(REFERENCIA_KEY),
        name: 'identificacion',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(FECHA_PUBLICACION_KEY),
        name: 'fechaPublicacion',
        type: ColumnType.DATE
      },
      {
        title: this.translate.instant(FECHA_PROVISIONAL_KEY),
        name: 'fechaProvisional',
        type: ColumnType.DATE
      },
      {
        title: this.translate.instant(FECHA_CONCESION_KEY),
        name: 'fechaConcesion',
        type: ColumnType.DATE
      },
      {
        title: this.translate.instant(DURACION_KEY),
        name: 'duracion',
        type: ColumnType.NUMBER
      },
      {
        title: this.translate.instant(AMBITO_GEOGRAFICO_KEY),
        name: 'ambitoGeografico',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(REGIMEN_CONCURRENCIA_KEY),
        name: 'regimenConcurrencia',
        type: ColumnType.STRING
      },
      {
        title: this.translate.instant(ENTIDAD_GESTORA_KEY),
        name: 'entidadGestora',
        type: ColumnType.STRING
      },

    ];
    return columns;
  }

  public fillRows(resultados: IConvocatoriaReportData[], index: number, reportConfig: IReportConfig<IConvocatoriaReportOptions>): any[] {
    const convocatoria = resultados[index];

    const elementsRow: any[] = [];
    elementsRow.push(convocatoria.convocatoria?.titulo ?? '');
    elementsRow.push(convocatoria.convocatoria?.estado ? this.translate.instant(ESTADO_MAP.get(convocatoria.convocatoria.estado)) : '');
    elementsRow.push(convocatoria.convocatoria?.formularioSolicitud ?
      this.translate.instant(FORMULARIO_SOLICITUD_MAP.get(convocatoria.convocatoria?.formularioSolicitud)) : '');
    elementsRow.push(convocatoria.convocatoria?.unidadGestion?.nombre ?? '');
    elementsRow.push(convocatoria.convocatoria?.modeloEjecucion?.nombre ?? '');
    elementsRow.push(convocatoria.convocatoria?.finalidad?.nombre ?? '');
    elementsRow.push(convocatoria.convocatoria?.codigo ?? '');
    elementsRow.push(convocatoria.convocatoria?.fechaPublicacion ? LuxonUtils.toBackend(convocatoria.convocatoria?.fechaPublicacion) : '');
    elementsRow.push(convocatoria.convocatoria?.fechaProvisional ? LuxonUtils.toBackend(convocatoria.convocatoria?.fechaProvisional) : '');
    elementsRow.push(convocatoria.convocatoria?.fechaConcesion ? LuxonUtils.toBackend(convocatoria.convocatoria?.fechaConcesion) : '');
    elementsRow.push(convocatoria.convocatoria?.duracion ? convocatoria.convocatoria?.duracion.toString() : '');
    elementsRow.push(convocatoria.convocatoria?.ambitoGeografico?.nombre ?? '');
    elementsRow.push(convocatoria.convocatoria?.regimenConcurrencia?.nombre ?? '');
    elementsRow.push(convocatoria.entidadGestora?.empresa ? (convocatoria.entidadGestora?.empresa.nombre + ' - ' + convocatoria.entidadGestora?.empresa.numeroIdentificacion) : '');

    return elementsRow;
  }

}
