import { AfterViewInit, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { ESTADO_MAP, IReparto } from '@core/models/pii/reparto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import {
  RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilter,
  SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult, SgiRestSortDirection
} from '@sgi/framework/http';
import { merge, Observable, of, Subscription } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { InvencionActionService } from '../../invencion.action.service';
import { InvencionRepartosFragment } from './invencion-repartos.fragment';

const MSG_ERROR = marker('error.load');
const REPARTO_KEY = marker('pii.reparto');

@Component({
  selector: 'sgi-invencion-repartos',
  templateUrl: './invencion-repartos.component.html',
  styleUrls: ['./invencion-repartos.component.scss']
})
export class InvencionRepartosComponent extends FragmentComponent implements OnInit, OnDestroy, AfterViewInit {
  ROUTE_NAMES = ROUTE_NAMES;

  private subscriptions: Subscription[] = [];
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  formPart: InvencionRepartosFragment;
  repartos$: Observable<IReparto[]>;

  columnas: string[];
  elementosPagina: number[];
  totalElementos: number;
  filter: SgiRestFilter;
  formGroup: FormGroup;
  msgParamEntity = {};

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get ESTADO_REPARTO_MAP() {
    return ESTADO_MAP;
  }

  constructor(
    public actionService: InvencionActionService,
    private readonly snackBarService: SnackBarService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.REPARTOS, actionService);
    this.formPart = this.fragment as InvencionRepartosFragment;
    this.elementosPagina = [5, 10, 25, 100];
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.totalElementos = 0;
    this.formGroup = this.buildFormGroup();
    this.initColumns();
    this.initFlexProperties();
  }

  ngAfterViewInit(): void {
    // Merge events that trigger load table data
    this.subscriptions.push(merge(
      // Link pageChange event to fire new request
      this.paginator?.page,
      // Link sortChange event to fire new request
      this.sort?.sortChange
    ).pipe(
      tap(() => {
        // Load table
        this.loadTable();
      }),
      catchError(err => {
        return err;
      })
    ).subscribe());
    this.loadTable();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  onSearch(): void {
    this.filter = this.createFilter();
    this.loadTable(true);
  }

  onClearFilters(): void {
    FormGroupUtil.clean(this.formGroup);
    this.formGroup.controls.fechaDesde.setValue(null);
    this.formGroup.controls.fechaHasta.setValue(null);
    this.filter = undefined;
    this.loadTable(true);
  }

  private buildFormGroup(): FormGroup {
    return new FormGroup({
      fechaDesde: new FormControl(),
      fechaHasta: new FormControl(),
      estado: new FormControl(null),
    });
  }

  private getObservableLoadTable(reset?: boolean): Observable<IReparto[]> {
    // Do the request with paginator/sort/filter values
    const observable$ = this.formPart.createObservable(this.getFindOptions());
    return observable$?.pipe(
      map((response: SgiRestListResult<IReparto>) => {
        // Map respose total
        this.totalElementos = response.total;
        // Reset pagination to first page
        if (reset && this.paginator) {
          this.paginator.pageIndex = 0;
        }
        // Return the values
        return response.items;
      }),
      catchError((error) => {
        // On error reset pagination values
        this.paginator?.firstPage();
        this.totalElementos = 0;
        this.showMensajeErrorLoadTable();
        return of([]);
      })
    );
  }

  private getFindOptions(reset?: boolean): SgiRestFindOptions {
    const options: SgiRestFindOptions = {
      page: {
        index: reset ? 0 : this.paginator?.pageIndex,
        size: this.paginator?.pageSize,
      },
      sort: new RSQLSgiRestSort(this.sort?.active, SgiRestSortDirection.fromSortDirection(this.sort?.direction)),
      filter: this.filter,
    };
    return options;
  }

  private showMensajeErrorLoadTable(): void {
    this.snackBarService.showError(MSG_ERROR);
  }

  private initColumns(): void {
    this.columnas = ['fecha', 'importeUniversidad', 'estado', 'acciones'];
  }

  private loadTable(reset?: boolean): void {
    this.repartos$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    return new RSQLSgiRestFilter('fecha', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaDesde.value))
      .and('fecha', SgiRestFilterOperator.LOWER_OR_EQUAL,
        LuxonUtils.toBackend(controls.fechaHasta.value))
      .and('estado', SgiRestFilterOperator.EQUALS, controls.estado.value);
  }

  private initFlexProperties(): void {
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(19%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  private setupI18N(): void {
    this.translate.get(
      REPARTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });
  }
}
