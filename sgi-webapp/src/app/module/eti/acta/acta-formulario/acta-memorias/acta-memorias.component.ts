import { Component, OnInit } from '@angular/core';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IDocumento } from '@core/models/sgdoc/documento';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { BehaviorSubject } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { ActaActionService } from '../../acta.action.service';
import { ActaMemoriasFragment, MemoriaListado } from './acta-memorias.fragment';
@Component({
  selector: 'sgi-acta-memorias',
  templateUrl: './acta-memorias.component.html',
  styleUrls: ['./acta-memorias.component.scss']
})
export class ActaMemoriasComponent extends FragmentComponent implements OnInit {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  displayedColumns: string[];

  memorias$: BehaviorSubject<MemoriaListado[]>;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    protected readonly convocatoriaReunionService: ConvocatoriaReunionService,
    formService: ActaActionService,
    private documentoService: DocumentoService,
    private evaluacionService: EvaluacionService
  ) {
    super(formService.FRAGMENT.MEMORIAS, formService);
    this.displayedColumns = ['numReferencia', 'version', 'dictamen.nombre', 'informe'];
    this.memorias$ = (this.fragment as ActaMemoriasFragment).memorias$;
  }

  /**
   * Visualiza el informe de evaluación seleccionado.
   * @param idEvaluacion id de la evaluación del informe
   */
  visualizarInforme(idEvaluacion: number): void {
    const documento: IDocumento = {} as IDocumento;
    this.evaluacionService.getDocumentoEvaluacion(idEvaluacion).pipe(
      switchMap((documentoInfo: IDocumento) => {
        documento.nombre = documentoInfo.nombre;
        return this.documentoService.downloadFichero(documentoInfo.documentoRef);
      })
    ).subscribe(response => {
      triggerDownloadToUser(response, documento.nombre);
    });
  }
}
