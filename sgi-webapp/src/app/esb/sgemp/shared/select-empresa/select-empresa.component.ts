import { FocusMonitor } from '@angular/cdk/a11y';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, Inject, Input, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatFormField, MatFormFieldControl, MAT_FORM_FIELD } from '@angular/material/form-field';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SearchResult, SelectDialogComponent } from '@core/component/select-dialog/select-dialog.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SearchEmpresaModalComponent, SearchEmpresaModalData } from './dialog/search-empresa.component';

const SGEMP_NOT_FOUND = marker("error.sgemp.not-found");

@Component({
  selector: 'sgi-select-empresa',
  templateUrl: '../../../../core/component/select-dialog/select-dialog.component.html',
  styleUrls: ['../../../../core/component/select-dialog/select-dialog.component.scss'],
  // tslint:disable-next-line: no-inputs-metadata-property
  inputs: ['disabled', 'disableRipple'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  // tslint:disable-next-line: no-host-metadata-property
  host: {
    role: 'search',
    'aria-autocomplete': 'none',
    '[attr.id]': 'id',
    '[attr.aria-label]': 'ariaLabel || null',
    '[attr.aria-required]': 'required.toString()',
    '[attr.aria-disabled]': 'disabled.toString()',
    '[attr.aria-invalid]': 'errorState',
    '[attr.aria-describedby]': 'ariaDescribedby || null',
    '(keydown)': 'handleKeydown($event)',
  },
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectEmpresaComponent
    }
  ],
})
export class SelectEmpresaComponent extends SelectDialogComponent<SearchEmpresaModalComponent, IEmpresa> {

  @Input()
  selectedEmpresas: IEmpresa[];

  constructor(
    changeDetectorRef: ChangeDetectorRef,
    elementRef: ElementRef,
    @Optional() @Inject(MAT_FORM_FIELD) parentFormField: MatFormField,
    @Self() @Optional() ngControl: NgControl,
    dialog: MatDialog,
    focusMonitor: FocusMonitor,
    private empresaService: EmpresaService,
    private readonly translate: TranslateService
  ) {

    super(changeDetectorRef, elementRef, parentFormField, ngControl, dialog, SearchEmpresaModalComponent, focusMonitor);
    this.displayWith = (option) => this.getDisplayValue(option);
  }

  protected getDialogData(): SearchEmpresaModalData {
    return {
      ...super.getDialogData(),
      selectedEmpresas: this.selectedEmpresas ?? []
    };
  }

  protected search(term: string): Observable<SearchResult<IEmpresa>> {
    const options: SgiRestFindOptions = {
      page: {
        index: 0,
        size: 10
      },
      sort: new RSQLSgiRestSort('nombre', SgiRestSortDirection.ASC),
      filter: new RSQLSgiRestFilter('numeroIdentificacion', SgiRestFilterOperator.LIKE_ICASE, term)
        .or('nombre', SgiRestFilterOperator.LIKE_ICASE, term)
        .or('razonSocial', SgiRestFilterOperator.LIKE_ICASE, term)
    };
    return this.empresaService.findAll(options).pipe(
      map(response => {
        return {
          items: response.items,
          more: response.total > response.items.length
        };
      })
    );
  }

  private getDisplayValue(empresa: IEmpresa): string {
    const notFoundSelectedValue = !!empresa && Object.keys(empresa).length == 1 && !!empresa.id;
    setTimeout(() => this.setNotFoundSelectedValue(notFoundSelectedValue));

    if (notFoundSelectedValue) {
      return this.getErrorMsg(empresa.id);
    }

    return empresa.nombre;
  }

  private getErrorMsg(id: string): string {
    return this.translate.instant(SGEMP_NOT_FOUND, { ids: id, ...MSG_PARAMS.CARDINALIRY.SINGULAR })
  }

}
