import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { HttpProblem } from '@core/errors/http-problem';
import { MSG_PARAMS } from '@core/i18n';
import { IInvencion } from '@core/models/pii/invencion';
import { ISectorAplicacion } from '@core/models/pii/sector-aplicacion';
import { ESTADO_MAP } from '@core/models/pii/solicitud-proteccion';
import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { IViaProteccion } from '@core/models/pii/via-proteccion';
import { IPais } from '@core/models/sgo/pais';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { DialogService } from '@core/services/dialog.service';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { SectorAplicacionService } from '@core/services/pii/sector-aplicacion/sector-aplicacion.service';
import { TipoProteccionService } from '@core/services/pii/tipo-proteccion/tipo-proteccion.service';
import { ViaProteccionService } from '@core/services/pii/via-proteccion/via-proteccion.service';
import { PaisService } from '@core/services/sgo/pais/pais.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';

const MSG_ERROR = marker('error.load');
const MSG_BUTTON_NEW = marker('btn.add.entity');
const MSG_REACTIVE = marker('msg.reactivate.entity');
const MSG_SUCCESS_REACTIVE = marker('msg.reactivate.entity.success');
const MSG_ERROR_REACTIVE = marker('error.reactivate.entity');
const MSG_DEACTIVATE = marker('msg.deactivate.entity');
const MSG_ERROR_DEACTIVATE = marker('error.deactivate.entity');
const MSG_SUCCESS_DEACTIVATE = marker('msg.deactivate.entity.success');
const INVENCION_KEY = marker('pii.invencion');

@Component({
  selector: 'sgi-invencion-listado',
  templateUrl: './invencion-listado.component.html',
  styleUrls: ['./invencion-listado.component.scss']
})
export class InvencionListadoComponent extends AbstractTablePaginationComponent<IInvencion> implements OnInit {

  MSG_PARAMS = MSG_PARAMS;
  ROUTE_NAMES = ROUTE_NAMES;
  textoCrear: string;
  textoDesactivar: string;
  textoReactivar: string;
  textoErrorDesactivar: string;
  textoSuccessDesactivar: string;
  textoSuccessReactivar: string;
  textoErrorReactivar: string;

  fxFlexProperties: FxFlexProperties;
  fxFlexProperties50: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  invencion$: Observable<IInvencion[]>;
  private busquedaAvanzada = false;

  readonly sectoresAplicacion$: Observable<ISectorAplicacion[]>;
  readonly tiposProteccion$: Observable<ITipoProteccion[]>;
  readonly viasProteccion$ = new BehaviorSubject<IViaProteccion[]>([]);
  readonly paises$ = new BehaviorSubject<IPais[]>([]);
  readonly showFiltroPais$ = new BehaviorSubject<boolean>(false);

  get ESTADO_SOLICITUD_PROTECCION_MAP() {
    return ESTADO_MAP;
  }

  get TipoColectivo() {
    return TipoColectivo;
  }

  get isBusquedaAvanzada() {
    return this.busquedaAvanzada;
  }

  constructor(
    protected readonly snackBarService: SnackBarService,
    private readonly dialogService: DialogService,
    private readonly logger: NGXLogger,
    private readonly invencionService: InvencionService,
    private readonly translate: TranslateService,
    sectorAplicacionService: SectorAplicacionService,
    tipoProteccionService: TipoProteccionService,
    private viaProteccionService: ViaProteccionService,
    private paisService: PaisService

  ) {
    super(snackBarService, MSG_ERROR);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(19%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxFlexProperties50 = new FxFlexProperties();
    this.fxFlexProperties50.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties50.md = '0 1 calc(50%-10px)';
    this.fxFlexProperties50.gtMd = '0 1 calc(50%-10px)';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
    this.sectoresAplicacion$ = sectorAplicacionService.findAll().pipe(map(({ items }) => items));
    this.tiposProteccion$ = tipoProteccionService.findAll().pipe(map(({ items }) => items));
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.formGroup = new FormGroup({
      id: new FormControl(''),
      fechaComunicacionDesde: new FormControl(),
      fechaComunicacionHasta: new FormControl(),
      sectorAplicacion: new FormControl(null),
      tipoProteccion: new FormControl(null),
      inventor: new FormControl(''),
      titulo: new FormControl(''),
      solicitudNumero: new FormControl(''),
      solicitudInicioFechaDesde: new FormControl(null),
      solicitudInicioFechaHasta: new FormControl(null),
      solicitudFinFechaDesde: new FormControl(null),
      solicitudFinFechaHasta: new FormControl(null),
      solicitudTitulo: new FormControl(''),
      solicitudViaProteccion: new FormControl(null),
      solicitudPais: new FormControl(null),
      solicitudEstado: new FormControl(''),
    });
    this.loadViasProteccion();
    this.loadPaises();

    this.suscripciones.push(this.formGroup.controls.solicitudViaProteccion.valueChanges.subscribe(
      (viaProteccion: IViaProteccion) => {
        if (viaProteccion.paisEspecifico) {
          this.showFiltroPais$.next(true);
        } else {
          this.showFiltroPais$.next(false);
          this.formGroup.controls.solicitudPais.setValue(null);
        }
      }
    ));
  }

  protected createObservable(): Observable<SgiRestListResult<IInvencion>> {
    const observable$ = this.invencionService.findTodos(this.getFindOptions());
    return observable$;
  }

  protected initColumns(): void {
    this.columnas = ['id', 'fechaComunicacion', 'titulo', 'tipoProteccion.nombre', 'activo', 'acciones'];
  }

  protected loadTable(reset?: boolean): void {
    this.invencion$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('id', SgiRestFilterOperator.EQUALS, controls.id.value)
      .and('fechaComunicacion', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaComunicacionDesde.value))
      .and('fechaComunicacion', SgiRestFilterOperator.LOWER_OR_EQUAL,
        LuxonUtils.toBackend(controls.fechaComunicacionHasta.value))
      // TODO incluir and anidado con or de tipoProteccion.id y tipoProteccion.padre.id
      .and('tipoProteccion.id', SgiRestFilterOperator.EQUALS, controls.tipoProteccion.value?.id?.toString())
      .and('sectoresAplicacion.sectorAplicacion.id', SgiRestFilterOperator.EQUALS, controls.sectorAplicacion.value?.id?.toString())
      .and('titulo', SgiRestFilterOperator.LIKE_ICASE, controls.titulo.value)
      .and('inventores.inventorRef', SgiRestFilterOperator.LIKE_ICASE, controls.inventor.value?.id?.toString());
    if (this.isBusquedaAvanzada) {
      filter.and('solicitudesProteccion.numeroSolicitud', SgiRestFilterOperator.EQUALS, controls.solicitudNumero.value)
        .and('solicitudesProteccion.fechaPrioridadSolicitud', SgiRestFilterOperator.GREATHER_OR_EQUAL,
          LuxonUtils.toBackend(controls.solicitudInicioFechaDesde?.value))
        .and('solicitudesProteccion.fechaPrioridadSolicitud', SgiRestFilterOperator.LOWER_OR_EQUAL,
          LuxonUtils.toBackend(controls.solicitudInicioFechaHasta?.value))
        .and('solicitudesProteccion.fechaFinPriorPresFasNacRec', SgiRestFilterOperator.GREATHER_OR_EQUAL,
          LuxonUtils.toBackend(controls.solicitudFinFechaDesde?.value))
        .and('solicitudesProteccion.fechaFinPriorPresFasNacRec', SgiRestFilterOperator.LOWER_OR_EQUAL,
          LuxonUtils.toBackend(controls.solicitudFinFechaHasta?.value))
        .and('solicitudesProteccion.titulo', SgiRestFilterOperator.LIKE_ICASE, controls.solicitudTitulo.value)
        .and('solicitudesProteccion.viaProteccion.id', SgiRestFilterOperator.EQUALS, controls.solicitudViaProteccion.value?.id?.toString())
        .and('solicitudesProteccion.paisProteccionRef', SgiRestFilterOperator.EQUALS, controls.solicitudPais.value?.id?.toString())
        .and('solicitudesProteccion.estado', SgiRestFilterOperator.EQUALS, controls.solicitudEstado.value);
    }

    return filter;
  }

  onClearFilters() {
    super.onClearFilters();
    this.formGroup.controls.fechaComunicacionDesde.setValue(null);
    this.formGroup.controls.fechaComunicacionHasta.setValue(null);
    this.formGroup.controls.solicitudInicioFechaDesde.setValue(null);
    this.formGroup.controls.solicitudInicioFechaHasta.setValue(null);
    this.formGroup.controls.solicitudFinFechaDesde.setValue(null);
    this.formGroup.controls.solicitudFinFechaHasta.setValue(null);

    this.onSearch();
  }

  /**
   * Mostrar busqueda avanzada
   */
  toggleBusquedaAvanzada(): void {
    this.busquedaAvanzada = !this.busquedaAvanzada;
    this.onSearch();
  }

  /**
   * Desactivar un registro de Invención
   * @param invencion  Invención.
   */
  deactivateInvencion(invencion: IInvencion): void {
    const subcription = this.dialogService.showConfirmation(this.textoDesactivar)
      .pipe(switchMap((accept) => {
        if (accept) {
          return this.invencionService.desactivar(invencion.id);
        } else {
          return of();
        }
      })).subscribe(
        () => {
          this.snackBarService.showSuccess(this.textoSuccessDesactivar);
          this.loadTable();
        },
        (error) => {
          this.logger.error(error);
          if (error instanceof HttpProblem) {
            this.snackBarService.showError(error);
          }
          else {
            this.snackBarService.showError(this.textoErrorDesactivar);
          }
        }
      );
    this.suscripciones.push(subcription);
  }

  /**
   * Activar un registro de Invención
   * @param invencion  Invención.
   */
  activateInvencion(invencion: IInvencion): void {
    const subcription = this.dialogService.showConfirmation(this.textoReactivar)
      .pipe(switchMap((accept) => {
        if (accept) {
          return this.invencionService.activar(invencion.id);
        } else {
          return of();
        }
      })).subscribe(
        () => {
          this.snackBarService.showSuccess(this.textoSuccessReactivar);
          this.loadTable();
        },
        (error) => {
          this.logger.error(error);
          if (error instanceof HttpProblem) {
            this.snackBarService.showError(error);
          }
          else {
            this.snackBarService.showError(this.textoErrorReactivar);
          }
        }
      );
    this.suscripciones.push(subcription);
  }

  private setupI18N(): void {
    this.translate.get(
      INVENCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_NEW,
          { entity: value }
        );
      })
    ).subscribe((value) => this.textoCrear = value);

    this.translate.get(
      INVENCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDesactivar = value);

    this.translate.get(
      INVENCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoErrorDesactivar = value);

    this.translate.get(
      INVENCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoSuccessDesactivar = value);

    this.translate.get(
      INVENCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoReactivar = value);

    this.translate.get(
      INVENCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoSuccessReactivar = value);

    this.translate.get(
      INVENCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoErrorReactivar = value);
  }

  private loadViasProteccion(): void {

    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('activo', SgiRestFilterOperator.EQUALS, 'true')
    };

    this.viaProteccionService.findTodos(options).pipe(
      map(response => response.items),
      catchError(error => {
        this.logger.error(error);
        return of(void 0);
      })
    ).subscribe((vias: IViaProteccion[]) => this.viasProteccion$.next(vias));
  }

  private loadPaises(): void {
    this.paisService.findAll().pipe(
      map(response => response.items),
      catchError(error => {
        this.logger.error(error);
        return of(void 0);
      })
    ).subscribe(paises => this.paises$.next(paises));
  }

}