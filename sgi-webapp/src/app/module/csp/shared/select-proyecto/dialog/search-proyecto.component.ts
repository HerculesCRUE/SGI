import { AfterViewInit, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MSG_PARAMS } from '@core/i18n';
import { IProyecto } from '@core/models/csp/proyecto';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { merge, Subject, Subscription } from 'rxjs';
import { tap } from 'rxjs/operators';

@Component({
  templateUrl: './search-proyecto.component.html',
  styleUrls: ['./search-proyecto.component.scss']
})
export class SearchProyectoModalComponent implements OnInit, AfterViewInit, OnDestroy {
  formGroup: FormGroup;
  displayedColumns = ['titulo', 'acronimo', 'codigoExterno', 'fechaInicio',
    'fechaFin', 'fechaFinDefinitiva', 'modeloEjecucion', 'acciones'];
  elementosPagina = [5, 10, 25, 100];
  totalElementos = 0;
  @ViewChild(MatSort, { static: true }) private sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) private paginator: MatPaginator;

  private subscriptions: Subscription[] = [];
  readonly proyectos$ = new Subject<IProyecto[]>();

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    public dialogRef: MatDialogRef<SearchProyectoModalComponent, IProyecto>,
    private readonly proyectoService: ProyectoService
  ) { }

  ngOnInit(): void {
    this.formGroup = new FormGroup({
      titulo: new FormControl(),
      acronimo: new FormControl(),
      codigoExterno: new FormControl(),
      fechaInicioDesde: new FormControl(),
      fechaInicioHasta: new FormControl(),
      fechaFinDesde: new FormControl(),
      fechaFinHasta: new FormControl(),
      responsableProyecto: new FormControl(),
      modeloEjecucion: new FormControl(),
      convocatoria: new FormControl(),
      entidadFinanciadora: new FormControl()
    });
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

    this.proyectoService.findAll(options).subscribe(result => {
      this.totalElementos = result.total;
      this.proyectos$.next(result.items);
    });
  }
  private buildFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('titulo', SgiRestFilterOperator.LIKE_ICASE, controls.titulo.value)
      .and('acronimo', SgiRestFilterOperator.LIKE_ICASE, controls.acronimo.value)
      .and('codigoExterno', SgiRestFilterOperator.LIKE_ICASE, controls.codigoExterno.value)
      .and('fechaInicio', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioDesde.value))
      .and(
        'fechaInicio',
        SgiRestFilterOperator.LOWER_OR_EQUAL,
        LuxonUtils.toBackend(controls.fechaInicioHasta.value?.plus({ hours: 23, minutes: 59, seconds: 59 }))
      )
      .and('fechaFin', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinDesde.value))
      .and(
        'fechaFin',
        SgiRestFilterOperator.LOWER_OR_EQUAL,
        LuxonUtils.toBackend(controls.fechaFinHasta.value?.plus({ hours: 23, minutes: 59, seconds: 59 }))
      );
    if (controls.responsableProyecto.value) {
      filter.and('equipo.personaRef', SgiRestFilterOperator.EQUALS, controls.responsableProyecto.value.id)
        .and('equipo.rolProyecto.rolPrincipal', SgiRestFilterOperator.EQUALS, 'true');
    }
    filter.and('modeloEjecucion.id', SgiRestFilterOperator.EQUALS, controls.modeloEjecucion.value?.id?.toString())
      .and('convocatoria.id', SgiRestFilterOperator.EQUALS, controls.convocatoria.value?.id?.toString())
      .and('entidadesFinanciadoras.id', SgiRestFilterOperator.EQUALS, controls.entidadFinanciadora.value?.id?.toString());
    return filter;
  }

  closeModal(proyecto?: IProyecto): void {
    this.dialogRef.close(proyecto);
  }
}
