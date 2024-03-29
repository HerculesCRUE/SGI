import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudRrhhRequisitoNivelAcademicoService } from './solicitud-rrhh-requisito-nivel-academico.service';

describe('SolicitudRrhhRequisitoNivelAcademicoService', () => {
  let service: SolicitudRrhhRequisitoNivelAcademicoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudRrhhRequisitoNivelAcademicoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
