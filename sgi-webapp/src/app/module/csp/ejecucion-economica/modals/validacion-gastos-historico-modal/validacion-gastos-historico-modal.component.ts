import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { ESTADO_MAP, IEstadoGastoProyecto } from '@core/models/csp/estado-gasto-proyecto';
import { IGastoProyecto } from '@core/models/csp/gasto-proyecto';
import { GastoProyectoService } from '@core/services/csp/gasto-proyecto/gasto-proyecto-service';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';

const VALIDACION_GASTO_HISTORICO_ESTADO_KEY = marker('title.ejecucion-economica.validacion-gastos.historico-estados');

@Component({
  templateUrl: './validacion-gastos-historico-modal.component.html',
  styleUrls: ['./validacion-gastos-historico-modal.component.scss']
})
export class ValidacionGastosHistoricoModalComponent
  implements OnInit {

  displayedColumns = ['estado', 'fechaEstado', 'comentario'];
  elementosPagina = [5, 10, 25, 100];

  dataSource: MatTableDataSource<IEstadoGastoProyecto> = new MatTableDataSource<IEstadoGastoProyecto>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  msgParamEntities = {};

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  protected subscriptions: Subscription[] = [];
  historicoEstado$ = new BehaviorSubject<IEstadoGastoProyecto[]>([]);

  constructor(
    public matDialogRef: MatDialogRef<ValidacionGastosHistoricoModalComponent>,
    private gastoProyectoService: GastoProyectoService,
    private readonly translate: TranslateService,
    @Inject(MAT_DIALOG_DATA) public data: number
  ) { }

  ngOnInit(): void {

    this.setupI18N();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    if (this.data) {
      this.subscriptions.push(this.gastoProyectoService.findEstadoGastoProyecto(this.data).pipe(
        map((response) => response.items)
      ).subscribe((historicoEstados) => {
        this.historicoEstado$.next(historicoEstados);
      }));
    }

    this.subscriptions.push(this.historicoEstado$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      VALIDACION_GASTO_HISTORICO_ESTADO_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamEntities = { entity: value });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
