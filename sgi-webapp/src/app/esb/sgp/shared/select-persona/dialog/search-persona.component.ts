import { F } from '@angular/cdk/keycodes';
import { AfterViewInit, Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IPersona } from '@core/models/sgp/persona';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilter, SgiRestFilterOperator, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable, of } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { ACTION_MODAL_MODE } from 'src/app/esb/shared/formly-forms/core/base-formly-modal.component';
import { IPersonaFormlyData, PersonaFormlyModalComponent } from '../../../formly-forms/persona-formly-modal/persona-formly-modal.component';

const MSG_LISTADO_ERROR = marker('error.load');
const TIPO_PERSONA_KEY = marker('sgp.persona');

export interface SearchPersonaModalData {
  tipoColectivo: string;
  colectivos: string[];
}

class JoinRSQLSgiRestFilter implements SgiRestFilter {

  private readonly filter1: SgiRestFilter;
  private readonly filter2: SgiRestFilter;

  constructor(filter1: SgiRestFilter, filter2: SgiRestFilter) {
    this.filter1 = filter1;
    this.filter2 = filter2;
  }

  and(field: string, operator: SgiRestFilterOperator, value: string | string[]): SgiRestFilter {
    throw new Error('Method not implemented.');
  }
  or(field: string, operator: SgiRestFilterOperator, value: string | string[]): SgiRestFilter {
    throw new Error('Method not implemented.');
  }
  contains(field: string): boolean {
    throw new Error('Method not implemented.');
  }
  remove(field: string): void {
    throw new Error('Method not implemented.');
  }

  public toString(): string {
    const strFilter1 = this.filter1.toString() !== '' ? '(' + this.filter1.toString() + ');' : '';
    const strFilter2 = this.filter2.toString() !== '' ? '(' + this.filter2.toString() + ')' : '';
    const strJoinFilters = strFilter1 + strFilter2;
    return strJoinFilters;
  }
}

@Component({
  templateUrl: './search-persona.component.html',
  styleUrls: ['./search-persona.component.scss']
})
export class SearchPersonaModalComponent implements OnInit, AfterViewInit {

  formGroup: FormGroup;

  displayedColumns = ['nombre', 'apellidos', 'numeroDocumento', 'entidad', 'acciones'];
  elementosPagina = [5, 10, 25, 100];
  totalElementos = 0;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  personas$: Observable<IPersona[]> = of();

  msgParamEntity: {};

  constructor(
    private readonly logger: NGXLogger,
    public dialogRef: MatDialogRef<SearchPersonaModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SearchPersonaModalData,
    private personaService: PersonaService,
    private empresaService: EmpresaService,
    private snackBarService: SnackBarService,
    private readonly translate: TranslateService,
    private personaCreateMatDialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.formGroup = new FormGroup({
      datosPersona: new FormControl()
    });
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      TIPO_PERSONA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });
  }

  ngAfterViewInit(): void {
    this.search(true);

    merge(
      this.paginator.page,
      this.sort.sortChange
    ).pipe(
      tap(() => this.search())
    ).subscribe();
  }

  closeModal(persona?: IPersona): void {
    this.dialogRef.close(persona);
  }

  search(reset?: boolean) {
    this.personas$ = this.personaService
      .findAll(
        {
          page: {
            index: reset ? 0 : this.paginator.pageIndex,
            size: this.paginator.pageSize
          },
          sort: new RSQLSgiRestSort(this.sort?.active, SgiRestSortDirection.fromSortDirection(this.sort?.direction)),
          filter: this.buildFilter(this.dialogRef.componentInstance.data)
        }
      )
      .pipe(
        switchMap(response => {
          const requestsEmpresa: Observable<IPersona>[] = [];
          response.items.forEach(persona => {
            if (persona.entidad) {
              requestsEmpresa.push(this.empresaService.findById(persona.entidad.id).pipe(
                map(empresa => {
                  persona.entidad = empresa;
                  return persona;
                })
              ));
            } else {
              requestsEmpresa.push(of(persona));
            }
          });
          return of(response).pipe(
            tap(() => merge(...requestsEmpresa).subscribe())
          );
        }),
        map((response) => {
          // Map response total
          this.totalElementos = response.total;
          // Reset pagination to first page
          if (reset) {
            this.paginator.pageIndex = 0;
          }
          // Return the values
          return response.items;
        }),
        catchError((error) => {
          this.logger.error(error);
          // On error reset pagination values
          this.paginator.firstPage();
          this.totalElementos = 0;
          this.snackBarService.showError(MSG_LISTADO_ERROR);
          return of([]);
        })
      );
  }

  /**
   * Clean filters an reload the table
   */
  onClearFilters(): void {
    this.formGroup.reset();
    this.search(true);
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  /**
   * Recupera el filtro resultante de componer los filtros recibidos y los filtros del formulario
   *
   * @param filter filtros que se quieren aplicar
   * @returns el filtro
   */
  private buildFilter(filter: SearchPersonaModalData): SgiRestFilter {
    const controls = this.formGroup.controls;

    let rsqlFilterColectivo = null;

    const rsqlFilter = new RSQLSgiRestFilter('nombre', SgiRestFilterOperator.LIKE_ICASE, controls.datosPersona.value)
      .or('apellidos', SgiRestFilterOperator.LIKE_ICASE, controls.datosPersona.value)
      .or('numeroDocumento', SgiRestFilterOperator.LIKE_ICASE, controls.datosPersona.value);

    if (filter?.colectivos?.length) {
      rsqlFilterColectivo = new RSQLSgiRestFilter('colectivoId', SgiRestFilterOperator.IN, filter.colectivos);
    } else if (filter?.tipoColectivo) {
      rsqlFilterColectivo = new RSQLSgiRestFilter('tipoColectivo', SgiRestFilterOperator.EQUALS, filter.tipoColectivo);
    }

    if (rsqlFilterColectivo) {
      return new JoinRSQLSgiRestFilter(rsqlFilter, rsqlFilterColectivo);
    } else {
      return rsqlFilter;
    }
  }

  openPersonaCreateModal(): void {
    this.openPersonaFormlyModal(ACTION_MODAL_MODE.NEW);
  }

  openPersonaEditModal(persona: IPersona): void {
    this.openPersonaFormlyModal(ACTION_MODAL_MODE.EDIT, persona);
  }

  openPersonaViewModal(persona: IPersona): void {
    this.openPersonaFormlyModal(ACTION_MODAL_MODE.VIEW, persona);
  }

  private openPersonaFormlyModal(modalAction: ACTION_MODAL_MODE, personaItem?: IPersona): void {
    const personaData: IPersonaFormlyData = {
      personaId: personaItem ? personaItem.id : undefined,
      action: modalAction
    };

    const config = {
      panelClass: 'sgi-dialog-container',
      data: personaData
    };
    this.personaCreateMatDialog.open(PersonaFormlyModalComponent, config);
  }

}
