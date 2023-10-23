import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ACTION_MODAL_MODE } from 'src/app/esb/shared/formly-forms/core/base-formly-modal.component';
import { IPersonaFormlyData, PersonaFormlyModalComponent } from '../../formly-forms/persona-formly-modal/persona-formly-modal.component';
import { IPersona } from '@core/models/sgp/persona';

const PERSONA_KEY = marker('sgp.persona');
const MSG_UPDATE_SUCCESS = marker('msg.update.request.entity.success');

@Component({
  selector: 'sgi-icon-view-persona-detail',
  templateUrl: './icon-view-persona-detail.component.html',
  styleUrls: ['./icon-view-persona-detail.component.scss']
})
export class IconViewPersonaDetailComponent implements OnInit, OnDestroy {

  @Input()
  personaId: string;

  @Output()
  readonly personaUpdated = new EventEmitter<IPersona>();

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
      PERSONA_KEY,
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

  public openPersonaFormlyModal(): void {
    const personaData: IPersonaFormlyData = {
      personaId: this.personaId,
      action: this.hasEditAuthority() ? ACTION_MODAL_MODE.EDIT : ACTION_MODAL_MODE.VIEW
    };

    const config = {
      panelClass: 'sgi-dialog-container',
      data: personaData
    };
    const dialogRef = this.matDialog.open(PersonaFormlyModalComponent, config);

    this.subscriptions.push(
      dialogRef.afterClosed().subscribe(
        (persona) => {
          if (persona) {
            this.personaUpdated.emit(persona);
            this.snackBarService.showSuccess(this.textoUpdateSuccess);
          }
        }
      )
    );
  }

  private hasEditAuthority(): boolean {
    return this.authService.hasAuthority('ESB-PER-E');
  }

}
