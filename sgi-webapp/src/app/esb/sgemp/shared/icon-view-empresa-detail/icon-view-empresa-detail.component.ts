import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ACTION_MODAL_MODE } from 'src/app/esb/shared/formly-forms/core/base-formly-modal.component';
import { EmpresaFormlyModalComponent, IEmpresaFormlyData } from '../../formly-forms/empresa-formly-modal/empresa-formly-modal.component';

const EMPRESA_KEY = marker('sgemp.empresa');
const MSG_UPDATE_SUCCESS = marker('msg.update.request.entity.success');

@Component({
  selector: 'sgi-icon-view-empresa-detail',
  templateUrl: './icon-view-empresa-detail.component.html',
  styleUrls: ['./icon-view-empresa-detail.component.scss']
})
export class IconViewEmpresaDetailComponent implements OnInit, OnDestroy {

  @Input()
  empresaId: string;

  @Output()
  readonly empresaUpdated = new EventEmitter<IEmpresa>();

  private textoUpdateSuccess: string;

  protected subscriptions: Subscription[] = [];

  constructor(
    private readonly snackBarService: SnackBarService,
    private readonly translate: TranslateService,
    private readonly authService: SgiAuthService,
    private matDialog: MatDialog
  ) {
  }

  ngOnInit(): void {
    this.setupI18N();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(x => x.unsubscribe());
  }

  private setupI18N(): void {
    this.translate.get(
      EMPRESA_KEY,
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

  public openEmpresaFormlyModal(): void {
    const empresaData: IEmpresaFormlyData = {
      empresaId: this.empresaId,
      action: this.hasEditAuthority() ? ACTION_MODAL_MODE.EDIT : ACTION_MODAL_MODE.VIEW
    };

    const config = {
      panelClass: 'sgi-dialog-container',
      data: empresaData
    };
    const dialogRef = this.matDialog.open(EmpresaFormlyModalComponent, config);

    this.subscriptions.push(
      dialogRef.afterClosed().subscribe(
        (empresa) => {
          if (empresa) {
            this.empresaUpdated.emit(empresa);
            this.snackBarService.showSuccess(this.textoUpdateSuccess);
          }
        }
      )
    );
  }

  private hasEditAuthority(): boolean {
    return this.authService.hasAuthority('ESB-EMP-E');
  }

}
