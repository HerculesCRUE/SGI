import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConfigService } from '@core/services/csp/config.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { IProyectoEconomicoFormlyData, IProyectoEconomicoFormlyResponse, ProyectoEconomicoFormlyModalComponent } from 'src/app/esb/sge/formly-forms/proyecto-economico-formly-modal/proyecto-economico-formly-modal.component';
import { SearchProyectoEconomicoModalComponent, SearchProyectoEconomicoModalData } from 'src/app/esb/sge/shared/search-proyecto-economico-modal/search-proyecto-economico-modal.component';
import { ACTION_MODAL_MODE } from 'src/app/esb/shared/formly-forms/core/base-formly-modal.component';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoProyectosSgeFragment } from './proyecto-proyectos-sge.fragment';

const IDENTIFICADOR_SGE_KEY = marker('csp.proyecto-proyecto-sge.identificador-sge');
const TIPO_PROYECTO_KEY = marker('sge.proyecto');
const MSG_SAVE_SUCCESS = marker('msg.save.request.entity.success');
const MSG_UPDATE_SUCCESS = marker('msg.update.request.entity.success');

@Component({
  selector: 'sgi-proyecto-proyectos-sge',
  templateUrl: './proyecto-proyectos-sge.component.html',
  styleUrls: ['./proyecto-proyectos-sge.component.scss']
})
export class ProyectoProyectosSgeComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: ProyectoProyectosSgeFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['proyectoSgeRef', 'sectorIva', 'acciones'];

  msgParamEntity = {};
  private textoCrearSuccess: string;
  private textoUpdateSuccess: string;

  dataSource = new MatTableDataSource<StatusWrapper<IProyectoProyectoSge>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  canDeleteProyectosSge: boolean;
  private _altaBuscadorSgeEnabled: boolean = true;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get isBuscadorSgeEnabled(): boolean {
    return this._altaBuscadorSgeEnabled;
  }

  constructor(
    private actionService: ProyectoActionService,
    private matDialog: MatDialog,
    private readonly cspConfigService: ConfigService,
    private readonly snackBarService: SnackBarService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.PROYECTOS_SGE, actionService);

    this.formPart = this.fragment as ProyectoProyectosSgeFragment;

    this.subscriptions.push(
      this.cspConfigService.isAltaBuscadorSgeEnabled().subscribe(altaBuscadorSgeEnabled => {
        this._altaBuscadorSgeEnabled = altaBuscadorSgeEnabled;
      })
    );
  }

  private initColumns(isSectorIvaSgeEnabled: boolean): void {
    this.displayedColumns = ['proyectoSgeRef', 'acciones'];

    if (isSectorIvaSgeEnabled) {
      this.displayedColumns.splice(1, 0, 'sectorIva');
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IProyectoProyectoSge>, property: string) => {
        switch (property) {
          case 'proyectoSgeRef':
            return wrapper.value.proyectoSge.id;
          default:
            return wrapper[property];
        }
      };

    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.proyectosSge$.subscribe(elements => {
      this.dataSource.data = elements;
    }));

    this.subscriptions.push(
      this.formPart.isSectorIvaSgeEnabled$.subscribe(isSectorIvaSgeEnabled => {
        this.initColumns(isSectorIvaSgeEnabled);
      })
    );

  }

  private setupI18N(): void {
    this.translate.get(
      IDENTIFICADOR_SGE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      TIPO_PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SAVE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrearSuccess = value);

    this.translate.get(
      TIPO_PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_UPDATE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoUpdateSuccess = value);
  }

  openProyectoSgeCreateModal(): void {
    this.openProyectoSgeFormlyModal(ACTION_MODAL_MODE.NEW, null, this.textoCrearSuccess);
  }

  openProyectoSgeEditModal(proyectoSge: IProyectoSge): void {
    this.openProyectoSgeFormlyModal(ACTION_MODAL_MODE.EDIT, proyectoSge, this.textoUpdateSuccess);
  }

  openProyectoSgeViewModal(proyectoSge: IProyectoSge): void {
    this.openProyectoSgeFormlyModal(ACTION_MODAL_MODE.VIEW, proyectoSge);
  }

  openProyectoSgeSearchModal(): void {
    const data: SearchProyectoEconomicoModalData = {
      searchTerm: null,
      extended: true,
      selectedProyectos: this.dataSource.data.map((proyectoProyectoSge) => proyectoProyectoSge.value.proyectoSge),
      proyectoSgiId: this.formPart.getKey() as number,
      selectAndNotify: true,
      grupoInvestigacion: null
    };

    const config = {
      data
    };
    const dialogRef = this.matDialog.open(SearchProyectoEconomicoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (proyectoSge) => {
        if (proyectoSge) {
          this.formPart.addProyectoSge(proyectoSge);
        }
      }
    );
  }

  private openProyectoSgeFormlyModal(modalAction: ACTION_MODAL_MODE, proyectoSge?: IProyectoSge, textoActionSuccess?: string): void {
    const proyectoSgeData: IProyectoEconomicoFormlyData = {
      proyectoSge,
      proyectoSgiId: this.formPart.getKey() as number,
      grupoInvestigacion: null,
      action: modalAction
    };

    const config = {
      panelClass: 'sgi-dialog-container',
      data: proyectoSgeData
    };
    const dialogRef = this.matDialog.open(ProyectoEconomicoFormlyModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (response: IProyectoEconomicoFormlyResponse) => {
        if (response?.createdOrUpdated) {
          this.snackBarService.showSuccess(textoActionSuccess);
          if (response.proyectoSge && ACTION_MODAL_MODE.NEW === modalAction) {
            this.formPart.addProyectoSge(proyectoSge);
          }
          this.formPart.refreshSolicitudesProyectoPendientes();
        }
      }
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
