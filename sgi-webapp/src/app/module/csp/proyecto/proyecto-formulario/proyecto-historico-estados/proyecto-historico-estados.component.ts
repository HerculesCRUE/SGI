import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { ESTADO_MAP, IEstadoProyecto } from '@core/models/csp/estado-proyecto';
import { LanguageService } from '@core/services/language.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ProyectoHistoricoEstadosFragment } from './proyecto-historico-estados.fragment';

const PROYECTO_HISTORICO_ESTADO_KEY = marker('title.csp.proyecto-historico-estado');

@Component({
  selector: 'sgi-proyecto-historico-estados',
  templateUrl: './proyecto-historico-estados.component.html',
  styleUrls: ['./proyecto-historico-estados.component.scss']
})
export class ProyectoHistoricoEstadosComponent extends FragmentComponent implements OnInit, OnDestroy {
  private formPart: ProyectoHistoricoEstadosFragment;
  private subscriptions: Subscription[] = [];

  displayedColumns = ['estado', 'fechaEstado', 'comentario'];
  elementosPagina = [5, 10, 25, 100];

  dataSource: MatTableDataSource<IEstadoProyecto> = new MatTableDataSource<IEstadoProyecto>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  msgParamEntities = {};

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  constructor(
    protected snackBarService: SnackBarService,
    private actionService: ProyectoActionService,
    private readonly translate: TranslateService,
    private readonly languageService: LanguageService
  ) {
    super(actionService.FRAGMENT.HISTORICO_ESTADOS, actionService, translate);
    this.formPart = this.fragment as ProyectoHistoricoEstadosFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor =
      (estadoProyecto: IEstadoProyecto, property: string) => {
        switch (property) {
          case 'estado':
            return estadoProyecto.estado;
          case 'fechaEstado':
            return estadoProyecto.fechaEstado;
          case 'comentario':
            return this.languageService.getFieldValue(estadoProyecto.comentario);
          default:
            return estadoProyecto[property];
        }
      };
    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart.historicoEstado$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  protected setupI18N(): void {
    this.translate.get(
      PROYECTO_HISTORICO_ESTADO_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamEntities = { entity: value });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }
}

