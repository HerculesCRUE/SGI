import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { GrupoResponsableEconomicoService } from './grupo-responsable-economico.service';

describe('GrupoResponsableEconomicoService', () => {
  let service: GrupoResponsableEconomicoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(GrupoResponsableEconomicoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
