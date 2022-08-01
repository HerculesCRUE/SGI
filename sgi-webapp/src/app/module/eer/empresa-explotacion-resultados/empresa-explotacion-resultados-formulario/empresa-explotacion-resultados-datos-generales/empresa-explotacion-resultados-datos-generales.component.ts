import { Component, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { EstadoEmpresa, ESTADO_EMPRESA_EXPLOTACION_RESULTADOS_MAP, IEmpresaExplotacionResultados, TipoEmpresa, TIPO_EMPRESA_EXPLOTACION_RESULTADOS_MAP } from '@core/models/eer/empresa-explotacion-resultados';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { Subscription } from 'rxjs';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';
import { EmpresaExplotacionResultadosActionService } from '../../empresa-explotacion-resultados.action.service';
import { EmpresaExplotacionResultadosDatosGeneralesFragment } from './empresa-explotacion-resultados-datos-generales.fragment';

const EMPRESA_NOMBRE_RAZON_SOCIAL_KEY = marker('eer.empresa-explotacion-resultados.nombre-razon-social');
const EMPRESA_OBJETO_SOCIAL_KEY = marker('eer.empresa-explotacion-resultados.objeto-social');
const EMPRESA_OBSERVACIONES_KEY = marker('eer.empresa-explotacion-resultados.observaciones');
const EMPRESA_FECHA_SOLICITUD_KEY = marker('eer.empresa-explotacion-resumen.fecha-solicitud');
const EMPRESA_CONOCIMIENTO_KEY = marker('eer.empresa-explotacion-resultados.conocimiento');
const EMPRESA_TECNOLOGIA_KEY = marker('eer.empresa-explotacion-resultados.tecnologia');
const EMPRESA_NUMERO_PROTOCOLO_KEY = marker('eer.empresa-explotacion-resultados.numero-protocolo');
const EMPRESA_NOTARIO_KEY = marker('eer.empresa-explotacion-resultados.notario');

@Component({
  selector: 'sgi-empresa-explotacion-resultados-datos-generales',
  templateUrl: './empresa-explotacion-resultados-datos-generales.component.html',
  styleUrls: ['./empresa-explotacion-resultados-datos-generales.component.scss']
})
export class EmpresaExplotacionResultadosDatosGeneralesComponent extends FormFragmentComponent<IEmpresaExplotacionResultados> implements OnInit {
  formPart: EmpresaExplotacionResultadosDatosGeneralesFragment;

  TIPO_COLECTIVO = TipoColectivo;
  TIPO_EMPRESA = TipoEmpresa;

  msgParamTituloEntity = {};
  msgParamNombreRazonSocialEntity = {};
  msgParamObservacionesEntity = {};
  msgParamFechaSolicitudEntity = {};
  msgParamConocimientoEntity = {};
  msgParamTecnologiaEntity = {};
  msgParamObjetoSocialEntity = {};
  msgParamNotarioEntity = {};
  msgParamNumeroProtocoloEntity = {};

  subscriptions: Subscription[] = [];

  estados = new Map<EstadoEmpresa, string>();

  get TIPO_EMPRESA_MAP() {
    return TIPO_EMPRESA_EXPLOTACION_RESULTADOS_MAP;
  }

  constructor(
    protected actionService: EmpresaExplotacionResultadosActionService,
    public authService: SgiAuthService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as EmpresaExplotacionResultadosDatosGeneralesFragment;

    this.estados = this.getEstadosWithoutActiva();

    this.subscriptions.push(
      this.formPart.getFormGroup().controls.entidad.valueChanges.subscribe(
        (value) => {
          if (value) {
            this.estados = ESTADO_EMPRESA_EXPLOTACION_RESULTADOS_MAP;
          } else {
            this.estados = this.getEstadosWithoutActiva();
          }
        }
      )
    );
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private getEstadosWithoutActiva(): Map<EstadoEmpresa, string> {
    const estadosNew = new Map<EstadoEmpresa, string>();
    ESTADO_EMPRESA_EXPLOTACION_RESULTADOS_MAP.forEach((value, key) => {
      if (key !== EstadoEmpresa.ACTIVA) {
        estadosNew.set(key, value);
      }
    });
    if (this.formGroup.controls.estado.value === EstadoEmpresa.ACTIVA) {
      this.formGroup.controls.estado.setValue(null);
    }
    return estadosNew;
  }

  private setupI18N(): void {

    this.translate.get(
      EMPRESA_NOMBRE_RAZON_SOCIAL_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreRazonSocialEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      EMPRESA_OBSERVACIONES_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamObservacionesEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      EMPRESA_OBJETO_SOCIAL_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamObjetoSocialEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      EMPRESA_FECHA_SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaSolicitudEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      EMPRESA_CONOCIMIENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamConocimientoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      EMPRESA_TECNOLOGIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTecnologiaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      EMPRESA_NOTARIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNotarioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      EMPRESA_NUMERO_PROTOCOLO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNumeroProtocoloEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }
}
