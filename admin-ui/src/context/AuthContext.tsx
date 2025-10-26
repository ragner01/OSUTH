import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react'
import { UserManager, User } from 'oidc-client-ts'

interface AuthContextType {
  user: User | null
  isAuthenticated: boolean
  login: () => Promise<void>
  logout: () => Promise<void>
  isLoading: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

const authConfig = {
  authority: import.meta.env.VITE_KEYCLOAK_URL + '/realms/' + import.meta.env.VITE_KEYCLOAK_REALM,
  client_id: import.meta.env.VITE_KEYCLOAK_CLIENT_ID,
  redirect_uri: `${window.location.origin}/login/callback`,
  response_type: 'code',
  scope: 'openid profile email',
}

const userManager = new UserManager(authConfig)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    userManager.getUser().then((user) => {
      setUser(user)
      setIsLoading(false)
    })

    userManager.events.addUserLoaded((user) => {
      setUser(user)
      setIsLoading(false)
    })

    userManager.events.addUserUnloaded(() => {
      setUser(null)
      setIsLoading(false)
    })

    return () => {
      userManager.events.removeUserLoaded(() => {})
      userManager.events.removeUserUnloaded(() => {})
    }
  }, [])

  const login = async () => {
    await userManager.signinRedirect()
  }

  const logout = async () => {
    await userManager.signoutRedirect()
    setUser(null)
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user && !user.expired,
        login,
        logout,
        isLoading,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}

