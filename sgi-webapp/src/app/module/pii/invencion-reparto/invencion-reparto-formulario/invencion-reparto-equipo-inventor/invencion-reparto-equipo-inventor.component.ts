import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IRepartoGasto } from '@core/models/pii/reparto-gasto';
import { IRepartoIngreso } from '@core/models/pii/reparto-ingreso';
import { ITramoReparto } from '@core/models/pii/tramo-reparto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NumberValidator } from '@core/validators/number-validator';
import { Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter } from 'rxjs/operators';
import { TramoRepartoTramoPipe } from '../../../tramo-reparto/pipes/tramo-reparto-tramo.pipe';
import { InvencionRepartoActionService } from '../../invencion-reparto.action.service';
import { InvencionRepartoEquipoInventorFragment, IRepartoEquipoInventorTableData } from './invencion-reparto-equipo-inventor.fragment';
import { TranslateService } from '@ngx-translate/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { RepartoEquipoModalComponent } from '../../modals/reparto-equipo-modal/reparto-equipo-modal.component';
import { NumberUtils } from '@core/utils/number.utils';
import { ESTADO_MAP } from '@core/models/pii/reparto';
import { getPersonaEmailListConcatenated } from 'src/app/esb/sgp/shared/pipes/persona-email.pipe';

const UNIVERSIDAD_PERCENTAGE = marker('pii.reparto.reparto-equipo.porcentaje-universidad');
const INVENTORES_PERCENTAGE = marker('pii.reparto.reparto-equipo.porcentaje-inventores');
const REPARTO_IMPORTE_REPARTO_EQUIPO_INVENTOR_KEY = marker('pii.reparto.reparto-equipo.importe-reparto-equipo-inventor');
const REPARTO_ESTADO_KEY = marker('pii.invencion-reparto.estado');

@Component({
  selector: 'sgi-invencion-reparto-equipo-inventor',
  templateUrl: './invencion-reparto-equipo-inventor.component.html',
  styleUrls: ['./invencion-reparto-equipo-inventor.component.scss'],
  providers: [
    TramoRepartoTramoPipe
  ]
})
export class InvencionRepartoEquipoInventorComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  formPart: InvencionRepartoEquipoInventorFragment;
  @ViewChild('sortGastos', { static: true }) sortGastos: MatSort;
  gastosDataSource = new MatTableDataSource<IRepartoGasto>();
  @ViewChild('sortIngresos', { static: true }) sortIngresos: MatSort;
  ingresosDataSource = new MatTableDataSource<IRepartoIngreso>();
  sortEquipoInventor: MatSort;
  equipoInventorDataSource = new MatTableDataSource<StatusWrapper<IRepartoEquipoInventorTableData>>();
  @ViewChild('sortEquipoInventor', { static: false }) set initSortSortEquipoInventor(sortEquipoInventor: MatSort) {
    this.sortEquipoInventor = sortEquipoInventor;
    this.equipoInventorDataSource.sortingDataAccessor = (wrapper: StatusWrapper<IRepartoEquipoInventorTableData>, property: string) => {
      switch (property) {
        case 'nombre':
          return wrapper.value?.repartoEquipoInventor?.invencionInventor?.inventor?.nombre;
        case 'apellidos':
          return wrapper.value?.repartoEquipoInventor?.invencionInventor?.inventor?.apellidos;
        case 'persona':
          return getPersonaEmailListConcatenated(wrapper.value?.repartoEquipoInventor?.invencionInventor?.inventor);
        case 'entidad':
          return wrapper.value?.repartoEquipoInventor?.invencionInventor?.inventor?.entidad?.nombre;
        case 'participacion':
          return wrapper.value?.repartoEquipoInventor?.invencionInventor?.participacion;
        case 'porcentajeRepartoInventor':
          return wrapper.value?.porcentajeRepartoInventor;
        case 'importeNomina':
          return wrapper.value?.repartoEquipoInventor?.importeNomina;
        case 'importeProyecto':
          return wrapper.value?.repartoEquipoInventor?.importeProyecto;
        case 'importeOtros':
          return wrapper.value?.repartoEquipoInventor?.importeOtros;
        case 'importeTotal':
          return wrapper.value?.importeTotalInventor;
        default:
          return wrapper[property];
      }
    };
    this.equipoInventorDataSource.sort = this.sortEquipoInventor;
  }

  totalGastosCompensar = 0;
  totalIngresosRepartir = 0;
  totalRepartir = 0;
  hasTramoReparto = false;
  tramoRepartoFormGroup: FormGroup;
  repartoFormGroup: FormGroup;

  msgParamImporteRepartoEquipoInventorEntity = {};
  msgParamEstadoEntity = {};

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get REPARTO_ESTADO_MAP() {
    return ESTADO_MAP;
  }

  constructor(
    public actionService: InvencionRepartoActionService,
    private readonly tramoRepartoPipe: TramoRepartoTramoPipe,
    private readonly translate: TranslateService,
    private readonly matDialog: MatDialog,
  ) {
    super(actionService.FRAGMENT.REPARTO_EQUIPO_INVENTOR, actionService);
    this.formPart = this.fragment as InvencionRepartoEquipoInventorFragment;
  }

  ngOnInit(): void {
    this.initFormPartSubscriptions();
    super.ngOnInit();
    this.initializeRepartoFormGroup();
    this.initFlexProperties();
    this.initializeGastosDataSource();
    this.initializeIngresosDataSource();
    this.setupI18N();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  private initializeRepartoFormGroup(): void {
    this.repartoFormGroup = new FormGroup({
      estado: new FormControl(
        { value: this.formPart.repartoEstado, disabled: this.formPart.isRepartoEjecutado }, [Validators.required]
      )
    });

    if (!this.formPart.isRepartoEjecutado) {
      this.subscriptions.push(
        this.repartoFormGroup.valueChanges
          .subscribe(estado => this.formPart.onRepartoEstadoChanges(estado))
      );
    }
  }

  private initializeGastosDataSource(): void {
    this.gastosDataSource.sortingDataAccessor = (repartoGasto: IRepartoGasto, property: string) => {
      switch (property) {
        case 'solicitudProteccion':
          return repartoGasto.invencionGasto?.solicitudProteccion?.titulo;
        case 'importePendienteDeducir':
          return repartoGasto.invencionGasto?.importePendienteDeducir;
        case 'importeADeducir':
          return repartoGasto.importeADeducir;
        default:
          const gastoColumn = this.formPart.gastosColumns.find(column => column.id === property);
          return gastoColumn ? repartoGasto.invencionGasto.gasto.columnas[gastoColumn.id] : repartoGasto[property];
      }
    };
    this.gastosDataSource.sort = this.sortGastos;
    this.subscriptions.push(this.formPart.getRepartoGastos$()
      .pipe(
        filter(elements => !!elements)
      )
      .subscribe(elements => this.gastosDataSource.data = elements));
  }

  private initializeIngresosDataSource(): void {
    this.ingresosDataSource.sortingDataAccessor = (repartoIngreso: IRepartoIngreso, property: string) => {
      switch (property) {
        case 'importePendienteRepartir':
          return repartoIngreso.invencionIngreso?.importePendienteRepartir;
        case 'importeARepartir':
          return repartoIngreso.importeARepartir;
        default:
          const gastoColumn = this.formPart.gastosColumns.find(column => column.id === property);
          return gastoColumn ? repartoIngreso.invencionIngreso.ingreso.columnas[gastoColumn.id] : repartoIngreso[property];
      }
    };
    this.ingresosDataSource.sort = this.sortIngresos;
    this.subscriptions.push(this.formPart.getRepartoIngresos$()
      .pipe(
        filter(elements => !!elements)
      )
      .subscribe(elements => this.ingresosDataSource.data = elements));
  }

  private initFormPartSubscriptions(): void {
    this.subscriptions.push(
      this.formPart.getTotalRepartir$().subscribe(({ importeGastos, importeIngresos }) => {
        this.totalGastosCompensar = importeGastos;
        this.totalIngresosRepartir = importeIngresos;
        this.totalRepartir = importeIngresos - importeGastos;
        if (this.totalRepartir > 0) {
          this.initializeEquipoInventorTable();
          this.initTramoRepartoFormGroup();
        }
      })
    );
    this.subscriptions.push(
      this.formPart.getTramoReparto$().subscribe((tramoReparto) => {
        this.hasTramoReparto = !!tramoReparto;
        const importeRepartoEquipoInventor = this.calculateImporteRepartoEquipoInventor(tramoReparto);
        this.tramoRepartoFormGroup.controls.importeRepartoEquipoInventor.markAsTouched();
        this.tramoRepartoFormGroup.patchValue({
          resultadoRepartir: NumberUtils.roundNumber(this.totalRepartir),
          rango: this.tramoRepartoPipe.transform(tramoReparto),
          porcentajeUniversidad: this.translate.instant(UNIVERSIDAD_PERCENTAGE, { percentage: tramoReparto?.porcentajeUniversidad }),
          porcentajeInventores: this.translate.instant(INVENTORES_PERCENTAGE, { percentage: tramoReparto?.porcentajeInventores }),
          importeRepartoUniversidad: NumberUtils.roundNumber(this.totalRepartir - importeRepartoEquipoInventor),
          importeRepartoEquipoInventor: NumberUtils.roundNumber(importeRepartoEquipoInventor)
        });
        if (this.formPart.isRepartoEjecutado) {
          this.tramoRepartoFormGroup.disable();
        }
      })
    );
    this.subscriptions.push(
      this.formPart.getReadonly$().pipe(
        filter(readonly => readonly)
      ).subscribe(() => {
        if (this.repartoFormGroup) {
          this.repartoFormGroup.disable();
        }
        if (this.tramoRepartoFormGroup) {
          this.tramoRepartoFormGroup.disable();
        }
      })
    );
  }

  private initializeEquipoInventorTable(): void {
    this.subscriptions.push(this.formPart.getRepartoEquipoInventorTableData$()
      .subscribe(elements => this.equipoInventorDataSource.data = elements));
  }

  private initTramoRepartoFormGroup(): void {
    this.tramoRepartoFormGroup = new FormGroup({
      resultadoRepartir: new FormControl({
        value: undefined,
        disabled: true
      }),
      rango: new FormControl({
        value: undefined,
        disabled: true
      }),
      porcentajeUniversidad: new FormControl({
        value: undefined,
        disabled: true
      }),
      porcentajeInventores: new FormControl({
        value: undefined,
        disabled: true
      }),
      importeRepartoUniversidad: new FormControl({
        value: undefined,
        disabled: true
      }),
      importeRepartoEquipoInventor: new FormControl(
        undefined,
        [
          Validators.required, Validators.min(0),
          Validators.max(NumberUtils.roundNumber(this.totalRepartir)),
          NumberValidator.maxDecimalDigits(2)
        ]
      )
    });

    this.subscriptions.push(
      this.tramoRepartoFormGroup.controls.importeRepartoEquipoInventor.valueChanges.pipe(
        distinctUntilChanged(),
        debounceTime(500)
      ).subscribe(importeRepartoEquipoInventor => {
        if (this.tramoRepartoFormGroup.controls.importeRepartoEquipoInventor.status === 'VALID' ||
          this.tramoRepartoFormGroup.controls.importeRepartoEquipoInventor.status === 'DISABLED') {
          this.formPart.onImporteRepartoEquipoInventorChanges(
            importeRepartoEquipoInventor,
            this.tramoRepartoFormGroup.controls.importeRepartoEquipoInventor.dirty
          );
          this.tramoRepartoFormGroup.controls.importeRepartoUniversidad.setValue(
            NumberUtils.roundNumber(this.totalRepartir - importeRepartoEquipoInventor)
          );
        } else {
          this.formPart.onImporteRepartoEquipoInventorChanges(importeRepartoEquipoInventor, false, true);
        }
      })
    );
  }

  private calculateImporteRepartoEquipoInventor(tramoReparto: ITramoReparto): number {
    if (typeof this.formPart.importeEquipoInventor === 'number' && this.formPart.importeEquipoInventor >= 0) {
      return this.formPart.importeEquipoInventor;
    }
    if (tramoReparto) {
      return (this.totalRepartir * tramoReparto.porcentajeInventores) / 100;
    }

    return 0;
  }

  getTotalGastosCaptionColspan(): number {
    return this.formPart.displayGastosColumns.length - 1;
  }

  getTotalIngresosCaptionColspan(): number {
    return this.formPart.displayIngresosColumns.length - 1;
  }

  getTotalRepartoEquipoInventorCaptionColspan(): number {
    return this.formPart.displayEquipoInventorColumns.length - 1;
  }

  openModalRepartoEquipoInventor(wrapper: StatusWrapper<IRepartoEquipoInventorTableData>): void {
    const config: MatDialogConfig<IRepartoEquipoInventorTableData> = {
      panelClass: 'sgi-dialog-container',
      data: wrapper.value
    };
    const dialogRef = this.matDialog.open(RepartoEquipoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: IRepartoGasto) => {
        if (result) {
          this.formPart.modifyRepartoEquipo(wrapper);
        }
      });
  }

  getTotalRepartirRounded(): number {
    return NumberUtils.roundNumber(this.totalRepartir);
  }

  private initFlexProperties() {
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.xs = 'column';
  }

  private setupI18N(): void {
    this.translate.get(
      REPARTO_IMPORTE_REPARTO_EQUIPO_INVENTOR_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamImporteRepartoEquipoInventorEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );
    this.translate.get(
      REPARTO_ESTADO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamEstadoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );
  }
}
