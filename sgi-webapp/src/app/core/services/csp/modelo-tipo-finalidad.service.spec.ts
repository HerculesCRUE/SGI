import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ModeloTipoFinalidadService } from './modelo-tipo-finalidad.service';

describe('ModeloTipoFinalidadService', () => {
  let service: ModeloTipoFinalidadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ModeloTipoFinalidadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
