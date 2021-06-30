import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ConvocatoriaRequisitoEquipoService } from './convocatoria-requisito-equipo.service';


describe('ConvocatoriaRequisitoEquipoService', () => {
  let service: ConvocatoriaRequisitoEquipoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ConvocatoriaRequisitoEquipoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

