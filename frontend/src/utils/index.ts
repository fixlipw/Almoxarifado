export function formatDate (input: string | Date, locale = 'pt-BR'): string {
  try {
    const d = typeof input === 'string' ? new Date(input) : input
    if (Number.isNaN(d.getTime())) return ''
    return new Intl.DateTimeFormat(locale, {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: "2-digit"
    }).format(d)
  } catch {
    return ''
  }
}

export function noop (): void {}

