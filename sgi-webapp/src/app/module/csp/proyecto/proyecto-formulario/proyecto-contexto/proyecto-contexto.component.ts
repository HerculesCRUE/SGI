import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { IProyectoContexto, PROPIEDAD_RESULTADOS_MAP } from '@core/models/csp/proyecto-contexto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Subscription } from 'rxjs';
import { ProyectoContextoModalComponent, ProyectoContextoModalData } from '../../modals/proyecto-contexto-modal/proyecto-contexto-modal.component';
import { ProyectoActionService } from '../../proyecto.action.service';
import { AreaTematicaProyectoData, ProyectoContextoFragment } from './proyecto-contexto.fragment';

const PROYECTO_CONTEXTO_INTERESES_KEY = marker('csp.proyecto.contexto.intereses');
const PROYECTO_CONTEXTO_OBJETIVOS_KEY = marker('csp.proyecto.contexto.objetivos');
const PROYECTO_CONTEXTO_RESULTADOS_KEY = marker('csp.proyecto.contexto.resultados');
const AREA_KEY = marker('csp.area');
const SELECCIONAR_AREA_TEMATICA_KEY = marker('csp.proyecto.select-area');
const AREA_TEMATICA_KEY = marker('csp.area-tematica');



export interface AreaTematicaListado {
  padre: IAreaTematica;
  areasTematicasConvocatoria: string;
  areaTematicaProyecto: IAreaTematica;
}
@Component({
  selector: 'sgi-proyecto-contexto',
  templateUrl: './proyecto-contexto.component.html',
  styleUrls: ['./proyecto-contexto.component.scss']
})
export class ProyectoContextoComponent extends FormFragmentComponent<IProyectoContexto> implements OnInit {

  formPart: ProyectoContextoFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  totalElementos: number;
  displayedColumns: string[];
  elementosPagina: number[];

  msgParamInteresesEntity = {};
  msgParamObjetivosEntity = {};
  msgParamResultadosEntity = {};
  areasConvocatoria: IAreaTematica[];

  listadoAreaTematicas = new MatTableDataSource<AreaTematicaListado>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;
  columns = ['nombreRaizArbol', 'areaTematicaConvocatoria', 'areaTematicaProyecto', 'acciones'];

  private subscriptions = [] as Subscription[];

  msgParamEntity = {};
  msgParamAreaEntities = {};
  msgParamAreaTematicaSeleccionar = {};
  msgParamAreaTematicaEntities = {};

  constructor(
    protected actionService: ProyectoActionService,
    private matDialog: MatDialog,
    private readonly translate: TranslateService

  ) {
    super(actionService.FRAGMENT.CONTEXTO_PROYECTO, actionService);
    this.formPart = this.fragment as ProyectoContextoFragment;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.setupI18N();

    this.loadAreaTematicas();
  }

  private setupI18N(): void {

    this.translate.get(
      AREA_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamAreaEntities = { entity: value });

    this.translate.get(
      PROYECTO_CONTEXTO_INTERESES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamInteresesEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      PROYECTO_CONTEXTO_OBJETIVOS_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamObjetivosEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      PROYECTO_CONTEXTO_RESULTADOS_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamResultadosEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      SELECCIONAR_AREA_TEMATICA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamAreaTematicaSeleccionar = { entity: value });

    this.translate.get(
      AREA_TEMATICA_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamAreaTematicaEntities = { entity: value });
  }

  private loadAreaTematicas(): void {
    const subscription = this.formPart.areasTematicas$.subscribe((data) => {
      if (!data || data.length === 0) {
        this.listadoAreaTematicas.data = [];
      } else {
        this.areasConvocatoria = data[0]?.areasTematicasConvocatoria;
        const listadoAreas: AreaTematicaListado = {
          padre: data[0]?.root,
          areasTematicasConvocatoria: data[0]?.areasTematicasConvocatoria?.map(area => area.nombre).join(', '),
          areaTematicaProyecto: data[0]?.areaTematicaProyecto
        };
        this.listadoAreaTematicas.data = [listadoAreas];
      }
    }
    );
    this.subscriptions.push(subscription);
    this.listadoAreaTematicas.paginator = this.paginator;
    this.listadoAreaTematicas.sort = this.sort;
  }
  deleteAreaTematicaListado() {
    this.formPart.deleteAreaTematicaListado();
  }

  openModal(data?: AreaTematicaListado): void {
    const config = {
      panelClass: 'sgi-dialog-container',
      data: {
        padre: data?.padre ?? this.formGroup.controls.padre?.value,
        areasTematicasConvocatoria: this.areasConvocatoria,
        areaTematicaProyecto: data?.areaTematicaProyecto,
      } as ProyectoContextoModalData,
    };
    const dialogRef = this.matDialog.open(ProyectoContextoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: ProyectoContextoModalData) => {
        if (result) {
          this.formPart.updateListadoAreaTematica(result);
        }
      }
    );
  }


  get PROPIEDAD_RESULTADOS_MAP() {
    return PROPIEDAD_RESULTADOS_MAP;
  }

}
