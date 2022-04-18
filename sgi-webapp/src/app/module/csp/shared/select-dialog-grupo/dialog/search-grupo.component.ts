import { AfterViewInit, Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { SearchModalData } from '@core/component/select-dialog/select-dialog.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupo } from '@core/models/csp/grupo';
import { TIPO_MAP } from '@core/models/csp/grupo-tipo';
import { IPersona } from '@core/models/sgp/persona';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { RolProyectoColectivoService } from '@core/services/csp/rol-proyecto-colectivo/rol-proyecto-colectivo.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { LuxonUtils } from '@core/utils/luxon-utils';
import {
  RSQLSgiRestFilter,
  RSQLSgiRestSort,
  SgiRestFilter,
  SgiRestFilterOperator,
  SgiRestFindOptions, SgiRestSortDirection
} from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, from, merge, Observable, Subject, Subscription } from 'rxjs';
import { catchError, filter, map, mergeMap, switchMap, tap, toArray } from 'rxjs/operators';

interface IGrupoListado extends IGrupo {
  investigadoresPrincipales: IPersona[];
}

@Component({
  templateUrl: './search-grupo.component.html',
  styleUrls: ['./search-grupo.component.scss']
})
export class SearchGrupoModalComponent implements OnInit, AfterViewInit, OnDestroy {
  formGroup: FormGroup;
  displayedColumns = [
    'nombre',
    'investigadorPrincipal',
    'codigo',
    'fechaInicio',
    'fechaFin',
    'tipo',
    'acciones'
  ];
  elementosPagina = [5, 10, 25, 100];
  totalElementos = 0;
  @ViewChild(MatSort, { static: true }) private sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) private paginator: MatPaginator;

  private subscriptions: Subscription[] = [];
  readonly grupos$ = new Subject<IGrupoListado[]>();
  colectivosBusqueda: string[];


  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get TIPO_MAP() {
    return TIPO_MAP;
  }

  constructor(
    private readonly logger: NGXLogger,
    private readonly dialogRef: MatDialogRef<SearchGrupoModalComponent, IGrupo>,
    private readonly grupoService: GrupoService,
    private readonly personaService: PersonaService,
    private readonly rolProyectoColectivoService: RolProyectoColectivoService,
    @Inject(MAT_DIALOG_DATA) public data: SearchModalData
  ) { }

  ngOnInit(): void {
    this.buildFormGroup();
    this.loadColectivosBusqueda();
  }

  ngAfterViewInit(): void {
    this.search();

    this.subscriptions.push(
      merge(
        this.paginator.page,
        this.sort.sortChange
      ).pipe(
        tap(() => {
          this.search();
        })
      ).subscribe()
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  search(reset?: boolean) {
    const options: SgiRestFindOptions = {
      page: {
        index: reset ? 0 : this.paginator.pageIndex,
        size: this.paginator.pageSize
      },
      sort: new RSQLSgiRestSort(this.sort?.active, SgiRestSortDirection.fromSortDirection(this.sort?.direction)),
      filter: this.buildFilter()
    };

    this.subscriptions.push(
      this.grupoService.findAll(options).pipe(
        map(response => {
          this.totalElementos = response.total;
          return response.items.map((grupo) => grupo as IGrupoListado);
        }),
        switchMap(response =>
          from(response).pipe(
            mergeMap(grupo => this.fillInvestigadorPrincipal(grupo)),
            toArray(),
            map(() => {
              return response;
            })
          )
        )
      ).subscribe(result => {
        this.grupos$.next(result);
      })
    );
  }

  closeModal(grupo?: IGrupo): void {
    this.dialogRef.close(grupo);
  }

  /**
   * Clean filters an reload the table
   */
  onClearFilters(): void {
    FormGroupUtil.clean(this.formGroup);
    this.search(true);
  }

  private buildFormGroup() {
    this.formGroup = new FormGroup({
      nombre: new FormControl(this.data.searchTerm),
      codigo: new FormControl(),
      miembroEquipo: new FormControl(),
      proyectoSgeRef: new FormControl(),
      fechaInicioDesde: new FormControl(),
      fechaInicioHasta: new FormControl()
    });
  }

  private buildFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const restFilter = new RSQLSgiRestFilter('nombre', SgiRestFilterOperator.LIKE_ICASE, controls.nombre.value)
      .and('codigo', SgiRestFilterOperator.LIKE_ICASE, controls.codigo.value)
      .and('miembrosEquipo.personaRef', SgiRestFilterOperator.EQUALS, controls.miembroEquipo.value?.id)
      .and('proyectoSgeRef', SgiRestFilterOperator.EQUALS, controls.proyectoSgeRef.value);

    if (controls.fechaInicioDesde.value) {
      restFilter.and('fechaInicio', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioDesde.value));
    }

    if (controls.fechaInicioHasta.value) {
      const fechaFilter = LuxonUtils.toBackend(controls.fechaInicioHasta.value?.plus({ hours: 23, minutes: 59, seconds: 59 }));
      restFilter.and('fechaInicio', SgiRestFilterOperator.LOWER_OR_EQUAL, fechaFilter);
    }

    return restFilter;
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
    )
  }

  private loadColectivosBusqueda(): void {
    this.subscriptions.push(
      this.rolProyectoColectivoService.findColectivosActivos().subscribe(colectivos => {
        this.colectivosBusqueda = colectivos
      })
    );
  }

}
