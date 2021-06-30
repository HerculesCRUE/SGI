import { TestBed } from '@angular/core/testing';

import { TipoActividadService } from './tipo-actividad.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';

describe('TipoActividadService', () => {
  let service: TipoActividadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(TipoActividadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
