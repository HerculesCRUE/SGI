import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { SolicitudRrhhRequisitoNivelAcademicoPublicService } from './solicitud-rrhh-requisito-nivel-academico-public.service';

describe('SolicitudRrhhRequisitoNivelAcademicoPublicService', () => {
  let service: SolicitudRrhhRequisitoNivelAcademicoPublicService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudRrhhRequisitoNivelAcademicoPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
