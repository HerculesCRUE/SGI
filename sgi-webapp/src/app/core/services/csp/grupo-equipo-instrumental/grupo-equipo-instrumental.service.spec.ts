import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { GrupoEquipoInstrumentalService } from './grupo-equipo-instrumental.service';

describe('GrupoEquipoInstrumentalService', () => {
  let service: GrupoEquipoInstrumentalService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(GrupoEquipoInstrumentalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
