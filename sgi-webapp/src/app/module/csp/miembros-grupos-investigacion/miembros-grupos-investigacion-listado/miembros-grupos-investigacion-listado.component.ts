import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractMenuContentComponent } from '@core/component/abstract-menu-content.component';
import { SgiError } from '@core/errors/sgi-error';
import { IGrupo } from '@core/models/csp/grupo';
import { Dedicacion, DEDICACION_MAP } from '@core/models/csp/grupo-equipo';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { OutputReport } from '@core/models/rep/output-report.enum';
import { IPersona } from '@core/models/sgp/persona';
import { ConfigService as ConfigCnfService } from '@core/services/cnf/config.service';
import { GrupoEquipoService } from '@core/services/csp/grupo-equipo/grupo-equipo.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { DialogService } from '@core/services/dialog.service';
import { IReportConfig } from '@core/services/rep/abstract-table-export.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { forkJoin, Observable, Subscription } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { IMiembroGrupoInvestigacionListadoReportOptions, MiembrosGruposInvestigacionListadoExportService } from '../miembros-grupos-investigacion-listado-export.service';

const MSG_DOWNLOAD_ERROR = marker('error.file.download');
const WARN_EXPORT_EXCEL_KEY = marker('msg.export.max-registros-warning');

export interface IMiembroGrupoInvestigacionListadoData {
  rol: IRolProyecto;
  activo: boolean;
  fechaInicioParticipacion: DateTime;
  fechaFinParticipacion: DateTime;
  dedicacion: Dedicacion;
  participacion: number;
  grupo: IGrupo;
  persona: IPersona;
}

@Component({
  selector: 'sgi-miembros-grupos-investigacion-listado',
  templateUrl: './miembros-grupos-investigacion-listado.component.html',
  styleUrls: ['./miembros-grupos-investigacion-listado.component.scss']
})
export class MiembrosGruposInvestigacionListadoComponent extends AbstractMenuContentComponent implements OnInit, OnDestroy {

  columnas: string[];
  elementosPagina: number[];
  formGroup: FormGroup;
  dataSource = new MatTableDataSource<IMiembroGrupoInvestigacionListadoData>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  private subscriptions: Subscription[] = [];
  private limiteRegistrosExportacionExcel: number;

  private mapPersonas = new Map<string, IPersona>();
  private mapGrupos = new Map<number, IGrupo>();
  private batchSize = 50;

  get DEDICACION_MAP() {
    return DEDICACION_MAP;
  }

  constructor(
    private readonly grupoEquipoService: GrupoEquipoService,
    private readonly grupoService: GrupoService,
    private readonly personaService: PersonaService,
    private readonly exportService: MiembrosGruposInvestigacionListadoExportService,
    private dialogService: DialogService,
    private readonly configCnfService: ConfigCnfService,
    protected readonly translate: TranslateService
  ) {
    super();
    this.elementosPagina = [5, 10, 25, 100];
  }

  ngOnInit(): void {
    this.formGroup = new FormGroup({
      grupoNombre: new FormControl(),
      grupoCodigo: new FormControl(),
      grupoProyectoSge: new FormControl(),
      miembroEquipo: new FormControl(),
      miembrosActivos: new FormControl('todos'),
      grupoFechaInicioDesde: new FormControl(),
      grupoFechaInicioHasta: new FormControl(),
      grupoLineaInvestigacion: new FormControl()
    });

    this.initColumns();
    this.initDataSource();

    this.subscriptions.push(
      this.configCnfService.getLimiteRegistrosExportacionExcel('csp-exp-max-num-registros-excel-listado-miembros-grupos-investigacion').subscribe(value => {
        this.limiteRegistrosExportacionExcel = value ? +value : null;
      }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  /**
  * Load table data
  */
  onSearch(): void {
    this.clearProblems();

    const fechaActual = DateTime.now();

    this.subscriptions.push(
      this.grupoEquipoService.findAll(this.getFindOptions()).pipe(
        map(response => response.items.map(miembroGrupo => {
          return {
            dedicacion: miembroGrupo.dedicacion,
            participacion: miembroGrupo.participacion,
            fechaInicioParticipacion: miembroGrupo.fechaInicio,
            fechaFinParticipacion: miembroGrupo.fechaFin,
            rol: miembroGrupo.rol,
            grupo: miembroGrupo.grupo,
            persona: miembroGrupo.persona
          } as IMiembroGrupoInvestigacionListadoData
        })),
        switchMap(miembrosGruposInvestigacion => forkJoin({
          personas: this.getDatosPersonas(miembrosGruposInvestigacion.map(m => m.persona.id)),
          grupos: this.getDatosGrupos(miembrosGruposInvestigacion.map(m => m.grupo.id))
        }).pipe(
          map(({ personas, grupos }) => {
            return miembrosGruposInvestigacion.map(miembroGrupo => {
              miembroGrupo.persona = personas.get(miembroGrupo.persona.id);
              miembroGrupo.grupo = grupos.get(miembroGrupo.grupo.id);
              if (!miembroGrupo.fechaFinParticipacion) {
                miembroGrupo.fechaFinParticipacion = miembroGrupo.grupo.fechaFin;
              }
              if (!miembroGrupo.fechaInicioParticipacion) {
                miembroGrupo.fechaInicioParticipacion = miembroGrupo.grupo.fechaInicio;
              }

              miembroGrupo.activo = (!miembroGrupo.fechaInicioParticipacion || miembroGrupo.fechaInicioParticipacion < fechaActual)
                && (!miembroGrupo.fechaFinParticipacion || miembroGrupo.fechaFinParticipacion > fechaActual);

              return miembroGrupo;
            });
          })
        ))
      ).subscribe(miembrosGrupos => this.dataSource.data = miembrosGrupos)
    );
  }

  private getDatosPersonas(personaRefs: string[]): Observable<Map<string, IPersona>> {
    const personaRefsUnicos = [...new Set<string>(personaRefs)];
    const personaRefsNoCacheadas = personaRefsUnicos.filter(personaRef => !this.mapPersonas.has(personaRef));

    return this.personaService.findAllInBactchesByIdIn(personaRefsNoCacheadas, this.batchSize).pipe(
      tap(personas => personas.forEach(persona => this.mapPersonas.set(persona.id, persona))),
      map(() => this.mapPersonas)
    );
  }

  private getDatosGrupos(grupoIds: number[]): Observable<Map<number, IGrupo>> {
    const grupoIdsUnicos = [...new Set<number>(grupoIds)];
    const grupoIdsNoCacheados = grupoIdsUnicos.filter(id => !this.mapGrupos.has(id));

    return this.grupoService.findTodosInBactchesByIdIn(grupoIdsNoCacheados, this.batchSize).pipe(
      tap(grupos => grupos.forEach(grupo => this.mapGrupos.set(grupo.id, grupo))),
      map(() => this.mapGrupos)
    );
  }

  /**
   * Clean filters
   */
  onClearFilters(): void {
    this.resetFilters();
    this.clearProblems();
    this.dataSource.data = [];
  }

  exportCSV(): void {
    this.export(OutputReport.CSV);
  }

  exportXLSX(): void {
    this.export(OutputReport.XLSX);
  }

  private export(outputType: OutputReport): void {
    if (this.isTotalRegistosGreatherThanLimite(this.dataSource.data.length)) {
      this.dialogService.showInfoDialog(WARN_EXPORT_EXCEL_KEY, { max: this.limiteRegistrosExportacionExcel });
      return;
    }

    this.problems$.next([]);
    this.subscriptions.push(this.exportService.export(this.getReportOptions(outputType)).subscribe(
      () => { },
      ((error: Error) => {
        if (error instanceof SgiError) {
          this.problems$.next([error]);
        } else {
          this.problems$.next([new SgiError(MSG_DOWNLOAD_ERROR)]);
        }
      })
    ));
  }

  private getReportOptions(outputType: OutputReport): IReportConfig<IMiembroGrupoInvestigacionListadoReportOptions> {
    const reportModalData: IReportConfig<IMiembroGrupoInvestigacionListadoReportOptions> = {
      outputType,
      reportOptions: {
        miembrosGruposInvestigacion: this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort)
      }
    };
    return reportModalData;
  }

  private isTotalRegistosGreatherThanLimite(totalRegistrosExportacion: number): boolean {
    return totalRegistrosExportacion && this.limiteRegistrosExportacionExcel && totalRegistrosExportacion > this.limiteRegistrosExportacionExcel;
  }

  private initColumns(): void {
    this.columnas = [
      'nombre',
      'apellidos',
      'email',
      'rol',
      'activo',
      'fechaInicioParticipacion',
      'fechaFinParticipacion',
      'dedicacion',
      'participacion',
      'grupoNombre',
      'grupoCodigo'
    ];
  }

  private initDataSource(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (miembroGrupoInvestigacion: IMiembroGrupoInvestigacionListadoData, property: string) => {
        switch (property) {
          case 'dedicacion':
            return miembroGrupoInvestigacion.dedicacion ? this.translate.instant(DEDICACION_MAP.get(miembroGrupoInvestigacion.dedicacion)) : '';
          case 'nombre':
            return miembroGrupoInvestigacion.persona?.nombre ?? '';
          case 'apellidos':
            return miembroGrupoInvestigacion.persona?.apellidos ?? '';
          case 'email':
            return miembroGrupoInvestigacion.persona?.emails ?? '';
          case 'grupoNombre':
            return miembroGrupoInvestigacion.grupo?.nombre ?? '';
          case 'grupoCodigo':
            return miembroGrupoInvestigacion.grupo?.codigo ?? '';
          default:
            return miembroGrupoInvestigacion[property];
        }
      };
    this.dataSource.sort = this.sort;
  }

  private getFindOptions(): SgiRestFindOptions {
    return {
      filter: this.getFilter()
    };
  }

  private getFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;

    const filters = new RSQLSgiRestFilter('grupo.nombre', SgiRestFilterOperator.LIKE_ICASE, controls.grupoNombre.value)
      .and('grupo.codigo', SgiRestFilterOperator.LIKE_ICASE, controls.grupoCodigo.value)
      .and('grupo.proyectoSgeRef', SgiRestFilterOperator.EQUALS, controls.grupoProyectoSge.value)
      .and('personaRef', SgiRestFilterOperator.EQUALS, controls.miembroEquipo.value?.id)
      .and('grupo.proyectoSgeRef', SgiRestFilterOperator.EQUALS_ICASE, controls.grupoProyectoSge.value)
      .and('adscritoLineaInvestigacion', SgiRestFilterOperator.EQUALS, controls.grupoLineaInvestigacion.value?.id?.toString());

    if (controls.miembrosActivos.value !== 'todos') {
      filters.and('activo', SgiRestFilterOperator.EQUALS, controls.miembrosActivos.value);
    }

    if (!!controls.grupoFechaInicioDesde.value && !!controls.grupoFechaInicioHasta.value) {
      filters.and('grupo.fechaInicio', SgiRestFilterOperator.BETWEEN, [LuxonUtils.toBackend(controls.grupoFechaInicioDesde.value), LuxonUtils.toBackend(controls.grupoFechaInicioHasta.value)]);
    } else if (!!controls.grupoFechaInicioDesde.value && !controls.grupoFechaInicioHasta.value) {
      filters.and('grupo.fechaInicio', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.grupoFechaInicioDesde.value));
    } else if (!controls.grupoFechaInicioDesde.value && !!controls.grupoFechaInicioHasta.value) {
      filters.and('grupo.fechaInicio', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.grupoFechaInicioHasta.value));
    }

    filters.and('grupo.activo', SgiRestFilterOperator.EQUALS, 'true');

    return filters;
  }

  private resetFilters(): void {
    FormGroupUtil.clean(this.formGroup);
    this.formGroup.controls.miembrosActivos.setValue('todos');
  }

}
