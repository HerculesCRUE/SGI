import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { GrupoLineaInvestigadorService } from './grupo-linea-investigador.service';

describe('GrupoLineaInvestigadorService', () => {
  let service: GrupoLineaInvestigadorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(GrupoLineaInvestigadorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
