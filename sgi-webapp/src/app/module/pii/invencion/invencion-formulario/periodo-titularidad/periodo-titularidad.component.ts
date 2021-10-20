import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { FragmentComponent } from '@core/component/fragment.component';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { InvencionActionService } from '../../invencion.action.service';

@Component({
  selector: 'sgi-periodo-titularidad',
  templateUrl: './periodo-titularidad.component.html',
  styleUrls: ['./periodo-titularidad.component.scss']
})
export class PeriodoTitularidadComponent extends FragmentComponent implements OnInit {

  constructor(
    protected actionService: InvencionActionService,
    private readonly translate: TranslateService,
    private readonly logger: NGXLogger,
    private readonly snackBarService: SnackBarService,
    private readonly dialogService: DialogService,
    private readonly matDialog: MatDialog
  ) {
    super(actionService.FRAGMENT.PERIODOS_TITULARIDAD, actionService);
  }

  ngOnInit(): void {
  }

}
