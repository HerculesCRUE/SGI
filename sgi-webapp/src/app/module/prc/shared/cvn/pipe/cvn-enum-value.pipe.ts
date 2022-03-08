import { Pipe, PipeTransform } from '@angular/core';
import { IAliasEnumerado } from '@core/models/prc/alias-enumerado';
import { IValorCampo } from '@core/models/prc/valor-campo';
import { CVN_TRANSLATE_PREFIX } from '../literales-cvn';

@Pipe({
  name: 'cvnEnumValue'
})
export class CvnEnumValuePipe implements PipeTransform {

  transform([firstElement]: IValorCampo[], aliasEnumerados: IAliasEnumerado[]): string {
    if (!firstElement) {
      return '';
    }
    const selectedAliasEnumerado = aliasEnumerados.find(
      aliasEnumerado => aliasEnumerado?.codigo === firstElement?.campoProduccionCientifica?.codigo
    );
    if (selectedAliasEnumerado) {
      return CVN_TRANSLATE_PREFIX + selectedAliasEnumerado.prefijoEnumerado + '.' + firstElement.valor;
    } else {
      return CVN_TRANSLATE_PREFIX + firstElement.campoProduccionCientifica.codigo + '.' + firstElement.valor;
    }
  }

}
