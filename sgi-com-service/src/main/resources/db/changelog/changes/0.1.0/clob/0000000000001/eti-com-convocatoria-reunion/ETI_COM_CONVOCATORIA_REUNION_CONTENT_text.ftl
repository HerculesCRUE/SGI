<#assign horaSegunda = ETI_CONVOCATORIA_REUNION_HORA_INICIO_SEGUNDA />
<#assign fechaEvaluacion = ETI_CONVOCATORIA_REUNION_FECHA_EVALUACION />

En nombre del/de la Sr/a. Secretario/a del/de la ${ETI_COMITE_NOMBRE_INVESTIGACION}, le informo de que, como miembro del citado / de la citada ${ETI_COMITE}, se le convoca a la reunión que se celebrará por videoconferencia el próximo ${fechaEvaluacion?datetime("dd/MM/yyyy")?string("EEEE")?capitalize}, ${fechaEvaluacion?datetime("dd/MM/yyyy")?string("dd")} de ${fechaEvaluacion?datetime("dd/MM/yyyy")?string("MMMM")?capitalize}, a las ${ETI_CONVOCATORIA_REUNION_HORA_INICIO}:${ETI_CONVOCATORIA_REUNION_MINUTO_INICIO} horas en primera convocatoria<#if horaSegunda!="null"> y a las ${ETI_CONVOCATORIA_REUNION_HORA_INICIO_SEGUNDA}:${ETI_CONVOCATORIA_REUNION_MINUTO_INICIO_SEGUNDA} horas en segunda convocatoria</#if>, con el siguiente orden del día:
${ETI_CONVOCATORIA_REUNION_ORDEN_DEL_DIA}
Unirse a la reunión Zoom.
NOTA: La videoconferencia se realizará a través de la aplicación ZOOM.
https://umurcia.zoom.us/j/00000000000
Por favor, se ruega confirme su asistencia.
Un saludo.