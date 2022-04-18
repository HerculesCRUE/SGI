import { Component, OnInit } from '@angular/core';
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
import { INVENCION_REPARTO_ROUTE_NAMES } from '../invencion-reparto-route-names';
import { InvencionRepartoActionService } from '../invencion-reparto.action.service';

const MSG_BUTTON_SAVE = marker('btn.save.entity');
const MSG_SUCCESS = marker('msg.update.entity.success');
const MSG_ERROR = marker('error.update.entity');
const REPARTO_KEY = marker('pii.reparto');

@Component({
  selector: 'sgi-invencion-reparto-editar',
  templateUrl: './invencion-reparto-editar.component.html',
  styleUrls: ['./invencion-reparto-editar.component.scss'],
  viewProviders: [
    InvencionRepartoActionService
  ]
})
export class InvencionRepartoEditarComponent extends ActionComponent implements OnInit {
  INVENCION_REPARTO_ROUTE_NAMES = INVENCION_REPARTO_ROUTE_NAMES;

  textoCrear: string;
  textoUpdateSuccess: string;
  textoUpdateError: string;

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: InvencionRepartoActionService,
    dialogService: DialogService,
    private readonly translate: TranslateService) {
    super(router, route, actionService, dialogService);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      REPARTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_SAVE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrear = value);

    this.translate.get(
      REPARTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoUpdateSuccess = value);

    this.translate.get(
      REPARTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
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

  cancel(): void {
    this.router.navigate(['../'], { relativeTo: this.activatedRoute });
  }
}
