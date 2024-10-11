import { AfterViewInit, Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogCommonComponent } from '@core/component/dialog-common.component';
import { SearchModalData } from '@core/component/select-dialog/select-dialog.component';
import { MSG_PARAMS } from '@core/i18n';
import { IPersona } from '@core/models/sgp/persona';
import { ConfigService } from '@core/services/cnf/config.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilter, SgiRestFilterOperator, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, Observable, from, merge, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, tap, toArray } from 'rxjs/operators';
import { ACTION_MODAL_MODE } from 'src/app/esb/shared/formly-forms/core/base-formly-modal.component';
import { IPersonaFormlyData, PersonaFormlyModalComponent } from '../../../formly-forms/persona-formly-modal/persona-formly-modal.component';

const TIPO_PERSONA_KEY = marker('sgp.persona');
const MSG_SAVE_SUCCESS = marker('msg.save.request.entity.success');
const MSG_UPDATE_SUCCESS = marker('msg.update.request.entity.success');

export interface SearchPersonaModalData extends SearchModalData {
  tipoColectivo: string;
  colectivos: string[];
  selectionDisableWith?: (persona: IPersona) => boolean;
}

@Component({
  templateUrl: './search-persona.component.html',
  styleUrls: ['./search-persona.component.scss']
})
export class SearchPersonaModalComponent extends DialogCommonComponent implements OnInit, AfterViewInit {

  formGroup: FormGroup;

  displayedColumns = ['nombre', 'apellidos', 'email', 'entidad', 'acciones'];
  elementosPagina = [5, 10, 25, 100];
  totalElementos = 0;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  personas$: Observable<IPersona[]> = of();
  isInvestigador: boolean;
  msgParamEntity: {};
  private textoCrearSuccess: string;
  private textoUpdateSuccess: string;
  private sgpAlta: boolean = true;

  get selectionDisableWith() {
    return this._selectionDisableWith;
  }

  get sgpAltaDisabled(): boolean {
    return !this.sgpAlta;
  }
  // tslint:disable-next-line: variable-name
  private _selectionDisableWith: (persona: IPersona) => boolean = () => false;

  constructor(
    private readonly logger: NGXLogger,
    private readonly snackBarService: SnackBarService,
    public dialogRef: MatDialogRef<SearchPersonaModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SearchPersonaModalData,
    private personaService: PersonaService,
    private empresaService: EmpresaService,
    private readonly translate: TranslateService,
    private readonly authService: SgiAuthService,
    private personaCreateMatDialog: MatDialog,
    private configService: ConfigService
  ) {
    super(dialogRef);
    if (!!data.selectionDisableWith) {
      this._selectionDisableWith = data.selectionDisableWith;
    }

    this.subscriptions.push(this.configService.isAltaSgpEnabled().subscribe(value => {
      this.sgpAlta = value;
    }));
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.formGroup = new FormGroup({
      datosPersona: new FormControl(this.data.searchTerm)
    });
    this.isInvestigador = this.authService.hasAuthority('CSP-SOL-INV-ER');
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      TIPO_PERSONA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      TIPO_PERSONA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SAVE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrearSuccess = value);

    this.translate.get(
      TIPO_PERSONA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_UPDATE_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoUpdateSuccess = value);

  }

  ngAfterViewInit(): void {
    super.ngAfterViewInit();
    merge(
      this.paginator.page,
      this.sort.sortChange
    ).pipe(
      tap(() => this.search())
    ).subscribe();

    if (this.data.searchTerm) {
      this.search();
    }
  }

  closeModal(persona?: IPersona): void {
    this.dialogRef.close(persona);
  }

  search(reset?: boolean) {
    this.clearProblems();
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
        switchMap(response =>
          from(response.items).pipe(
            mergeMap(persona => {
              if (persona.entidad) {
                return this.empresaService.findById(persona.entidad.id).pipe(
                  map(empresa => {
                    persona.entidad = empresa;
                    return persona;
                  }),
                  catchError((error) => {
                    this.logger.error(error);
                    this.processError(error);
                    return EMPTY;
                  })
                );
              }
              return of(persona);
            }),
            mergeMap(persona => {
              if (persona.entidadPropia) {
                return this.empresaService.findById(persona.entidadPropia.id).pipe(
                  map(empresa => {
                    persona.entidadPropia = empresa;
                    return persona;
                  }),
                  catchError((error) => {
                    this.logger.error(error);
                    this.processError(error);
                    return EMPTY;
                  })
                );
              }
              return of(persona);
            }),
            toArray(),
            map(() => {
              return response;
            })
          )
        ),
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
          this.processError(error);
          return of([]);
        })
      );
  }

  /**
   * Clean filters an reload the table
   */
  onClearFilters(): void {
    this.formGroup.reset();
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

    const rsqlFilter = new RSQLSgiRestFilter(
      new RSQLSgiRestFilter('nombreApellidos', SgiRestFilterOperator.LIKE_ICASE, controls.datosPersona.value)
        .or('email', SgiRestFilterOperator.LIKE_ICASE, controls.datosPersona.value)
    );

    if (filter?.colectivos?.length) {
      rsqlFilter.and('colectivoId', SgiRestFilterOperator.IN, filter.colectivos);
    } else if (filter?.tipoColectivo) {
      rsqlFilter.and('tipoColectivo', SgiRestFilterOperator.EQUALS, filter.tipoColectivo);
    }

    return rsqlFilter;
  }

  openPersonaCreateModal(): void {
    this.openPersonaFormlyModal(ACTION_MODAL_MODE.NEW, null, this.textoCrearSuccess);
  }

  openPersonaEditModal(persona: IPersona): void {
    this.openPersonaFormlyModal(ACTION_MODAL_MODE.EDIT, persona, this.textoUpdateSuccess);
  }

  openPersonaViewModal(persona: IPersona): void {
    this.openPersonaFormlyModal(ACTION_MODAL_MODE.VIEW, persona);
  }

  private openPersonaFormlyModal(modalAction: ACTION_MODAL_MODE, personaItem?: IPersona, textoActionSuccess?: string): void {
    const personaData: IPersonaFormlyData = {
      personaId: personaItem ? personaItem.id : undefined,
      action: modalAction
    };

    const config = {
      panelClass: 'sgi-dialog-container',
      data: personaData
    };
    const dialogRef = this.personaCreateMatDialog.open(PersonaFormlyModalComponent, config);

    dialogRef.afterClosed().subscribe(
      (response) => {
        if (response?.createdOrUpdated) {
          this.snackBarService.showSuccess(textoActionSuccess);
          if (!!response.persona) {
            this.closeModal(response.persona);
          }
        }
      }
    );

  }
}
