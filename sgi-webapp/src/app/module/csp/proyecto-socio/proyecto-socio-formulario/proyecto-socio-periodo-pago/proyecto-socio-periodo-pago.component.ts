import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoSocioPeriodoPago } from '@core/models/csp/proyecto-socio-periodo-pago';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ProyectoSocioPeriodoPagoModalComponent, ProyectoSocioPeriodoPagoModalData } from '../../modals/proyecto-socio-periodo-pago-modal/proyecto-socio-periodo-pago-modal.component';
import { ProyectoSocioActionService } from '../../proyecto-socio.action.service';
import { ProyectoSocioPeriodoPagoFragment } from './proyecto-socio-periodo-pago.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const PROYECTO_SOCIO_PERIODO_PAGO_KEY = marker('csp.proyecto-socio.periodo-pago');

@Component({
  selector: 'sgi-proyecto-socio-periodo-pago',
  templateUrl: './proyecto-socio-periodo-pago.component.html',
  styleUrls: ['./proyecto-socio-periodo-pago.component.scss']
})
export class ProyectoSocioPeriodoPagoComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ProyectoSocioPeriodoPagoFragment;
  private subscriptions: Subscription[] = [];

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['numPeriodo', 'fechaPrevistaPago', 'importe', 'fechaPago', 'acciones'];

  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IProyectoSocioPeriodoPago>>();
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get readonly(): boolean {
    return this.actionService.readonly;
  }

  constructor(
    private actionService: ProyectoSocioActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.PERIODO_PAGO, actionService);
    this.formPart = this.fragment as ProyectoSocioPeriodoPagoFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    const subcription = this.formPart.periodoPagos$.subscribe(
      (proyectoEquipos) => {
        this.dataSource.data = proyectoEquipos;
      }
    );
    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor = (wrapper, property) => wrapper.value[property];
    this.subscriptions.push(subcription);
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_SOCIO_PERIODO_PAGO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });


    this.translate.get(
      PROYECTO_SOCIO_PERIODO_PAGO_KEY,
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

  openModal(wrapper?: StatusWrapper<IProyectoSocioPeriodoPago>): void {
    const proyectoSocioPeriodoPago: IProyectoSocioPeriodoPago = {
      numPeriodo: this.dataSource.data.length + 1
    } as IProyectoSocioPeriodoPago;
    const fechaInicioProyectoSocio = this.actionService.proyectoSocio?.fechaInicio;
    const fechaFinProyectoSocio = this.actionService.proyectoSocio?.fechaFin;
    const data: ProyectoSocioPeriodoPagoModalData = {
      proyectoSocioPeriodoPago: wrapper?.value ?? proyectoSocioPeriodoPago,
      fechaInicioProyectoSocio,
      fechaFinProyectoSocio,
      isEdit: Boolean(wrapper),
      readonly: this.actionService.readonly
    };
    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(ProyectoSocioPeriodoPagoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: ProyectoSocioPeriodoPagoModalData) => {
        if (modalData) {
          if (wrapper) {
            this.formPart.updatePeriodoPago(wrapper);
          } else {
            this.formPart.addPeriodoPago(modalData.proyectoSocioPeriodoPago);
          }
        }
      }
    );
  }

  deleteProyectoEquipo(wrapper: StatusWrapper<IProyectoSocioPeriodoPago>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deletePeriodoPago(wrapper);
          }
        }
      )
    );
  }

}
