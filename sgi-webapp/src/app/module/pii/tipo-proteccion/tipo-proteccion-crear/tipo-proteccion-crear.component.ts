import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { HttpProblem } from '@core/errors/http-problem';
import { MSG_PARAMS } from '@core/i18n';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { PII_TIPO_PROTECCION_ROUTE_NAMES } from '../tipo-proteccion-route-names';
import { TipoProteccionActionService } from '../tipo-proteccion.action.service';

const MSG_BUTTON_SAVE = marker('btn.save.entity');
const MSG_SUCCESS = marker('msg.save.entity.success');
const MSG_ERROR = marker('error.save.entity');
const TIPO_PROTECCION_KEY = marker('pii.tipo-proteccion');

@Component({
  selector: 'sgi-tipo-proteccion-crear',
  templateUrl: './tipo-proteccion-crear.component.html',
  styleUrls: ['./tipo-proteccion-crear.component.scss'],
  viewProviders: [
    TipoProteccionActionService
  ]
})
export class TipoProteccionCrearComponent extends ActionComponent implements OnInit {

  PII_TIPO_PROTECCION_ROUTE_NAMES = PII_TIPO_PROTECCION_ROUTE_NAMES;
  textoCrear$: Observable<string>;
  textoCrearSuccess: string;
  textoCrearError: string;

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    protected readonly router: Router,
    protected readonly route: ActivatedRoute,
    public readonly actionService: TipoProteccionActionService,
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
    const tituloSingular$ = this.translate.get(
      TIPO_PROTECCION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(take(1), shareReplay(1));

    this.textoCrear$ = tituloSingular$.pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_SAVE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    );

    this.subscriptions.push(...[
      tituloSingular$.pipe(
        switchMap((value) => {
          return this.translate.get(
            MSG_SUCCESS,
            { entity: value, ...MSG_PARAMS.GENDER.MALE }
          );
        })
      ).subscribe((value) => this.textoCrearSuccess = value),
      tituloSingular$.pipe(
        switchMap((value) => {
          return this.translate.get(
            MSG_ERROR,
            { entity: value, ...MSG_PARAMS.GENDER.MALE }
          );
        })
      ).subscribe((value) => this.textoCrearError = value)
    ]);
  }

  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => { },
      (error) => {
        this.logger.error(error);
        if (error instanceof HttpProblem) {
          if (!!!error.managed) {
            this.snackBarService.showError(error);
          }
        }
        else {
          this.snackBarService.showError(this.textoCrearError);
        }
      },
      () => {
        this.snackBarService.showSuccess(this.textoCrearSuccess);
        const tipoProteccionId = this.actionService.getFragment(this.actionService.FRAGMENT.DATOS_GENERALES).getKey();
        this.router.navigate([`../${tipoProteccionId}`], { relativeTo: this.activatedRoute });
      }
    );
  }

}
