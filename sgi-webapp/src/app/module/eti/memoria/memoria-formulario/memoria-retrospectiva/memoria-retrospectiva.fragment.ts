import { IComite } from '@core/models/eti/comite';
import { ESTADO_RETROSPECTIVA } from '@core/models/eti/estado-retrospectiva';
import { TIPO_EVALUACION } from '@core/models/eti/tipo-evaluacion';
import { ApartadoService } from '@core/services/eti/apartado.service';
import { BloqueService } from '@core/services/eti/bloque.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { FormularioService } from '@core/services/eti/formulario.service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { RespuestaService } from '@core/services/eti/respuesta.service';
import { DatosAcademicosService } from '@core/services/sgp/datos-academicos.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion/vinculacion.service';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { MemoriaFormlyFormFragment } from '../../memoria-formly-form.fragment';

export class MemoriaRetrospectivaFragment extends MemoriaFormlyFormFragment {

  constructor(
    logger: NGXLogger,
    readonly: boolean,
    key: number,
    comite: IComite,
    moduloInv: boolean,
    formularioService: FormularioService,
    bloqueService: BloqueService,
    apartadoService: ApartadoService,
    respuestaService: RespuestaService,
    peticionEvaluacionService: PeticionEvaluacionService,
    vinculacionService: VinculacionService,
    datosAcademicosService: DatosAcademicosService,
    personaService: PersonaService,
    memoriaService: MemoriaService,
    evaluacionService: EvaluacionService
  ) {
    super(
      logger,
      key,
      comite,
      readonly,
      TIPO_EVALUACION.RETROSPECTIVA,
      moduloInv,
      formularioService,
      memoriaService,
      evaluacionService,
      bloqueService,
      respuestaService,
      peticionEvaluacionService,
      vinculacionService,
      datosAcademicosService,
      personaService,
      apartadoService
    );
  }

  protected isEditable(): boolean {
    if (!this.memoria.retrospectiva?.estadoRetrospectiva?.id) {
      return false;
    }
    const estado = this.memoria.retrospectiva.estadoRetrospectiva.id as ESTADO_RETROSPECTIVA;
    switch (+estado) {
      case ESTADO_RETROSPECTIVA.PENDIENTE:
      case ESTADO_RETROSPECTIVA.COMPLETADA:
        // La fecha de la restrospectiva es igual o menor a la fecha actual
        return this.memoria.retrospectiva.fechaRetrospectiva <= DateTime.now();
      default:
        return false;
    }
  }

}
