/**
 * Retorna a URL relativa para um relatório.
 */
export function getReportUrl(key: string, format: 'pdf' | 'excel' = 'pdf'): string {
  return `/relatorios/${encodeURIComponent(key)}?format=${format}`
}
