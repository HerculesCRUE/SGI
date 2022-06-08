import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { GrupoLineaInvestigacionService } from './grupo-linea-investigacion.service';

describe('GrupoLineaInvestigacionService', () => {
  let service: GrupoLineaInvestigacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(GrupoLineaInvestigacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
