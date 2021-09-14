![](./doc/images/logos_feder.png)

# HÉRCULES - SGI 
El objetivo del Proyecto SGI dentro del Proyecto HÉRCULES es crear un Prototipo de un Sistema de Gestión de Investigación (SGI) basado en datos abiertos semánticos que ofrezca una visión global de los datos de investigación del Sistema Universitario Español (SUE) para mejorar la gestión, el análisis y las posibles sinergias entre universidades y el gran público desarrollando e incorporando  soluciones que superen las actualmente disponibles en el mercado.

# HÉRCULES SGI - ESB SGDOC
Servicio para la integración con el sistema de gestión documental (SGDOC).

### Instalación

##### 1. Crear datasource en el WSO2 Enterprise Integrator
- Seleccionar la opción Datasources de la pestaña Configure y hacer click en el botón Add Datasource.
- Rellenar los datos del apartado New Datasource con los datos de la conexión, el nombre tiene que ser **SGDOC_DS**.
- Comprobar que la conexión se establece correctamente.
> El jar del driver de la db seleccionada debe de estar en <PRODUCT_HOME>/lib (ej. /home/wso2carbon/wso2ei-6.6.0/lib), es necesario reiniciar despues de añadirlo.

##### 2. Configurar ruta para almacenar los archivos en el WSO2 Enterprise Integrator
- Seleccionar la opción Local Entries de la pestaña Main.
- En Add Local Entries seleccionar Add in-lined Text Entry y poner en nombre **fileStorePath** y en value la ruta (ej. file:///home/wso2carbon/filestore).

##### 3. Comprobar dependencias
- Comprobar que esta desplegado el CAR del proyecto sgi-esb
- Comprobar que esta el jar del proyecto sgi-esb/class-mediators incluido en <PRODUCT_HOME>/lib

##### 4. Desplegar CAR
- Hacer click derecho sobre el subproyecto composite-application y seleccionar Export Composite Applicaction Project.
- Seleccionar la opción Add dentro de Carbon Applicactions de la pestaña Main y selecionar .car generado.
- Comprobar que aparece en la lista de Carbon Applications.