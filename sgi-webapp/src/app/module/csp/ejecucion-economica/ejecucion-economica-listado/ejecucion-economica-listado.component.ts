import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IRelacionEjecucionEconomica, TipoEntidad, TIPO_ENTIDAD_MAP } from '@core/models/csp/relacion-ejecucion-economica';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { ROUTE_NAMES } from '@core/route.names';
import { RelacionEjecucionEconomicaService } from '@core/services/csp/relacion-ejecucion-economica/relacion-ejecucion-economica.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { RolProyectoService } from '@core/services/csp/rol-proyecto.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, from, Observable, Subscription } from 'rxjs';
import { catchError, filter, map, mergeMap, switchMap, toArray } from 'rxjs/operators';
import { EJECUCION_ECONOMICA_ROUTE_NAMES } from '../ejecucion-economica-route-names';
import { IRelacionEjecucionEconomicaWithResponsables } from '../ejecucion-economica.action.service';

const MSG_ERROR = marker('error.load');

@Component({
  selector: 'sgi-ejecucion-economica-listado',
  templateUrl: './ejecucion-economica-listado.component.html',
  styleUrls: ['./ejecucion-economica-listado.component.scss']
})
export class EjecucionEconomicaListadoComponent extends AbstractTablePaginationComponent<IRelacionEjecucionEconomicaWithResponsables>
  implements OnInit, OnDestroy {
  ROUTE_NAMES = ROUTE_NAMES;

  dataSource$: Observable<IRelacionEjecucionEconomicaWithResponsables[]>;

  private subscriptions: Subscription[] = [];

  busquedaAvanzada = false;

  colectivosResponsable: string[];
  tipoEntidadSelected: TipoEntidad;

  get TIPO_ENTIDAD_MAP() {
    return TIPO_ENTIDAD_MAP;
  }

  get TipoEntidad() {
    return TipoEntidad;
  }

  get EJECUCION_ECONOMICA_ROUTE_NAMES() {
    return EJECUCION_ECONOMICA_ROUTE_NAMES;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    private rolProyectoService: RolProyectoService,
    private proyectoService: ProyectoService,
    private personaService: PersonaService,
    private relacionEjecucionEconomicaService: RelacionEjecucionEconomicaService,
    private grupoService: GrupoService,
  ) {
    super(snackBarService, MSG_ERROR);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.createFormGroup();
    this.loadColectivos();
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IRelacionEjecucionEconomicaWithResponsables>> {
    let relaciones$: Observable<SgiRestListResult<IRelacionEjecucionEconomica>>;
    let serviceGetResponsables: GrupoService | ProyectoService;

    switch (this.tipoEntidadSelected) {
      case TipoEntidad.GRUPO:
        relaciones$ = this.relacionEjecucionEconomicaService.findRelacionesGrupos(this.getFindOptions(reset));
        serviceGetResponsables = this.grupoService;
        break;
      case TipoEntidad.PROYECTO:
        relaciones$ = this.relacionEjecucionEconomicaService.findRelacionesProyectos(this.getFindOptions(reset));
        serviceGetResponsables = this.proyectoService;
        break;
      default:
        throw Error(`Invalid tipoEntidad "${this.tipoEntidadSelected}"`);
    }

    return relaciones$.pipe(
      map(result => result as SgiRestListResult<IRelacionEjecucionEconomicaWithResponsables>),
      switchMap(response =>
        from(response.items).pipe(
          mergeMap(relacion => {
            return serviceGetResponsables.findPersonaRefInvestigadoresPrincipales(relacion.id).pipe(
              filter(personaRefs => !!personaRefs),
              switchMap(personaRefs => this.personaService.findAllByIdIn(personaRefs).pipe(
                catchError((error) => {
                  this.logger.error(error);
                  return EMPTY;
                })
              )),
              map(responsables => {
                relacion.responsables = responsables.items;
                return relacion;
              }),
            );
          }),
          toArray(),
          map(() => {
            return response;
          })

        )
      )
    );
  }

  protected initColumns(): void {
    this.columnas = [
      'id',
      'proyectoSgeRef',
      'nombre',
      'responsable',
      'fechaInicio',
      'fechaFin',
      'acciones'
    ];
  }

  protected loadTable(reset?: boolean): void {
    this.dataSource$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;

    this.tipoEntidadSelected = this.formGroup.controls.tipoEntidad?.value;

    const restFilter = new RSQLSgiRestFilter('nombre', SgiRestFilterOperator.LIKE_ICASE, controls.nombre.value)
      .and('proyectoSgeRef', SgiRestFilterOperator.EQUALS, controls.identificadorSge.value)
      .and('fechaInicio',
        SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioDesde.value))
      .and('fechaInicio',
        SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioHasta.value))
      .and('fechaFin',
        SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinDesde.value))
      .and('fechaFin',
        SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinHasta.value));

    if (this.busquedaAvanzada) {
      if (this.tipoEntidadSelected === TipoEntidad.PROYECTO) {
        restFilter
          .and('proyecto.convocatoria.id', SgiRestFilterOperator.EQUALS, controls.convocatoria.value?.id?.toString())
      }

      restFilter
        .and('responsable', SgiRestFilterOperator.EQUALS, controls.responsable.value?.id);
    }

    return restFilter;
  }

  toggleBusquedaAvanzada(): void {
    this.busquedaAvanzada = !this.busquedaAvanzada;
  }

  onClearFilters(): void {
    this.initFormGroup(true);
    this.onSearch();
  }

  ngOnDestroy(): void {
    this.subscriptions?.forEach(x => x.unsubscribe());
  }

  private createFormGroup(): void {
    this.formGroup = new FormGroup({
      tipoEntidad: new FormControl(null),
      nombre: new FormControl(null),
      identificadorSge: new FormControl(null),
      fechaInicioDesde: new FormControl(null),
      fechaInicioHasta: new FormControl(null),
      fechaFinDesde: new FormControl(null),
      fechaFinHasta: new FormControl(null),
      convocatoria: new FormControl(null),
      responsable: new FormControl({ value: null, disabled: true })
    });

    this.initFormGroup();
  }

  private initFormGroup(reset = false): void {
    if (reset) {
      this.formGroup.reset();
    }

    this.formGroup.controls.tipoEntidad.setValue(TipoEntidad.PROYECTO);

    if (!this.colectivosResponsable || this.colectivosResponsable.length === 0) {
      this.formGroup.controls.responsable.disable();
    } else {
      this.formGroup.controls.responsable.enable();
    }

    this.tipoEntidadSelected = this.formGroup.controls.tipoEntidad?.value;
  }

  /**
   * Carga las listas de colectivos para hacer la busqueda de responsables de proyecto y activa el campo en el buscador.
   */
  private loadColectivos() {
    const queryOptionsResponsable: SgiRestFindOptions = {};
    queryOptionsResponsable.filter = new RSQLSgiRestFilter('rolPrincipal', SgiRestFilterOperator.EQUALS, 'true');
    this.subscriptions.push(
      this.rolProyectoService.findAll(queryOptionsResponsable).subscribe(
        (response) => {
          response.items.forEach((rolProyecto: IRolProyecto) => {
            this.rolProyectoService.findAllColectivos(rolProyecto.id).subscribe(
              (res) => {
                this.colectivosResponsable = res.items;
                this.formGroup.controls.responsable.enable();
              }
            );
          });
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR);
        }
      )
    );
  }

}
