/**
 * Conversion del objeto vinculacion de la UM al del SGI
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info("vinculacion-response-converter-mediator.mediate() - start");

  var vinculacion = mc.getPayloadJSON();
  if (vinculacion) {
    var vinculacionResponse = {};

    if (vinculacion.areaConocimiento) {
      vinculacionResponse.areaConocimiento = vinculacion.areaConocimiento;
      if (vinculacionResponse.areaConocimiento.nombre) {
        vinculacionResponse.areaConocimiento.nombre = escape(vinculacionResponse.areaConocimiento.nombre);
      }
      if (vinculacionResponse.areaConocimiento.anepDescripcion) {
        vinculacionResponse.areaConocimiento.anepDescripcion = escape(vinculacionResponse.areaConocimiento.anepDescripcion);
      }
    }
    if (vinculacion.empresaRef) {
      vinculacionResponse.empresaRef = vinculacion.empresaRef;
    }
    if (vinculacion.personalPropio) {
      vinculacionResponse.personalPropio = vinculacion.personalPropio;
    }
    if (vinculacion.entidadPropiaRef) {
      vinculacionResponse.entidadPropiaRef = vinculacion.entidadPropiaRef;
    }
    if (vinculacion.centro) {
      vinculacionResponse.centro = vinculacion.centro;
      if (vinculacionResponse.centro.nombre) {
        vinculacionResponse.centro.nombre = escape(vinculacionResponse.centro.nombre);
      }
    }

    var sgiVinculacionCategoriaProfesional = getSGIVinculacionCategoriaProfesional(vinculacion.vinculacionesCategoriasProfesionales);

    if (sgiVinculacionCategoriaProfesional.categoriaProfesional) {
      vinculacionResponse.categoriaProfesional = sgiVinculacionCategoriaProfesional.categoriaProfesional;
    }
    if (sgiVinculacionCategoriaProfesional.fechaObtencion) {
      vinculacionResponse.fechaObtencionCategoria = sgiVinculacionCategoriaProfesional.fechaObtencion;
    }

    if (sgiVinculacionCategoriaProfesional.tipoPersonal) {
      var sgiDepartamento = getSGIDepartmento(
        vinculacion.vinculacionesDepartamentos,
        sgiVinculacionCategoriaProfesional.tipoPersonal
      );
      if (sgiDepartamento) {
        vinculacionResponse.departamento = sgiDepartamento;
      }
    }

    mc.setPayloadJSON(vinculacionResponse);
  }

  log.info("vinculacion-response-converter-mediator.mediate() - end");
}

function getSGIVinculacionCategoriaProfesional(vinculacionesCategoriasProfesionales) {
  var sgiVinculacionCategoriaProfesional;
  if (Array.isArray(vinculacionesCategoriasProfesionales)) {
    vinculacionesCategoriasProfesionales.forEach(
      function (vinculacionCategoriaProfesional) {
        if (vinculacionCategoriaProfesional.tipoPersonal === 'PDI') {
          sgiVinculacionCategoriaProfesional = vinculacionCategoriaProfesional;
        } else if (
          !sgiVinculacionCategoriaProfesional
          && vinculacionCategoriaProfesional.tipoPersonal === 'PAS'
        ) {
          sgiVinculacionCategoriaProfesional = vinculacionCategoriaProfesional;
        }
      }
    );
  }

  if (sgiVinculacionCategoriaProfesional
    && sgiVinculacionCategoriaProfesional.categoriaProfesional
    && sgiVinculacionCategoriaProfesional.categoriaProfesional.nombre) {
    sgiVinculacionCategoriaProfesional.categoriaProfesional.nombre = escape(sgiVinculacionCategoriaProfesional.categoriaProfesional.nombre);
  }

  return sgiVinculacionCategoriaProfesional ? sgiVinculacionCategoriaProfesional : {};
}

function getSGIDepartmento(vinculacionesDepartamentos, tipoPersonal) {
  var sgiDepartamento;
  if (Array.isArray(vinculacionesDepartamentos)) {
    vinculacionesDepartamentos.forEach(
      function (vinculacionDepartamento) {
        if (vinculacionDepartamento.tipoPersonal === tipoPersonal) {
          sgiDepartamento = vinculacionDepartamento.departamento;
          if (sgiDepartamento.nombre) {
            sgiDepartamento.nombre = escape(sgiDepartamento.nombre);
          }
        }
      }
    );
  }
  return sgiDepartamento;
}

function escape(val) {
  if (typeof (val) != "string") return val;
  return val
    .replace(/[\\]/g, '\\\\')
    .replace(/[\/]/g, '\\/')
    .replace(/[\b]/g, '\\b')
    .replace(/[\f]/g, '\\f')
    .replace(/[\n]/g, '\\n')
    .replace(/[\r]/g, '\\r')
    .replace(/[\t]/g, '\\t')
    .replace(/[\"]/g, '\\"')
    .replace(/\\'/g, "\\'");
}