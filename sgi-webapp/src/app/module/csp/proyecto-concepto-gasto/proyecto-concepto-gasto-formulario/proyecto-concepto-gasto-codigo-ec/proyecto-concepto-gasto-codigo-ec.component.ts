import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoConceptoGasto } from '@core/models/csp/proyecto-concepto-gasto';
import { IProyectoConceptoGastoCodigoEc } from '@core/models/csp/proyecto-concepto-gasto-codigo-ec';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ProyectoConceptoGastoCodigoEcDataModal, ProyectoConceptoGastoCodigoEcModalComponent } from '../../modals/proyecto-concepto-gasto-codigo-ec-modal/proyecto-concepto-gasto-codigo-ec-modal.component';
import { ProyectoConceptoGastoActionService } from '../../proyecto-concepto-gasto.action.service';
import { CodigoEconomicoListado, ProyectoConceptoGastoCodigoEcFragment } from './proyecto-concepto-gasto-codigo-ec.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const PROYECTO_CONCEPTO_GASTO_ECONOMICO_PERMITIDO = marker('csp.proyecto-concepto-gasto-codigo-economico.permitido');
const PROYECTO_CONCEPTO_GASTO_ECONOMICO_NO_PERMITIDO = marker('csp.proyecto-concepto-gasto-codigo-economico.no-permitido');

@Component({
  selector: 'sgi-proyecto-concepto-gasto-codigo-ec',
  templateUrl: './proyecto-concepto-gasto-codigo-ec.component.html',
  styleUrls: ['./proyecto-concepto-gasto-codigo-ec.component.scss']
})
export class ProyectoConceptoGastoCodigoEcComponent extends FragmentComponent implements OnInit, OnDestroy {
  formGroup: FormGroup;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  formPart: ProyectoConceptoGastoCodigoEcFragment;
  private subscriptions: Subscription[] = [];

  elementosPagina: number[] = [5, 10, 25, 100];
  displayedColumns: string[] = ['helpIcon', 'codigoEconomico', 'fechaInicio', 'fechaFin', 'observaciones', 'acciones'];

  dataSource = new MatTableDataSource<CodigoEconomicoListado>();
  @ViewChild('paginator', { static: true }) paginator: MatPaginator;
  @ViewChild('sort', { static: true }) sort: MatSort;

  msgParamCodigoPermitidoEntity = {};
  msgParamCodigoNoPermitidoEntity = {};
  textoDeletePermitido: string;
  textoDeleteNoPermitido: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    protected readonly logger: NGXLogger,
    public readonly actionService: ProyectoConceptoGastoActionService,
    private matDialog: MatDialog,
    private readonly dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.CODIGOS_ECONOMICOS, actionService);
    this.formPart = this.fragment as ProyectoConceptoGastoCodigoEcFragment;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(50%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '1';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.dataSource = new MatTableDataSource<CodigoEconomicoListado>();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.proyectoConceptoGastoCodigosEcs$.subscribe(elements => {
      this.dataSource.data = elements;
    }));

    this.dataSource.sortingDataAccessor =
      (wrapper: CodigoEconomicoListado, property: string) => {
        switch (property) {
          default:
            return wrapper[property];
        }
      };
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_CONCEPTO_GASTO_ECONOMICO_PERMITIDO,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCodigoPermitidoEntity = { entity: value });

    this.translate.get(
      PROYECTO_CONCEPTO_GASTO_ECONOMICO_NO_PERMITIDO,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCodigoNoPermitidoEntity = { entity: value });

    this.translate.get(
      PROYECTO_CONCEPTO_GASTO_ECONOMICO_PERMITIDO,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDeletePermitido = value);

    this.translate.get(
      PROYECTO_CONCEPTO_GASTO_ECONOMICO_NO_PERMITIDO,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDeleteNoPermitido = value);
  }

  openModal(codigoEconomicoListado?: CodigoEconomicoListado, rowIndex?: number): void {

    // Necesario para sincronizar los cambios de orden de registros dependiendo de la ordenación y paginación
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    const row = (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;

    let proyectoConceptoGastoCodigoEcsTabla = this.dataSource.data
      .map(codigoEconomico => codigoEconomico.proyectoCodigoEconomico?.value);

    proyectoConceptoGastoCodigoEcsTabla.splice(row, 1);
    proyectoConceptoGastoCodigoEcsTabla = proyectoConceptoGastoCodigoEcsTabla
      .filter(codigoEconomico => !!codigoEconomico);

    const data: ProyectoConceptoGastoCodigoEcDataModal = {
      proyectoConceptoGastoCodigoEc: codigoEconomicoListado.proyectoCodigoEconomico?.value,
      convocatoriaConceptoGastoCodigoEc: codigoEconomicoListado.convocatoriaCodigoEconomico,
      proyectoConceptoGastoCodigoEcsTabla,
      permitido: this.actionService.permitido,
      editModal: true,
      readonly: this.formPart.readonly,
    };

    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(ProyectoConceptoGastoCodigoEcModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: ProyectoConceptoGastoCodigoEcDataModal) => {
        if (modalData.proyectoConceptoGastoCodigoEc) {
          if (!codigoEconomicoListado.proyectoCodigoEconomico) {
            const codigoEconomico = modalData.proyectoConceptoGastoCodigoEc;
            codigoEconomico.proyectoConceptoGasto = { conceptoGasto: this.actionService.conceptoGasto } as IProyectoConceptoGasto;
            this.formPart.addCodigoEconomico(codigoEconomico, modalData.convocatoriaConceptoGastoCodigoEc?.id);
          } else {
            const codigoEconomico = new StatusWrapper<IProyectoConceptoGastoCodigoEc>(modalData.proyectoConceptoGastoCodigoEc);
            this.formPart.updateCodigoEconomico(codigoEconomico, row);
          }
        }
      }
    );
  }

  openModalCrear(permitido: boolean): void {
    const proyectoConceptoGastoCodigoEcsTabla = this.dataSource.data
      .filter(codigoEconomico => codigoEconomico.proyectoCodigoEconomico)
      .map(codigoEconomico => codigoEconomico.proyectoCodigoEconomico.value);

    const proyectoConceptoGastoCodigoEc: IProyectoConceptoGastoCodigoEc = {
      proyectoConceptoGasto: { id: this.fragment.getKey() as number } as IProyectoConceptoGasto,
      codigoEconomico: null,
      fechaFin: null,
      fechaInicio: null,
      id: null,
      observaciones: undefined,
      convocatoriaConceptoGastoCodigoEcId: null
    };

    const data: ProyectoConceptoGastoCodigoEcDataModal = {
      proyectoConceptoGastoCodigoEc,
      convocatoriaConceptoGastoCodigoEc: undefined,
      proyectoConceptoGastoCodigoEcsTabla,
      permitido,
      editModal: false,
      readonly: this.formPart.readonly
    };

    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(ProyectoConceptoGastoCodigoEcModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: ProyectoConceptoGastoCodigoEcDataModal) => {
        if (modalData) {
          const convConceptoGastoEc = {
            proyectoConceptoGasto: modalData.proyectoConceptoGastoCodigoEc.proyectoConceptoGasto,
            id: modalData.proyectoConceptoGastoCodigoEc.id,
            codigoEconomico: modalData.proyectoConceptoGastoCodigoEc.codigoEconomico,
            fechaInicio: modalData.proyectoConceptoGastoCodigoEc.fechaInicio,
            fechaFin: modalData.proyectoConceptoGastoCodigoEc.fechaFin,
            observaciones: modalData.proyectoConceptoGastoCodigoEc.observaciones,
            convocatoriaConceptoGastoCodigoEcId: modalData.proyectoConceptoGastoCodigoEc.convocatoriaConceptoGastoCodigoEcId
          } as IProyectoConceptoGastoCodigoEc;
          convConceptoGastoEc.proyectoConceptoGasto = { conceptoGasto: this.actionService.conceptoGasto } as IProyectoConceptoGasto;
          this.formPart.addCodigoEconomico(convConceptoGastoEc);
        }
      }
    );
  }

  deleteCodigoEconomico(wrapper: StatusWrapper<IProyectoConceptoGastoCodigoEc>) {
    const messageConfirmation = this.actionService.permitido ? this.textoDeletePermitido : this.textoDeleteNoPermitido;
    this.subscriptions.push(
      this.dialogService.showConfirmation(messageConfirmation).subscribe(
        (aceptado: boolean) => {
          if (aceptado) {
            this.formPart.deleteCodigoEconomico(wrapper);
          }
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
