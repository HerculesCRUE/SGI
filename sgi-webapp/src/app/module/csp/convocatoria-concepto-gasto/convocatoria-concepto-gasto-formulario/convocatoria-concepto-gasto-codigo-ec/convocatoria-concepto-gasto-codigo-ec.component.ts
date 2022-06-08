import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ConvocatoriaConceptoGastoActionService } from '../../convocatoria-concepto-gasto.action.service';
import { ConvocatoriaConceptoGastoCodigoEcModalComponent, IConvocatoriaConceptoGastoCodigoEcModalComponent } from '../../modals/convocatoria-concepto-gasto-codigo-ec-modal/convocatoria-concepto-gasto-codigo-ec-modal.component';
import { ConvocatoriaConceptoGastoCodigoEc, ConvocatoriaConceptoGastoCodigoEcFragment } from './convocatoria-concepto-gasto-codigo-ec.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const CONVOCATORIA_CONCEPTO_GASTO_ECONOMICO_PERMITIDO = marker('csp.convocatoria-concepto-gasto.codigo-economico.permitido');
const CONVOCATORIA_CONCEPTO_GASTO_ECONOMICO_NO_PERMITIDO = marker('csp.convocatoria-concepto-gasto.codigo-economico.no-permitido');

@Component({
  selector: 'sgi-convocatoria-concepto-gasto-codigo-ec',
  templateUrl: './convocatoria-concepto-gasto-codigo-ec.component.html',
  styleUrls: ['./convocatoria-concepto-gasto-codigo-ec.component.scss']
})
export class ConvocatoriaConceptoGastoCodigoEcComponent extends FragmentComponent implements OnInit, OnDestroy {
  formGroup: FormGroup;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  formPart: ConvocatoriaConceptoGastoCodigoEcFragment;
  private subscriptions: Subscription[] = [];

  elementosPagina: number[] = [5, 10, 25, 100];
  displayedColumns: string[] = ['conceptoGasto.nombre', 'codigoEconomicoRef', 'fechaInicio', 'fechaFin', 'observaciones', 'acciones'];

  dataSource: MatTableDataSource<StatusWrapper<ConvocatoriaConceptoGastoCodigoEc>> =
    new MatTableDataSource<StatusWrapper<ConvocatoriaConceptoGastoCodigoEc>>();
  @ViewChild('paginator', { static: true }) paginator: MatPaginator;
  @ViewChild('sort', { static: true }) sort: MatSort;

  msgParamCodigoPermitidoEntity = {};
  msgParamCodigoNoPermitidoEntity = {};
  textoDeletePermitido: string;
  textoDeleteNoPermitido: string;

  constructor(
    protected readonly logger: NGXLogger,
    public readonly actionService: ConvocatoriaConceptoGastoActionService,
    private matDialog: MatDialog,
    private readonly dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.CODIGOS_ECONOMICOS, actionService);
    this.formPart = this.fragment as ConvocatoriaConceptoGastoCodigoEcFragment;

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
    this.dataSource = new MatTableDataSource<StatusWrapper<ConvocatoriaConceptoGastoCodigoEc>>();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart?.convocatoriaConceptoGastoCodigoEcs$.subscribe(elements => {
      this.dataSource.data = elements;
    }));

    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<ConvocatoriaConceptoGastoCodigoEc>, property: string) => {
        switch (property) {
          default:
            return wrapper.value[property];
        }
      };
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_CONCEPTO_GASTO_ECONOMICO_PERMITIDO,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCodigoPermitidoEntity = { entity: value });

    this.translate.get(
      CONVOCATORIA_CONCEPTO_GASTO_ECONOMICO_NO_PERMITIDO,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCodigoNoPermitidoEntity = { entity: value });

    this.translate.get(
      CONVOCATORIA_CONCEPTO_GASTO_ECONOMICO_PERMITIDO,
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
      CONVOCATORIA_CONCEPTO_GASTO_ECONOMICO_NO_PERMITIDO,
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

  openModal(wrapper?: StatusWrapper<ConvocatoriaConceptoGastoCodigoEc>, rowIndex?: number): void {
    // Necesario para sincronizar los cambios de orden de registros dependiendo de la ordenación y paginación
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    const row = (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;

    const convocatoriaConceptoGastoCodigoEcsTabla = this.dataSource.data.map(element => element.value);

    convocatoriaConceptoGastoCodigoEcsTabla.splice(row, 1);

    const data: IConvocatoriaConceptoGastoCodigoEcModalComponent = {
      convocatoriaConceptoGastoCodigoEc: wrapper.value,
      convocatoriaConceptoGastoCodigoEcsTabla,
      permitido: this.actionService.permitido,
      editModal: true,
      readonly: this.formPart.readonly,
    };

    const config = {
      data
    };
    const dialogRef = this.matDialog.open(ConvocatoriaConceptoGastoCodigoEcModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: IConvocatoriaConceptoGastoCodigoEcModalComponent) => {
        if (modalData.convocatoriaConceptoGastoCodigoEc) {
          if (wrapper) {
            if (!wrapper.created) {
              wrapper.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            const convConceptoGastoEc = {
              convocatoriaConceptoGasto: {
                id: modalData.convocatoriaConceptoGastoCodigoEc.convocatoriaConceptoGastoId
              },
              id: modalData.convocatoriaConceptoGastoCodigoEc.id,
              codigoEconomico: modalData.convocatoriaConceptoGastoCodigoEc.codigoEconomico,
              fechaInicio: modalData.convocatoriaConceptoGastoCodigoEc.fechaInicio,
              fechaFin: modalData.convocatoriaConceptoGastoCodigoEc.fechaFin,
              observaciones: modalData.convocatoriaConceptoGastoCodigoEc.observaciones,
              convocatoriaConceptoGastoId: modalData.convocatoriaConceptoGastoCodigoEc.convocatoriaConceptoGastoId
            } as ConvocatoriaConceptoGastoCodigoEc;
            convConceptoGastoEc.convocatoriaConceptoGasto = {
              conceptoGasto: this.actionService.conceptoGasto
            } as IConvocatoriaConceptoGasto;
            this.formPart.addConvocatoriaConceptoGastoCodigoEc(convConceptoGastoEc);
          }
        }
      }
    );
  }

  openModalCrear(permitido: boolean): void {
    const convocatoriaConceptoGastoCodigoEcsTabla = this.dataSource.data.map(wrapper => wrapper.value);

    const convocatoriaConceptoGastoCodigoEc: IConvocatoriaConceptoGastoCodigoEc = {
      convocatoriaConceptoGastoId: this.fragment.getKey() as number,
      codigoEconomico: null,
      fechaFin: null,
      fechaInicio: null,
      id: null,
      observaciones: undefined
    };

    const data: IConvocatoriaConceptoGastoCodigoEcModalComponent = {
      convocatoriaConceptoGastoCodigoEc,
      convocatoriaConceptoGastoCodigoEcsTabla,
      permitido,
      editModal: false,
      readonly: this.formPart.readonly
    };

    const config = {
      data
    };
    const dialogRef = this.matDialog.open(ConvocatoriaConceptoGastoCodigoEcModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: IConvocatoriaConceptoGastoCodigoEcModalComponent) => {
        if (modalData) {
          const convConceptoGastoEc = {
            convocatoriaConceptoGasto: {
              id: modalData.convocatoriaConceptoGastoCodigoEc.convocatoriaConceptoGastoId
            },
            id: modalData.convocatoriaConceptoGastoCodigoEc.id,
            codigoEconomico: modalData.convocatoriaConceptoGastoCodigoEc.codigoEconomico,
            fechaInicio: modalData.convocatoriaConceptoGastoCodigoEc.fechaInicio,
            fechaFin: modalData.convocatoriaConceptoGastoCodigoEc.fechaFin,
            observaciones: modalData.convocatoriaConceptoGastoCodigoEc.observaciones,
            convocatoriaConceptoGastoId: modalData.convocatoriaConceptoGastoCodigoEc.convocatoriaConceptoGastoId
          } as ConvocatoriaConceptoGastoCodigoEc;
          convConceptoGastoEc.convocatoriaConceptoGasto = { conceptoGasto: this.actionService.conceptoGasto } as IConvocatoriaConceptoGasto;
          this.formPart.addConvocatoriaConceptoGastoCodigoEc(convConceptoGastoEc);
        }
      }
    );
  }

  deleteConvocatoriaConceptoGastoCodigoEc(wrapper: StatusWrapper<ConvocatoriaConceptoGastoCodigoEc>) {
    const messageConfirmation = this.actionService.permitido ? this.textoDeletePermitido : this.textoDeleteNoPermitido;
    this.subscriptions.push(
      this.dialogService.showConfirmation(messageConfirmation).subscribe(
        (aceptado: boolean) => {
          if (aceptado) {
            this.formPart.deleteConvocatoriaConceptoGastoCodigoEc(wrapper);
          }
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
