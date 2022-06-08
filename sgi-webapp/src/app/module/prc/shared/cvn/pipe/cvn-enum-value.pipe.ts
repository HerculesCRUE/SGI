import { Pipe, PipeTransform } from '@angular/core';
import { IAliasEnumerado } from '@core/models/prc/alias-enumerado';
import { IValorCampo } from '@core/models/prc/valor-campo';
import { TranslateService } from '@ngx-translate/core';
import { CVN_TRANSLATE_PREFIX } from '../literales-cvn';

@Pipe({
  name: 'cvnEnumValue'
})
export class CvnEnumValuePipe implements PipeTransform {

  constructor(private readonly translateService: TranslateService) { }

  transform([firstElement]: IValorCampo[], aliasEnumerados: IAliasEnumerado[]): string {
    if (!firstElement) {
      return '';
    }
    const selectedAliasEnumerado = aliasEnumerados.find(
      aliasEnumerado => aliasEnumerado?.codigo === firstElement?.campoProduccionCientifica?.codigo
    );

    let key = '';
    if (selectedAliasEnumerado) {
      key = CVN_TRANSLATE_PREFIX + selectedAliasEnumerado.prefijoEnumerado + '.' + firstElement.valor;
    } else {
      key = CVN_TRANSLATE_PREFIX + firstElement.campoProduccionCientifica.codigo + '.' + firstElement.valor;
    }

    if (this.translateService.instant(key) === key) {
      return firstElement.valor;
    } else {
      return key;
    }

  }

}
