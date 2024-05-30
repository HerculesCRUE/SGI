import { Injectable } from '@angular/core';
import { ReportPrcService } from '@core/services/rep/report-prc/report-prc.service';
import { ReportService } from '@core/services/rep/report.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';

@Injectable()
export class PrcReportService {

  constructor(
    protected readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    protected readonly translate: TranslateService,
    protected reportService: ReportService,
    protected reportPrcService: ReportPrcService,
  ) {
  }

  /**
   * Visualiza el informe de detalle de grupo.
   * @param anio Año de la convocatoria.
   * @param grupoId identificador del grupo.
   */
  getInformeDetalleGrupo(anio: number, grupoId: number): Observable<Blob> {
    return this.reportPrcService.getInformeDetalleGrupo(anio, grupoId);
  }

  /**
 * Visualiza el informe de resumen de puntuación de grupos.
 * @param anio Año de la convocatoria.
 */
  getInformeResumenPuntuacionGrupos(anio: number): Observable<Blob> {
    return this.reportPrcService.getInformeResumenPuntuacionGrupos(anio);
  }

  /**
   * Visualiza el informe de Detalle producción investigador.
   * @param anio Año de la convocatoria
   * @param personaRef Id de la persona
   */
  getInformeDetalleProduccionInvestigador(anio: number, personaRef: string): Observable<Blob> {
    return this.reportPrcService.getDetalleProduccionInvestigador(anio, personaRef);
  }

}
