
import { IEmpresaEquipoEmprendedor } from '@core/models/eer/empresa-equipo-emprendedor';
import { IEmpresaExplotacionResultados } from '@core/models/eer/empresa-explotacion-resultados';
import { IPersona } from '@core/models/sgp/persona';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IEmpresaEquipoEmprendedorRequest } from './empresa-equipo-emprendedor-request';

class EmpresaEquipoEmprendedorRequestConverter
  extends SgiBaseConverter<IEmpresaEquipoEmprendedorRequest, IEmpresaEquipoEmprendedor> {
  toTarget(value: IEmpresaEquipoEmprendedorRequest): IEmpresaEquipoEmprendedor {
    if (!value) {
      return value as unknown as IEmpresaEquipoEmprendedor;
    }
    return {
      id: undefined,
      miembroEquipo: value.miembroEquipoRef ? { id: value.miembroEquipoRef } as IPersona : undefined,
      empresa: value.empresaId ? { id: value.empresaId } as IEmpresaExplotacionResultados : undefined,
    };
  }

  fromTarget(value: IEmpresaEquipoEmprendedor): IEmpresaEquipoEmprendedorRequest {
    if (!value) {
      return value as unknown as IEmpresaEquipoEmprendedorRequest;
    }
    return {
      id: value.id,
      miembroEquipoRef: value.miembroEquipo.id,
      empresaId: value.empresa.id,
    };
  }
}

export const EMPRESA_EQUIPO_EMPRENDEDOR_REQUEST_CONVERTER = new EmpresaEquipoEmprendedorRequestConverter();
