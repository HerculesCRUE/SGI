import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IPeriodoTitularidadTitular } from '@core/models/pii/periodo-titularidad-titular';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { filter, switchMap } from 'rxjs/operators';
import { InvencionActionService } from '../../../invencion.action.service';
import { PeriodoTitularidadTitularModalComponent, PeriodoTitularidadTitularModalData } from '../../../modals/periodo-titularidad-titular-modal/periodo-titularidad-titular-modal.component';
import { IPeriodoTitularidadFront, PeriodoTitularidadFragment } from '../periodo-titularidad.fragment';

const PERIODO_TITULARIDAD_TITULARES_KEY = marker('pii.invencion-titularidad.periodo-titularidad-titular');
const MSG_PERIODO_TITULARIDAD_TITULARES_DELETE = marker('msg.delete.entity');

@Component({
  selector: 'sgi-periodo-titularidad-titulares',
  templateUrl: './periodo-titularidad-titulares.component.html',
  styleUrls: ['./periodo-titularidad-titulares.component.scss']
})
export class PeriodoTitularidadTitularesComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: PeriodoTitularidadFragment;

  elementosPagina = [5, 10, 25, 100];
  columnas = ['titular', 'participacion', 'acciones'];
  msgParamEntity = {};
  msgParamEntityPlural = {};
  msgDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IPeriodoTitularidadTitular>>();
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

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor = (item: StatusWrapper<IPeriodoTitularidadTitular>, property: string) => {
      switch (property) {
        default: return item.value[property];
      }
    };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.periodosTitularidadTitular$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  /**
   * Apertura de modal de {@link IPeriodoTitularidadTitular}
   */
  openModal(periodoTitularidad: StatusWrapper<IPeriodoTitularidadTitular>): void {
    const periodoTitularidadTitularModalData: PeriodoTitularidadTitularModalData = {
      periodoTitularidadTitular: periodoTitularidad ?? this.formPart.createEmptyTitular(),
      isEdit: !!periodoTitularidad,
      titularesNotAllowed: this.formPart.empresasTitularesPeriodo
    };
    const config = {
      data: periodoTitularidadTitularModalData
    };
    const dialogRef = this.matDialog.open(PeriodoTitularidadTitularModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: StatusWrapper<IPeriodoTitularidadTitular>) => {
        if (!result) {
          return;
        }
        if (result.created) {
          this.formPart.addPeriodoTitularidadTitular(result);
        } else {
          this.formPart.editPeriodoTitularidadTitular(result);
        }
      });
  }

  deletePeriodoTitularidad(wrapper: StatusWrapper<IPeriodoTitularidadTitular>) {
    this.showConfirmationMessage(this.msgDelete, () => {
      this.formPart.deletePeriodoTitularidadTitular(wrapper);
    });
  }

  public canEdit(elem: StatusWrapper<IPeriodoTitularidadTitular>) {
    return this.formPart.periodoVigente?.value?.id === elem.value?.periodoTitularidad?.id
      && !(this.formPart.periodoVigente?.value as IPeriodoTitularidadFront)?.hasTramosReparto;
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

  private setupI18N(): void {

    this.translate.get(
      PERIODO_TITULARIDAD_TITULARES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      PERIODO_TITULARIDAD_TITULARES_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamEntityPlural = { entity: value });

    this.translate.get(
      PERIODO_TITULARIDAD_TITULARES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_PERIODO_TITULARIDAD_TITULARES_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })).subscribe((value) => this.msgDelete = value);

  }

}
