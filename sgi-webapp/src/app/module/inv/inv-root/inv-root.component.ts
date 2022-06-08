import { Component, OnDestroy } from '@angular/core';
import { MSG_PARAMS } from '@core/i18n';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { Subscription } from 'rxjs';
import { INV_ROUTE_NAMES } from '../inv-route-names';

@Component({
  selector: 'sgi-inv-root',
  templateUrl: './inv-root.component.html',
  styleUrls: ['./inv-root.component.scss']
})
export class InvRootComponent implements OnDestroy {

  get INV_ROUTE_NAMES() {
    return INV_ROUTE_NAMES;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  showEvaluaciones = false;
  showSeguimientos = false;
  showActas = false;

  private subscriptions: Subscription[] = [];

  constructor(private evaluadorService: EvaluadorService) {
    this.subscriptions.push(this.evaluadorService.hasAssignedEvaluaciones().subscribe((res) => this.showEvaluaciones = res));
    this.subscriptions.push(this.evaluadorService.hasAssignedEvaluacionesSeguimiento().subscribe((res) => this.showSeguimientos = res));
    this.subscriptions.push(this.evaluadorService.hasAssignedActas().subscribe((res) => this.showActas = res));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }
}
