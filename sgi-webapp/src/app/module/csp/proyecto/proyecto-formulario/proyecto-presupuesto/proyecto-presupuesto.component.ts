import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router, UrlTree } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoAnualidadResumen } from '@core/models/csp/proyecto-anualidad-resumen';
import { IProyectoPresupuestoTotales } from '@core/models/csp/proyecto-presupuesto-totales';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { SpawnSyncOptionsWithStringEncoding } from 'child_process';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { CSP_ROUTE_NAMES } from '../../../csp-route-names';
import { SOLICITUD_ROUTE_NAMES } from '../../../solicitud/solicitud-route-names';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoPresupuestoFragment } from './proyecto-presupuesto.fragment';

const ANUALIDADES_KEY = marker('csp.proyecto-presupuesto.anualidad');
const ANUALIDADES_GENERICA_KEY = marker('csp.proyecto-presupuesto.anualidad-generica');
const MSG_DELETE = marker('msg.delete.entity');

@Component({
  selector: 'sgi-proyecto-presupuesto',
  templateUrl: './proyecto-presupuesto.component.html',
  styleUrls: ['./proyecto-presupuesto.component.scss']
})
export class ProyectoPresupuestoComponent extends FormFragmentComponent<IProyecto> implements OnInit, OnDestroy {
  ROUTE_NAMES = ROUTE_NAMES;

  private subscriptions = [] as Subscription[];

  formPart: ProyectoPresupuestoFragment;
  fxFlexProperties: FxFlexProperties;
  fxFlexPropertiesOne: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;
  fxFlexPropertiesEntidad: FxFlexProperties;

  msgParaAnualidad = {};
  msgParaAnualidades = {};
  textoDelete: string;

  anualidades = new MatTableDataSource<StatusWrapper<IProyectoAnualidadResumen>>();
  valoresCalculadosData = {} as IProyectoPresupuestoTotales;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;
  columns = [];

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }
  constructor(
    public actionService: ProyectoActionService,
    private translate: TranslateService,
    private readonly proyectoService: ProyectoService,
    private readonly solicitudService: SolicitudService,
    private router: Router,
    private route: ActivatedRoute,
    private dialogService: DialogService) {
    super(actionService.FRAGMENT.PRESUPUESTO, actionService);

    this.formPart = this.fragment as ProyectoPresupuestoFragment;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '1';

    this.fxFlexPropertiesInline = new FxFlexProperties();
    this.fxFlexPropertiesInline.sm = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.md = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.order = '4';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.setupI18N();

    this.anualidades.paginator = this.paginator;
    this.anualidades.sort = this.sort;
    this.subscriptions.push(this.formPart.proyectoAnualidades$.subscribe(
      data => {
        this.anualidades.data = data;
        this.checkDisabledControls(data.length);

        if (this.formGroup.controls.anualidades.value !== null) {
          this.formPart.columnAnualidades$.next(this.formGroup.controls.anualidades.value);
          if (this.formGroup.controls.anualidades.value) {
            this.columns = ['anualidad', 'fechaInicio', 'fechaFin', 'totalGastosPresupuesto', 'totalGastosConcedido', 'totalIngresos', 'presupuestar', 'enviadoSge', 'acciones'];
          } else {
            this.columns = ['totalGastosPresupuesto', 'totalGastosConcedido', 'totalIngresos', 'presupuestar', 'enviadoSge', 'acciones'];
          }
        }
      })
    );

    this.subscriptions.push(this.formGroup.controls.anualidades.valueChanges.subscribe(
      (value) => {
        this.checkDisabledControls(this.anualidades.data.length);

        this.formPart.columnAnualidades$.next(value);
        if (value) {
          this.columns = ['anualidad', 'fechaInicio', 'fechaFin', 'totalGastosPresupuesto', 'totalGastosConcedido', 'totalIngresos', 'presupuestar', 'enviadoSge', 'acciones'];
        } else {
          this.columns = ['totalGastosPresupuesto', 'totalGastosConcedido', 'totalIngresos', 'presupuestar', 'enviadoSge', 'acciones'];
        }

        this.setTextoAniadirAnualidad();
      }
    ));

    this.subscriptions.push(this.proyectoService.getProyectoPresupuestoTotales(this.formPart.getKey() as number).
      subscribe(response => {
        this.valoresCalculadosData = response;
      }));

    this.updateImportesTotales();
  }

  private setupI18N(): void {
    this.translate.get(
      ANUALIDADES_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParaAnualidades = { entity: value });

    this.setTextoAniadirAnualidad();

    this.translate.get(
      ANUALIDADES_KEY,
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

  setTextoAniadirAnualidad() {
    if (this.formGroup.controls.anualidades.value) {
      this.translate.get(
        ANUALIDADES_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.msgParaAnualidad = { entity: value });
    } else {
      this.translate.get(
        ANUALIDADES_GENERICA_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((text) => this.msgParaAnualidad = { entity: text });
    }

  }

  deleteAnualidad(anualidad: StatusWrapper<IProyectoAnualidadResumen>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deleteAnualidad(anualidad);
          }
        }
      )
    );
  }

  showPresupuestoSolcitud() {
    this.subscriptions.push(this.solicitudService.hasSolicitudProyectoGlobal(this.formPart.proyecto.solicitudId as number).
      subscribe(value => {
        let presupuestoSolicitud: UrlTree;
        if (value) {
          presupuestoSolicitud = this.router.createUrlTree(['../',
            CSP_ROUTE_NAMES.SOLICITUD, this.formPart.proyecto.solicitudId, SOLICITUD_ROUTE_NAMES.DESGLOSE_PRESUPUESTO_GLOBAL],
            { relativeTo: this.route.parent.parent });
        } else {
          presupuestoSolicitud = this.router.createUrlTree(['../',
            CSP_ROUTE_NAMES.SOLICITUD, this.formPart.proyecto.solicitudId, SOLICITUD_ROUTE_NAMES.DESGLOSE_PRESUPUESTO_ENTIDADES],
            { relativeTo: this.route.parent.parent });
        }
        window.open(presupuestoSolicitud.toString(), '_blank');

      }));

  }

  private checkDisabledControls(numAnualidades: number) {
    if (this.formGroup.controls.anualidades.value === null) {
      this.formPart.disableAddAnualidad$.next(true);
    } else if (numAnualidades > 0) {
      if (!this.formGroup.controls.anualidades.value) {
        this.formPart.disableAddAnualidad$.next(true);
      }
      if (!this.formGroup.controls.anualidades.disabled) {
        this.formGroup.controls.anualidades.disable();
      }
    } else {
      this.formPart.disableAddAnualidad$.next(false);
      if (this.formGroup.controls.anualidades.disabled) {
        this.formGroup.controls.anualidades.enable();
      }
    }
  }

  private updateImportesTotales() {
    this.subscriptions.push(this.proyectoService.findAllProyectoAnualidadesGasto(this.formPart.getKey() as number)
      .subscribe(proyectoAnualidades => {

        /* Presupuesto por Universidad Sin Costes Indirectos */
        const importePresupuestoUniversidad = proyectoAnualidades.items
          .filter(anualidadGasto => !anualidadGasto.conceptoGasto.costesIndirectos)
          .reduce((total, anualidadGasto) => total + anualidadGasto.importePresupuesto, 0);
        this.valoresCalculadosData.importePresupuestoUniversidad = importePresupuestoUniversidad;
        /* Presupuesto por Universidad Con Costes Indirectos */
        const importePresupuestoUniversidadCostesIndirectos = proyectoAnualidades.items
          .filter(anualidadGasto => anualidadGasto.conceptoGasto.costesIndirectos)
          .reduce((total, anualidadGasto) => total + anualidadGasto.importePresupuesto, 0);
        this.valoresCalculadosData.importePresupuestoUniversidadCostesIndirectos = importePresupuestoUniversidadCostesIndirectos;
        /* Total Presupuesto por Universidad */
        const totalImportePresupuestoUniversidad = proyectoAnualidades.items.reduce(
          (total, anualidadGasto) => total + anualidadGasto.importePresupuesto, 0);
        this.valoresCalculadosData.totalImportePresupuestoUniversidad = totalImportePresupuestoUniversidad;

        /* Concedido por Universidad Sin Costes Indirectos */
        const importeConcedidoUniversidad = proyectoAnualidades.items
          .filter(anualidadGasto => !anualidadGasto.conceptoGasto.costesIndirectos)
          .reduce((total, anualidadGasto) => total + anualidadGasto.importeConcedido, 0);
        this.valoresCalculadosData.importeConcedidoUniversidad = importeConcedidoUniversidad;
        /* Concedido por Universidad Con Costes Indirectos */
        const importeConcedidoUniversidadCostesIndirectos = proyectoAnualidades.items
          .filter(anualidadGasto => anualidadGasto.conceptoGasto.costesIndirectos)
          .reduce((total, anualidadGasto) => total + anualidadGasto.importeConcedido, 0);
        this.valoresCalculadosData.importeConcedidoUniversidadCostesIndirectos = importeConcedidoUniversidadCostesIndirectos;
        /* Total Concedido por Universidad*/
        const totalImporteConcedidoUniversidad = proyectoAnualidades.items.reduce(
          (total, anualidadGasto) => total + anualidadGasto.importeConcedido, 0);
        this.valoresCalculadosData.totalImporteConcedidoUniversidad = totalImporteConcedidoUniversidad;
      })
    );
  }
}


