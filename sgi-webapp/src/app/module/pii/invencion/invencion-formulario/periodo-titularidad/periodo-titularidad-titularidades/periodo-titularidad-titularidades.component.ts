import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IPeriodoTitularidad } from '@core/models/pii/periodo-titularidad';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { filter, switchMap, take } from 'rxjs/operators';
import { InvencionActionService } from '../../../invencion.action.service';
import { IPeriodoTitularidadModalData, IResultPeriodoTitularidadModalData, PeriodoTitularidadModalComponent } from '../../../modals/periodo-titularidad-modal/periodo-titularidad-modal.component';
import { IPeriodoTitularidadFront, PeriodoTitularidadFragment } from '../periodo-titularidad.fragment';

const PERIODO_TITULARIDAD_KEY = marker('pii.invencion-titularidad.periodo-titularidad');
const MSG_PERIODO_TITULARIDAD_DELETE = marker('msg.delete.entity');
const MSG_TITULARES_LOST_CHANGES = marker('pii.invencion-titularidad.periodo-titularidad.lost-changes');
const MSG_PERIODO_TITULARIDAD_ACTIVATE_PREVIOUS = marker('pii.invencion-titularidad.periodo-titularidad.reactivar-anterior');

@Component({
  selector: 'sgi-periodo-titularidad-titularidades',
  templateUrl: './periodo-titularidad-titularidades.component.html',
  styleUrls: ['./periodo-titularidad-titularidades.component.scss']
})
export class PeriodoTitularidadTitularidadesComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: PeriodoTitularidadFragment;

  elementosPagina = [5, 10, 25, 100];
  columnas = ['fechaInicio', 'fechaFin', 'acciones'];
  msgParamEntity = {};
  msgParamEntityPlural = {};
  msgDelete: string;
  msgLostTitularesChanged: string;
  msgReactivatePrevious: string;

  dataSource = new MatTableDataSource<StatusWrapper<IPeriodoTitularidad>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    protected actionService: InvencionActionService,
    private readonly translate: TranslateService,
    private readonly matDialog: MatDialog,
    private readonly dialogService: DialogService) {
    super(actionService.FRAGMENT.PERIODOS_TITULARIDAD, actionService);
    this.formPart = this.fragment as PeriodoTitularidadFragment;
  }

  private showConfirmationMessage(message: string, callback) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(message)
        .pipe(
          filter(response => !!response)
        )
        .subscribe(
          () => callback()
        )
    );
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor = (item: StatusWrapper<IPeriodoTitularidad>, property: string) => {
      switch (property) {
        default: return item.value[property];
      }
    };
    this.sort.active = 'fechaFin';
    this.sort.direction = 'asc';
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.periodosTitularidad$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {

    this.translate.get(
      PERIODO_TITULARIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      PERIODO_TITULARIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamEntityPlural = { entity: value });

    this.translate.get(
      MSG_PERIODO_TITULARIDAD_ACTIVATE_PREVIOUS,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgReactivatePrevious = value);

    this.translate.get(
      PERIODO_TITULARIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_PERIODO_TITULARIDAD_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
        );
      })).subscribe((value) => this.msgDelete = value);

    this.translate.get(
      PERIODO_TITULARIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_TITULARES_LOST_CHANGES,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })).subscribe((value) => this.msgLostTitularesChanged = value);

  }

  editPeriodoTitularidad(wrapper: StatusWrapper<IPeriodoTitularidad>) {
    this.formPart.editPeriodoTitularidad(wrapper);
  }

  deletePeriodoTitularidad(wrapper: StatusWrapper<IPeriodoTitularidad>) {
    this.showConfirmationMessage(this.msgDelete, () => {
      this.formPart.deletePeriodoTitularidad(wrapper);
      this.formPart.periodosTitularidad$
        .pipe(take(1), filter(elem => elem.length !== 0))
        .subscribe(
          () => this.messageReactivatePrevious(wrapper)
        );
    });
  }

  private messageReactivatePrevious(elemToDelete: StatusWrapper<IPeriodoTitularidad>) {
    if (elemToDelete.created) {
      this.formPart.reactivatePreviousPeriodoTitularidad();
      return;
    }
    this.showConfirmationMessage(this.msgReactivatePrevious, () => {
      this.formPart.reactivatePreviousPeriodoTitularidad();
    });
  }

  setSelectedPeriodoTitularidad(elem: StatusWrapper<IPeriodoTitularidad>): void {
    const callback = () => {
      this.formPart.setSelectedPeriodoTitularidad(elem);
    };
    this.formPart.periodosTitularidadSelected$
      .pipe(
        take(1)
      )
      .subscribe((selected) => {
        if (selected?.value?.id !== elem?.value?.id && this.formPart.hasChangesTitulares()) {
          this.showConfirmationMessage(this.msgLostTitularesChanged, callback);
        } else {
          callback();
        }
      });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  /**
   * Apertura de modal de {@link IPeriodoTitularidad}
   */
  openModal(periodoTitularidad: StatusWrapper<IPeriodoTitularidad>): void {

    const periodoTitularidadModalData: IPeriodoTitularidadModalData = {
      periodoTitularidad: periodoTitularidad ?? this.formPart.createEmptyPeriodoTitularidad(),
      fechaInicioMinima: periodoTitularidad ? this.formPart.fechaInicioMinimaEdicion : this.formPart.fechaInicioMinimaCreacion,
      showFechaFin: !periodoTitularidad && !this.formPart.previousPeriodoTitularidadHistorico ? false : null
    };

    const config: MatDialogConfig = {
      panelClass: 'sgi-dialog-container',
      data: periodoTitularidadModalData,
      minWidth: '500px',
    };
    const dialogRef = this.matDialog.open(PeriodoTitularidadModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: IResultPeriodoTitularidadModalData) => {
        if (!result) {
          return;
        }
        if (result.periodoTitularidad.created) {
          this.formPart.addPeriodoTitularidad(result.periodoTitularidad);
        } else {
          this.editPeriodoTitularidad(result.periodoTitularidad);
        }
      });
  }

  /**
   * Devuelve {@link True} si se puede editar la Fecha de Inicio del {@link IPeriodoTitularidad }.
   * Se verifica que no existan tramos de reparto en el {@link IPeriodoTitularidad } y que sea al período activo.
   *
   * @param elem Objeto de tipo {@link IPeriodoTitularidad} a evaluar
   * @returns Valor Booleano
   */
  public canEdit(elem: StatusWrapper<IPeriodoTitularidad>): boolean {
    return !(elem.value as IPeriodoTitularidadFront).hasTramosReparto
      && this.formPart.periodoVigente?.value?.id === elem.value?.id
      && !this.formPart.periodoVigente?.created;
  }

  /**
   * Devuelve {@link True} si es posible eliminar el {@link IPeriodoTitularidad }.
   * Se verifica que no existan tramos de reparto en el {@link IPeriodoTitularidad } y que sea al período activo.
   *
   * @param elem Objeto de tipo {@link IPeriodoTitularidad} a evaluar
   * @returns Valores {@link True} o {@link False}
   */
  public canDelete(elem: StatusWrapper<IPeriodoTitularidad>): boolean {
    return !(elem.value as IPeriodoTitularidadFront).hasTramosReparto
      && this.formPart.periodoVigente?.value?.id === elem.value?.id;
  }

}
