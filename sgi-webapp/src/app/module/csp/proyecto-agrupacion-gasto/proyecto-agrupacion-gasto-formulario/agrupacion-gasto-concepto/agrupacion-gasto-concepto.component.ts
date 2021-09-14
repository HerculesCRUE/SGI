import { listLazyRoutes } from '@angular/compiler/src/aot/lazy_routes';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IAgrupacionGastoConcepto } from '@core/models/csp/agrupacion-gasto-concepto';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Observable, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { AgrupacionGastoConceptoModalComponent, AgrupacionGastoConceptoModalData } from '../../modals/agrupacion-gasto-concepto-modal/agrupacion-gasto-concepto-modal.component';
import { ProyectoAgrupacionGastoActionService } from '../../proyecto-agrupacion-gasto.action.service';
import { AgrupacionGastoConceptoFragment } from './agrupacion-gasto-concepto.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const CONCEPTO_GASTO_ADD_KEY = marker('title.csp.concepto-gasto.add');
const CONCEPTO_GASTO_DELETE_KEY = marker('title.csp.concepto-gasto.delete');
const MODAL_TITLE_KEY = marker('csp.agrupacion-gasto-concepto');

@Component({
  selector: 'sgi-agrupacion-gasto-concepto',
  templateUrl: './agrupacion-gasto-concepto.component.html',
  styleUrls: ['./agrupacion-gasto-concepto.component.scss']
})
export class AgrupacionGastoConceptoComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: AgrupacionGastoConceptoFragment;
  private subscriptions: Subscription[] = [];

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['conceptoGasto', 'acciones'];

  modalTitleEntity: string;
  msgParamEntity = {};
  textoDelete: string;
  listaConceptos: Observable<IConceptoGasto[]>;
  dataSource = new MatTableDataSource<StatusWrapper<IAgrupacionGastoConcepto>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    public actionService: ProyectoAgrupacionGastoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.AGRUPACION_GASTO_CONCEPTO, actionService);
    this.formPart = this.fragment as AgrupacionGastoConceptoFragment;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IAgrupacionGastoConcepto>, property: string) => {
        switch (property) {
          case 'conceptoGasto':
            return wrapper.value.conceptoGasto;
          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;

    const subcription = this.formPart.agrupacionGastoConceptos$.subscribe(
      (agrupacionGastoConceptos) => this.dataSource.data = agrupacionGastoConceptos
    );
    this.subscriptions.push(subcription);

  }

  private setupI18N(): void {
    this.translate.get(
      CONCEPTO_GASTO_ADD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      MODAL_TITLE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.modalTitleEntity = value);

    this.translate.get(
      CONCEPTO_GASTO_DELETE_KEY,
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

  openModal(): void {
    const data: AgrupacionGastoConceptoModalData = {
      proyectoId: this.actionService.proyectoId,
      agrupacionId: this.formPart.getKey() as number,
      conceptosEliminados: this.formPart.agrupacionGastoConceptosEliminados.map(element => element.value.conceptoGasto),
      titleEntity: this.modalTitleEntity,
      entidad: null,
      selectedEntidades: this.dataSource.data.map(element => element.value),
      readonly: this.actionService.readonly,
      isEdit: false
    };

    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(AgrupacionGastoConceptoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: AgrupacionGastoConceptoModalData) => {
        if (modalData) {
          modalData.conceptosEliminados.forEach(conceptoGasto => {
            const index = this.formPart.agrupacionGastoConceptosEliminados
              .map(element => element.value.conceptoGasto).findIndex((value: IConceptoGasto) => value === conceptoGasto);
            if (index >= 0) {
              this.formPart.agrupacionGastoConceptosEliminados.splice(index, 1);
            }
          });
          this.formPart.addAgrupacionGastoConcepto({ conceptoGasto: modalData.entidad } as IAgrupacionGastoConcepto);
        }
      }
    );
  }

  deleteAgrupacionGastoConcepto(wrapper: StatusWrapper<IAgrupacionGastoConcepto>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteAgrupacionGastoConcepto(wrapper);
          }
        }
      )
    );
  }
}
