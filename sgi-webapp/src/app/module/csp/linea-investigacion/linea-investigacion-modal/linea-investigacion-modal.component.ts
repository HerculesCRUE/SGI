import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogActionComponent } from '@core/component/dialog-action.component';
import { SearchResult } from '@core/component/select-dialog/select-dialog.component';
import { MSG_PARAMS } from '@core/i18n';
import { ILineaInvestigacion } from '@core/models/csp/linea-investigacion';
import { LineaInvestigacionService } from '@core/services/csp/linea-investigacion/linea-investigacion.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { catchError, debounceTime, map, startWith, switchMap } from 'rxjs/operators';

const LINEA_INVESTIGACION_KEY = marker('csp.linea-investigacion');
const LINEA_INVESTIGACION_NOMBRE_KEY = marker('csp.linea-investigacion.nombre');
const TITLE_NEW_ENTITY = marker('title.new.entity');

@Component({
  templateUrl: './linea-investigacion-modal.component.html',
  styleUrls: ['./linea-investigacion-modal.component.scss']
})
export class LineaInvestigacionModalComponent extends DialogActionComponent<ILineaInvestigacion> implements OnInit, OnDestroy {

  private readonly lineaInvestigacion: ILineaInvestigacion;
  title: string;
  msgParamNombreEntity = {};

  readonly searchResult$: Subject<ILineaInvestigacion[]> = new BehaviorSubject<ILineaInvestigacion[]>([]);

  constructor(
    matDialogRef: MatDialogRef<LineaInvestigacionModalComponent>,
    @Inject(MAT_DIALOG_DATA) data: ILineaInvestigacion,
    private readonly lineaInvestigacionService: LineaInvestigacionService,
    private readonly translate: TranslateService
  ) {
    super(matDialogRef, !!data?.id);

    if (this.isEdit()) {
      this.lineaInvestigacion = { ...data };
    } else {
      this.lineaInvestigacion = { activo: true } as ILineaInvestigacion;
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      LINEA_INVESTIGACION_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.isEdit()) {
      this.translate.get(
        LINEA_INVESTIGACION_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        LINEA_INVESTIGACION_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
          );
        })
      ).subscribe((value) => this.title = value);
    }
  }

  protected getValue(): ILineaInvestigacion {
    this.lineaInvestigacion.nombre = this.formGroup.controls.nombre.value;
    return this.lineaInvestigacion;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      nombre: new FormControl(this.lineaInvestigacion?.nombre ?? '', [Validators.maxLength(1000), Validators.required]),
    });

    this.subscriptions.push(formGroup.controls.nombre.valueChanges.pipe(
      startWith(''),
      debounceTime(200),
      switchMap(value => this.search(value))
    ).subscribe(
      (response => {
        this.searchResult$.next(response.items);
      })
    ));

    return formGroup;
  }

  protected saveOrUpdate(): Observable<ILineaInvestigacion> {
    const lineaInvestigacion = this.getValue();
    return this.isEdit() ? this.lineaInvestigacionService.update(lineaInvestigacion.id, lineaInvestigacion) :
      this.lineaInvestigacionService.create(lineaInvestigacion);
  }

  private search(value: string): Observable<SearchResult<ILineaInvestigacion>> {
    if (value?.length >= 3) {
      const findOptions: SgiRestFindOptions = {
        filter: new RSQLSgiRestFilter('nombre', SgiRestFilterOperator.LIKE_ICASE, value),
        sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC)
      };
      return this.lineaInvestigacionService.findAll(findOptions).pipe(
        map(response => {
          return {
            items: response.items.slice(0, 5).map(lineaInvestigacion => {
              return lineaInvestigacion;
            }),
            more: false
          };
        }),
        catchError(() => this.buildEmptyResponse())
      );
    }
    else {
      return this.buildEmptyResponse();
    }
  }

  private buildEmptyResponse(): Observable<SearchResult<ILineaInvestigacion>> {
    return of({
      items: [],
      more: false
    });
  }
}
