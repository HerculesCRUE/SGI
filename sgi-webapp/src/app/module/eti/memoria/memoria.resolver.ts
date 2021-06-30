import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IMemoria } from '@core/models/eti/memoria';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';

const MSG_NOT_FOUND = marker('error.load');

@Injectable()
export class MemoriaResolver extends SgiResolverResolver<IMemoria> {

  constructor(logger: NGXLogger, router: Router, snackBar: SnackBarService, private service: MemoriaService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IMemoria> {
    return this.service.findById(Number(route.paramMap.get('id')));
  }
}
