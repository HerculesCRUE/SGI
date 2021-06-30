import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { TipoEstadoMemoriaService } from './tipo-estado-memoria.service';

describe('TipoEstadoMemoriaService', () => {
  let service: TipoEstadoMemoriaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(TipoEstadoMemoriaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
