import { useEffect, useState } from 'react'

export default function App() {
    const [msg, setMsg] = useState('loading...')

    useEffect(() => {
        fetch('/api/hello').then(r => r.text()).then(setMsg).catch(() => setMsg('API not reachable'))
    }, [])

    return <h1>{msg} and so we begin</h1>
}