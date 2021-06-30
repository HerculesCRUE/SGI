import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IModeloTipoFinalidad } from '@core/models/csp/modelo-tipo-finalidad';
import { ITipoFinalidad } from '@core/models/csp/tipos-configuracion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ModeloEjecucionTipoFinalidadModalComponent, ModeloEjecucionTipoFinalidadModalData } from '../../modals/modelo-ejecucion-tipo-finalidad-modal/modelo-ejecucion-tipo-finalidad-modal.component';
import { ModeloEjecucionActionService } from '../../modelo-ejecucion.action.service';
import { ModeloEjecucionTipoFinalidadFragment } from './modelo-ejecucion-tipo-finalidad.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const MODELO_EJECUCION_TIPO_FINALIDAD_KEY = marker('csp.tipo-finalidad');

@Component({
  selector: 'sgi-modelo-ejecucion-tipo-finalidad',
  templateUrl: './modelo-ejecucion-tipo-finalidad.component.html',
  styleUrls: ['./modelo-ejecucion-tipo-finalidad.component.scss']
})
export class ModeloEjecucionTipoFinalidadComponent extends FragmentComponent implements OnInit, OnDestroy {
  private formPart: ModeloEjecucionTipoFinalidadFragment;
  private subscriptions = [] as Subscription[];

  columns = ['nombre', 'descripcion', 'acciones'];
  numPage = [5, 10, 25, 100];
  totalElements = 0;

  modelosTipoFinalidades = new MatTableDataSource<StatusWrapper<IModeloTipoFinalidad>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  msgParamEntity = {};
  textoDelete: string;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  constructor(
    private readonly dialogService: DialogService,
    private matDialog: MatDialog,
    actionService: ModeloEjecucionActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.TIPO_FINALIDADES, actionService);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.formPart = this.fragment as ModeloEjecucionTipoFinalidadFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    const subscription = this.formPart.modeloTipoFinalidad$.subscribe(
      (wrappers: StatusWrapper<IModeloTipoFinalidad>[]) => {
        this.modelosTipoFinalidades.data = wrappers;
      }
    );
    this.modelosTipoFinalidades.paginator = this.paginator;
    this.modelosTipoFinalidades.sortingDataAccessor =
      (wrapper: StatusWrapper<IModeloTipoFinalidad>, property: string) => {
        switch (property) {
          case 'nombre':
            return wrapper.value.tipoFinalidad.nombre;
          case 'descripcion':
            return wrapper.value.tipoFinalidad.descripcion;
          case 'activo':
            return wrapper.value.tipoFinalidad.activo;
          default:
            return wrapper[property];
        }
      };
    this.modelosTipoFinalidades.sort = this.sort;
    this.subscriptions.push(subscription);
  }

  private setupI18N(): void {
    this.translate.get(
      MODELO_EJECUCION_TIPO_FINALIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      MODELO_EJECUCION_TIPO_FINALIDAD_KEY,
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
    const modeloTipoFinalidad = { activo: true } as IModeloTipoFinalidad;
    const tipoFinalidades: ITipoFinalidad[] = [];
    this.modelosTipoFinalidades.data.forEach(
      (wrapper: StatusWrapper<IModeloTipoFinalidad>) => {
        tipoFinalidades.push(wrapper.value.tipoFinalidad);
      }
    );
    const config = {
      panelClass: 'sgi-dialog-container',
      data: { modeloTipoFinalidad, tipoFinalidades } as ModeloEjecucionTipoFinalidadModalData
    };
    const dialogRef = this.matDialog.open(ModeloEjecucionTipoFinalidadModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: IModeloTipoFinalidad) => {
        if (result) {
          this.formPart.addModeloTipoFinalidad(result);
        }
      }
    );
  }

  deleteModeloTipoFinalidad(wrapper?: StatusWrapper<IModeloTipoFinalidad>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado: boolean) => {
          if (aceptado) {
            this.formPart.deleteModeloTipoFinalidad(wrapper);
          }
        }
      )
    );
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

}
