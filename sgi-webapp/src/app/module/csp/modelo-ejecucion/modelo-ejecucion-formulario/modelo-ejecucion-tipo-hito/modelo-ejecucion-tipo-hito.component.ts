import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IModeloTipoHito } from '@core/models/csp/modelo-tipo-hito';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ModeloEjecucionTipoHitoModalComponent, ModeloEjecucionTipoHitoModalData } from '../../modals/modelo-ejecucion-tipo-hito-modal/modelo-ejecucion-tipo-hito-modal.component';
import { ModeloEjecucionActionService } from '../../modelo-ejecucion.action.service';
import { ModeloEjecucionTipoHitoFragment } from './modelo-ejecucion-tipo-hito.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const MODELO_EJECUCION_TIPO_HITO_KEY = marker('csp.tipo-hito');

@Component({
  selector: 'sgi-modelo-ejecucion-tipo-hito',
  templateUrl: './modelo-ejecucion-tipo-hito.component.html',
  styleUrls: ['./modelo-ejecucion-tipo-hito.component.scss']
})
export class ModeloEjecucionTipoHitoComponent extends FragmentComponent implements OnInit, OnDestroy {
  private formPart: ModeloEjecucionTipoHitoFragment;
  private subscriptions = [] as Subscription[];

  columns = ['nombre', 'descripcion', 'convocatorias', 'solicitudes', 'proyectos', 'acciones'];
  numPage = [5, 10, 25, 100];
  totalElements = 0;

  msgParamEntity = {};
  textoDelete: string;

  modelosTipoHitos = new MatTableDataSource<StatusWrapper<IModeloTipoHito>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  constructor(
    private readonly dialogService: DialogService,
    private matDialog: MatDialog,
    actionService: ModeloEjecucionActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.TIPO_HITOS, actionService);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
    this.formPart = this.fragment as ModeloEjecucionTipoHitoFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    const subscription = this.formPart.modeloTipoHito$.subscribe(
      (wrappers: StatusWrapper<IModeloTipoHito>[]) => {
        this.modelosTipoHitos.data = wrappers;
      }
    );
    this.subscriptions.push(subscription);
    this.modelosTipoHitos.paginator = this.paginator;
    this.modelosTipoHitos.sortingDataAccessor =
      (wrapper: StatusWrapper<IModeloTipoHito>, property: string) => {
        switch (property) {
          case 'nombre':
            return wrapper.value.tipoHito.nombre;
          case 'descripcion':
            return wrapper.value.tipoHito.descripcion;
          case 'convocatorias':
            return wrapper.value.convocatoria;
          case 'solicitudes':
            return wrapper.value.solicitud;
          case 'proyectos':
            return wrapper.value.proyecto;
          default:
            return wrapper[property];
        }
      };
    this.modelosTipoHitos.sort = this.sort;
  }

  private setupI18N(): void {
    this.translate.get(
      MODELO_EJECUCION_TIPO_HITO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      MODELO_EJECUCION_TIPO_HITO_KEY,
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

  /**
   * Abre el modal para crear/modificar
   */
  openModal(statusWrapper?: StatusWrapper<IModeloTipoHito>): void {
    const modeloTipoHito = {
      activo: true,
      convocatoria: false,
      solicitud: false,
      proyecto: false
    } as IModeloTipoHito;
    const tipoHitos: ITipoHito[] = [];
    this.modelosTipoHitos.data.forEach((wrapper: StatusWrapper<IModeloTipoHito>) => {
      tipoHitos.push(wrapper.value.tipoHito);
    });

    const config = {
      panelClass: 'sgi-dialog-container',
      data: {
        modeloTipoHito: statusWrapper ? statusWrapper.value : modeloTipoHito,
        tipoHitos
      } as ModeloEjecucionTipoHitoModalData
    };
    const dialogRef = this.matDialog.open(ModeloEjecucionTipoHitoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: IModeloTipoHito) => {
        if (result) {
          if (statusWrapper) {
            if (!statusWrapper.created) {
              statusWrapper.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            this.formPart.addModeloTipoHito(result);
          }
        }
      }
    );
  }

  deleteModeloTipoHito(wrapper: StatusWrapper<IModeloTipoHito>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado: boolean) => {
          if (aceptado) {
            this.formPart.deleteModeloTipoHito(wrapper);
          }
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }


}

