<#assign data = CSP_COM_INICIO_PRESENTACION_GASTO_DATA?eval />
<#--
  Formato CSP_COM_INICIO_PRESENTACION_GASTO_DATA:
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
    <p>In the month of <b>${data.fecha?date.iso?string.MMMM}</b> of <b>${data.fecha?date.iso?string.yyyy}</b>, the period for submitting the justification of expenses for the following projects begins:</p>

    <ul>
    <#list data.proyectos as proyecto>
      <li><b><i><#outputformat "HTML">${proyecto.titulo?esc}</#outputformat></i></b>
        <ul>
          <li>First day for the presentation of the justification documentation <b>${proyecto.fechaInicio?datetime.iso?string.medium}</b></li>
          <#if proyecto.fechaFin??>
          <li>Last day for the presentation of the justification documentation <b>${proyecto.fechaFin?datetime.iso?string.medium}</b></li>
          </#if>
        </ul>
      </li>
    </#list>
    </ul>
  </body>
</html>