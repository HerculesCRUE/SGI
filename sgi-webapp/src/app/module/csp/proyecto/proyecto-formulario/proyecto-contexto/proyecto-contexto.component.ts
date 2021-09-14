import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoContexto, PROPIEDAD_RESULTADOS_MAP } from '@core/models/csp/proyecto-contexto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { ProyectoContextoModalComponent, ProyectoContextoModalData } from '../../modals/proyecto-contexto-modal/proyecto-contexto-modal.component';
import { ProyectoActionService } from '../../proyecto.action.service';
import { AreaTematicaProyectoData, ProyectoContextoFragment } from './proyecto-contexto.fragment';

const PROYECTO_CONTEXTO_INTERESES_KEY = marker('csp.proyecto.contexto.intereses');
const PROYECTO_CONTEXTO_OBJETIVOS_KEY = marker('csp.proyecto.contexto.objetivos');
const PROYECTO_CONTEXTO_RESULTADOS_KEY = marker('csp.proyecto.contexto.resultados');
const AREA_KEY = marker('csp.area');
const AREA_TEMATICA_KEY = marker('csp.area-tematica');

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

  convocatoriaAreaTematicas = new MatTableDataSource<AreaTematicaProyectoData>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;
  columns = ['nombreRaizArbol', 'areaTematicaConvocatoria', 'areaTematicaProyecto', 'acciones'];

  private subscriptions = [] as Subscription[];

  msgParamEntity = {};
  msgParamAreaEntities = {};
  msgParamAreaTematicaEntity = {};
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
      AREA_TEMATICA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamAreaTematicaEntity = { entity: value });

    this.translate.get(
      AREA_TEMATICA_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamAreaTematicaEntities = { entity: value });
  }

  private loadAreaTematicas(): void {
    this.subscriptions.push(this.formPart.areasTematicas$.subscribe(
      data => this.convocatoriaAreaTematicas.data = data
    ));
    this.convocatoriaAreaTematicas.paginator = this.paginator;
    this.convocatoriaAreaTematicas.sort = this.sort;
  }

  openModal(wrapper?: AreaTematicaProyectoData): void {
    const config = {
      panelClass: 'sgi-dialog-container',
      data: wrapper ? wrapper : {} as ProyectoContextoModalData
    };
    const dialogRef = this.matDialog.open(ProyectoContextoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result) => {
        if (wrapper) {
          this.formPart.updateAreaTematica(result);
        } else {
          this.formPart.addAreaTematica(result);
        }
      }
    );
  }


  get PROPIEDAD_RESULTADOS_MAP() {
    return PROPIEDAD_RESULTADOS_MAP;
  }

}
