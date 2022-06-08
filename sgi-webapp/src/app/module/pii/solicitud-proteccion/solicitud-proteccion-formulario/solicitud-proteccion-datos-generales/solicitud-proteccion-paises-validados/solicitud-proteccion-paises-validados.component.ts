import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IPaisValidado } from '@core/models/pii/pais-validado';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { of, Subscription } from 'rxjs';
import { filter, switchMap, tap } from 'rxjs/operators';
import { PaisValidadoModalComponent, PaisValidadoModalData } from '../../../modals/pais-validado-modal/pais-validado-modal.component';
import { SolicitudProteccionActionService } from '../../../solicitud-proteccion.action.service';
import { SolicitudProteccionDatosGeneralesFragment } from '../solicitud-proteccion-datos-generales.fragment';

const PAIS_VALIDADO_KEY = marker('pii.solicitud-proteccion.pais-validado.titulo');
const MSG_PAIS_VALIDADO_DELETE = marker('msg.delete.entity');

@Component({
  selector: 'sgi-solicitud-proteccion-paises-validados',
  templateUrl: './solicitud-proteccion-paises-validados.component.html',
  styleUrls: ['./solicitud-proteccion-paises-validados.component.scss']
})
export class SolicitudProteccionPaisesValidadosComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: SolicitudProteccionDatosGeneralesFragment;

  elementosPagina = [5, 10, 25, 100];
  columnas = ['fechaValidacion', 'pais', 'codigoInvencion', 'acciones'];
  msgParamEntity = {};
  msgParamEntityPlural = {};
  msgDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IPaisValidado>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    protected actionService: SolicitudProteccionActionService,
    private readonly translate: TranslateService,
    private readonly matDialog: MatDialog,
    private readonly dialogService: DialogService) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as SolicitudProteccionDatosGeneralesFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor = (item: StatusWrapper<IPaisValidado>, property: string) => {
      switch (property) {
        default: return item.value[property];
      }
    };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.paisesValidados$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  /**
   * Apertura de modal de {@link IPaisValidado}
   */
  openModal(paisValidado: StatusWrapper<IPaisValidado>): void {
    if (!paisValidado) {
      paisValidado = this.formPart.createEmptyPaisValidado();
    }
    const config = {
      data: {
        paisValidado,
        paises: this.formPart.paises$.getValue()
      } as PaisValidadoModalData
    };
    const dialogRef = this.matDialog.open(PaisValidadoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: StatusWrapper<IPaisValidado>) => {
        if (!result) {
          return;
        }
        if (result.created) {
          this.formPart.addPaisValidado(result);
        } else {
          this.formPart.editPaisValidado(result);
        }
      });
  }

  deletePaisValidado(wrapper: StatusWrapper<IPaisValidado>) {
    this.showConfirmationMessage(this.msgDelete, () => {
      this.formPart.deletePaisValidado(wrapper);
    });
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
      PAIS_VALIDADO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    )
      .pipe(
        tap(value => { this.msgParamEntity = { entity: value } })
      )
      .subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      PAIS_VALIDADO_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamEntityPlural = { entity: value });

    of(
      this.msgParamEntity
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_PAIS_VALIDADO_DELETE,
          { ...value, ...MSG_PARAMS.GENDER.MALE }
        );
      })).subscribe((value) => this.msgDelete = value);

  }
}
