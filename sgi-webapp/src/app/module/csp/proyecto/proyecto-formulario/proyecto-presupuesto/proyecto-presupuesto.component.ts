import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router, UrlTree } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { TipoPartida } from '@core/enums/tipo-partida';
import { MSG_PARAMS } from '@core/i18n';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoAnualidadNotificacionSge } from '@core/models/csp/proyecto-anualidad-notificacion-sge';
import { IProyectoAnualidadResumen } from '@core/models/csp/proyecto-anualidad-resumen';
import { IProyectoPresupuestoTotales } from '@core/models/csp/proyecto-presupuesto-totales';
import { IProyectoAnualidadPartida } from '@core/models/sge/proyecto-anualidad-partida';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { DialogService } from '@core/services/dialog.service';
import { ProyectoSgeService } from '@core/services/sge/proyecto-sge.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { from, Subscription } from 'rxjs';
import { map, mergeMap, switchMap, tap } from 'rxjs/operators';
import { CSP_ROUTE_NAMES } from '../../../csp-route-names';
import { SOLICITUD_ROUTE_NAMES } from '../../../solicitud/solicitud-route-names';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoPresupuestoFragment } from './proyecto-presupuesto.fragment';

const ANUALIDADES_KEY = marker('csp.proyecto-presupuesto.anualidad');
const ANUALIDADES_GENERICA_KEY = marker('csp.proyecto-presupuesto.anualidad-generica');
const MSG_DELETE = marker('msg.delete.entity');
const ENVIO_SGE_KEY = marker('msg.csp.proyecto-presupuesto.enviar-sge');

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
  textoEnvio: string;

  proyectoAnualidadEnvio: IProyectoAnualidadNotificacionSge[] = [];

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
    private readonly proyectoSgeService: ProyectoSgeService,
    private proyectoAnualidadService: ProyectoAnualidadService,
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

    this.translate.get(
      ENVIO_SGE_KEY
    ).subscribe((value) => this.textoEnvio = value);
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

  public sendSGE(data: StatusWrapper<IProyectoAnualidadResumen>) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoEnvio).subscribe(
        (aceptado: boolean) => {
          if (aceptado) {
            const proyectoAnualidadesPartidas: IProyectoAnualidadPartida[] = [];
            return this.proyectoAnualidadService.findAllAnualidadGasto(data.value.id)
              .pipe(
                map(anualidadesGasto => {
                  const proyectoAnualidadPartidasGasto = anualidadesGasto.items.map(anualidadGasto => {
                    const proyectoAnualidadPartida: IProyectoAnualidadPartida = {
                      anualidad: data.value.anio,
                      importe: anualidadGasto.importeConcedido,
                      tipoDatoEconomico: TipoPartida.GASTO,
                      partidaPresupuestaria: anualidadGasto.proyectoPartida.codigo,
                      proyecto: { id: anualidadGasto.proyectoSgeRef } as IProyectoSge
                    };
                    return proyectoAnualidadPartida;
                  });

                  proyectoAnualidadesPartidas.push(...proyectoAnualidadPartidasGasto);

                  return data;
                }),
                mergeMap(() => {
                  return this.proyectoAnualidadService.findAllAnualidadIngreso(data.value.id)
                    .pipe(
                      tap(anualidadesIngreso => {
                        const proyectoAnualidadPartidasIngreso = anualidadesIngreso.items.map(anualidadGasto => {
                          const proyectoAnualidadPartida: IProyectoAnualidadPartida = {
                            anualidad: data.value.anio,
                            importe: anualidadGasto.importeConcedido,
                            tipoDatoEconomico: TipoPartida.INGRESO,
                            partidaPresupuestaria: anualidadGasto.proyectoPartida.codigo,
                            proyecto: { id: anualidadGasto.proyectoSgeRef } as IProyectoSge
                          };
                          return proyectoAnualidadPartida;
                        });

                        proyectoAnualidadesPartidas.push(...proyectoAnualidadPartidasIngreso);
                      })
                    );
                }),
              ).pipe(
                map(() => {
                  const gastosMap = new Map<string, IProyectoAnualidadPartida>();
                  proyectoAnualidadesPartidas
                    .filter(element => element.tipoDatoEconomico === TipoPartida.GASTO)
                    .forEach(element => {
                      const key = `${element.partidaPresupuestaria}-${element.proyecto.id}-${element.anualidad}`;
                      const existing = gastosMap.get(key);
                      if (existing) {
                        existing.importe += element.importe;
                      } else {
                        gastosMap.set(key, element);
                      }
                    });

                  const ingresosMap = new Map<string, IProyectoAnualidadPartida>();
                  proyectoAnualidadesPartidas
                    .filter(element => element.tipoDatoEconomico === TipoPartida.INGRESO)
                    .forEach(element => {
                      const key = `${element.partidaPresupuestaria}-${element.proyecto.id}-${element.anualidad}`;
                      const existing = ingresosMap.get(key);
                      if (existing) {
                        existing.importe += element.importe;
                      } else {
                        ingresosMap.set(key, element);
                      }
                    });

                  return [...gastosMap.values(), ...ingresosMap.values()];
                }),
                switchMap((anualidadesPartidasPresupuestarias) =>
                  this.proyectoSgeService.createProyectoAnualidadesPartidas(anualidadesPartidasPresupuestarias)
                ),
                switchMap(() => {
                  return from(this.proyectoAnualidadEnvio).pipe(
                    switchMap(proyectoAnualidad => this.proyectoAnualidadService.notificarSge(proyectoAnualidad.id))
                  );
                })
              ).subscribe(() =>
                data.value.enviadoSge = true);

          }
        }
      )
    );
  }
}


