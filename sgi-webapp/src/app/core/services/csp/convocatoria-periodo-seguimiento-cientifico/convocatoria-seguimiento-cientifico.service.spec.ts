import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ConvocatoriaSeguimientoCientificoService } from './convocatoria-seguimiento-cientifico.service';

describe('ConvocatoriaSeguimientoCientificoService', () => {
  let service: ConvocatoriaSeguimientoCientificoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ],
    });
    service = TestBed.inject(ConvocatoriaSeguimientoCientificoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
