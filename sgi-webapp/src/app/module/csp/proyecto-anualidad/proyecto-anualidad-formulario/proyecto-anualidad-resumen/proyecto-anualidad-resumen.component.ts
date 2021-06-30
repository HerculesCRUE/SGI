import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { TIPO_PARTIDA_MAP } from '@core/enums/tipo-partida';
import { MSG_PARAMS } from '@core/i18n';
import { IAnualidadResumen } from '@core/models/csp/anualidad-resumen';
import { ESTADO_MAP, IEstadoProyecto } from '@core/models/csp/estado-proyecto';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { ProyectoAnualidadActionService } from '../../proyecto-anualidad.action.service';
import { IAnualidad, ProyectoAnualidadResumenFragment } from './proyecto-anualidad-resumen.fragment';

const PROYECTO_HISTORICO_ESTADO_KEY = marker('title.csp.proyecto-historico-estado');

@Component({
  selector: 'sgi-proyecto-anualidad-resumen',
  templateUrl: './proyecto-anualidad-resumen.component.html',
  styleUrls: ['./proyecto-anualidad-resumen.component.scss']
})
export class ProyectoAnualidadResumenComponent extends FragmentComponent implements OnInit, OnDestroy {
  private formPart: ProyectoAnualidadResumenFragment;
  private subscriptions: Subscription[] = [];

  displayedColumns = ['tipo', 'partidaPresupuestaria', 'importePresupuesto', 'importeConcedido'];

  dataSource: MatTableDataSource<IAnualidadResumen> = new MatTableDataSource<IAnualidadResumen>();

  msgParamEntities = {};

  get TIPO_PARTIDA_MAP() {
    return TIPO_PARTIDA_MAP;
  }
  constructor(
    protected snackBarService: SnackBarService,
    private actionService: ProyectoAnualidadActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.RESUMEN, actionService);
    this.formPart = this.fragment as ProyectoAnualidadResumenFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.subscriptions.push(this.formPart.anualidades$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_HISTORICO_ESTADO_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamEntities = { entity: value });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }
}

