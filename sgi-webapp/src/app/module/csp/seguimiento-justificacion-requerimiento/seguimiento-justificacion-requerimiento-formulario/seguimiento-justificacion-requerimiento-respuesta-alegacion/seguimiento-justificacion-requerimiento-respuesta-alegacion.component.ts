import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IAlegacionRequerimiento } from '@core/models/csp/alegacion-requerimiento';
import { IIncidenciaDocumentacionRequerimiento } from '@core/models/csp/incidencia-documentacion-requerimiento';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { IncidenciaDocumentacionRequerimientoAlegacionModalComponent } from '../../modals/incidencia-documentacion-requerimiento-alegacion-modal/incidencia-documentacion-requerimiento-alegacion-modal.component';
import { SeguimientoJustificacionRequerimientoActionService } from '../../seguimiento-justificacion-requerimiento.action.service';
import { SeguimientoJustificacionRequerimientoRespuestaAlegacionFragment } from './seguimiento-justificacion-requerimiento-respuesta-alegacion.fragment';

const REQUERIMIENTO_JUSTIFICANTE_REINTEGRO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.respuesta-alegacion.justificante-reintegro');
const REQUERIMIENTO_OBSERVACIONES_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.respuesta-alegacion.observaciones');

@Component({
  selector: 'sgi-seguimiento-justificacion-requerimiento-respuesta-alegacion',
  templateUrl: './seguimiento-justificacion-requerimiento-respuesta-alegacion.component.html',
  styleUrls: ['./seguimiento-justificacion-requerimiento-respuesta-alegacion.component.scss']
})
export class SeguimientoJustificacionRequerimientoRespuestaAlegacionComponent
  extends FormFragmentComponent<IAlegacionRequerimiento> implements OnInit, OnDestroy {

  formPart: SeguimientoJustificacionRequerimientoRespuestaAlegacionFragment;
  private subscriptions: Subscription[] = [];

  msgParamJustificanteReintegroEntity = {};
  msgParamObservacionesEntity = {};

  displayedColumns = ['nombreDocumento', 'incidencia', 'alegacion', 'acciones'];
  elementosPagina = [5, 10, 25, 100];
  dataSource = new MatTableDataSource<StatusWrapper<IIncidenciaDocumentacionRequerimiento>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    readonly actionService: SeguimientoJustificacionRequerimientoActionService,
    private readonly translate: TranslateService,
    private matDialog: MatDialog,
  ) {
    super(actionService.FRAGMENT.RESPUESTA_ALEGACION, actionService);
    this.formPart = this.fragment as SeguimientoJustificacionRequerimientoRespuestaAlegacionFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.initIncidenciaDocumentacionTable();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  private initIncidenciaDocumentacionTable(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.getIncidenciaDocumentacionTableData();
  }

  private getIncidenciaDocumentacionTableData(): void {
    this.subscriptions.push(this.actionService.getIncidenciasDocumentacion$()
      .subscribe(elements => this.dataSource.data = elements));
  }

  openModal(wrapper?: StatusWrapper<IIncidenciaDocumentacionRequerimiento>, rowIndex?: number): void {
    const row = this.resolveTableRowIndexMatchingWithDataSource(rowIndex);

    const config: MatDialogConfig<IIncidenciaDocumentacionRequerimiento> = {
      data: wrapper ? wrapper.value : {} as IIncidenciaDocumentacionRequerimiento
    };

    const dialogRef = this.matDialog.open(IncidenciaDocumentacionRequerimientoAlegacionModalComponent, config);
    dialogRef.afterClosed().subscribe((incidenciaDocumentacionRequerimiento: IIncidenciaDocumentacionRequerimiento) => {
      if (incidenciaDocumentacionRequerimiento) {
        this.formPart.updateIncidenciaDocumentacion(incidenciaDocumentacionRequerimiento, row);
      }
    });
  }

  private resolveTableRowIndexMatchingWithDataSource(rowIndex: number) {
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    return (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;
  }

  private setupI18N(): void {
    this.translate.get(
      REQUERIMIENTO_JUSTIFICANTE_REINTEGRO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamJustificanteReintegroEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      REQUERIMIENTO_OBSERVACIONES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamObservacionesEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );
  }
}
