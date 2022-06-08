import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IGrupo } from '@core/models/csp/grupo';
import { TIPO_MAP } from '@core/models/csp/grupo-tipo';
import { IPersona } from '@core/models/sgp/persona';
import { ROUTE_NAMES } from '@core/route.names';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { LineaInvestigacionService } from '@core/services/csp/linea-investigacion/linea-investigacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, from, Observable } from 'rxjs';
import { catchError, filter, map, mergeMap, switchMap, toArray } from 'rxjs/operators';
import { CSP_ROUTE_NAMES } from '../../csp-route-names';

const MSG_ERROR_LOAD = marker('error.load');

interface IGrupoListado extends IGrupo {
  investigadoresPrincipales: IPersona[];
}

@Component({
  selector: 'sgi-grupo-listado',
  templateUrl: './grupo-listado-inv.component.html',
  styleUrls: ['./grupo-listado-inv.component.scss']
})
export class GrupoListadoInvComponent extends AbstractTablePaginationComponent<IGrupoListado> implements OnInit {
  ROUTE_NAMES = ROUTE_NAMES;
  CSP_ROUTE_NAMES = CSP_ROUTE_NAMES;

  grupos$: Observable<IGrupoListado[]>;

  get TIPO_MAP() {
    return TIPO_MAP;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    private grupoService: GrupoService,
    private personaService: PersonaService,
    public authService: SgiAuthService,
    private lineaInvestigacionService: LineaInvestigacionService
  ) {
    super(snackBarService, MSG_ERROR_LOAD);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.buildFormGroup();
    this.filter = this.createFilter();
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IGrupoListado>> {
    const gruposInvestigacion$ = this.grupoService.findAll(this.getFindOptions(reset));
    return gruposInvestigacion$.pipe(
      map(result => {
        return {
          page: result.page,
          total: result.total,
          items: result.items.map((grupo) => grupo as IGrupoListado)
        } as SgiRestListResult<IGrupoListado>;
      }),
      switchMap(response =>
        from(response.items).pipe(
          mergeMap(grupo => this.fillInvestigadorPrincipal(grupo)),
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
      'nombre',
      'investigadorPrincipal',
      'codigo',
      'fechaInicio',
      'fechaFin',
      'tipo',
      'acciones'
    ];
  }

  protected loadTable(reset?: boolean): void {
    this.grupos$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;

    return new RSQLSgiRestFilter('nombre', SgiRestFilterOperator.LIKE_ICASE, controls.nombre.value)
      .and('codigo', SgiRestFilterOperator.LIKE_ICASE, controls.codigo.value)
      .and('proyectoSgeRef', SgiRestFilterOperator.EQUALS, controls.proyectoSgeRef.value);
  }

  onClearFilters() {
    super.onClearFilters();
    this.buildFormGroup();
    this.onSearch();
  }

  private buildFormGroup() {
    this.formGroup = new FormGroup({
      nombre: new FormControl(null),
      codigo: new FormControl(null),
      proyectoSgeRef: new FormControl(null)
    });
  }

  private fillInvestigadorPrincipal(grupo: IGrupoListado): Observable<IGrupoListado> {
    return this.grupoService.findPersonaRefInvestigadoresPrincipalesWithMaxParticipacion(grupo.id).pipe(
      filter(investigadoresPrincipales => !!investigadoresPrincipales),
      switchMap(investigadoresPrincipales => this.personaService.findAllByIdIn(investigadoresPrincipales)),
      map(investigadoresPrincipales => {
        grupo.investigadoresPrincipales = investigadoresPrincipales.items;
        return grupo;
      }),
      catchError((error) => {
        this.logger.error(error);
        return EMPTY;
      })
    );
  }

}
