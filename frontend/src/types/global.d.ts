// Global type declarations for the project

declare global {
    interface ApiResponse<T = unknown> {
        data: T
        success: boolean
        message?: string
    }
}

export {}

