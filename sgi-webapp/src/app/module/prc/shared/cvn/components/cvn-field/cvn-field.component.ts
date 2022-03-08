import { ChangeDetectionStrategy, Component, Inject, Input, OnInit } from '@angular/core';
import { IAliasEnumerado } from '@core/models/prc/alias-enumerado';
import { ICampoProduccionCientifica, ICampoProduccionCientificaWithConfiguracion } from '@core/models/prc/campo-produccion-cientifica';
import { TipoFormato } from '@core/models/prc/configuracion-campo';
import { IValorCampo } from '@core/models/prc/valor-campo';
import { Observable, of } from 'rxjs';
import { ALIAS_ENUMERADOS } from '../../alias-enumerado.token';
import { CvnValorCampoService } from '../../services/cvn-valor-campo.service';

@Component({
  selector: 'sgi-cvn-field',
  templateUrl: './cvn-field.component.html',
  styleUrls: ['./cvn-field.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CvnFieldComponent implements OnInit {
  readonly TIPO_FORMATO = TipoFormato;

  valoresCampo$: Observable<IValorCampo[]>;
  campoConConfiguracion: ICampoProduccionCientificaWithConfiguracion | undefined;
  @Input()
  set getValoresCampoFn(fn: (value: ICampoProduccionCientifica) => Observable<IValorCampo[]>) {
    if (fn) {
      this._getValoresCampoFn = fn;
    }
    this.fetchValoresCampo();
  }
  get getValoresCampoFn(): (value: ICampoProduccionCientifica) => Observable<IValorCampo[]> {
    return this._getValoresCampoFn;
  }
  // tslint:disable-next-line: variable-name
  private _getValoresCampoFn: (value: ICampoProduccionCientifica) => Observable<IValorCampo[]> =
    this.cvnValorCampoService.findCvnValorCampo;

  @Input()
  set campoCVN(value: string) {
    if (value) {
      this._campoCVN = value;
    }
    this.initialize();
  }
  get campoCVN(): string {
    return this._campoCVN;
  }
  // tslint:disable-next-line: variable-name
  private _campoCVN = '';

  @Input()
  set campoProduccionCientificaMap(value: Map<string, ICampoProduccionCientificaWithConfiguracion>) {
    if (value) {
      this._campoProduccionCientificaMap = value;
    }
    this.initialize();
  }
  get campoProduccionCientificaMap(): Map<string, ICampoProduccionCientificaWithConfiguracion> {
    return this._campoProduccionCientificaMap;
  }
  // tslint:disable-next-line: variable-name
  private _campoProduccionCientificaMap = new Map<string, ICampoProduccionCientificaWithConfiguracion>();

  constructor(
    @Inject(ALIAS_ENUMERADOS) public aliasEnumerados$: Readonly<Observable<IAliasEnumerado[]>>,
    private cvnValorCampoService: CvnValorCampoService
  ) { }

  ngOnInit(): void {
    this.initialize();
  }

  private initialize(): void {
    this.campoConConfiguracion = this._campoProduccionCientificaMap.get(this.campoCVN);
    this.fetchValoresCampo();
  }

  private fetchValoresCampo(): void {
    if (typeof this.campoConConfiguracion !== 'undefined') {
      this.valoresCampo$ = this._getValoresCampoFn(this.campoConConfiguracion.campoProduccionCientifica);
    } else {
      this.valoresCampo$ = of([]);
    }
  }
}
