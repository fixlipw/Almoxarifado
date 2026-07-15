import {apiFetch} from '@/services/api'
import type {DashboardResponse} from '@/types/dtos'

export async function getDashboard(): Promise<DashboardResponse> {
    return await apiFetch<DashboardResponse>('/dashboard')
}
