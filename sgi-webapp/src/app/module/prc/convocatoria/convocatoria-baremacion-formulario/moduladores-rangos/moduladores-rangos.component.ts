import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { Tipo } from '@core/models/prc/modulador';
import { IRango, TipoRango, TipoTemporalidad, TIPO_TEMPORALIDAD_MAP } from '@core/models/prc/rango';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';
import { ConvocatoriaBaremacionActionService } from '../../convocatoria-baremacion.action.service';
import { RangoModalComponent, RangoModalData } from '../../modals/rango-modal/rango-modal.component';
import { ModuladoresFormData, ModuladoresRangosFragment, ModuladorListado } from './moduladores-rangos.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const MSG_ERROR_COSTES_INDIRECTOS_NO_DELETABLE = marker('prc.moduladores-rangos.rango-costes-indirectos.no-deletable');
const MSG_ERROR_CONTRATOS_NO_DELETABLE = marker('prc.moduladores-rangos.rango-contratos.no-deletable');
const MSG_ERROR_LICENCIAS_NO_DELETABLE = marker('prc.moduladores-rangos.rango-licencias.no-deletable');
const RANGOS_COSTES_INDIRECTOS_KEY = marker('prc.moduladores-rangos.rango-costes-indirectos');
const RANGOS_CONTRATOS_KEY = marker('prc.moduladores-rangos.rango-contratos');
const RANGOS_LICENCIAS_KEY = marker('prc.moduladores-rangos.rango-licencias');

@Component({
  selector: 'sgi-moduladores-rangos',
  templateUrl: './moduladores-rangos.component.html',
  styleUrls: ['./moduladores-rangos.component.scss']
})
export class ModuladoresRangosComponent extends FormFragmentComponent<ModuladoresFormData> implements OnInit {

  fxLayoutProperties: FxLayoutProperties;
  formPart: ModuladoresRangosFragment;
  private subscriptions: Subscription[] = [];

  msgParamRango = {};

  msgParamRangosCostesIndirectosEntity = {};
  msgDeleteRangosCostesIndirectos: string;
  titleRangosCostesIndirectos: string;

  msgParamRangosContratosEntity = {};
  msgDeleteRangosContratos: string;
  titleRangosContratos: string;

  msgParamRangosLicenciasEntity = {};
  msgDeleteRangosLicencias: string;
  titleRangosLicencias: string;

  displayedColumnsRangos = ['rango', 'tipo', 'puntos', 'acciones'];
  displayedColumnsModuladorAreas = ['area', 'puntos'];
  displayedColumnsModuladorNumeroAutores = ['area', 'valor1', 'valor2', 'valor3', 'valor4', 'valor5'];
  elementsPage = [5, 10, 25, 100];

  moduladoresAreasDataSource = new MatTableDataSource<ModuladorListado>();
  @ViewChild('sortModuladoresAreas', { static: true }) sortModuladoresAreas: MatSort;

  moduladoresAutoresAreaDataSource = new MatTableDataSource<ModuladorListado>();
  @ViewChild('sortModuladoresAutoresArea', { static: true }) sortModuladoresAutoresArea: MatSort;

  rangosCuantiaCostesIndirectosDataSource = new MatTableDataSource<StatusWrapper<IRango>>();
  @ViewChild('sortRangosCuantiaCostesIndirectos', { static: false }) sortRangosCuantiaCostesIndirectos: MatSort;

  rangosCuantiaContratosDataSource = new MatTableDataSource<StatusWrapper<IRango>>();
  @ViewChild('sortRangosCuantiaContratos', { static: true }) sortRangosCuantiaContratos: MatSort;

  rangosIngresosLicenciasDataSource = new MatTableDataSource<StatusWrapper<IRango>>();
  @ViewChild('sortRangosIngresosLicencias', { static: true }) sortRangosIngresosLicencias: MatSort;

  get TIPO_TEMPORALIDAD_MAP() {
    return TIPO_TEMPORALIDAD_MAP;
  }

  get TipoRango() {
    return TipoRango;
  }

  constructor(
    public readonly actionService: ConvocatoriaBaremacionActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService,
    private readonly snackBarService: SnackBarService
  ) {
    super(actionService.FRAGMENT.MODULADORES_RANGOS, actionService);
    this.formPart = this.fragment as ModuladoresRangosFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.configSortModuladores(Tipo.AREAS);
    this.subscribeToModuladores(Tipo.AREAS);
    this.configSortModuladores(Tipo.NUMERO_AUTORES);
    this.subscribeToModuladores(Tipo.NUMERO_AUTORES);
    this.configSortRangos(TipoRango.CUANTIA_CONTRATOS);
    this.configSortRangos(TipoRango.LICENCIA);
    this.subscribeToRangos(TipoRango.CUANTIA_CONTRATOS);
    this.subscribeToRangos(TipoRango.CUANTIA_COSTES_INDIRECTOS);
    this.subscribeToRangos(TipoRango.LICENCIA);
    this.setupI18N();

    this.subscriptions.push(
      this.formPart.hasCostesIndirectosTipoRango$.subscribe(value => {
        if (value) {
          setTimeout(() => {
            this.configSortRangos(TipoRango.CUANTIA_COSTES_INDIRECTOS);
          }, 0);
        }
      })
    );

  }

  openModalRango(tipoRango: TipoRango, wrapper?: StatusWrapper<IRango>): void {
    const rangosDataSource = this.getRangosDataSource(tipoRango);
    // Necesario para sincronizar los cambios de orden de registros dependiendo de la ordenación
    rangosDataSource.sortData(
      rangosDataSource.filteredData,
      rangosDataSource.sort
    );

    const data: RangoModalData = {
      title: this.getTitleModal(tipoRango),
      entidad: wrapper?.value ?? {} as IRango,
      isEdit: Boolean(wrapper),
      readonly: !this.formPart.isEditPerm,
      hasRangoInicial: rangosDataSource.data.some(rango =>
        rango.value.tipoTemporalidad === TipoTemporalidad.INICIAL),
      hasRangoFinal: rangosDataSource.data.some(rango =>
        rango.value.tipoTemporalidad === TipoTemporalidad.FINAL),
      rangoMaxHasta: Math.max(...rangosDataSource.data.map(rango => rango.value.hasta))
    };

    const config: MatDialogConfig = {
      data
    };

    const dialogRef = this.matDialog.open(RangoModalComponent, config);
    dialogRef.afterClosed().subscribe((modalData: RangoModalData) => {
      if (modalData) {
        if (!wrapper) {
          this.formPart.addRango(modalData.entidad, tipoRango);
        } else {
          this.formPart.updateRango(wrapper, tipoRango);
        }
      }
    });
  }

  deleteRango(tipo: TipoRango, wrapper: StatusWrapper<IRango>): void {
    const maxDesde = Math.max(...this.getRangosDataSource(tipo).data.map(rango => rango.value.desde ?? 0));
    if ((wrapper.value.desde ?? 0) < maxDesde) {
      this.snackBarService.showError(this.getMsgNoDeletable(tipo));
    } else {
      this.subscriptions.push(
        this.dialogService.showConfirmation(this.getMsgDelete(tipo)).subscribe(
          (aceptado) => {
            if (aceptado) {
              this.formPart.deleteRango(wrapper, tipo);
            }
          }
        )
      );
    }
  }

  hasRangosFinal(tipo: TipoRango): boolean {
    return this.getRangosDataSource(tipo).data.some(rango =>
      rango.value.tipoTemporalidad === TipoTemporalidad.FINAL);
  }

  private configSortModuladores(tipo: Tipo): void {
    const moduladoresDataSource = this.getModuladoresDataSource(tipo);
    const moduladoresSort = this.getModuladoresSort(tipo);

    moduladoresDataSource.sortingDataAccessor =
      (wrapper: ModuladorListado, property: string) => {
        switch (property) {
          case 'area':
            return wrapper.area.nombre;
          default:
            return wrapper[property];
        }
      };
    moduladoresDataSource.sort = moduladoresSort;
  }

  private subscribeToModuladores(tipo: Tipo): void {
    const moduladoresDataSource = this.getModuladoresDataSource(tipo);
    this.subscriptions.push(
      this.formPart.getModuladores$(tipo).subscribe(elements => {
        moduladoresDataSource.data = elements;
      })
    );
  }

  private getModuladoresDataSource(tipo: Tipo): MatTableDataSource<ModuladorListado> {
    let moduladoresDataSource: MatTableDataSource<ModuladorListado>;
    switch (tipo) {
      case Tipo.AREAS:
        moduladoresDataSource = this.moduladoresAreasDataSource;
        break;
      case Tipo.NUMERO_AUTORES:
        moduladoresDataSource = this.moduladoresAutoresAreaDataSource;
        break;
    }

    return moduladoresDataSource;
  }

  private getModuladoresSort(tipo: Tipo): MatSort {
    let moduladoresSort: MatSort;
    switch (tipo) {
      case Tipo.AREAS:
        moduladoresSort = this.sortModuladoresAreas;
        break;
      case Tipo.NUMERO_AUTORES:
        moduladoresSort = this.sortModuladoresAutoresArea;
        break;
    }

    return moduladoresSort;
  }

  private configSortRangos(tipo: TipoRango): void {
    const rangosDataSource = this.getRangosDataSource(tipo);
    const rangosSort = this.getRangosSort(tipo);

    rangosDataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IRango>, property: string) => {
        switch (property) {
          case 'rango':
            return wrapper.value.desde ?? 0;
          default:
            return wrapper.value[property];
        }
      };
    rangosDataSource.sort = rangosSort;
  }

  private subscribeToRangos(tipo: TipoRango): void {
    const rangosDataSource = this.getRangosDataSource(tipo);
    this.subscriptions.push(
      this.formPart.getRangos$(tipo).subscribe(elements => {
        rangosDataSource.data = elements;
      })
    );
  }

  private getRangosDataSource(tipo: TipoRango): MatTableDataSource<StatusWrapper<IRango>> {
    let rangosDataSource: MatTableDataSource<StatusWrapper<IRango>>;
    switch (tipo) {
      case TipoRango.CUANTIA_CONTRATOS:
        rangosDataSource = this.rangosCuantiaContratosDataSource;
        break;
      case TipoRango.CUANTIA_COSTES_INDIRECTOS:
        rangosDataSource = this.rangosCuantiaCostesIndirectosDataSource;
        break;
      case TipoRango.LICENCIA:
        rangosDataSource = this.rangosIngresosLicenciasDataSource;
        break;
    }

    return rangosDataSource;
  }

  private getRangosSort(tipo: TipoRango): MatSort {
    let rangosSort: MatSort;
    switch (tipo) {
      case TipoRango.CUANTIA_CONTRATOS:
        rangosSort = this.sortRangosCuantiaContratos;
        break;
      case TipoRango.CUANTIA_COSTES_INDIRECTOS:
        rangosSort = this.sortRangosCuantiaCostesIndirectos;
        break;
      case TipoRango.LICENCIA:
        rangosSort = this.sortRangosIngresosLicencias;
        break;
    }

    return rangosSort;
  }

  private getTitleModal(tipo: TipoRango): string {
    let title: string;
    switch (tipo) {
      case TipoRango.CUANTIA_CONTRATOS:
        title = this.titleRangosContratos;
        break;
      case TipoRango.CUANTIA_COSTES_INDIRECTOS:
        title = this.titleRangosCostesIndirectos;
        break;
      case TipoRango.LICENCIA:
        title = this.titleRangosLicencias;
        break;
    }

    return title;
  }

  private getMsgDelete(tipo: TipoRango): string {
    let title: string;
    switch (tipo) {
      case TipoRango.CUANTIA_CONTRATOS:
        title = this.msgDeleteRangosContratos;
        break;
      case TipoRango.CUANTIA_COSTES_INDIRECTOS:
        title = this.msgDeleteRangosCostesIndirectos;
        break;
      case TipoRango.LICENCIA:
        title = this.msgDeleteRangosLicencias;
        break;
    }

    return title;
  }

  private getMsgNoDeletable(tipo: TipoRango): string {
    let msg: string;
    switch (tipo) {
      case TipoRango.CUANTIA_CONTRATOS:
        msg = MSG_ERROR_CONTRATOS_NO_DELETABLE;
        break;
      case TipoRango.CUANTIA_COSTES_INDIRECTOS:
        msg = MSG_ERROR_COSTES_INDIRECTOS_NO_DELETABLE;
        break;
      case TipoRango.LICENCIA:
        msg = MSG_ERROR_LICENCIAS_NO_DELETABLE;
        break;
    }

    return msg;
  }

  private setupI18N(): void {
    this.translate.get(
      RANGOS_CONTRATOS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamRangosContratosEntity = {
      entity: value,
      ...MSG_PARAMS.GENDER.MALE,
      ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      RANGOS_CONTRATOS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.titleRangosContratos = value);

    this.translate.get(
      RANGOS_CONTRATOS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.msgDeleteRangosContratos = value);

    this.translate.get(
      RANGOS_COSTES_INDIRECTOS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamRangosCostesIndirectosEntity = {
      entity: value,
      ...MSG_PARAMS.GENDER.MALE,
      ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      RANGOS_COSTES_INDIRECTOS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.titleRangosCostesIndirectos = value);

    this.translate.get(
      RANGOS_COSTES_INDIRECTOS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.msgDeleteRangosCostesIndirectos = value);

    this.translate.get(
      RANGOS_LICENCIAS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      tap(value => this.msgParamRangosLicenciasEntity = {
        entity: value,
        ...MSG_PARAMS.GENDER.MALE,
        ...MSG_PARAMS.CARDINALIRY.SINGULAR
      }),
      tap(value => this.titleRangosLicencias = value),
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      }),
      tap(value => this.msgDeleteRangosLicencias = value)
    ).subscribe();
  }

}
