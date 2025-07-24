import { Pipe, PipeTransform } from '@angular/core';
import { Language } from '@core/i18n/language';
import { IApartado } from '@core/models/eti/apartado';
import { LanguageService } from '@core/services/language.service';

export function getApartadoNombre(apartado?: IApartado, lang?: Language): string {
  let ordenNumber = '';
  if (apartado.padre) {
    const apartados = getSubapartados(apartado.padre, []);
    apartados.reverse().forEach(aptdo => {
      ordenNumber += '.' + aptdo.orden;
    });
  }
  if (apartado.bloque.orden === 0) {
    return apartado?.padre ? apartado?.padre.definicion.find(a => a.lang === lang)?.nombre : (apartado.definicion.find(a => a.lang === lang)?.nombre);
  } else {
    return apartado?.padre ?
      (apartado?.bloque?.orden + ordenNumber + ' ' + apartado?.padre.definicion.find(a => a.lang === lang)?.nombre) : (apartado?.bloque?.orden + ordenNumber + '.' + apartado?.orden + ' ' + apartado.definicion.find(a => a.lang === lang)?.nombre);
  }
}

export function getSubApartadoNombre(apartado?: IApartado, lang?: Language): string {
  let ordenNumber = '';
  if (apartado.padre) {
    const apartados = getSubapartados(apartado.padre, []);
    apartados.reverse().forEach(aptdo => {
      ordenNumber += '.' + aptdo.orden;
    });
    return apartado?.bloque?.orden + ordenNumber + '.' + apartado?.orden + ' ' + apartado.definicion.find(a => a.lang === lang)?.nombre;
  } else {
    return null;
  }

}

function getSubapartados(apartado: IApartado, apartados: IApartado[]): IApartado[] {
  apartados.push(apartado);
  if (apartado?.padre) {
    return getSubapartados(apartado.padre, apartados);
  }
  return apartados;
}

@Pipe({
  name: 'bloqueApartado'
})
export class BloqueApartadoPipe implements PipeTransform {

  constructor(private languageService: LanguageService) {
  }

  transform(apartado: IApartado): string {
    if (apartado) {
      return this.getApartadosNombre(apartado);
    }
  }

  private getApartadosNombre(apartado?: IApartado): string {
    let ordenNumber = '';
    if (apartado.bloque.orden === 0) {
      return apartado?.padre ? apartado?.padre.definicion.find(a => a.lang === this.languageService.getLanguage())?.nombre : (apartado.definicion.find(a => a.lang === this.languageService.getLanguage())?.nombre);
    } else if (apartado.padre) {
      const apartados = getSubapartados(apartado.padre, []);
      apartados.reverse().forEach(aptdo => {
        ordenNumber += '.' + aptdo.orden;
      });
      return apartado?.bloque?.orden + ordenNumber + '.' + apartado?.orden + ' ' + apartado.definicion.find(a => a.lang === this.languageService.getLanguage())?.nombre;
    } else {
      return apartado?.bloque?.orden + '.' + apartado?.orden + ' ' + apartado.definicion.find(a => a.lang === this.languageService.getLanguage())?.nombre;
    }
  }

}
