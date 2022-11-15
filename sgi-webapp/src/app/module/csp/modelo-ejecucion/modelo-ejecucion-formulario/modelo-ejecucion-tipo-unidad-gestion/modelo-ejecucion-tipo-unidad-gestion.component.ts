import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IModeloUnidad } from '@core/models/csp/modelo-unidad';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { IModeloEjecucionTipoUnidadModal, ModeloEjecucionTipoUnidadGestionModalComponent } from '../../modals/modelo-ejecucion-tipo-unidad-gestion-modal/modelo-ejecucion-tipo-unidad-gestion-modal.component';
import { ModeloEjecucionActionService } from '../../modelo-ejecucion.action.service';
import { ModeloEjecucionTipoUnidadGestionFragment } from './modelo-ejecucion-tipo-unidad-gestion.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const MODELO_EJECUCION_TIPO_UNIDAD_GESTION_KEY = marker('csp.unidad-gestion');

@Component({
  selector: 'sgi-modelo-ejecucion-tipo-unidad-gestion',
  templateUrl: './modelo-ejecucion-tipo-unidad-gestion.component.html',
  styleUrls: ['./modelo-ejecucion-tipo-unidad-gestion.component.scss']
})
export class ModeloEjecucionTipoUnidadGestionComponent extends FragmentComponent implements OnInit, OnDestroy {
  private formPart: ModeloEjecucionTipoUnidadGestionFragment;
  private subscriptions = [] as Subscription[];

  columns = ['acronimo', 'nombre', 'acciones'];

  modelosTipoUnidades = new MatTableDataSource<StatusWrapper<IModeloUnidad>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  msgParamEntity = {};
  textoDelete: string;

  constructor(
    private readonly dialogService: DialogService,
    private matDialog: MatDialog,
    actionService: ModeloEjecucionActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.UNIDAD_GESTION, actionService);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.formPart = this.fragment as ModeloEjecucionTipoUnidadGestionFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    const subscription = this.formPart.modeloUnidad$.subscribe(
      (wrappers: StatusWrapper<IModeloUnidad>[]) => {
        this.modelosTipoUnidades.data = wrappers;
      }
    );
    this.modelosTipoUnidades.paginator = this.paginator;
    this.modelosTipoUnidades.sortingDataAccessor =
      (wrapper: StatusWrapper<IModeloUnidad>, property: string) => {
        switch (property) {
          case 'acronimo':
            return wrapper.value.unidadGestion.acronimo;
          case 'nombre':
            return wrapper.value.unidadGestion.nombre;
          default:
            return wrapper[property];
        }
      };
    this.modelosTipoUnidades.sort = this.sort;
    this.subscriptions.push(subscription);
  }

  private setupI18N(): void {
    this.translate.get(
      MODELO_EJECUCION_TIPO_UNIDAD_GESTION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      MODELO_EJECUCION_TIPO_UNIDAD_GESTION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);

  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  openModal(): void {
    const modeloTipoUnidad: IModeloUnidad = {
      id: null,
      unidadGestion: null,
      modeloEjecucion: null,
      activo: true
    };


    const tipoUnidades = this.modelosTipoUnidades.data.map(wrapper => wrapper.value.unidadGestion);

    const dataModal: IModeloEjecucionTipoUnidadModal = {
      modeloTipoUnidad,
      tipoUnidades
    };

    const config = {
      data: dataModal
    };
    const dialogRef = this.matDialog.open(ModeloEjecucionTipoUnidadGestionModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: IModeloUnidad) => {
        if (result) {
          this.formPart.addmodeloTipoUnidad(result);
        }
      }
    );
  }

  deleteModelotipoUnidadGestion(wrapper?: StatusWrapper<IModeloUnidad>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado: boolean) => {
          if (aceptado) {
            this.formPart.deletemodeloTipoUnidad(wrapper);
          }
        }
      )
    );
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }
}

