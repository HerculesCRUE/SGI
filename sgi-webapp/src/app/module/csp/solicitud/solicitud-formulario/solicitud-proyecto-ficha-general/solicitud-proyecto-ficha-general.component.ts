import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { MSG_PARAMS } from '@core/i18n';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { ISolicitudProyecto, TIPO_PRESUPUESTO_MAP } from '@core/models/csp/solicitud-proyecto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { AreaTematicaModalData, SolicitudAreaTematicaModalComponent } from '../../modals/solicitud-area-tematica-modal/solicitud-area-tematica-modal.component';
import { SOLICITUD_ROUTE_NAMES } from '../../solicitud-route-names';
import { SolicitudActionService } from '../../solicitud.action.service';
import { SolicitudProyectoFichaGeneralFragment } from './solicitud-proyecto-ficha-general.fragment';

const SOLICITUD_DATOS_PROYECTO_FICHA_GENERAL_ACRONIMO_KEY = marker('csp.solicitud-datos-proyecto-ficha-general.acronimo');
const SOLICITUD_DATOS_PROYECTO_FICHA_GENERAL_COD_EXTERNO_KEY = marker('csp.solicitud-datos-proyecto-ficha-general.codigo-externo');
const SOLICITUD_DATOS_PROYECTO_FICHA_GENERAL_COLABORATIVO_KEY = marker('csp.solicitud-datos-proyecto-ficha-general.proyecto-colaborativo');
const SOLICITUD_DATOS_PROYECTO_FICHA_GENERAL_COORDINADO_KEY = marker('csp.solicitud-datos-proyecto-ficha-general.proyecto-coordinado');
const SOLICITUD_DATOS_PROYECTO_FICHA_GENERAL_COORDINADOR_EXTERNO_KEY = marker('csp.solicitud-datos-proyecto-ficha-general.coordinador-externo');
const SOLICITUD_DATOS_PROYECTO_FICHA_GENERAL_TIPO_DESGLOSE_PRESUPUESTO_KEY = marker('csp.solicitud-datos-proyecto-ficha-general.tipo-desglose-presupuesto');
const AREA_TEMATICA_KEY = marker('csp.area-tematica');
const AREA_KEY = marker('csp.area');

export interface AreaTematicaListado {
  padre: IAreaTematica;
  areasTematicasConvocatoria: string;
  areaTematicaSolicitud: IAreaTematica;
}
@Component({
  selector: 'sgi-solicitud-proyecto-ficha-general',
  templateUrl: './solicitud-proyecto-ficha-general.component.html',
  styleUrls: ['./solicitud-proyecto-ficha-general.component.scss']
})
export class SolicitudProyectoFichaGeneralComponent extends FormFragmentComponent<ISolicitudProyecto> implements OnInit, OnDestroy {
  formPart: SolicitudProyectoFichaGeneralFragment;
  fxFlexProperties: FxFlexProperties;
  fxFlexProperties100: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  private subscriptions = [] as Subscription[];

  msgParamAcronimoEntity = {};
  msgParamColaborativoEntity = {};
  msgParamCoordinadoEntity = {};
  msgParamCoordinadorExternoEntity = {};
  msgParamTipoDesglosePresupuestoEntity = {};
  msgParamAreaTematicaEntities = {};
  msgParamAreaEntities: {};
  msgParamCodExternoEntity = {};

  areasConvocatoria: IAreaTematica[];

  listadoAreaTematicas = new MatTableDataSource<AreaTematicaListado>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;
  columns = ['nombreRaizArbol', 'areaTematicaConvocatoria', 'areaTematicaSolicitud', 'acciones'];

  constructor(
    public actionService: SolicitudActionService,
    private router: Router,
    private route: ActivatedRoute,
    private matDialog: MatDialog,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.PROYECTO_DATOS, actionService);
    this.formPart = this.fragment as SolicitudProyectoFichaGeneralFragment;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(50%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxFlexProperties100 = new FxFlexProperties();
    this.fxFlexProperties100.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties100.md = '0 1 calc(100%-10px)';
    this.fxFlexProperties100.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexProperties100.order = '3';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();
    if (this.actionService.formularioSolicitud !== FormularioSolicitud.PROYECTO) {
      this.router.navigate(['../', SOLICITUD_ROUTE_NAMES.DATOS_GENERALES], { relativeTo: this.route });
    }
    this.loadAreaTematicas();
    this.setupI18N();
  }

  get TIPO_PRESUPUESTO_MAP() {
    return TIPO_PRESUPUESTO_MAP;
  }

  private setupI18N(): void {
    this.translate.get(
      AREA_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamAreaEntities = { entity: value });

    this.translate.get(
      SOLICITUD_DATOS_PROYECTO_FICHA_GENERAL_ACRONIMO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamAcronimoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_DATOS_PROYECTO_FICHA_GENERAL_COD_EXTERNO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamCodExternoEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_DATOS_PROYECTO_FICHA_GENERAL_COLABORATIVO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamColaborativoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_DATOS_PROYECTO_FICHA_GENERAL_COORDINADO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCoordinadoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_DATOS_PROYECTO_FICHA_GENERAL_COORDINADOR_EXTERNO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCoordinadorExternoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_DATOS_PROYECTO_FICHA_GENERAL_TIPO_DESGLOSE_PRESUPUESTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoDesglosePresupuestoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE });

    this.translate.get(
      AREA_TEMATICA_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamAreaTematicaEntities = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  private loadAreaTematicas(): void {
    const subscription = this.formPart.areasTematicas$.subscribe((data) => {
      if (!data || data.length === 0) {
        this.listadoAreaTematicas.data = [];
      } else {
        this.areasConvocatoria = data[0]?.areaTematicaConvocatoria;
        const listadoAreas: AreaTematicaListado = {
          padre: data[0]?.rootTree,
          areasTematicasConvocatoria: data[0]?.areaTematicaConvocatoria?.map(area => area.nombre).join(', '),
          areaTematicaSolicitud: data[0]?.areaTematicaSolicitud
        };
        this.listadoAreaTematicas.data = [listadoAreas];
      }
    }
    );
    this.subscriptions.push(subscription);
    this.listadoAreaTematicas.paginator = this.paginator;
    this.listadoAreaTematicas.sort = this.sort;
  }

  deleteAreaTematicaListado(data?: AreaTematicaListado) {
    this.formPart.deleteAreaTematica();
  }

  openModal(data?: AreaTematicaListado): void {
    const config = {
      data: {
        padre: data?.padre ?? this.formGroup.controls.padre?.value,
        areasTematicasConvocatoria: this.areasConvocatoria,
        areaTematicaSolicitud: data?.areaTematicaSolicitud,
      } as AreaTematicaModalData,
    };
    const dialogRef = this.matDialog.open(SolicitudAreaTematicaModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: AreaTematicaModalData) => {
        if (result) {
          this.formPart.updateAreaTematica(result);
        }
      }
    );
  }
}
