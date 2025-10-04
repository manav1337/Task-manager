import React, { createContext, useState, useContext, useEffect } from 'react';

// Create Context
const AuthContext = createContext();

// Custom hook to use auth context
export const useAuth = () => {
  return useContext(AuthContext);
};

// Provider component
export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);        // Current user data
  const [token, setToken] = useState(localStorage.getItem('token')); // JWT token
  const [loading, setLoading] = useState(true);  // Loading state
  
// Add this computed property
  const isAuthenticated = !!token; // Simple check - token exists = authenticated

  //  Check token on app start
  useEffect(() => {
    if (token) {
      // Could verify token or fetch user data here
      setLoading(false);
    } else {
      setLoading(false);
    }
  }, [token]);

  //  Login function
  const login = async (token, userData) => {
    localStorage.setItem('token', token); // Save to browser
    setToken(token);
    setUser(userData);
  };

  //  Logout function  
  const logout = () => {
    localStorage.removeItem('token'); // Remove from browser
    setToken(null);
    setUser(null);
  };

  //  Value provided to all components
  const value = {
    user,
    token,
    isAuthenticated,
    login,
    logout,
    loading
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};