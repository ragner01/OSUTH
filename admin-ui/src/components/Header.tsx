import { useAuth } from '../context/AuthContext'
import { UserIcon } from '@heroicons/react/24/outline'

export default function Header() {
  const { user, logout } = useAuth()

  return (
    <header className="bg-white border-b border-gray-200 px-6 py-4">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-primary-600">Osun HIS</h1>
          <p className="text-sm text-gray-500">Operations Console</p>
        </div>
        
        <div className="flex items-center gap-4">
          <div className="flex items-center gap-2">
            <UserIcon className="h-5 w-5 text-gray-400" />
            <span className="text-sm text-gray-700">{user?.profile.name || user?.profile.preferred_username}</span>
          </div>
          <button
            onClick={logout}
            className="text-sm text-red-600 hover:text-red-700 font-medium"
          >
            Logout
          </button>
        </div>
      </div>
    </header>
  )
}

