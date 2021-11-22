import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { TipoPartida } from '@core/enums/tipo-partida';
import { MSG_PARAMS } from '@core/i18n';
import { ESTADO_MAP } from '@core/models/csp/estado-proyecto';
import { IProyectoAnualidadNotificacionSge } from '@core/models/csp/proyecto-anualidad-notificacion-sge';
import { IProyectoAnualidadPartida } from '@core/models/sge/proyecto-anualidad-partida';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { DialogService } from '@core/services/dialog.service';
import { ProyectoSgeService } from '@core/services/sge/proyecto-sge.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { from, Observable, Subscription } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';
import { CSP_ROUTE_NAMES } from '../../csp-route-names';
import { PROYECTO_ANUALIDAD_ROUTE_NAMES } from '../../proyecto-anualidad/proyecto-anualidad-route-names';
import { PROYECTO_ROUTE_NAMES } from '../../proyecto/proyecto-route-names';

const MSG_ERROR = marker('error.load');
const MSG_NOTIFICADO_SUCCESS = marker('msg.csp.notificacion-presupuesto-sge.success');
const MSG_CONTINUE_NOTIFICACION_PRESUPUESTO_KEY = marker('msg.continue.notificacion.presupuesto');

export interface IProyectoPartidaEnvioSge {
  proyectoAnualidadPartida: IProyectoAnualidadPartida;
  proyectoAnualidadId: number;
}

@Component({
  selector: 'sgi-notificacion-presupuesto-sge-listado',
  templateUrl: './notificacion-presupuesto-sge-listado.component.html',
  styleUrls: ['./notificacion-presupuesto-sge-listado.component.scss']
})
export class NotificacionPresupuestoSgeListadoComponent extends AbstractTablePaginationComponent<IProyectoAnualidadNotificacionSge>
  implements OnInit, OnDestroy {

  dataSource = new MatTableDataSource<IProyectoAnualidadNotificacionSge>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  private subscriptions: Subscription[] = [];
  formGroup: FormGroup;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  proyectoAnualidadEnvio: IProyectoAnualidadNotificacionSge[] = [];

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  constructor(
    protected snackBarService: SnackBarService,
    private proyectoAnualidadService: ProyectoAnualidadService,
    private router: Router,
    private dialogService: DialogService,
    private route: ActivatedRoute,
    private readonly translate: TranslateService,
    private readonly proyectoSgeService: ProyectoSgeService
  ) {
    super(snackBarService, MSG_ERROR);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.formGroup = new FormGroup({
      anualidad: new FormControl(''),
      tituloProyecto: new FormControl(''),
      estado: new FormControl(''),
      numeroIdentificacionSge: new FormControl(''),
      fechaInicioDesde: new FormControl(null),
      fechaInicioHasta: new FormControl(null),
      fechaFinDesde: new FormControl(null),
      fechaFinHasta: new FormControl(null),
    });

    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (element: IProyectoAnualidadNotificacionSge, property: string) => {
        switch (property) {
          case 'proyectoAnualidad.proyecto.titulo':
            return element.proyectoTitulo;
          case 'proyectoAnualidad.proyecto.acronimo':
            return element.proyectoAcronimo;
          case 'proyectoAnualidad.proyecto.fechaInicio':
            return element.proyectoFechaInicio;
          case 'proyectoAnualidad.proyecto.fechaFin':
            return element.proyectoFechaFin;
          case 'proyectoAnualidad.proyecto.estado.estado':
            return element.proyectoEstado;
          case 'proyectoSgeRef':
            return element.proyectoSgeRef;
          case 'totalGastos':
            return element.totalGastos;
          case 'totalIngresos':
            return element.totalIngresos;
          case 'enviadoSge':
            return element.enviadoSge;
          default:
            return element[property];
        }
      };
    this.dataSource.sort = this.sort;
    this.search();
  }

  search(): void {
    this.filter = this.createFilter();
    this.loadTable();
  }

  onClearFilters() {
    super.onClearFilters();
    this.formGroup.controls.fechaInicioDesde.setValue(null);
    this.formGroup.controls.fechaInicioHasta.setValue(null);
    this.formGroup.controls.fechaFinDesde.setValue(null);
    this.formGroup.controls.fechaFinHasta.setValue(null);
    this.search();
  }

  protected createObservable(): Observable<SgiRestListResult<IProyectoAnualidadNotificacionSge>> {
    return this.proyectoAnualidadService.findNotificacionesSge(this.getFindOptions());
  }
  protected initColumns(): void {
    this.columnas = [
      'seleccionar',
      'proyectoAnualidad.proyecto.titulo',
      'proyectoAnualidad.proyecto.acronimo',
      'proyectoAnualidad.proyecto.fechaInicio',
      'proyectoAnualidad.proyecto.fechaFin',
      'proyectoAnualidad.proyecto.estado.estado',
      'proyectoSgeRef',
      'proyectoAnualidad.anio',
      'totalGastos',
      'totalIngresos',
      'enviadoSge',
      'acciones'
    ];
  }

  protected loadTable(reset?: boolean): void {
    this.getObservableLoadTable(reset).subscribe(elements => {
      this.dataSource.data = elements;
    });
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('proyectoAnualidad.anio', SgiRestFilterOperator.EQUALS, controls.anualidad.value?.toString());

    filter
      .and('proyectoAnualidad.proyecto.titulo', SgiRestFilterOperator.LIKE_ICASE, controls.tituloProyecto.value)
      .and('proyectoAnualidad.proyecto.estado.estado', SgiRestFilterOperator.EQUALS, controls.estado.value)
      .and('proyectoSgeRef', SgiRestFilterOperator.LIKE_ICASE, controls.numeroIdentificacionSge.value)
      .and('proyectoAnualidad.proyecto.fechaInicio', SgiRestFilterOperator.GREATHER_OR_EQUAL,
        LuxonUtils.toBackend(controls.fechaInicioDesde.value))
      .and('proyectoAnualidad.proyecto.fechaInicio', SgiRestFilterOperator.LOWER_OR_EQUAL,
        LuxonUtils.toBackend(controls.fechaInicioHasta.value))
      .and('proyectoAnualidad.proyecto.fechaFin', SgiRestFilterOperator.GREATHER_OR_EQUAL,
        LuxonUtils.toBackend(controls.fechaFinDesde.value))
      .and('proyectoAnualidad.proyecto.fechaFin', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinHasta.value));

    return filter;
  }

  showPoyectoAnualidad(proyectoId: number, proyectoAnualidadId: number) {
    this.router.navigate(['../',
      CSP_ROUTE_NAMES.PROYECTO, proyectoId, PROYECTO_ROUTE_NAMES.PRESUPUESTO,
      proyectoAnualidadId, PROYECTO_ANUALIDAD_ROUTE_NAMES.DATOS_GENERALES],
      { relativeTo: this.route.parent });
  }

  ngOnDestroy(): void {
    this.subscriptions?.forEach(x => x.unsubscribe());
  }

  checkEnvioSge(proyectoAnualidadNotificacionSge: IProyectoAnualidadNotificacionSge, $event: MatCheckboxChange): void {
    const index = this.proyectoAnualidadEnvio.map(p => p.id).indexOf(proyectoAnualidadNotificacionSge.id);

    if ($event.checked && index < 0) {
      this.proyectoAnualidadEnvio.push(proyectoAnualidadNotificacionSge);
    } else if (index >= 0) {
      this.proyectoAnualidadEnvio.splice(index, 1);
    }
  }

  checkAllEnvioSge($event: MatCheckboxChange): void {
    if ($event.checked) {
      this.proyectoAnualidadEnvio = this.dataSource.data.filter(presupuesto => !presupuesto.enviadoSge);
    } else {
      this.proyectoAnualidadEnvio = [];
    }
  }

  isCheckend(proyectoAnualidadNotificacionSge: IProyectoAnualidadNotificacionSge): Boolean {
    const proyectoAnualidadNotificacionSgeCheckeado =
      this.proyectoAnualidadEnvio.find(presupuesto => presupuesto.id === proyectoAnualidadNotificacionSge.id);

    if (proyectoAnualidadNotificacionSgeCheckeado) {
      return true;
    }
    return false;
  }

  isAllSelected(): Boolean {
    const presupuestosNofificacion = this.dataSource.data.filter(presupuesto => !presupuesto.enviadoSge);
    if (this.proyectoAnualidadEnvio.length === presupuestosNofificacion.length) {
      return true;
    }
    return false;
  }

  notificarSge(): void {
    if (this.proyectoAnualidadEnvio.length === 0) {
      return;
    }

    this.dialogService.showConfirmation(MSG_CONTINUE_NOTIFICACION_PRESUPUESTO_KEY).subscribe(
      (aceptado) => {
        if (aceptado) {
          this.enviarSges();
        }
      }
    )
  }

  enviarSges() {
    const proyectoAnualidadesPartidas: IProyectoAnualidadPartida[] = [];

    this.subscriptions.push(
      from(this.proyectoAnualidadEnvio)
        .pipe(
          mergeMap(proyectoAnualidad => {
            return this.proyectoAnualidadService.findAllAnualidadGasto(proyectoAnualidad.id)
              .pipe(
                map(anualidadesGasto => {
                  const proyectoAnualidadPartidasGasto = anualidadesGasto.items.map(anualidadGasto => {
                    const proyectoAnualidadPartida: IProyectoAnualidadPartida = {
                      anualidad: proyectoAnualidad.anio,
                      importe: anualidadGasto.importeConcedido,
                      tipoDatoEconomico: TipoPartida.GASTO,
                      partidaPresupuestaria: anualidadGasto.proyectoPartida.codigo,
                      proyecto: { id: anualidadGasto.proyectoSgeRef } as IProyectoSge
                    };
                    return proyectoAnualidadPartida;
                  });

                  proyectoAnualidadesPartidas.push(...proyectoAnualidadPartidasGasto);

                  return proyectoAnualidad;
                })
              );
          }),
          mergeMap(proyectoAnualidad => {
            return this.proyectoAnualidadService.findAllAnualidadIngreso(proyectoAnualidad.id)
              .pipe(
                tap(anualidadesIngreso => {

                  const proyectoAnualidadPartidasGasto = anualidadesIngreso.items.map(anualidadGasto => {
                    const proyectoAnualidadPartida: IProyectoAnualidadPartida = {
                      anualidad: proyectoAnualidad.anio,
                      importe: anualidadGasto.importeConcedido,
                      tipoDatoEconomico: TipoPartida.INGRESO,
                      partidaPresupuestaria: anualidadGasto.proyectoPartida.codigo,
                      proyecto: { id: anualidadGasto.proyectoSgeRef } as IProyectoSge
                    };
                    return proyectoAnualidadPartida;
                  });

                  proyectoAnualidadesPartidas.push(...proyectoAnualidadPartidasGasto);
                })
              );
          }),
          takeLast(1)
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
        )
        .subscribe(() => {
          this.loadTable(true);
          this.snackBarService.showSuccess(MSG_NOTIFICADO_SUCCESS);
        })
    );
  }

}
