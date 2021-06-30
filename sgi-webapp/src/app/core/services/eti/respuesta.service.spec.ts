import { TestBed } from '@angular/core/testing';

import { RespuestaService } from './respuesta.service';
import TestUtils from '@core/utils/test-utils';
import { LayoutService } from '../layout.service';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('RespuestaService', () => {
  let service: RespuestaService;

  beforeEach(() => {
    // Mock FormulariosDinamicosService
    const tipoFungibleServiceSpy = jasmine.createSpyObj(RespuestaService.name,
      TestUtils.getMethodNames(RespuestaService.prototype));

    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        TestUtils.getIdiomas(),
        LoggerTestingModule,
      ],
      providers: [
        { provide: RespuestaService, useValue: tipoFungibleServiceSpy },
        LayoutService,
      ]
    });
    service = TestBed.inject(RespuestaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});