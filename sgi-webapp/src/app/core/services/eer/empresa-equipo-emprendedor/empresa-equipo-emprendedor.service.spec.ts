import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { EmpresaEquipoEmprendedorService } from './empresa-equipo-emprendedor.service';

describe('EmpresaEquipoEmprendedorService', () => {
  let service: EmpresaEquipoEmprendedorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(EmpresaEquipoEmprendedorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
