import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { switchMap } from 'rxjs/operators';
import { CONVOCATORIA_REUNION_ROUTE_NAMES } from '../convocatoria-reunion-route-names';
import { ConvocatoriaReunionActionService } from '../convocatoria-reunion.action.service';

const MSG_BUTTON_SAVE = marker('btn.save');
const MSG_SUCCESS = marker('msg.update.entity.success');
const MSG_ERROR = marker('error.update.entity');
const CONVOCATORIA_REUNION_KEY = marker('eti.convocatoria-reunion');

@Component({
  selector: 'sgi-convocatoria-reunion-editar',
  templateUrl: './convocatoria-reunion-editar.component.html',
  styleUrls: ['./convocatoria-reunion-editar.component.scss'],
  encapsulation: ViewEncapsulation.None,
  providers: [
    ConvocatoriaReunionActionService
  ]
})
export class ConvocatoriaReunionEditarComponent extends ActionComponent implements OnInit {
  CONVOCATORIA_REUNION_ROUTE_NAMES = CONVOCATORIA_REUNION_ROUTE_NAMES;

  textoUpdate = MSG_BUTTON_SAVE;
  textoUpdateSuccess: string;
  textoUpdateError: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: ConvocatoriaReunionActionService,
    dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(router, route, actionService, dialogService);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_REUNION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoUpdateSuccess = value);

    this.translate.get(
      CONVOCATORIA_REUNION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoUpdateError = value);
  }

  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => { },
      (error) => {
        this.logger.error(error);
        if (error instanceof SgiError) {
          if (!!!error.managed) {
            this.snackBarService.showError(error);
          }
        }
        else {
          this.snackBarService.showError(this.textoUpdateError);
        }
      },
      () => {
        this.snackBarService.showSuccess(this.textoUpdateSuccess);
      }
    );
  }
}
