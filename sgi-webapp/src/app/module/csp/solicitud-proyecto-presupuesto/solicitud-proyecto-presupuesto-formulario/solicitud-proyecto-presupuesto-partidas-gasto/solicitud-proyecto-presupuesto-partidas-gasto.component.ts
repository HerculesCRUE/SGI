import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { ISolicitudProyectoPresupuesto } from '@core/models/csp/solicitud-proyecto-presupuesto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { PartidaGastoDataModal, PartidaGastoModalComponent } from '../../../shared/partida-gasto-modal/partida-gasto-modal.component';
import { SolicitudProyectoPresupuestoActionService } from '../../solicitud-proyecto-presupuesto.action.service';
import { SolicitudProyectoPresupuestoListado, SolicitudProyectoPresupuestoPartidasGastoFragment } from './solicitud-proyecto-presupuesto-partidas-gasto.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const SOLICITUD_PROYECTO_PRESUPUESTP_PARTIDA_GASTO_KEY = marker('csp.solicitud-proyecto-presupuesto.partida-gasto');

@Component({
  selector: 'sgi-solicitud-proyecto-presupuesto-partidas-gasto',
  templateUrl: './solicitud-proyecto-presupuesto-partidas-gasto.component.html',
  styleUrls: ['./solicitud-proyecto-presupuesto-partidas-gasto.component.scss']
})
export class SolicitudProyectoPresupuestoPartidasGastoComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: SolicitudProyectoPresupuestoPartidasGastoFragment;

  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;

  displayedColumns = [
    'conceptoGasto',
    'anualidad',
    'importe',
    'importeTotal',
    'observaciones',
    'acciones'
  ];
  elementsPage = [5, 10, 25, 100];

  textoDelete: string;

  dataSource = new MatTableDataSource<SolicitudProyectoPresupuestoListado>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    protected logger: NGXLogger,
    protected actionService: SolicitudProyectoPresupuestoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.PARTIDAS_GASTO, actionService);
    this.formPart = this.fragment as SolicitudProyectoPresupuestoPartidasGastoFragment;
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(36%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.setupI18N();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor =
      (element: SolicitudProyectoPresupuestoListado, property: string) => {
        switch (property) {
          case 'conceptoGasto':
            return element.partidaGasto.value.conceptoGasto?.nombre;
          case 'anualidad':
            return element.partidaGasto.value.anualidad;
          case 'importe':
            return element.partidaGasto.value.importeSolicitado;
          case 'importeTotal':
            return element.importeTotalConceptoGasto;
          case 'observaciones':
            return element.partidaGasto.value.observaciones;
          default:
            return element[property];
        }
      };

    const subcription = this.formPart.partidasGastos$
      .subscribe((partidasGasto) => {
        this.dataSource.data = partidasGasto;
      });
    this.subscriptions.push(subcription);
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_PROYECTO_PRESUPUESTP_PARTIDA_GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  deletePartidaGasto(partidaGasto: SolicitudProyectoPresupuestoListado) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deletePartidaGasto(partidaGasto);
          }
        }
      )
    );
  }

  openModal(wrapper?: SolicitudProyectoPresupuestoListado): void {
    const data: PartidaGastoDataModal = {
      partidaGasto: wrapper ? wrapper.partidaGasto.value : {} as ISolicitudProyectoPresupuesto,
      convocatoriaId: this.formPart.convocatoriaId,
      readonly: this.formPart.readonly
    };

    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };

    const dialogRef = this.matDialog.open(PartidaGastoModalComponent, config);
    dialogRef.afterClosed().subscribe((partidaGasto: ISolicitudProyectoPresupuesto) => {
      if (partidaGasto) {
        if (!wrapper) {
          this.formPart.addPartidaGasto(partidaGasto);
        } else {
          if (!wrapper.partidaGasto.created) {
            const wrapperUpdated = new StatusWrapper<ISolicitudProyectoPresupuesto>(wrapper.partidaGasto.value);
            this.formPart.updatePartidaGasto(wrapperUpdated);
          } else {
            this.formPart.updateImporteTotalConceptoGasto(wrapper);
          }

        }
      }
    });
  }

}
