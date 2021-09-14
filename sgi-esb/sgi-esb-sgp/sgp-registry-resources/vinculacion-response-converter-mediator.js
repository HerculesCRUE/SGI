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
    }
    if (vinculacion.empresaRef) {
      vinculacionResponse.empresaRef = vinculacion.empresaRef;
    }

    if (vinculacion.categoriaProfesionalPDI) {
      if (vinculacion.categoriaProfesionalPDI) {
        vinculacionResponse.categoriaProfesional = vinculacion.categoriaProfesionalPDI;
      }
      if (vinculacion.fechaObtencionCategoriaPDI) {
        vinculacionResponse.fechaObtencionCategoria = vinculacion.fechaObtencionCategoriaPDI;
      }
      if (vinculacion.departamentoPDI) {
        vinculacionResponse.departamento = vinculacion.departamentoPDI;
      }
    } else if (vinculacion.categoriaProfesionalPAS) {
      if (vinculacion.categoriaProfesionalPAS) {
        vinculacionResponse.categoriaProfesional = vinculacion.categoriaProfesionalPAS;
      }
      if (vinculacion.fechaObtencionCategoriaPAS) {
        vinculacionResponse.fechaObtencionCategoria = vinculacion.fechaObtencionCategoriaPAS;
      }
      if (vinculacion.departamentoPAS) {
        vinculacionResponse.departamento = vinculacion.departamentoPAS;
      }
    }

    mc.setPayloadJSON(vinculacionResponse);
  }

  log.info("vinculacion-response-converter-mediator.mediate() - end");
}
