export class NumberUtils {

  private constructor() { }

  public static roundNumber(amount: number, maxDecimalDigits = 2): number {
    const maxDecimals = Math.pow(10, maxDecimalDigits);
    return Math.round((amount + Number.EPSILON) * maxDecimals) / maxDecimals;
  }
}
