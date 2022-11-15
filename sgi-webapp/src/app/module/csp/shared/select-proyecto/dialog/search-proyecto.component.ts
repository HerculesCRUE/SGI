import { AfterViewInit, Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogCommonComponent } from '@core/component/dialog-common.component';
import { SearchModalData } from '@core/component/select-dialog/select-dialog.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { IPersona } from '@core/models/sgp/persona';
import { Module } from '@core/module';
import { ROUTE_NAMES } from '@core/route.names';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import {
  RSQLSgiRestFilter,
  RSQLSgiRestSort,
  SgiRestFilter,
  SgiRestFilterOperator,
  SgiRestFindOptions,
  SgiRestListResult,
  SgiRestSortDirection
} from '@sgi/framework/http';
import { merge, of, Subject } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { CSP_ROUTE_NAMES } from '../../../csp-route-names';

const ENTITY_KEY = marker('csp.proyecto');

export interface SearchProyectoModalData extends SearchModalData {
  personas: IPersona[];
}

interface IProyectoListado extends IProyecto {
  proyectosSGE: string;
}

@Component({
  templateUrl: './search-proyecto.component.html',
  styleUrls: ['./search-proyecto.component.scss']
})
export class SearchProyectoModalComponent extends DialogCommonComponent implements OnInit, AfterViewInit, OnDestroy {
  formGroup: FormGroup;
  displayedColumns = ['id', 'codigoSGE', 'titulo', 'acronimo', 'codigoExterno', 'fechaInicio',
    'fechaFin', 'fechaFinDefinitiva', 'modeloEjecucion', 'acciones'];
  elementosPagina = [5, 10, 25, 100];
  totalElementos = 0;
  @ViewChild(MatSort, { static: true }) private sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) private paginator: MatPaginator;

  readonly proyectos$ = new Subject<IProyectoListado[]>();
  public msgMiembrosEquipoFullName: string;

  msgParamEntity: {};

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    dialogRef: MatDialogRef<SearchProyectoModalComponent, IProyecto>,
    private readonly proyectoService: ProyectoService,
    @Inject(MAT_DIALOG_DATA) public data: SearchProyectoModalData,
    private readonly translate: TranslateService,
    private readonly router: Router
  ) {
    super(dialogRef);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.formGroup = new FormGroup({
      titulo: new FormControl(this.data.searchTerm),
      acronimo: new FormControl(),
      codigoExterno: new FormControl(),
      fechaInicioDesde: new FormControl(),
      fechaInicioHasta: new FormControl(),
      fechaFinDesde: new FormControl(),
      fechaFinHasta: new FormControl(),
      responsableProyecto: new FormControl(),
      modeloEjecucion: new FormControl(),
      convocatoria: new FormControl(),
      entidadFinanciadora: new FormControl(),
      identificadorInterno: new FormControl(null, [Validators.pattern(/^[0-9]\d*$/)]),
      codigoIdentificacionSGE: new FormControl(),
      tipoFinalidad: new FormControl(),
      miembroEquipo: new FormControl(),
      miembrosParticipantes: new FormControl()
    });
    this.setupI18N();
    this.setNombresMiembrosParticipantes();
  }

  private setupI18N(): void {
    this.translate.get(
      ENTITY_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

  }

  private setNombresMiembrosParticipantes(): void {
    if (this.data.personas?.length > 0) {
      this.msgMiembrosEquipoFullName = this.data.personas.map((persona: IPersona) => {
        return persona.nombre + ' ' + persona.apellidos;
      }).join(', ');
    }
  }

  ngAfterViewInit(): void {
    super.ngAfterViewInit();
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
      this.proyectoService.findAll(options)
        .pipe(
          map((response: SgiRestListResult<IProyectoListado>) => {
            this.totalElementos = response.total;
            return response.items;
          }), switchMap(items => {
            items.forEach(item => this.resolveProyectosSgeSubscription(item));
            const proyectos: number[] = [];
            return of(items.filter(proyecto => {
              if (!proyectos.includes(proyecto.id)) {
                proyectos.push(proyecto.id);
                return true;
              }
              return false;
            }));
          })).subscribe(result => {
            this.proyectos$.next(result);
          }));
  }

  private resolveProyectosSgeSubscription(item: IProyectoListado): void {
    this.subscriptions.push(
      this.proyectoService.findAllProyectosSgeProyecto(item.id)
        .pipe(
          switchMap((proyectosSge: SgiRestListResult<IProyectoProyectoSge>) => {
            item.proyectosSGE = proyectosSge.items.map(element => element.proyectoSge.id).join(', ');
            return of(item);
          })).subscribe());
  }

  private buildFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('titulo', SgiRestFilterOperator.LIKE_ICASE, controls.titulo.value)
      .and('acronimo', SgiRestFilterOperator.LIKE_ICASE, controls.acronimo.value)
      .and('codigoExterno', SgiRestFilterOperator.LIKE_ICASE, controls.codigoExterno.value);

    if (controls.fechaInicioDesde.value) {
      filter.and('fechaInicio', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioDesde.value));
    }
    if (controls.fechaInicioHasta.value) {
      const fechaFilter = LuxonUtils.toBackend(controls.fechaInicioHasta.value?.plus({ hours: 23, minutes: 59, seconds: 59 }));
      filter.and('fechaInicio', SgiRestFilterOperator.LOWER_OR_EQUAL, fechaFilter);
    }
    if (controls.fechaFinDesde.value) {
      filter.and('fechaFin', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinDesde.value));
    }
    if (controls.fechaFinHasta.value) {
      const fechaFilter = LuxonUtils.toBackend(controls.fechaFinHasta.value?.plus({ hours: 23, minutes: 59, seconds: 59 }));
      filter.and('fechaFin', SgiRestFilterOperator.LOWER_OR_EQUAL, fechaFilter);
    }
    if (controls.responsableProyecto.value) {
      filter.and('equipo.personaRef', SgiRestFilterOperator.EQUALS, controls.responsableProyecto.value.id)
        .and('equipo.rolProyecto.rolPrincipal', SgiRestFilterOperator.EQUALS, 'true');
    }
    filter.and('modeloEjecucion.id', SgiRestFilterOperator.EQUALS, controls.modeloEjecucion.value?.id?.toString())
      .and('convocatoria.id', SgiRestFilterOperator.EQUALS, controls.convocatoria.value?.id?.toString())
      .and('entidadesFinanciadoras.id', SgiRestFilterOperator.EQUALS, controls.entidadFinanciadora.value?.id?.toString())
      .and('id', SgiRestFilterOperator.EQUALS, controls.identificadorInterno.value?.toString())
      .and('identificadoresSge.proyectoSgeRef', SgiRestFilterOperator.EQUALS, controls.codigoIdentificacionSGE.value?.toString())
      .and('finalidad.id', SgiRestFilterOperator.EQUALS, controls.tipoFinalidad.value?.id?.toString())
      .and('equipo.personaRef', SgiRestFilterOperator.EQUALS, controls.miembroEquipo.value?.id?.toString());

    if (controls.miembrosParticipantes.value) {
      filter.and('equipo.personaRef', SgiRestFilterOperator.IN, this.data.personas.map(persona => persona.id));
    }
    return filter;
  }

  closeModal(proyecto?: IProyecto): void {
    this.close(proyecto);
  }

  /**
   * Clean filters an reload the table
   */
  onClearFilters(): void {
    FormGroupUtil.clean(this.formGroup);
    this.search(true);
  }

  openCreate(): void {
    window.open(this.router.serializeUrl(this.router.createUrlTree(['/', Module.CSP.path, CSP_ROUTE_NAMES.PROYECTO, ROUTE_NAMES.NEW])), '_blank');
  }
}
