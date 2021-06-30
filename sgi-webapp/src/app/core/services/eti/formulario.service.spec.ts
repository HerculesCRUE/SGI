import { TestBed } from '@angular/core/testing';

import { FormularioService } from './formulario.service';
import { LoggerTestingModule } from 'ngx-logger/testing';
import TestUtils from '@core/utils/test-utils';
import { LayoutService } from '@core/services/layout.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('FormularioService', () => {
  let service: FormularioService;

  beforeEach(() => {
    // Mock FormulariosDinamicosService
    const tipoFungibleServiceSpy = jasmine.createSpyObj(FormularioService.name,
      TestUtils.getMethodNames(FormularioService.prototype));

    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule
      ],
      providers: [
        { provide: FormularioService, useValue: tipoFungibleServiceSpy },
        LayoutService,
      ]
    });
    service = TestBed.inject(FormularioService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});