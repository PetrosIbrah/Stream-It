export type Section = 'home' | 'library' | 'recommended' | 'movies' | 'shows'

export interface MediaItem {
    id: string
    title: string
    thumbnailUrl: string
    sections: Section[]
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
    const res = await fetch(path, init)
    if (!res.ok) {
        throw new Error(`${res.status} ${res.statusText}`)
    }
    return res.status === 204 ? (undefined as T) : ((await res.json()) as T)
}

export const homeApi = {
    getMedia: (signal?: AbortSignal) =>
        request<string[]>('/api/mediaimages', { signal }),

    logOut: () => request<void>('/api/auth/logout', { method: 'POST' }),
}