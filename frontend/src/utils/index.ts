export const DEFAULT_TIME_ZONE = 'America/Sao_Paulo'

export function formatDate(
    input: string | Date,
    locale = 'pt-BR',
    timeZone = DEFAULT_TIME_ZONE,
): string {
    try {
        const d = typeof input === 'string' ? new Date(input) : input
        if (Number.isNaN(d.getTime())) return ''
        return new Intl.DateTimeFormat(locale, {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            timeZone,
        }).format(d)
    } catch {
        return ''
    }
}

export function noop(): void {
}

export function getLocalISOString(date: Date = new Date()): string {
    const pad = (num: number) => (num < 10 ? '0' : '') + num
    return date.getFullYear() +
        '-' + pad(date.getMonth() + 1) +
        '-' + pad(date.getDate()) +
        'T' + pad(date.getHours()) +
        ':' + pad(date.getMinutes()) +
        ':' + pad(date.getSeconds())
}
