import { environment } from '@env';
import { FormlyFieldConfig } from '@ngx-formly/core';
import { Settings } from 'luxon';
import { FormlyUtils } from './formly-utils';

describe('FormlyUtils', () => {

  const modelPersona = {
    personaId: '1',
    nombre: 'nombre de prueba',
    apellidos: 'apellidos de prueba',
    tipoDocumentoId: '2',
    numeroDocumento: '07852369Y',
    sexoId: '2',
    fechaNacimiento: '2021-05-11T00:00:00.000+02:00',
    paisNacimientoId: '1',
    regionNacimientoId: '3',
    ciudadNacimiento: 'Madrid',
    empresaId: '3',
    tipoViaContactoId: '3',
    nombreViaContacto: 'Castellana',
    numeroViaContacto: '123',
    ampliacion: 'ampliación',
    paisContactoId: '1',
    regionContactoId: '2',
    provinciaContactoId: '10',
    codigoPostalContacto: '48152',
    ciudadContacto: 'Alcorcón (Madrid)',
    areaConocimientoId: '1',
    nivelAcademicoId: '1',
    fechaObtencion: '2021-05-11T00:00:00.000+02:00',
    categoriaPdiId: '1',
    departamentoPdiId: '2',
    fechaCategoriaPdi: '2021-05-25T00:00:00.000+02:00',
    fechaFinCategoriaPdi: '2021-05-26T00:00:00.000+02:00',
    categoriaPasId: '2',
    unidadPasId: '1',
    fechaCategoriaPas: '2021-05-25T00:00:00.000+02:00',
    fechaFincategoriaPas: '2021-05-27T00:00:00.000+02:00',
    fechaInicioVinculacion: '2021-05-26T00:00:00.000+02:00',
    emails: [
      'a@prueba.com',
      'b@prueba.com'
    ],
    telefonos: [
      '666123456',
      '666654321'
    ]
  };

  const modelPersonaTableOne = {
    personaId: '1',
    nombre: 'nombre de prueba',
    apellidos: 'apellidos de prueba',
    tipoDocumentoId: '2',
    numeroDocumento: '07852369Y',
    sexoId: '2',
    fechaNacimiento: '2021-05-11T00:00:00.000+02:00',
    paisNacimientoId: '1',
    regionNacimientoId: '3',
    ciudadNacimiento: 'Madrid',
    empresaId: '3',
    tipoViaContactoId: '3',
    nombreViaContacto: 'Castellana',
    numeroViaContacto: '123',
    ampliacion: 'ampliación',
    paisContactoId: '1',
    regionContactoId: '2',
    provinciaContactoId: '10',
    codigoPostalContacto: '48152',
    ciudadContacto: 'Alcorcón (Madrid)',
    areaConocimientoId: '1',
    nivelAcademicoId: '1',
    fechaObtencion: '2021-05-11T00:00:00.000+02:00',
    categoriaPdiId: '1',
    departamentoPdiId: '2',
    fechaCategoriaPdi: '2021-05-25T00:00:00.000+02:00',
    fechaFinCategoriaPdi: '2021-05-26T00:00:00.000+02:00',
    categoriaPasId: '2',
    unidadPasId: '1',
    fechaCategoriaPas: '2021-05-25T00:00:00.000+02:00',
    fechaFincategoriaPas: '2021-05-27T00:00:00.000+02:00',
    fechaInicioVinculacion: '2021-05-26T00:00:00.000+02:00',
    emails: [{
      email:
        'a@prueba.com'
    },
    {
      email:
        'b@prueba.com'
    }
    ],
    telefonos: [{
      telefono:
        '666123456'
    },
    {
      telefono:
        '666654321'
    }
    ]
  };

  const formlyPersona: FormlyFieldConfig[] = [
    {
      fieldGroup: [
        {
          fieldGroup: [
            {
              key: 'nombre',
              type: 'input',
              templateOptions: {
                label: 'Nombre',
                placeholder: 'Introduce nombre',
                disabled: true
              }
            },
            {
              key: 'apellidos',
              type: 'input',
              templateOptions: {
                label: 'Apellidos',
                placeholder: 'Introduce apellidos',
                disabled: true
              }
            },
            {
              fieldGroupClassName: 'row-formly',
              fieldGroup: [
                {
                  key: 'tipoDocumentoId',
                  type: 'select',
                  className: 'flex-1',
                  templateOptions: {
                    label: 'Tipo de documento',
                    placeholder: 'Introduce tipo de documento',
                    disabled: true,
                    options: [
                      {
                        value: '1',
                        label: 'NIF'
                      },
                      {
                        value: '2',
                        label: 'NIE'
                      },
                      {
                        value: '3',
                        label: 'Pasaporte'
                      }
                    ]
                  }
                },
                {
                  key: 'numeroDocumento',
                  type: 'input',
                  className: 'flex-1',
                  templateOptions: {
                    label: 'Número de documento',
                    placeholder: 'Introduce número de documento',
                    disabled: true
                  }
                }
              ]
            },
            {
              key: 'sexoId',
              type: 'select',
              className: 'flex-1',
              templateOptions: {
                label: 'Sexo',
                placeholder: 'Introduce sexoId',
                disabled: true,
                options: [
                  {
                    value: '1',
                    label: 'Hombre'
                  },
                  {
                    value: '2',
                    label: 'Mujer'
                  }
                ]
              }
            }
          ]
        },
        {
          wrappers: [
            'mat-card-group'
          ],
          templateOptions: {
            label: 'Datos Personales'
          },
          fieldGroup: [
            {
              fieldGroupClassName: 'row-formly',
              fieldGroup: [
                {
                  key: 'fechaNacimiento',
                  type: 'datepicker',
                  className: 'flex-1',
                  templateOptions: {
                    label: 'Fecha de nacimiento',
                    placeholder: 'Introduce fecha de nacimiento',
                    disabled: true
                  }
                },
                {
                  key: 'paisNacimientoId',
                  type: 'select-paises',
                  className: 'flex-1',
                  templateOptions: {
                    label: 'País de nacimiento',
                    placeholder: 'Introduce país de nacimiento',
                    disabled: true,
                    valueProp: 'id',
                    labelProp: 'nombre'
                  }
                }
              ]
            },
            {
              fieldGroupClassName: 'row-formly',
              fieldGroup: [
                {
                  key: 'regionNacimientoId',
                  type: 'select-comunidades',
                  className: 'flex-1',
                  templateOptions: {
                    label: 'C. Autón./Reg. de nacimiento',
                    placeholder: 'Introduce C. Autón./Reg. de nacimiento',
                    propertyBound: 'paisNacimientoId',
                    disabled: true,
                    valueProp: 'id',
                    labelProp: 'nombre'
                  }
                },
                {
                  key: 'ciudadNacimiento',
                  type: 'input',
                  className: 'flex-1',
                  templateOptions: {
                    label: 'Ciudad de nacimiento',
                    placeholder: 'Introduce ciudad de nacimiento',
                    disabled: true
                  }
                }
              ]
            }
          ]
        },
        {
          wrappers: [
            'mat-card-group'
          ],
          templateOptions: {
            label: 'Datos académicos'
          },
          fieldGroup: [
            {
              fieldGroupClassName: 'row-formly',
              fieldGroup: [
                {
                  key: 'nivelAcademicoId',
                  type: 'select',
                  className: 'flex-1',
                  templateOptions: {
                    label: 'Nivel académico',
                    placeholder: 'Introduce nivel académico',
                    disabled: true,
                    options: [
                      {
                        value: '1',
                        label: 'Doctor'
                      },
                      {
                        value: '2',
                        label: 'Otro'
                      }
                    ]
                  }
                },
                {
                  key: 'fechaObtencion',
                  type: 'dateTimePicker',
                  className: 'flex-1',
                  templateOptions: {
                    label: 'Fecha de obtención',
                    placeholder: 'Introduce fecha de obtención',
                    disabled: true
                  }
                }
              ]
            }
          ]
        },
        {
          wrappers: [
            'mat-card-group'
          ],
          templateOptions: {
            label: 'Datos de Vinculación'
          },
          fieldGroup: [
            {
              fieldGroupClassName: 'row-formly',
              fieldGroup: [
                {
                  key: 'categoriaPdiId',
                  type: 'select',
                  className: 'flex-1',
                  templateOptions: {
                    label: 'Categoría',
                    placeholder: 'Introduce categoría',
                    disabled: true,
                    options: [
                      {
                        value: '1',
                        label: 'Doctor'
                      },
                      {
                        value: '2',
                        label: 'Otro'
                      }
                    ]
                  }
                },
                {
                  key: 'departamentoPdiId',
                  type: 'select',
                  className: 'flex-1',
                  templateOptions: {
                    label: 'Departamento',
                    placeholder: 'Introduce departamento',
                    disabled: true,
                    options: [
                      {
                        value: '1',
                        label: 'Biomedicina'
                      },
                      {
                        value: '2',
                        label: 'Otro'
                      }
                    ]
                  }
                }
              ]
            },
            {
              fieldGroupClassName: 'row-formly',
              fieldGroup: [
                {
                  key: 'fechaCategoriaPdi',
                  type: 'dateTimePicker',
                  className: 'flex-1',
                  templateOptions: {
                    label: 'Fecha de categoría',
                    placeholder: 'Introduce fecha de categoría',
                    disabled: true
                  }
                },
                {
                  key: 'fechaFinCategoriaPdi',
                  type: 'dateTimePicker',
                  className: 'flex-1',
                  templateOptions: {
                    label: 'Fecha fin de categoría',
                    placeholder: 'Introduce fecha fin de categoría',
                    disabled: true
                  }
                }
              ]
            },
            {
              type: 'table-crud',
              key: 'areaConocimiento',
              templateOptions: {
                disabled: true
              },
              fieldArray: {
                templateOptions: {
                  text: 'Área de conocimiento',
                  gender: 'male'
                },
                fieldGroup: [
                  {
                    key: 'niveles',
                    type: 'input',
                    templateOptions: {
                      label: 'Niveles',
                      placeholder: 'Introduce niveles',
                      required: true,
                      order: 20
                    }
                  },
                  {
                    key: 'nivelSeleccionado',
                    type: 'input',
                    templateOptions: {
                      label: 'Nivel seleccionado',
                      placeholder: 'Introduce nivel seleccionado',
                      required: true,
                      order: 30
                    }
                  }
                ]
              }
            },
            {
              fieldGroupClassName: 'row-formly',
              fieldGroup: [
                {
                  key: 'categoriaPasId',
                  type: 'select',
                  className: 'flex-1',
                  templateOptions: {
                    label: 'Categoría',
                    placeholder: 'Introduce categoría',
                    disabled: true,
                    options: [
                      {
                        value: '1',
                        label: 'Doctor'
                      },
                      {
                        value: '2',
                        label: 'Otro'
                      }
                    ]
                  }
                },
                {
                  key: 'unidadPasId',
                  type: 'select',
                  className: 'flex-1',
                  templateOptions: {
                    label: 'Unidad',
                    placeholder: 'Introduce unidad',
                    disabled: true,
                    options: [
                      {
                        value: '1',
                        label: 'Biomedicina'
                      },
                      {
                        value: '2',
                        label: 'Otro'
                      }
                    ]
                  }
                }
              ]
            },
            {
              fieldGroupClassName: 'row-formly',
              fieldGroup: [
                {
                  key: 'fechaCategoriaPas',
                  type: 'dateTimePicker',
                  className: 'flex-1',
                  templateOptions: {
                    label: 'Fecha de categoría',
                    placeholder: 'Introduce fecha de categoría',
                    disabled: true
                  }
                },
                {
                  key: 'fechaFincategoriaPas',
                  type: 'dateTimePicker',
                  className: 'flex-1',
                  templateOptions: {
                    label: 'Fecha fin de categoría',
                    placeholder: 'Introduce fecha fin de categoría',
                    disabled: true
                  }
                }
              ]
            },
            {
              fieldGroupClassName: 'row-formly',
              fieldGroup: [
                {
                  key: 'empresaId',
                  type: 'select-empresas',
                  className: 'flex-2',
                  templateOptions: {
                    label: 'Entidad externa',
                    placeholder: 'Seleccion entidad externa',
                    disabled: false
                  }
                },
                {
                  key: 'fechaInicioVinculacion',
                  type: 'dateTimePicker',
                  className: 'flex-1',
                  templateOptions: {
                    label: 'Fecha inicio de vinculación',
                    placeholder: 'Introduce fecha inicio de vinculación',
                    disabled: false
                  }
                }
              ]
            },
            {
              type: 'table-crud',
              key: 'historicosEntidades',
              templateOptions: {
                title: 'Histórico de entidades externas de vinculación',
                entity: 'histórico de entidad',
                gender: 'male',
                disabled: true
              },
              fieldArray: {
                templateOptions: {
                  text: 'Histórico de entidades externas de vinculación',
                  gender: 'male'
                },
                fieldGroup: [
                  {
                    key: 'entidad',
                    type: 'input',
                    templateOptions: {
                      label: 'Entidad',
                      placeholder: 'Introduce entidad',
                      required: true,
                      order: 20
                    }
                  },
                  {
                    key: 'inicioVinculacion',
                    type: 'dateTimePicker',
                    templateOptions: {
                      label: 'Inicio vinculación',
                      placeholder: 'Introduce inicio de vinculación',
                      luxonFormat: 'short',
                      required: true,
                      order: 30
                    }
                  },
                  {
                    key: 'finVinculacion',
                    type: 'dateTimePicker',
                    templateOptions: {
                      label: 'Fin vinculación',
                      placeholder: 'Introduce fin de vinculación',
                      luxonFormat: 'short',
                      required: true,
                      order: 40
                    }
                  }
                ]
              }
            }
          ]
        },
        {
          wrappers: [
            'mat-card-group'
          ],
          templateOptions: {
            label: 'Datos de Contacto'
          },
          fieldGroup: [
            {
              fieldGroupClassName: 'row-formly',
              fieldGroup: [
                {
                  key: 'tipoViaContactoId',
                  type: 'select',
                  className: 'flex-1',
                  templateOptions: {
                    label: 'Tipo de vía',
                    placeholder: 'Introduce tipo de vía',
                    disabled: true,
                    options: [
                      {
                        value: '1',
                        label: 'Calle'
                      },
                      {
                        value: '2',
                        label: 'Avenida'
                      },
                      {
                        value: '3',
                        label: 'Paseo'
                      },
                      {
                        value: '4',
                        label: 'Callejón'
                      }
                    ]
                  }
                }
              ]
            },
            {
              fieldGroupClassName: 'row-formly',
              fieldGroup: [
                {
                  key: 'nombreViaContacto',
                  type: 'input',
                  className: 'flex-2',
                  templateOptions: {
                    disabled: true,
                    label: 'Nombre de vía',
                    placeholder: 'Introduce nombre de vía'
                  }
                },
                {
                  key: 'numeroViaContacto',
                  type: 'input',
                  className: 'flex-1',
                  templateOptions: {
                    disabled: true,
                    label: 'Número',
                    placeholder: 'Introduce número'
                  }
                }
              ]
            },
            {
              key: 'ampliacion',
              type: 'input',
              className: 'flex-1',
              templateOptions: {
                disabled: true,
                label: 'Ampliación',
                placeholder: 'Introduce ampliación'
              }
            },
            {
              fieldGroupClassName: 'row-formly',
              fieldGroup: [
                {
                  key: 'paisContactoId',
                  type: 'select-paises',
                  className: 'flex-1',
                  templateOptions: {
                    label: 'País de contacto',
                    placeholder: 'Introduce país de dirección de contacto',
                    disabled: true,
                    valueProp: 'id',
                    labelProp: 'nombre'
                  }
                },
                {
                  key: 'regionContactoId',
                  type: 'select-comunidades',
                  className: 'flex-1',
                  templateOptions: {
                    label: 'C. Autón./Reg. de dirección de contacto',
                    placeholder: 'Introduce C. Autón./Reg. de dirección de contacto',
                    propertyBound: 'paisContactoId',
                    disabled: true,
                    valueProp: 'id',
                    labelProp: 'nombre'
                  }
                },
                {
                  key: 'provinciaContactoId',
                  type: 'select-provincias',
                  className: 'flex-1',
                  templateOptions: {
                    label: 'Provincia de dirección de contacto',
                    placeholder: 'Introduce provincia de dirección de contacto',
                    propertyBound: 'regionContactoId',
                    disabled: true,
                    valueProp: 'id',
                    labelProp: 'nombre'
                  }
                }
              ]
            },
            {
              fieldGroupClassName: 'row-formly',
              fieldGroup: [
                {
                  key: 'codigoPostalContacto',
                  type: 'input',
                  className: 'flex-1',
                  templateOptions: {
                    label: 'Código postal de contacto',
                    placeholder: 'Introduce código postal de contacto',
                    disabled: true
                  }
                }
              ]
            },
            {
              fieldGroupClassName: 'row-formly',
              fieldGroup: [
                {
                  key: 'ciudadContacto',
                  type: 'input',
                  className: 'flex-1',
                  templateOptions: {
                    label: 'Ciudad de contacto',
                    placeholder: 'Introduce ciudad de contacto',
                    disabled: true
                  }
                }
              ]
            },
            {
              type: 'table-crud-one-element',
              key: 'telefonos',
              templateOptions: {
                title: 'Télefonos',
                entity: 'teléfono',
                gender: 'male',
                disabled: false
              },
              fieldArray: {
                templateOptions: {
                  text: 'Télefonos'
                },
                fieldGroup: [
                  {
                    key: 'telefono',
                    type: 'input',
                    templateOptions: {
                      label: 'Teléfono',
                      placeholder: 'Introduce teléfono',
                      required: true,
                      order: 20
                    }
                  }
                ]
              }
            },
            {
              type: 'table-crud-one-element',
              key: 'emails',
              templateOptions: {
                title: 'emails',
                entity: 'email',
                gender: 'male',
                disabled: false
              },
              fieldArray: {
                templateOptions: {
                  text: 'Emails'
                },
                fieldGroup: [
                  {
                    key: 'email',
                    type: 'input',
                    templateOptions: {
                      label: 'Email',
                      placeholder: 'Introduce Email',
                      required: true,
                      order: 20
                    }
                  }
                ]
              }
            }
          ]
        }
      ]
    }
  ];

  const formlyUtils = (FormlyUtils as any);

  beforeEach(() => {
    Settings.defaultZoneName = environment.defaultTimeZone;
  });

  afterEach(() => {
    Settings.defaultZoneName = undefined;
  });

  it('Comprueba que se cambian las fechas de tipo datepicker y dateTimePicker al parsear de JSON a Model y a la inversa', () => {
    console.log(formlyPersona);
    console.log(modelPersona);
    formlyUtils.convertStringToDateTime(modelPersona);
    formlyUtils.convertDateTimeToString(modelPersona, formlyPersona);
    console.log(modelPersona);
    expect(modelPersona.fechaNacimiento === '2021-05-11').toBeTruthy();
    expect(modelPersona.fechaCategoriaPdi === '2021-05-24T22:00:00Z').toBeTruthy();
    expect(modelPersona.fechaFinCategoriaPdi === '2021-05-25T22:00:00Z').toBeTruthy();
  });

  it('Comprueba que se parsean los campos table-crud-one-element al parsear de JSON a Model', () => {
    console.log('modelPersona INICIO');
    console.log(modelPersona);
    formlyUtils.convertJSONToTableOneElement(modelPersona, formlyPersona);
    console.log('modelPersona convertJSONToTableOneElement');
    console.log(modelPersona);
    expect(modelPersona.telefonos.length > 0).toBeTruthy();
    expect(modelPersona.telefonos[0]['telefono'] === '666123456').toBeTruthy();
    expect(modelPersona.telefonos[1]['telefono'] === '666654321').toBeTruthy();
    expect(modelPersona.emails[0]['email'] === 'a@prueba.com').toBeTruthy();
    expect(modelPersona.emails[1]['email'] === 'b@prueba.com').toBeTruthy();

  });

  it('Comprueba que se parsean los campos table-crud-one-element al parsear de Model a JSON', () => {

    console.log('modelPersonaTableOne');
    console.log(modelPersonaTableOne);
    formlyUtils.convertTableOneElementToJSON(modelPersonaTableOne, formlyPersona);
    console.log(modelPersonaTableOne);
    console.log('modelPersonaTableOne FIN');
    expect(modelPersonaTableOne.telefonos.length > 0).toBeTruthy();
    expect(!modelPersonaTableOne.telefonos[0]['telefono']).toBeTruthy();
    expect(!modelPersonaTableOne.emails[0]['email']).toBeTruthy();
  });
});
