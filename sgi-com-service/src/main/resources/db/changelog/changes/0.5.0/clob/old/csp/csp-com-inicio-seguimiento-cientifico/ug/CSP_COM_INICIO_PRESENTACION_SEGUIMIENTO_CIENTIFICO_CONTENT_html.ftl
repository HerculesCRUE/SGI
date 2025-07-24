<#assign data = CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PRESENTACION_SEGUIMIENTO_CIENTIFICO_DATA:
  { 
    "fecha": "2022-01-01",
    "proyectos" : [
      {
        "titulo": "Proyecto 1",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      },
      {
        "titulo": "Proyecto 2",
        "fechaInicio": "2022-01-01T00:00:00Z",
        "fechaFin": "2022-01-31T23:59:59Z"
      }
    ]
  }
-->
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>En el mes de <b>${data.fecha?date.iso?string.MMMM}</b> de <b>${data.fecha?date.iso?string.yyyy}</b> se inicia el per&iacute;odo de presentaci&oacute;n de la justificaci&oacute;n del seguimiento científico de los siguientes proyectos:</p>

    <ul>
    <#list data.proyectos as proyecto>
      <li><b><i><#outputformat "HTML">${proyecto.titulo?esc}</#outputformat></i></b>
        <ul>
          <li>Primer d&iacute;a para la presentaci&oacute;n de la documentaci&oacute;n de justificaci&oacute;n <b>${proyecto.fechaInicio?datetime.iso?string.medium}</b></li>
          <#if proyecto.fechaFin??>
          <li>&Uacute;ltimo d&iacute;a para la presentaci&oacute;n de la documentaci&oacute;n de justificaci&oacute;n <b>${proyecto.fechaFin?datetime.iso?string.medium}</b></li>
          </#if>
        </ul>
      </li>
    </#list>
    </ul>
  </body>
</html>