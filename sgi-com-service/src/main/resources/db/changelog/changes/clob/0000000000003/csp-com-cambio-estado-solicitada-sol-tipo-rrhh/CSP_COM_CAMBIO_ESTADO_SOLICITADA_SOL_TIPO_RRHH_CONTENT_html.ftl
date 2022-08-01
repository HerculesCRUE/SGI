<#assign data = CSP_COM_CAMBIO_ESTADO_SOLICITADA_SOL_TIPO_RRHH_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>

    <p>Le informamos que con fecha ${data.fechaEstado?datetime.iso?string('dd/MM/yyyy')}, ${data.nombreApellidosSolicitante} ha registrado la solicitud ${data.codigoInternoSolicitud} <#if data.tituloConvocatoria?has_content>dentro de la convocatoria ${data.tituloConvocatoria}<#if data.fechaProvisionalConvocatoria??> que tiene fecha de resolución provisional ${data.fechaProvisionalConvocatoria?datetime.iso?string('dd/MM/yyyy')}</#if></#if> indicando que usted participará como tutor/a del trabajo asociado. Es necesario que valide la solicitud en el sistema SGI Hércules <a href="${data.enlaceAplicacionMenuValidacionTutor}" target="_blank">${data.enlaceAplicacionMenuValidacionTutor}</a>.</p>

    <p>Reciba un cordial saludo.</p>

    <p>Sección de Recursos Humanos de la Investigación y Plan Propio</p>

    <p>Área de Investigación</p>
  </body>
</html>