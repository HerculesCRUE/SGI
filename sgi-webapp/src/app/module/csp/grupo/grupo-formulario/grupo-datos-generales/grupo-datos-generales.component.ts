import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupo } from '@core/models/csp/grupo';
import { IGrupoEspecialInvestigacion } from '@core/models/csp/grupo-especial-investigacion';
import { IGrupoTipo, TIPO_MAP } from '@core/models/csp/grupo-tipo';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { RolProyectoColectivoService } from '@core/services/csp/rol-proyecto-colectivo/rol-proyecto-colectivo.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { CKEDITOR_CONFIG, CkEditorConfig } from '@shared/sgi-ckeditor-config';
import Editor from 'ckeditor5-custom-build/build/ckeditor';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { IProyectoEconomicoFormlyData, IProyectoEconomicoFormlyResponse, ProyectoEconomicoFormlyModalComponent } from 'src/app/esb/sge/formly-forms/proyecto-economico-formly-modal/proyecto-economico-formly-modal.component';
import { SearchProyectoEconomicoModalComponent, SearchProyectoEconomicoModalData } from 'src/app/esb/sge/shared/search-proyecto-economico-modal/search-proyecto-economico-modal.component';
import { ACTION_MODAL_MODE } from 'src/app/esb/shared/formly-forms/core/base-formly-modal.component';
import { GrupoActionService } from '../../grupo.action.service';
import { GrupoDatosGeneralesFragment } from './grupo-datos-generales.fragment';

const GRUPO_NOMBRE_KEY = marker('csp.grupo.nombre');
const GRUPO_INVESTIGADOR_PRINCIPAL_KEY = marker('csp.grupo.investigador-principal');
const GRUPO_CODIGO_KEY = marker('csp.grupo.codigo');
const GRUPO_FECHA_INICIO_KEY = marker('label.fecha-inicio');
const GRUPO_ESPECIAL_INVESTIGACION_KEY = marker('csp.grupo.especial-investigacion');
const GRUPO_RESUMEN_KEY = marker('csp.grupo.resumen');
const IDENTIFICADOR_SGE_KEY = marker('csp.proyecto-proyecto-sge.identificador-sge');
const PROYECTO_SGE_KEY = marker('sge.proyecto');

const MSG_SAVE_SUCCESS = marker('msg.save.request.entity.success');
const MSG_UPDATE_SUCCESS = marker('msg.update.request.entity.success');
const MSG_DELETE_RELACION_PROYECTO = marker('msg.grupo-proyecto-sge.eliminar-relacion');

@Component({
  selector: 'sgi-grupo-datos-generales',
  templateUrl: './grupo-datos-generales.component.html',
  styleUrls: ['./grupo-datos-generales.component.scss']
})
export class GrupoDatosGeneralesComponent extends FormFragmentComponent<IGrupo> implements OnInit {
  public readonly CkEditor = Editor;

  formPart: GrupoDatosGeneralesFragment;

  colectivosBusqueda: string[];

  private subscriptions = [] as Subscription[];

  msgParamTituloEntity = {};
  msgParamNombreEntity = {};
  msgParamInvestigadorPrincipalEntity = {};
  msgParamCodigoEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamEspecialInvestigacionEntity = {};
  msgParamResumenEntity = {};
  msgParamIdentificadorSgeEntity = {};

  private textoCrearSuccess: string;
  private textoUpdateSuccess: string;

  tiposGrupo = new MatTableDataSource<IGrupoTipo>();
  columnsTipo = ['tipo', 'fechaInicioTipo', 'fechaFinTipo'];

  especialesInvestigacionGrupo = new MatTableDataSource<IGrupoEspecialInvestigacion>();
  columnsEspecialInvestigacion = ['especialInvestigacion', 'fechaInicio', 'fechaFin'];

  proyectosSgeDataSource = new MatTableDataSource<StatusWrapper<IProyectoSge>>();
  proyectosSgeDisplayedColumns = ['proyectoSgeRef', 'acciones'];

  @ViewChild(MatPaginator, { static: true }) proyectosSgePaginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) proyectosSgeSort: MatSort;

  private _altaBuscadorSgeEnabled: boolean = true;

  get TIPO_MAP() {
    return TIPO_MAP;
  }

  get grupo(): IGrupo {
    return this.formPart.getValue();
  }

  get isBuscadorSgeEnabled(): boolean {
    return this._altaBuscadorSgeEnabled;
  }

  constructor(
    protected actionService: GrupoActionService,
    public authService: SgiAuthService,
    private readonly dialogService: DialogService,
    private readonly matDialog: MatDialog,
    private readonly rolProyectoColectivoService: RolProyectoColectivoService,
    private readonly snackBarService: SnackBarService,
    private readonly translate: TranslateService,
    @Inject(CKEDITOR_CONFIG) public readonly configCkEditor: CkEditorConfig
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService, translate);
    this.formPart = this.fragment as GrupoDatosGeneralesFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.loadColectivosBusqueda();
    this.loadHistoricoTipos();
    this.loadHistoricoEspecialesInvestigacion();
    this.initProyectosSgeTable();
  }

  protected setupI18N(): void {

    this.translate.get(
      GRUPO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_INVESTIGADOR_PRINCIPAL_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamInvestigadorPrincipalEntity = { entity: value, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_CODIGO_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamCodigoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_ESPECIAL_INVESTIGACION_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamEspecialInvestigacionEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      IDENTIFICADOR_SGE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamIdentificadorSgeEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      GRUPO_RESUMEN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamResumenEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_SGE_KEY,
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
      PROYECTO_SGE_KEY,
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

  openProyectoSgeEditModal(wrapper: StatusWrapper<IProyectoSge>): void {
    this.openProyectoSgeFormlyModal(ACTION_MODAL_MODE.EDIT, wrapper?.value, this.textoUpdateSuccess);
  }

  openProyectoSgeViewModal(wrapper: StatusWrapper<IProyectoSge>): void {
    this.openProyectoSgeFormlyModal(ACTION_MODAL_MODE.VIEW, wrapper?.value);
  }

  openProyectoSgeSearchModal(): void {
    const data: SearchProyectoEconomicoModalData = {
      searchTerm: null,
      extended: true,
      selectedProyectos: this.proyectosSgeDataSource.data.map((proyectoProyectoSge) => proyectoProyectoSge.value),
      proyectoSgiId: this.formPart.getKey() as number,
      selectAndNotify: true,
      grupoInvestigacion: this.grupo
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

  deleteRelacionProyecto(wrapper: StatusWrapper<IProyectoSge>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(MSG_DELETE_RELACION_PROYECTO).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteRelacionProyectoSge(wrapper);
          }
        }
      )
    );
  }

  private openProyectoSgeFormlyModal(modalAction: ACTION_MODAL_MODE, proyectoSge?: IProyectoSge, textoActionSuccess?: string): void {
    const proyectoSgeData: IProyectoEconomicoFormlyData = {
      proyectoSge,
      proyectoSgiId: this.formPart.getKey() as number,
      grupoInvestigacion: this.grupo,
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
        }
      }
    );
  }

  private loadHistoricoTipos() {
    this.subscriptions.push(this.formPart.tipos$.subscribe((data) => {
      if (!data || data.length === 0) {
        this.tiposGrupo.data = [];
      } else {
        this.tiposGrupo.data = data;
      }
    }
    ));
  }

  private loadHistoricoEspecialesInvestigacion() {
    this.subscriptions.push(this.formPart.especialesInvestigacion$.subscribe((data) => {
      if (!data || data.length === 0) {
        this.especialesInvestigacionGrupo.data = [];
      } else {
        this.especialesInvestigacionGrupo.data = data;
      }
    }
    ));
  }

  private initProyectosSgeTable(): void {
    this.proyectosSgeDataSource.paginator = this.proyectosSgePaginator;
    this.proyectosSgeDataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<IProyectoSge>, property: string) => {
        switch (property) {
          case 'proyectoSgeRef':
            return wrapper.value.id;
          default:
            return wrapper[property];
        }
      };

    this.proyectosSgeDataSource.sort = this.proyectosSgeSort;

    this.subscriptions.push(this.formPart.proyectosSge$.subscribe(elements => {
      this.proyectosSgeDataSource.data = elements;
    }));
  }

  private loadColectivosBusqueda(): void {
    this.subscriptions.push(
      this.rolProyectoColectivoService.findColectivosActivos().subscribe(colectivos => {
        this.colectivosBusqueda = colectivos
      })
    );
  }

}
