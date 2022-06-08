import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IAnualidadIngreso } from '@core/models/csp/anualidad-ingreso';
import { ROUTE_NAMES } from '@core/route.names';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ProyectoAnualidadIngresoModalComponent, ProyectoAnualidadIngresoModalData } from '../../modals/proyecto-anualidad-ingreso-modal/proyecto-anualidad-ingreso-modal.component';
import { ProyectoAnualidadActionService } from '../../proyecto-anualidad.action.service';
import { ProyectoAnualidadIngresosFragment } from './proyecto-anualidad-ingresos.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const PROYECTO_PARTIDA_INGRESOS_KEY = marker('csp.proyecto-anualidad.partida-ingreso');

@Component({
  selector: 'sgi-proyecto-anualidad-ingresos',
  templateUrl: './proyecto-anualidad-ingresos.component.html',
  styleUrls: ['./proyecto-anualidad-ingresos.component.scss']
})
export class ProyectoAnualidadIngresosComponent extends FragmentComponent implements OnInit, OnDestroy {
  ROUTE_NAMES = ROUTE_NAMES;

  formPart: ProyectoAnualidadIngresosFragment;
  private subscriptions: Subscription[] = [];

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['identificadorSge', 'codigoEconomico', 'proyectoPartida', 'importeConcedido', 'acciones'];

  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IAnualidadIngreso>>();
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(public actionService: ProyectoAnualidadActionService,
    private dialogService: DialogService,
    private readonly translate: TranslateService,
    private matDialog: MatDialog) {
    super(actionService.FRAGMENT.INGRESOS, actionService);
    this.formPart = this.fragment as ProyectoAnualidadIngresosFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.subscriptions.push(this.formPart.anualidadIngresos$.subscribe(
      (anualidadIngresos) => {
        this.dataSource.data = anualidadIngresos;
      }
    ));

    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor = (wrapper, property) => wrapper.value[property];
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_PARTIDA_INGRESOS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      PROYECTO_PARTIDA_INGRESOS_KEY,
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
  openModal(anualidadIngreso?: IAnualidadIngreso): void {

    const data: ProyectoAnualidadIngresoModalData = {
      anualidadIngreso: anualidadIngreso ? anualidadIngreso : {} as IAnualidadIngreso,
      proyectoId: this.formPart.proyectoId,
      isEdit: false,
      readonly: this.actionService.readonly
    };
    const config = {
      data
    };
    const dialogRef = this.matDialog.open(ProyectoAnualidadIngresoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (partidaIngreso) => {
        if (partidaIngreso) {
          if (!anualidadIngreso) {
            this.formPart.addAnualidadIngreso(partidaIngreso.anualidadIngreso);
          } else {
            this.formPart.updateAnualidadIngreso(new StatusWrapper<IAnualidadIngreso>(partidaIngreso.anualidadIngreso));
          }
        }
      }
    );
  }

  deleteAnualidadIngreso(wrapper: StatusWrapper<IAnualidadIngreso>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteAnualidadIngreso(wrapper);
          }
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }


}
