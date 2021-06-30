import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ProyectoResponsableEconomicoService } from './proyecto-responsable-economico.service';

describe('ProyectoResponsableEconomicoService', () => {
  let service: ProyectoResponsableEconomicoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoResponsableEconomicoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
