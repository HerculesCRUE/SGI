import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { INotificacionProyectoExternoCVN } from '@core/models/csp/notificacion-proyecto-externo-cvn';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { NotificacionProyectoExternoCvnService } from '@core/services/csp/notificacion-proyecto-externo-cvn/notificacion-proyecto-externo-cvn.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiRestListResult, SgiRestFilter, RSQLSgiRestFilter, SgiRestFilterOperator } from '@sgi/framework/http';
import { from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, toArray } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';

const MSG_ERROR_LOAD = marker('error.load');

@Component({
  selector: 'sgi-notificacion-cvn-listado',
  templateUrl: './notificacion-cvn-listado.component.html',
  styleUrls: ['./notificacion-cvn-listado.component.scss']
})
export class NotificacionCvnListadoComponent extends AbstractTablePaginationComponent<INotificacionProyectoExternoCVN> implements OnInit {
  fxLayoutProperties: FxLayoutProperties;

  notificaciones$: Observable<INotificacionProyectoExternoCVN[]>;

  TIPO_COLECTIVO = TipoColectivo;

  constructor(
    protected readonly snackBarService: SnackBarService,
    private readonly notificacionProyectoExternoCvnService: NotificacionProyectoExternoCvnService,
    private readonly personaService: PersonaService,
    private readonly empresaService: EmpresaService
  ) {
    super(snackBarService, MSG_ERROR_LOAD);
    this.initFlexProperties();
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.initFormGroup();
  }

  private initFormGroup(): void {
    this.formGroup = new FormGroup({
      investigador: new FormControl(null),
      titulo: new FormControl(''),
      fechaInicioProyectoDesde: new FormControl(null),
      fechaInicioProyectoHasta: new FormControl(null),
      fechaFinProyectoDesde: new FormControl(null),
      fechaFinProyectoHasta: new FormControl(null),
      entidadParticipacion: new FormControl(null)
    });
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<INotificacionProyectoExternoCVN>> {
    return this.notificacionProyectoExternoCvnService.findAll(this.getFindOptions(reset)).pipe(
      switchMap(notificaciones => from(notificaciones.items).pipe(
        mergeMap(notificacion => this.fillNotificacionSolicitante(notificacion)),
        mergeMap(notificacion => this.fillNotificacionEntidadParticipacion(notificacion)),
        mergeMap(notificacion => this.fillNotificacionInvestigadorPrincipal(notificacion)),
        toArray(),
        map(items => {
          notificaciones.items = items;
          return notificaciones;
        })
      ))
    );
  }

  private fillNotificacionSolicitante(notificacion: INotificacionProyectoExternoCVN): Observable<INotificacionProyectoExternoCVN> {
    if (!notificacion.solicitante?.id) {
      return of(notificacion);
    }
    return this.personaService.findById(notificacion.solicitante.id).pipe(
      map(solicitante => {
        notificacion.solicitante = solicitante;
        return notificacion;
      })
    );
  }

  private fillNotificacionEntidadParticipacion(notificacion: INotificacionProyectoExternoCVN): Observable<INotificacionProyectoExternoCVN> {
    if (!notificacion.entidadParticipacion?.id) {
      return of(notificacion);
    }
    return this.empresaService.findById(notificacion.entidadParticipacion.id).pipe(
      map(entidadParticipacion => {
        notificacion.entidadParticipacion = entidadParticipacion;
        return notificacion;
      })
    );
  }

  private fillNotificacionInvestigadorPrincipal(
    notificacion: INotificacionProyectoExternoCVN
  ): Observable<INotificacionProyectoExternoCVN> {
    if (!notificacion.responsable?.id) {
      return of(notificacion);
    }
    return this.personaService.findById(notificacion.responsable.id).pipe(
      map(responsable => {
        notificacion.responsable = responsable;
        return notificacion;
      })
    );
  }


  protected initColumns(): void {
    this.columnas = ['investigador', 'titulo', 'entidadParticipacion', 'ip', 'fechaInicio', 'fechaFin', 'acciones'];
  }

  protected loadTable(reset?: boolean): void {
    this.notificaciones$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    return new RSQLSgiRestFilter(
      'solicitanteRef', SgiRestFilterOperator.EQUALS, controls.investigador.value?.id)
      .and('titulo', SgiRestFilterOperator.LIKE_ICASE, controls.titulo.value)
      .and('fechaInicio', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioProyectoDesde.value))
      .and('fechaInicio', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioProyectoHasta.value))
      .and('fechaFin', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinProyectoDesde.value))
      .and('fechaFin', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinProyectoHasta.value))
      .and('entidadParticipacionRef', SgiRestFilterOperator.EQUALS, controls.entidadParticipacion.value?.id);
  }

  onClearFilters() {
    super.onClearFilters();
    this.formGroup.controls.fechaInicioProyectoDesde.setValue(null);
    this.formGroup.controls.fechaInicioProyectoHasta.setValue(null);
    this.formGroup.controls.fechaFinProyectoDesde.setValue(null);
    this.formGroup.controls.fechaFinProyectoHasta.setValue(null);
    this.onSearch();
  }

  private initFlexProperties(): void {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '1%';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }
}
