import {sigaaFetch} from '@/services/api'
import {hasSigaaAlunoError, type SigaaAluno, type SigaaCredentials} from '@/types/auth'

export async function validarCredenciaisSigaa(payload: SigaaCredentials): Promise<SigaaAluno> {
  const response = await sigaaFetch<SigaaAluno>('/sigaa', {
	method: 'POST',
	headers: {
	  'Content-Type': 'application/json',
	},
	body: JSON.stringify(payload),
  })

  if (hasSigaaAlunoError(response)) {
	throw new Error(response.message || 'Não foi possível validar seu acesso. Verifique suas credenciais e tente novamente.')
  }

  return response
}

