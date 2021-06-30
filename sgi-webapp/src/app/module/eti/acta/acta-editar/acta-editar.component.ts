import { Component, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { MSG_PARAMS } from '@core/i18n';
import { ActionService } from '@core/services/action-service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { switchMap } from 'rxjs/operators';
import { ACTA_ROUTE_NAMES } from '../acta-route-names';
import { ActaActionService } from '../acta.action.service';

const MSG_BUTTON_SAVE = marker('btn.save');
const MSG_SUCCESS = marker('msg.update.entity.success');
const MSG_ERROR = marker('error.update.entity');
const ACTA_KEY = marker('eti.acta')
@Component({
  selector: 'sgi-acta-editar',
  templateUrl: './acta-editar.component.html',
  styleUrls: ['./acta-editar.component.scss'],
  encapsulation: ViewEncapsulation.None,
  providers: [
    ActaActionService,
    {
      provide: ActionService,
      useExisting: ActaActionService
    }
  ]
})
export class ActaEditarComponent extends ActionComponent {
  ACTA_ROUTE_NAMES = ACTA_ROUTE_NAMES;

  textoCrear = MSG_BUTTON_SAVE;
  textoSuccess: string;
  textoError: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: ActaActionService,
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
      ACTA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoSuccess = value);

    this.translate.get(
      ACTA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoError = value);
  }

  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => { },
      (error) => {
        this.logger.error(error);
        this.snackBarService.showError(this.textoError);
      },
      () => {
        this.snackBarService.showSuccess(this.textoSuccess);
        this.router.navigate(['../'], { relativeTo: this.activatedRoute });
      }
    );
  }

}
