import {sigaaFetch} from '@/services/api'
import type { SigaaAluno, SigaaCredentials } from '@/types/auth'

export async function validarCredenciaisSigaa(payload: SigaaCredentials): Promise<SigaaAluno> {
  const response = await sigaaFetch<SigaaAluno>('/sigaa', {
	method: 'POST',
	headers: {
	  'Content-Type': 'application/json',
	},
	body: JSON.stringify(payload),
  })

  if (response.error) {
	throw new Error(response.message || 'Não foi possível validar o acesso no SIGAA.')
  }

  return response
}

