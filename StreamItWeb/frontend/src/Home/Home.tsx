import { useEffect, useState } from 'react'
import { homeApi, type Section } from './HomeAPI'
import './Home.css'

const SECTION_TITLES: Record<Section, string> = {
    home: 'Home',
    library: 'Library',
    recommended: 'Recommended',
    movies: 'Movies',
    shows: 'Shows',
}

interface HomeProps {
    onOpenMedia?: (url: string) => void
    onOpenSettings?: () => void
    onLoggedOut?: () => void
}

export default function Home({ onOpenMedia, onOpenSettings, onLoggedOut }: HomeProps) {
    const [section, setSection] = useState<Section>('home')
    const [items, setItems] = useState<string[]>([])
    const [menuOpen, setMenuOpen] = useState(false)
    const [profileOpen, setProfileOpen] = useState(false)
    const [serverError, setServerError] = useState(false)

    useEffect(() => {
        const controller = new AbortController()

        homeApi.getMedia(controller.signal)
            .then((data) => {
                console.log('Image paths from backend:', data)
                setItems(data)
                setServerError(false)
            })
            .catch((err: unknown) => {
                if (err instanceof DOMException && err.name === 'AbortError') return
                setServerError(true)
            })

        return () => controller.abort()
    }, [])

    useEffect(() => {
        const onKey = (e: KeyboardEvent) => {
            if (e.key === 'Escape') {
                setMenuOpen(false)
                setProfileOpen(false)
            }
        }
        window.addEventListener('keydown', onKey)
        return () => window.removeEventListener('keydown', onKey)
    }, [])

    const toggleMenu = () => {
        setMenuOpen((open) => !open)
        setProfileOpen(false)
    }

    const toggleProfile = () => {
        setProfileOpen((open) => !open)
        setMenuOpen(false)
    }

    const chooseSection = (next: Section) => {
        setSection(next)
        setMenuOpen(false)
    }

    const handleSettings = () => {
        setProfileOpen(false)
        onOpenSettings?.()
    }

    const handleLogOut = async () => {
        setProfileOpen(false)
        try {
            await homeApi.logOut()
            onLoggedOut?.()
        } catch {
            setServerError(true)
        }
    }

    return (
        <div className="home">
            <header className="home-header">
                <button
                    className="icon-button menu-button"
                    aria-label="Menu"
                    aria-expanded={menuOpen}
                    onClick={toggleMenu}
                >
                    <MenuIcon />
                </button>

                <h1 className="home-title">{SECTION_TITLES[section]}</h1>

                <button
                    className="icon-button profile-button"
                    aria-label="Profile"
                    aria-expanded={profileOpen}
                    onClick={toggleProfile}
                >
                    <UserIcon />
                </button>
            </header>

            <main className="gallery-scroll">
                <div className="gallery">
                    {items.map((url, index) => (
                        <button
                            key={`${url}-${index}`}
                            className="media-image"
                            title={`Image ${index + 1}`}
                            onClick={() => onOpenMedia?.(url)}
                        >
                            <img
                                src={`/images/${encodeURIComponent(url)}`}
                                alt={`Image ${index + 1}`}
                            />
                        </button>
                    ))}
                </div>
            </main>

            {menuOpen && (
                <nav className="dropdown menu-pane" aria-label="Sections">
                    <button onClick={() => chooseSection('home')}>Home</button>
                    <button onClick={() => chooseSection('library')}>Library</button>
                    <hr />
                    <button onClick={() => chooseSection('recommended')}>Recommended</button>
                    <hr />
                    <button onClick={() => chooseSection('movies')}>Movies</button>
                    <button onClick={() => chooseSection('shows')}>Shows</button>
                </nav>
            )}

            {profileOpen && (
                <div className="dropdown profile-pane">
                    <button onClick={handleSettings}>Settings</button>
                    <hr />
                    <button onClick={handleLogOut}>Log Out</button>
                </div>
            )}

            {serverError && (
                <div className="error-pane" role="alert">
                    <WarningIcon />
                    <span className="error-text">Unable to Connect to Server!</span>
                    <button
                        className="error-close"
                        aria-label="Dismiss error"
                        onClick={() => setServerError(false)}
                    >
                        X
                    </button>
                </div>
            )}
        </div>
    )
}

function MenuIcon() {
    return (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" aria-hidden="true">
            <path d="M4 6h16M4 12h16M4 18h16" />
        </svg>
    )
}

function UserIcon() {
    return (
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
            <circle cx="12" cy="8" r="4" />
            <path d="M4 21c0-4 4-6 8-6s8 2 8 6" />
        </svg>
    )
}

function WarningIcon() {
    return (
        <svg className="error-icon" viewBox="0 0 24 24" fill="none" stroke="#ff8080" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
            <path d="M12 3 2 21h20L12 3Z" />
            <path d="M12 10v5M12 18v.01" />
        </svg>
    )
}