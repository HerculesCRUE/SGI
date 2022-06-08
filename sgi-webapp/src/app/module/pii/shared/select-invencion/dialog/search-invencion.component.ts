import { AfterViewInit, Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogCommonComponent } from '@core/component/dialog-common.component';
import { SearchModalData } from '@core/component/select-dialog/select-dialog.component';
import { MSG_PARAMS } from '@core/i18n';
import { IInvencion } from '@core/models/pii/invencion';
import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { Module } from '@core/module';
import { ROUTE_NAMES } from '@core/route.names';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { TipoProteccionService } from '@core/services/pii/tipo-proteccion/tipo-proteccion.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { merge, Observable, Subject } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { PII_ROUTE_NAMES } from '../../../pii-route-names';

const ENTITY_KEY = marker('pii.invencion');

export interface SearchInvencionModalData extends SearchModalData {

}

@Component({
  selector: 'sgi-search-invencion',
  templateUrl: './search-invencion.component.html',
  styleUrls: ['./search-invencion.component.scss']
})
export class SearchInvencionModalComponent extends DialogCommonComponent implements OnInit, AfterViewInit {
  formGroup: FormGroup;
  displayedColumns = ['id', 'fechaComunicacion', 'titulo', 'acciones'];
  elementosPagina = [5, 10, 25, 100];
  totalElementos = 0;
  @ViewChild(MatSort, { static: true }) private sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) private paginator: MatPaginator;

  readonly invenciones$ = new Subject<IInvencion[]>();
  readonly tiposProteccion$: Observable<ITipoProteccion[]>;

  msgParamEntity: {};

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    dialogRef: MatDialogRef<SearchInvencionModalComponent, IInvencion>,
    @Inject(MAT_DIALOG_DATA) public data: SearchInvencionModalData,
    private readonly invencionService: InvencionService,
    readonly tipoProteccionService: TipoProteccionService,
    private readonly translate: TranslateService,
    private readonly router: Router
  ) {
    super(dialogRef);
    this.tiposProteccion$ = tipoProteccionService.findAll().pipe(map(({ items }) => items));
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.formGroup = new FormGroup({
      id: new FormControl(),
      fechaComunicacionDesde: new FormControl(),
      fechaComunicacionHasta: new FormControl(),
      titulo: new FormControl(this.data.searchTerm),
      tipoProteccion: new FormControl(),
    });
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      ENTITY_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

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

  /**
   * Clean filters an reload the table
   */
  onClearFilters(): void {
    this.formGroup.reset();
    this.search(true);
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

    this.invencionService.findAll(options).subscribe(result => {
      this.totalElementos = result.total;
      this.invenciones$.next(result.items);
    });
  }

  private buildFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;

    return new RSQLSgiRestFilter('id', SgiRestFilterOperator.EQUALS, controls.id.value)
      .and('fechaComunicacion', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaComunicacionDesde.value))
      .and('fechaComunicacion', SgiRestFilterOperator.LOWER_OR_EQUAL,
        LuxonUtils.toBackend(controls.fechaComunicacionHasta.value?.plus({ hour: 23, minutes: 59, seconds: 59 })))
      .and('titulo', SgiRestFilterOperator.LIKE_ICASE, controls.titulo.value)
      .and('tipoProteccion', SgiRestFilterOperator.EQUALS, controls.tipoProteccion.value?.id?.toString());;
  }

  openCreate(): void {
    window.open(this.router.serializeUrl(this.router.createUrlTree(['/', Module.PII.path, PII_ROUTE_NAMES.INVENCION, ROUTE_NAMES.NEW])), '_blank');
  }
}
