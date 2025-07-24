import { KeyValue } from '@angular/common';
import { ChangeDetectionStrategy, Component, ViewChild } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormularioService } from '@core/services/eti/formulario.service';
import { LanguageService } from '@core/services/language.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { ConfigSelectFileComponent } from '../config-select-file/config-select-file.component';
import { ResourceUploadComponent } from '../resource-upload/resource-upload.component';

const TOOLTIP_SELECT_VERSION_FORMULARIO = marker("adm.config.eti.REP_FORMULARIO_MEMORIA.descripcion");

@Component({
  selector: 'sgi-config-select-file-eti',
  templateUrl: './../config-select-file/config-select-file.component.html',
  styleUrls: ['./../config-select-file/config-select-file.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ConfigSelectFileEtiComponent extends ConfigSelectFileComponent {

  private _uploader: ResourceUploadComponent;

  @ViewChild('uploader') set uploader(component: ResourceUploadComponent) {
    if (component) {
      this._uploader = component;
      this.subscriptions.push(component.selectionChange.subscribe(file => {
        this.fileSelection = file;
      }));
    }
  }

  private fileSelection: File = null;

  constructor(
    protected readonly translate: TranslateService,
    protected readonly snackBarService: SnackBarService,
    protected readonly formularioService: FormularioService,
    protected readonly languageService: LanguageService
  ) {
    super(translate, snackBarService, formularioService, languageService);
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

  ngAfterViewInit(): void {
    super.ngAfterViewInit();
  }

  protected getValues(codigo: string): Observable<KeyValue<string, string>[]> {
    return this.formularioService.findByCodigoFormulario(codigo).pipe(
      map((configValues) => {
        const keyValueArray: KeyValue<string, string>[] = [];
        configValues.items.forEach(configValue => {
          const selectOption: KeyValue<string, string> = {
            key: configValue.id.toString(),
            value: configValue.codigo
          };
          keyValueArray.push(selectOption);
        });
        return keyValueArray;
      }),
      catchError((error) => {
        this.error.next(error);
        return of([]);
      })
    );
  }

  protected download(formularioId: number): Observable<Blob> {
    return this.formularioService.downloadReport(formularioId);
  }

  protected upload(formularioId: number): Observable<void> {
    return this._uploader.handleUploadRequest(this.formularioService.updateResourceFormularioWithStatus(formularioId, this.fileSelection));
  }

  protected textoTooltip(): string {
    return TOOLTIP_SELECT_VERSION_FORMULARIO;
  }
}
