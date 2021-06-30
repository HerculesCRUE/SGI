import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ConvocatoriaEntidadConvocanteService } from './convocatoria-entidad-convocante.service';

describe('ConvocatoriaEntidadConvocanteService', () => {
  let service: ConvocatoriaEntidadConvocanteService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ],
      providers: [
      ],
    });
    service = TestBed.inject(ConvocatoriaEntidadConvocanteService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
