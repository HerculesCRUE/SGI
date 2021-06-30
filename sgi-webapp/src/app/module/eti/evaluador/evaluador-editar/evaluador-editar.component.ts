import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { MSG_PARAMS } from '@core/i18n';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { switchMap } from 'rxjs/operators';
import { EVALUADOR_ROUTE_NAMES } from '../evaluador-route-names';
import { EvaluadorActionService } from '../evaluador.action.service';

const MSG_BUTTON_SAVE = marker('btn.save');
const MSG_SUCCESS = marker('msg.update.entity.success');
const MSG_ERROR = marker('error.update.entity');
const EVALUADOR_KEY = marker('eti.evaluador');

@Component({
  selector: 'sgi-evaluador-editar',
  templateUrl: './evaluador-editar.component.html',
  styleUrls: ['./evaluador-editar.component.scss'],
  viewProviders: [
    EvaluadorActionService
  ]
})
export class EvaluadorEditarComponent extends ActionComponent implements OnInit {
  EVALUADOR_ROUTE_NAMES = EVALUADOR_ROUTE_NAMES;

  textoActualizar = MSG_BUTTON_SAVE;
  textoActualizarSuccess: string;
  textoActualizarError: string;

  get MSG_PARMS() {
    return MSG_PARAMS;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: EvaluadorActionService,
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
      EVALUADOR_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoActualizarSuccess = value);

    this.translate.get(
      EVALUADOR_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoActualizarError = value);
  }


  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => { },
      (error) => {
        this.logger.error(error);
        this.snackBarService.showError(this.textoActualizarError);
      },
      () => {
        this.snackBarService.showSuccess(this.textoActualizarSuccess);
        this.router.navigate(['../'], { relativeTo: this.activatedRoute });
      }
    );
  }
}
