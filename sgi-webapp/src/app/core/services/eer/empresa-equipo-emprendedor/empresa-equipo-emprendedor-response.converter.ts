
import { IEmpresaEquipoEmprendedor } from '@core/models/eer/empresa-equipo-emprendedor';
import { IEmpresaExplotacionResultados } from '@core/models/eer/empresa-explotacion-resultados';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IEmpresaEquipoEmprendedorResponse } from './empresa-equipo-emprendedor-response';


class EmpresaEquipoEmprendedorResponseConverter
  extends SgiBaseConverter<IEmpresaEquipoEmprendedorResponse, IEmpresaEquipoEmprendedor> {
  toTarget(value: IEmpresaEquipoEmprendedorResponse): IEmpresaEquipoEmprendedor {
    if (!value) {
      return value as unknown as IEmpresaEquipoEmprendedor;
    }
    return {
      id: value.id,
      miembroEquipo: value.miembroEquipoRef ? { id: value.miembroEquipoRef } as IPersona : undefined,
      empresa: value.empresaId ? { id: value.empresaId } as IEmpresaExplotacionResultados : undefined,
    };
  }

  fromTarget(value: IEmpresaEquipoEmprendedor): IEmpresaEquipoEmprendedorResponse {
    if (!value) {
      return value as unknown as IEmpresaEquipoEmprendedorResponse;
    }
    return {
      id: value.id,
      miembroEquipoRef: value.miembroEquipo.id,
      empresaId: value.empresa.id,
    };
  }
}

export const EMPRESA_EQUIPO_EMPRENDEDOR_RESPONSE_CONVERTER = new EmpresaEquipoEmprendedorResponseConverter();
