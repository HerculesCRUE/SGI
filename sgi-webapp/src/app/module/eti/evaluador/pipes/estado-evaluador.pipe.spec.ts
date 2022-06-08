import { EstadoEvaluadorPipe } from './estado-evaluador.pipe';

describe('EstadoEvaluadorPipe', () => {
  it('create an instance', () => {
    const pipe = new EstadoEvaluadorPipe(null);
    expect(pipe).toBeTruthy();
  });
});
