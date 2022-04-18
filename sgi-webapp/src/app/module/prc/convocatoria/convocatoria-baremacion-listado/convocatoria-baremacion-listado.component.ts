import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaBaremacion } from '@core/models/prc/convocatoria-baremacion';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { DialogService } from '@core/services/dialog.service';
import { ConvocatoriaBaremacionService } from '@core/services/prc/convocatoria-baremacion/convocatoria-baremacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { isConvocatoriaBaremacionEditable } from '../convocatoria-baremacion.resolver';

const MSG_ERROR = marker('error.load');
const MSG_BUTTON_NEW = marker('btn.add.entity');
const MSG_REACTIVE = marker('msg.reactivate.entity');
const MSG_SUCCESS_REACTIVE = marker('msg.reactivate.entity.success');
const MSG_ERROR_REACTIVE = marker('error.reactivate.entity');
const MSG_DEACTIVATE = marker('msg.deactivate.entity');
const MSG_ERROR_DEACTIVATE = marker('error.deactivate.entity');
const MSG_SUCCESS_DEACTIVATE = marker('msg.deactivate.entity.success');
const CONVOCATORIA_BAREMACION_KEY = marker('prc.convocatoria');

@Component({
  selector: 'sgi-convocatoria-baremacion-listado',
  templateUrl: './convocatoria-baremacion-listado.component.html',
  styleUrls: ['./convocatoria-baremacion-listado.component.scss']
})
export class ConvocatoriaBaremacionListadoComponent extends AbstractTablePaginationComponent<IConvocatoriaBaremacion> implements OnInit {
  ROUTE_NAMES = ROUTE_NAMES;
  fxLayoutProperties: FxLayoutProperties;

  textoCrear: string;
  textoDesactivar: string;
  textoReactivar: string;
  textoErrorDesactivar: string;
  textoSuccessDesactivar: string;
  textoSuccessReactivar: string;
  textoErrorReactivar: string;

  convocatoriasBaremacion$: Observable<IConvocatoriaBaremacion[]>;

  constructor(
    private readonly dialogService: DialogService,
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly convocatoriaBaremacionService: ConvocatoriaBaremacionService,
    private readonly translate: TranslateService,
  ) {
    super(snackBarService, MSG_ERROR);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.initFlexProperties();
    this.initFormGroup();
  }

  private initFormGroup(): void {
    this.formGroup = new FormGroup({
      nombre: new FormControl(''),
      anio: new FormControl(''),
      activo: new FormControl('true'),
    });
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IConvocatoriaBaremacion>> {
    return this.convocatoriaBaremacionService.findTodos(this.getFindOptions(reset));
  }

  protected initColumns(): void {
    this.columnas = ['nombre', 'anio', 'importeTotal', 'activo', 'fechaFinEjecucion', 'acciones'];
  }

  protected loadTable(reset?: boolean): void {
    this.convocatoriasBaremacion$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;

    const filter = new RSQLSgiRestFilter('nombre', SgiRestFilterOperator.LIKE_ICASE, controls.nombre.value)
      .and('anio', SgiRestFilterOperator.EQUALS, controls.anio.value);
    if (controls.activo.value !== 'todos') {
      filter.and('activo', SgiRestFilterOperator.EQUALS, controls.activo.value);
    }

    return filter;
  }

  onClearFilters() {
    super.onClearFilters();
    this.formGroup.controls.activo.setValue('true');

    this.onSearch();
  }

  deactivate(convocatoriaBaremacion: IConvocatoriaBaremacion): void {
    const subcription = this.dialogService.showConfirmation(this.textoDesactivar)
      .pipe(switchMap((accept) => {
        if (accept) {
          return this.convocatoriaBaremacionService.desactivar(convocatoriaBaremacion.id);
        } else {
          return of();
        }
      })).subscribe(
        () => {
          this.snackBarService.showSuccess(this.textoSuccessDesactivar);
          this.loadTable();
        },
        (error) => {
          this.logger.error(error);
          if (error instanceof SgiError) {
            this.snackBarService.showError(error);
          }
          else {
            this.snackBarService.showError(this.textoErrorDesactivar);
          }
        }
      );
    this.suscripciones.push(subcription);
  }

  activate(convocatoriaBaremacion: IConvocatoriaBaremacion): void {
    const subcription = this.dialogService.showConfirmation(this.textoReactivar)
      .pipe(switchMap((accept) => {
        if (accept) {
          return this.convocatoriaBaremacionService.activar(convocatoriaBaremacion.id);
        } else {
          return of();
        }
      })).subscribe(
        () => {
          this.snackBarService.showSuccess(this.textoSuccessReactivar);
          this.loadTable();
        },
        (error) => {
          this.logger.error(error);
          if (error instanceof SgiError) {
            this.snackBarService.showError(error);
          }
          else {
            this.snackBarService.showError(this.textoErrorReactivar);
          }
        }
      );
    this.suscripciones.push(subcription);
  }

  isConvocatoriaEditable(convocatoriaBaremacion: IConvocatoriaBaremacion): boolean {
    return isConvocatoriaBaremacionEditable(convocatoriaBaremacion);
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_BAREMACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_NEW,
          { entity: value }
        );
      })
    ).subscribe((value) => this.textoCrear = value);

    this.translate.get(
      CONVOCATORIA_BAREMACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDesactivar = value);

    this.translate.get(
      CONVOCATORIA_BAREMACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoErrorDesactivar = value);

    this.translate.get(
      CONVOCATORIA_BAREMACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoSuccessDesactivar = value);

    this.translate.get(
      CONVOCATORIA_BAREMACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoReactivar = value);

    this.translate.get(
      CONVOCATORIA_BAREMACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoSuccessReactivar = value);

    this.translate.get(
      CONVOCATORIA_BAREMACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoErrorReactivar = value);
  }

  private initFlexProperties(): void {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '1%';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }
}
