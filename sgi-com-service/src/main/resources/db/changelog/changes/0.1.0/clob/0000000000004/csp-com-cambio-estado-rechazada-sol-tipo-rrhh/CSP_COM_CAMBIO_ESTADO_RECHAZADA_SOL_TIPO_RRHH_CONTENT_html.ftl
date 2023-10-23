<#assign data = CSP_COM_CAMBIO_ESTADO_RECHAZADA_SOL_TIPO_RRHH_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>

    <p>Le informamos que con fecha ${data.fechaEstado?datetime.iso?string('dd/MM/yyyy')}, su solicitud ${data.codigoInternoSolicitud} <#if data.tituloConvocatoria?has_content>dentro de la convocatoria ${data.tituloConvocatoria} </#if>ha sido rechazada por el/la tutor/a.</p>

    <p>Reciba un cordial saludo.</p>

    <p>Secci&oacute;n de Recursos Humanos de la Investigaci&oacute;n y Plan Propio</p>

    <p>Área de Investigaci&oacute;n</p>
  </body>
</html>
