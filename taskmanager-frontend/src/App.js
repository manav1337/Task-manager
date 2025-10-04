// App.js - Add detailed debugging
import React, { useState, useEffect } from 'react';
import { useAuth } from './context/AuthContext';
import Login from './components/Login';
import Register from './components/Register';
import Dashboard from './components/Dashboard';
import AdminDashboard from './components/AdminDashboard';
import './App.css';

function App() {
  const { isAuthenticated, loading, user } = useAuth();
  const [showRegister, setShowRegister] = useState(false);

  // Enhanced debugging
  useEffect(() => {
    console.log('🔍 App.js - Auth State:', {
      isAuthenticated,
      loading,
      user,
      userRole: user?.role,
      isAdmin: user?.role === 'ROLE_ADMIN'
    });
    
    // Check localStorage directly
    const storedUser = localStorage.getItem('user');
    const storedToken = localStorage.getItem('token');
    console.log('🔍 LocalStorage - User:', storedUser);
    console.log('🔍 LocalStorage - Token exists:', !!storedToken);
    
    if (storedUser) {
      try {
        const parsedUser = JSON.parse(storedUser);
        console.log('🔍 Parsed User from Storage:', parsedUser);
        console.log('🔍 Role from Storage:', parsedUser.role);
        console.log('🔍 Is Admin from Storage:', parsedUser.role === 'ROLE_ADMIN');
      } catch (error) {
        console.error('Error parsing user from localStorage:', error);
      }
    }
  }, [isAuthenticated, user]);

  if (loading) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '100vh' 
      }}>
        <div>Loading...</div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return (
      <div className="App">
        {showRegister ? (
          <Register switchToLogin={() => setShowRegister(false)} />
        ) : (
          <Login switchToRegister={() => setShowRegister(true)} />
        )}
      </div>
    );
  }

  // Check if user is admin - with multiple role checks
  const isAdmin = user?.role === 'ROLE_ADMIN' || 
                  user?.role === 'ROLE_ADMIN' || 
                  user?.role?.includes('ADMIN');

  console.log('🎯 Final Decision - Rendering:', {
    username: user?.username,
    role: user?.role,
    isAdmin: isAdmin,
    rendering: isAdmin ? 'AdminDashboard' : 'UserDashboard'
  });

  return (
    <div className="App">
      {isAdmin ? <AdminDashboard /> : <Dashboard />}
    </div>
  );
}

export default App;