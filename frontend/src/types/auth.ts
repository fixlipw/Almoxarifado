import type { Usuario } from '@/types/entities'

export interface SigaaCredentials {
  login: string
  senha: string
}

export interface SigaaCadeira {
  codigo: string
  componente: string
  carga_horaria: number
  local: string
  dias: string
  horario: string
}

export interface SigaaAluno {
  error: boolean
  login: string
  nome: string
  foto: string
  matricula: string
  curso: string
  nivel: string
  status: string
  entrada: string
  semestre: string
  cadeiras: SigaaCadeira[]
  message?: string
}

export interface RegistroComplementarForm {
  email: string
  senha: string
  confirmarSenha: string
  sobrenome: string
}

export interface AuthSession {
  usuario: Usuario
  sigaa: SigaaAluno
  authenticatedAt: string
}

