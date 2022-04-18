import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IAnualidadGasto } from '@core/models/csp/anualidad-gasto';
import { ROUTE_NAMES } from '@core/route.names';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ProyectoAnualidadGastoModalComponent, ProyectoAnualidadGastoModalData } from '../../modals/proyecto-anualidad-gasto-modal/proyecto-anualidad-gasto-modal.component';
import { ProyectoAnualidadActionService } from '../../proyecto-anualidad.action.service';
import { ProyectoAnualidadGastosFragment } from './proyecto-anualidad-gastos.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const PROYECTO_PARTIDA_GASTO_KEY = marker('csp.proyecto-anualidad.partida-gasto');

@Component({
  selector: 'sgi-proyecto-anualidad-gastos',
  templateUrl: './proyecto-anualidad-gastos.component.html',
  styleUrls: ['./proyecto-anualidad-gastos.component.scss']
})
export class ProyectoAnualidadGastosComponent extends FragmentComponent implements OnInit, OnDestroy {
  ROUTE_NAMES = ROUTE_NAMES;

  formPart: ProyectoAnualidadGastosFragment;
  private subscriptions: Subscription[] = [];

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['identificadorSge', 'conceptoGasto', 'codigoEconomico', 'proyectoPartida', 'importePresupuesto', 'importeConcedido', 'acciones'];

  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IAnualidadGasto>>();
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get readonly(): boolean {
    return this.actionService.readonly;
  }

  constructor(private actionService: ProyectoAnualidadActionService,
    private dialogService: DialogService,
    private readonly translate: TranslateService,
    private matDialog: MatDialog) {
    super(actionService.FRAGMENT.GASTOS, actionService);
    this.formPart = this.fragment as ProyectoAnualidadGastosFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.subscriptions.push(this.formPart.anualidadGastos$.subscribe(
      (anualidadGastos) => {
        this.dataSource.data = anualidadGastos;
      }
    ));

    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor = (wrapper, property) => wrapper.value[property];
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_PARTIDA_GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      PROYECTO_PARTIDA_GASTO_KEY,
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

  /**
   * Se muestra el modal de Partidas de gasto
   */
  openModal(anualidadGasto?: IAnualidadGasto): void {
    const data: ProyectoAnualidadGastoModalData = {
      anualidadGasto: anualidadGasto ? anualidadGasto : {} as IAnualidadGasto,
      proyectoId: this.formPart.proyectoId,
      fechaInicioAnualidad: this.formPart.fechaInicioAnualidad,
      fechaFinAnualidad: this.formPart.fechaFinAnualidad,
      isEdit: !!anualidadGasto,
      readonly: this.actionService.readonly
    };
    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(ProyectoAnualidadGastoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (partidaGasto) => {
        if (partidaGasto) {
          if (!anualidadGasto) {
            this.formPart.addAnualidadGasto(partidaGasto.anualidadGasto);
          } else {
            this.formPart.updateAnualidadGasto(new StatusWrapper<IAnualidadGasto>(partidaGasto.anualidadGasto));
          }
        }

      }
    );
  }

  deleteAnualidadGasto(wrapper: StatusWrapper<IAnualidadGasto>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteAnualidadGasto(wrapper);
          }
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }


}
