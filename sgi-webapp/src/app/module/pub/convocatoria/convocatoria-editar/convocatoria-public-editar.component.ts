import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { MSG_PARAMS } from '@core/i18n';
import { DialogService } from '@core/services/dialog.service';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { CONVOCATORIA_PUBLIC_ROUTE_NAMES } from '../convocatoria-public-route-names';
import { ConvocatoriaPublicActionService } from '../convocatoria-public.action.service';

const CONVOCATORIA_KEY = marker('csp.convocatoria');
const MSG_BUTTON_EDIT = marker('btn.save.entity');
const MSG_BUTTON_REGISTRAR = marker('csp.convocatoria.registrar');

@Component({
  selector: 'sgi-convocatoria-public-editar',
  templateUrl: './convocatoria-public-editar.component.html',
  styleUrls: ['./convocatoria-public-editar.component.scss'],
  providers: [
    ConvocatoriaPublicActionService
  ]
})
export class ConvocatoriaPublicEditarComponent extends ActionComponent implements OnInit {

  textoEditar: string;
  textoRegistrar = MSG_BUTTON_REGISTRAR;
  canEdit = false;

  disableRegistrar$: Subject<boolean> = new BehaviorSubject<boolean>(true);

  get CONVOCATORIA_ROUTE_NAMES() {
    return CONVOCATORIA_PUBLIC_ROUTE_NAMES;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }


  constructor(
    router: Router,
    route: ActivatedRoute,
    public actionService: ConvocatoriaPublicActionService,
    dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(router, route, actionService, dialogService);
    this.disableRegistrar$.next(true);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  saveOrUpdate(): void {
    throw new Error('Method not implemented.');
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_EDIT,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoEditar = value);
  }

}
