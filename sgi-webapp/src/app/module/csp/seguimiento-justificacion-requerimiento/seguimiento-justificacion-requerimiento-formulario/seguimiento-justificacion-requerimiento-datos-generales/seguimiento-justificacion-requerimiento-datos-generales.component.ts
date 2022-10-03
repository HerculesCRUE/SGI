import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { MSG_PARAMS } from '@core/i18n';
import { IIncidenciaDocumentacionRequerimiento } from '@core/models/csp/incidencia-documentacion-requerimiento';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { IncidenciaDocumentoRequerimientoModalComponent } from '../../modals/incidencia-documento-requerimiento-modal/incidencia-documento-requerimiento-modal.component';
import { SeguimientoJustificacionRequerimientoActionService } from '../../seguimiento-justificacion-requerimiento.action.service';
import { SeguimientoJustificacionRequerimientoDatosGeneralesFragment } from './seguimiento-justificacion-requerimiento-datos-generales.fragment';

const MSG_DELETE_KEY = marker('msg.delete.entity');
const REQUERIMIENTO_PROYECTO_SGI_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.proyecto-sgi');
const REQUERIMIENTO_TIPO_REQUERIMIENTO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.tipo-requerimiento');
const REQUERIMIENTO_OBSERVACIONES_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.observaciones');
const INCIDENCIA_DOCUMENTO_KEY = marker('csp.ejecucion-economica.seguimiento-justificacion.requerimiento.incidencia-documento');

@Component({
  selector: 'sgi-seguimiento-justificacion-requerimiento-datos-generales',
  templateUrl: './seguimiento-justificacion-requerimiento-datos-generales.component.html',
  styleUrls: ['./seguimiento-justificacion-requerimiento-datos-generales.component.scss']
})
export class SeguimientoJustificacionRequerimientoDatosGeneralesComponent
  extends FormFragmentComponent<IRequerimientoJustificacion> implements OnInit, OnDestroy {
  fxLayoutProperties: FxLayoutProperties;
  private subscriptions: Subscription[] = [];
  formPart: SeguimientoJustificacionRequerimientoDatosGeneralesFragment;

  msgParamProyectoSgiEntity = {};
  msgParamTipoRequerimientoEntity = {};
  msgParamObservaciones = {};

  displayedColumns = ['nombreDocumento', 'incidencia', 'acciones'];
  elementosPagina = [5, 10, 25, 100];

  dataSource = new MatTableDataSource<StatusWrapper<IIncidenciaDocumentacionRequerimiento>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  textoDeleteIncidenciaDocumentacion: string;
  msgParamIncidenciaDocumentacionEntity = {};

  constructor(
    readonly actionService: SeguimientoJustificacionRequerimientoActionService,
    private readonly translate: TranslateService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as SeguimientoJustificacionRequerimientoDatosGeneralesFragment;
    this.initFlexProperties();
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

  selectFirstProyectoProyectoSgeIfOnlyOneOption(options: SelectValue<IProyectoProyectoSge>[]): void {
    if (options?.length === 1 && !this.formGroup.controls.proyectoProyectoSge.value) {
      this.formGroup.controls.proyectoProyectoSge.setValue(options[0].item);
    }
  }

  deleteIncidenciaDocumentacion(wrapper: StatusWrapper<IIncidenciaDocumentacionRequerimiento>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDeleteIncidenciaDocumentacion).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteIncidenciaDocumentacion(wrapper);
          }
        }
      )
    );
  }

  openModal(wrapper?: StatusWrapper<IIncidenciaDocumentacionRequerimiento>, rowIndex?: number): void {
    const row = this.resolveTableRowIndexMatchingWithDataSource(rowIndex);

    const config: MatDialogConfig<IIncidenciaDocumentacionRequerimiento> = {
      data: wrapper ? wrapper.value : {} as IIncidenciaDocumentacionRequerimiento
    };

    const dialogRef = this.matDialog.open(IncidenciaDocumentoRequerimientoModalComponent, config);
    dialogRef.afterClosed().subscribe((incidenciaDocumentacionRequerimiento: IIncidenciaDocumentacionRequerimiento) => {
      if (incidenciaDocumentacionRequerimiento) {
        if (wrapper) {
          this.formPart.updateIncidenciaDocumentacion(incidenciaDocumentacionRequerimiento, row);
        } else {
          this.formPart.addIncidenciaDocumentacion(incidenciaDocumentacionRequerimiento);
        }
      }
    });
  }

  private resolveTableRowIndexMatchingWithDataSource(rowIndex: number) {
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    return (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;
  }

  private initFlexProperties(): void {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '1%';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  private setupI18N(): void {
    this.translate.get(
      REQUERIMIENTO_PROYECTO_SGI_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamProyectoSgiEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      REQUERIMIENTO_TIPO_REQUERIMIENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamTipoRequerimientoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      REQUERIMIENTO_OBSERVACIONES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamObservaciones = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.PLURAL }
    );

    this.translate.get(
      INCIDENCIA_DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamIncidenciaDocumentacionEntity = { entity: value });

    this.translate.get(
      INCIDENCIA_DOCUMENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE_KEY,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDeleteIncidenciaDocumentacion = value);
  }
}
