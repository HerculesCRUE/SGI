export function toString(value: any): string {
  if (value === null || value === undefined) {
    return '';
  }
  return value.toString();
}