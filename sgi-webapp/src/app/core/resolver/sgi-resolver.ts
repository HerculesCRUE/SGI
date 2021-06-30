import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from '@angular/router';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';

export abstract class SgiResolverResolver<T> implements Resolve<T> {

  private errorMsg: string;

  constructor(private logger: NGXLogger, protected router: Router, private snackBar: SnackBarService, msgError: string) {
    this.errorMsg = msgError;
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): T | Observable<T> | Promise<T> {
    return this.resolveEntity(route).pipe(
      catchError(e => {
        this.logger.error(e);
        this.snackBar.showError(this.errorMsg);
        // If is the first navigation redirect two levels back, else return to original url
        if (this.router.getCurrentNavigation().id === 1) {
          const s = this.router.getCurrentNavigation().finalUrl.toString().split('/');
          // Pop 2 last segments
          s.pop();
          s.pop();
          this.router.navigateByUrl(s.join('/'));
        }
        else {
          this.router.navigateByUrl(this.router.routerState.snapshot.url);
        }
        return EMPTY;
      })
    );
  }

  protected abstract resolveEntity(route: ActivatedRouteSnapshot): Observable<T>;
}
