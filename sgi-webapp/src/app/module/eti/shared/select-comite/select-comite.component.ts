import { ChangeDetectionStrategy, Component, Optional, Self } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatFormFieldControl } from '@angular/material/form-field';
import { SelectServiceComponent } from '@core/component/select-service/select-service.component';
import { IComite } from '@core/models/eti/comite';
import { ComiteService } from '@core/services/eti/comite.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'sgi-select-comite',
  templateUrl: '../../../../core/component/select-common/select-common.component.html',
  styleUrls: ['../../../../core/component/select-common/select-common.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectComiteComponent
    }
  ]
})
export class SelectComiteComponent extends SelectServiceComponent<IComite> {

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    private service: ComiteService,
    @Self() @Optional() ngControl: NgControl) {
    super(defaultErrorStateMatcher, ngControl);

    this.displayWith = (comite: IComite) => comite?.comite;
  }

  protected loadServiceOptions(): Observable<IComite[]> {
    return this.service.findAll().pipe(
      map(response => response.items)
    );
  }

}
