import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { MSG_PARAMS } from '@core/i18n';
import { IComite } from '@core/models/eti/comite';
import { IConvocatoriaReunion } from '@core/models/eti/convocatoria-reunion';
import { TipoConvocatoriaReunion } from '@core/models/eti/tipo-convocatoria-reunion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { DialogService } from '@core/services/dialog.service';
import { ComiteService } from '@core/services/eti/comite.service';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { TipoConvocatoriaReunionService } from '@core/services/eti/tipo-convocatoria-reunion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { Observable, of, Subscription } from 'rxjs';
import { map, startWith, switchMap } from 'rxjs/operators';

const MSG_BUTTON_NEW = marker('btn.add.entity');
const MSG_ERROR = marker('error.load');
const MSG_CONFIRMATION_DELETE = marker('msg.delete.entity');
const MSG_SUCCESS_DELETE = marker('msg.delete.entity.success');
const CONVOCATORIA_REUNION_KEY = marker('eti.convocatoria-reunion');
const MSG_SUCCESS_ENVIADO = marker('msg.envio-comunicado.entity.success');
const MSG_ERROR_ENVIADO = marker('msg.envio-comunicado.entity.error');

@Component({
  selector: 'sgi-convocatoria-reunion-listado',
  templateUrl: './convocatoria-reunion-listado.component.html',
  styleUrls: ['./convocatoria-reunion-listado.component.scss']
})
export class ConvocatoriaReunionListadoComponent
  extends AbstractTablePaginationComponent<IConvocatoriaReunion> implements OnInit, OnDestroy {
  ROUTE_NAMES = ROUTE_NAMES;

  FormGroupUtil = FormGroupUtil;

  displayedColumns: string[];
  totalElementos: number;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  textoCrear: string;
  textoDelete: string;
  textoDeleteSuccess: string;
  textoEnviadoSuccess: string;
  textoEnviadoError: string;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  comiteListado: IComite[];
  filteredComites: Observable<IComite[]>;

  tipoConvocatoriaReunionListado: TipoConvocatoriaReunion[];
  filteredTiposConvocatoriaReunion: Observable<TipoConvocatoriaReunion[]>;

  convocatoriaReunion$: Observable<IConvocatoriaReunion[]> = of();
  private dialogSubscription: Subscription;
  private convocatoriaReunionDeleteSubscription: Subscription;
  private comitesSubscription: Subscription;
  private tiposConvocatoriaReunionSubscription: Subscription;

  mapEliminable: Map<number, boolean> = new Map();
  mapModificable: Map<number, boolean> = new Map();

  constructor(
    private readonly convocatoriaReunionService: ConvocatoriaReunionService,
    private readonly dialogService: DialogService,
    protected readonly snackBarService: SnackBarService,
    private readonly comiteService: ComiteService,
    private readonly tipoConvocatoriaReunionService: TipoConvocatoriaReunionService,
    private formBuilder: FormBuilder,
    private readonly translate: TranslateService
  ) {

    super(snackBarService, MSG_ERROR);

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(25%-10px)';
    this.fxFlexProperties.md = '0 1 calc(25%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(15%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.totalElementos = 0;
  }


  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IConvocatoriaReunion>> {
    const observable$ = this.convocatoriaReunionService.findAll(this.getFindOptions(reset)).pipe(
      map(response => {
        const convocatorias = response.items;
        convocatorias.forEach(convocatoriaReunion => {
          this.suscripciones.push(this.convocatoriaReunionService.eliminable(convocatoriaReunion.id).subscribe((value) => {
            this.mapEliminable.set(convocatoriaReunion.id, value);
          }));
          this.suscripciones.push(this.convocatoriaReunionService.modificable(convocatoriaReunion.id).subscribe((value) => {
            this.mapModificable.set(convocatoriaReunion.id, value);
          }));
        });
        return response as SgiRestListResult<IConvocatoriaReunion>;
      })
    );
    return observable$;
  }
  protected initColumns(): void {
    this.displayedColumns = ['comite', 'fechaEvaluacion', 'codigo', 'horaInicio', 'horaInicioSegunda', 'lugar', 'tipoConvocatoriaReunion', 'fechaEnvio', 'acciones'];
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('comite.id', SgiRestFilterOperator.EQUALS, controls.comite.value?.id?.toString())
      .and('tipoConvocatoriaReunion.id', SgiRestFilterOperator.EQUALS, controls.tipoConvocatoriaReunion.value?.id?.toString())
      .and('fechaEvaluacion', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaEvaluacionDesde.value))
      .and('fechaEvaluacion', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaEvaluacionHasta.value));

    return filter;
  }

  onClearFilters(): void {
    super.onClearFilters();
    this.formGroup.controls.fechaEvaluacionDesde.setValue(null);
    this.formGroup.controls.fechaEvaluacionHasta.setValue(null);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    // Inicializa el formulario de busqueda
    this.formGroup = this.formBuilder.group({
      comite: new FormControl('', []),
      tipoConvocatoriaReunion: new FormControl('', []),
      fechaEvaluacionDesde: new FormControl(null, []),
      fechaEvaluacionHasta: new FormControl(null, [])
    });

    // Recupera los valores de los combos
    this.loadComites();
    this.loadTiposConvocatoriaReunion();
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_REUNION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_NEW,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoCrear = value);

    this.translate.get(
      CONVOCATORIA_REUNION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_CONFIRMATION_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);

    this.translate.get(
      CONVOCATORIA_REUNION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDeleteSuccess = value);

    this.translate.get(
      CONVOCATORIA_REUNION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_ENVIADO,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoEnviadoSuccess = value);

    this.translate.get(
      CONVOCATORIA_REUNION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_ENVIADO,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoEnviadoError = value);
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
    this.dialogSubscription?.unsubscribe();
    this.convocatoriaReunionDeleteSubscription?.unsubscribe();
    this.comitesSubscription?.unsubscribe();
    this.tiposConvocatoriaReunionSubscription?.unsubscribe();
  }

  /**
   * Recupera un listado de los comites que hay en el sistema.
   */
  private loadComites(): void {
    this.comitesSubscription = this.comiteService.findAll().subscribe(
      (response) => {
        this.comiteListado = response.items;

        this.filteredComites = this.formGroup.controls.comite.valueChanges
          .pipe(
            startWith(''),
            map(value => this._filterComite(value))
          );
      });
  }

  /**
   * Recupera un listado de los tipos de convocatoria reunion que hay en el sistema.
   */
  private loadTiposConvocatoriaReunion(): void {
    this.tiposConvocatoriaReunionSubscription = this.tipoConvocatoriaReunionService.findAll().subscribe(
      (response) => {
        this.tipoConvocatoriaReunionListado = response.items;

        this.filteredTiposConvocatoriaReunion = this.formGroup.controls.tipoConvocatoriaReunion.valueChanges
          .pipe(
            startWith(''),
            map(value => this._filterTipoConvocatoriaReunion(value))
          );
      });
  }

  /**
   * Devuelve el nombre del comite
   * @param comite comite
   *
   * @returns nombre del comite
   */
  getComite(comite: IComite): string {
    return comite?.comite;
  }

  /**
   * Devuelve el nombre del tipo de convocatoria reunion
   * @param tipoConvocatoriaReunion tipo convocatoria reunion
   *
   * @returns nombre del tipo de convocatoria reunion
   */
  getTipoConvocatoriaReunion(tipoConvocatoriaReunion: TipoConvocatoriaReunion): string {
    return tipoConvocatoriaReunion?.nombre;
  }


  /**
   * Filtro de campo autocompletable comite.
   * @param value value a filtrar (string o Comite.
   * @returns lista de comites filtrada.
   */
  private _filterComite(value: string | IComite): IComite[] {
    if (!value) {
      return this.comiteListado;
    }

    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.comite.toLowerCase();
    }

    return this.comiteListado.filter
      (comite => comite.comite.toLowerCase().includes(filterValue));
  }

  /**
   * Filtro de campo autocompletable tipo convocatoria reunion.
   * @param value value a filtrar (string o TipoConvocatoriaReunion).
   * @returns lista de tipos de convocatoria reunion filtrada.
   */
  private _filterTipoConvocatoriaReunion(value: string | TipoConvocatoriaReunion): TipoConvocatoriaReunion[] {
    if (!value) {
      return this.tipoConvocatoriaReunionListado;
    }

    let filterValue: string;
    if (typeof value === 'string') {
      filterValue = value.toLowerCase();
    } else {
      filterValue = value.nombre.toLowerCase();
    }

    return this.tipoConvocatoriaReunionListado.filter
      (tipoConvocatoriaReunion => tipoConvocatoriaReunion.nombre.toLowerCase().includes(filterValue));
  }

  protected loadTable(reset?: boolean) {
    this.convocatoriaReunion$ = this.getObservableLoadTable(reset);
  }

  /**
   * Elimina la convocatoria reunion.
   * @param convocatoriaReunionId id de la convocatoria reunion a eliminar.
   * @param event evento lanzado.
   */
  borrar(convocatoriaReunionId: number, $event: Event): void {
    $event.stopPropagation();
    $event.preventDefault();

    this.dialogSubscription = this.dialogService.showConfirmation(
      this.textoDelete
    ).subscribe(
      (aceptado) => {
        if (aceptado) {
          this.convocatoriaReunionDeleteSubscription = this.convocatoriaReunionService
            .deleteById(convocatoriaReunionId).pipe(
              map(() => {
                return this.loadTable();
              })
            ).subscribe(() => {
              this.snackBarService.showSuccess(this.textoDeleteSuccess);
            });
        }
        aceptado = false;
      });
  }

  /**
   * Envia la convocatoria reunion.
   * @param convocatoriaReunionId id de la convocatoria reunion a enviar.
   * @param event evento lanzado.
   */
  enviar(convocatoriaReunionId: number, $event: Event): void {
    this.convocatoriaReunionService.enviarComunicado(convocatoriaReunionId).subscribe(
      (response) => {
        if (response) {
          this.snackBarService.showSuccess(this.textoEnviadoSuccess);
          this.loadTable();
        } else {
          this.snackBarService.showError(this.textoEnviadoError);
        }
      },
      (error) => {
        this.snackBarService.showError(this.textoEnviadoError);
      }
    );
  }
}
