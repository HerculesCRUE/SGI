import { InjectionToken } from '@angular/core';
import { IAliasEnumerado } from '@core/models/prc/alias-enumerado';
import { AliasEnumeradoService } from '@core/services/prc/alias-enumerado/alias-enumerado.service';
import { Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';


export const ALIAS_ENUMERADOS =
  new InjectionToken<Readonly<Observable<IAliasEnumerado[]>>>(
    'Alias Enumerados');

export function aliasEnumeradosFactory(aliasEnumeradoService: AliasEnumeradoService): Observable<IAliasEnumerado[]> {
  return aliasEnumeradoService.findAll()
    .pipe(
      map(response => response.items),
      shareReplay()
    );
}
