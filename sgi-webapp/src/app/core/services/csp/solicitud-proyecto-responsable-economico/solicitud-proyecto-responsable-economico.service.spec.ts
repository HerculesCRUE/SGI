import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudProyectoResponsableEconomicoService } from './solicitud-proyecto-responsable-economico.service';

describe('SolicitudProyectoResponsableEconomicoService', () => {
  let service: SolicitudProyectoResponsableEconomicoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudProyectoResponsableEconomicoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
