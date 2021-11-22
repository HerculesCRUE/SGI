package org.crue.hercules.sgi.framework.exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class NotFoundExceptionTest {
  @Test
  void testNotFoundException_emptyConstructor() {
    Assertions.assertThatThrownBy(() -> {
      throw new NotFoundException();
    }).isInstanceOf(NotFoundException.class).hasMessage("");
  }

  @Test
  void testNotFoundException_messageConstructor() {
    Assertions.assertThatThrownBy(() -> {
      throw new NotFoundException("My message");
    }).isInstanceOf(NotFoundException.class).hasMessage("My message");
  }

}
