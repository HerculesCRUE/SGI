import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ConvocatoriaRequisitoEquipoPublicService } from './convocatoria-requisito-equipo-public.service';

describe('ConvocatoriaRequisitoEquipoPublicService', () => {
  let service: ConvocatoriaRequisitoEquipoPublicService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ConvocatoriaRequisitoEquipoPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

