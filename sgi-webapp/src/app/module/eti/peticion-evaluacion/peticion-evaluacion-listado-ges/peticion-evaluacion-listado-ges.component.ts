import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IBaseExportModalData } from '@core/component/base-export/base-export-modal-data';
import { MSG_PARAMS } from '@core/i18n';
import { IMemoria } from '@core/models/eti/memoria';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { ESTADO_MEMORIA_MAP } from '@core/models/eti/tipo-estado-memoria';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ConfigService } from '@core/services/cnf/config.service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';
import { PeticionEvaluacionListadoExportModalComponent } from '../modals/peticion-evaluacion-listado-export-modal/peticion-evaluacion-listado-export-modal.component';

const MSG_BUTTON_SAVE = marker('btn.add.entity');
const PETICION_EVALUACION_KEY = marker('eti.peticion-evaluacion');

@Component({
  selector: 'sgi-peticion-evaluacion-listado-ges',
  templateUrl: './peticion-evaluacion-listado-ges.component.html',
  styleUrls: ['./peticion-evaluacion-listado-ges.component.scss']
})
export class PeticionEvaluacionListadoGesComponent extends AbstractTablePaginationComponent<IPeticionEvaluacion> implements OnInit {

  ROUTE_NAMES = ROUTE_NAMES;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];
  totalElementos: number;

  textoCrear: string;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  peticionesEvaluacion$: Observable<IPeticionEvaluacion[]> = of();
  memorias$: Observable<IMemoria[]> = of();

  private limiteRegistrosExportacionExcel: string;

  get tipoColectivoSolicitante() {
    return TipoColectivo.SOLICITANTE_ETICA;
  }

  get ESTADO_MEMORIA_MAP() {
    return ESTADO_MEMORIA_MAP;
  }

  constructor(
    private readonly logger: NGXLogger,
    private readonly peticionesEvaluacionService: PeticionEvaluacionService,
    private readonly personaService: PersonaService,
    private readonly translate: TranslateService,
    private matDialog: MatDialog,
    private readonly cnfService: ConfigService,
  ) {
    super();

    this.totalElementos = 0;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.formGroup = new FormGroup({
      comite: new FormControl(null, []),
      titulo: new FormControl('', []),
      codigo: new FormControl('', []),
      tipoEstadoMemoria: new FormControl(null, []),
      solicitante: new FormControl('', [])
    });

    this.suscripciones.push(
      this.cnfService.getLimiteRegistrosExportacionExcel('eti-exp-max-num-registros-excel-peticion-evaluacion-listado').subscribe(value => {
        this.limiteRegistrosExportacionExcel = value;
      }));
  }

  private setupI18N(): void {
    this.translate.get(
      PETICION_EVALUACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_SAVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoCrear = value);
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IPeticionEvaluacion>> {
    return this.peticionesEvaluacionService.findAll(this.getFindOptions(reset)).pipe(
      map((response) => {
        // Return the values
        return response;
      }),
      switchMap((response) => {
        if (!response.items || response.items.length === 0) {
          return of({} as SgiRestListResult<IPeticionEvaluacion>);
        }
        const personaIdsEvaluadores = new Set<string>();

        response.items.forEach((peticionEvaluacion: IPeticionEvaluacion) => {
          personaIdsEvaluadores.add(peticionEvaluacion?.solicitante?.id);
        });

        const personaSubscription = this.personaService.findAllByIdIn([...personaIdsEvaluadores]).subscribe((result) => {
          const personas = result.items;
          response.items.forEach((peticionEvaluacion: IPeticionEvaluacion) => {
            const datosPersona = personas.find((persona) =>
              peticionEvaluacion.solicitante.id === persona.id);
            peticionEvaluacion.solicitante = datosPersona;
          });
        },
          (error) => {
            this.logger.error(error);
            this.processError(error);
          }
        );
        this.suscripciones.push(personaSubscription);
        let peticionesListado: SgiRestListResult<IPeticionEvaluacion>;
        return of(peticionesListado = {
          page: response.page,
          total: response.total,
          items: response.items
        });
      }),
      catchError((error) => {
        this.logger.error(error);
        this.processError(error);
        return of({} as SgiRestListResult<IPeticionEvaluacion>);
      })
    );
  }

  protected initColumns(): void {
    this.displayedColumns = ['solicitante', 'codigo', 'titulo', 'fuenteFinanciacion', 'fechaInicio', 'fechaFin', 'acciones'];
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    return new RSQLSgiRestFilter('peticionEvaluacion.codigo', SgiRestFilterOperator.LIKE_ICASE, controls.codigo.value)
      .and('peticionEvaluacion.titulo', SgiRestFilterOperator.LIKE_ICASE, controls.titulo.value)
      .and('comite.id', SgiRestFilterOperator.EQUALS, controls.comite.value?.id?.toString())
      .and('estadoActual.id', SgiRestFilterOperator.EQUALS, controls.tipoEstadoMemoria.value?.toString())
      .and('peticionEvaluacion.personaRef', SgiRestFilterOperator.EQUALS, controls.solicitante.value.id);
  }

  protected loadTable(reset?: boolean) {
    this.peticionesEvaluacion$ = this.getObservableLoadTable(reset);
  }

  public openExportModal() {
    const data: IBaseExportModalData = {
      findOptions: this.findOptions,
      totalRegistrosExportacionExcel: this.totalElementos,
      limiteRegistrosExportacionExcel: Number(this.limiteRegistrosExportacionExcel)
    };

    const config = {
      data
    };
    this.matDialog.open(PeticionEvaluacionListadoExportModalComponent, config);
  }
}
