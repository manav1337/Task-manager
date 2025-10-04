// components/AdminDashboard.js - UPDATED
import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { adminAPI } from '../services/api';
import UserManagement from './admin/UserManagement';
import TaskManagement from './admin/TaskManagement';

const AdminDashboard = () => {
  const { user, logout } = useAuth();
  const [stats, setStats] = useState(null);
  const [activeTab, setActiveTab] = useState('overview');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardStats();
  }, []);

  const fetchDashboardStats = async () => {
    try {
      console.log('🔄 Fetching dashboard stats...');
      const response = await adminAPI.getDashboardStats();
      console.log('✅ Stats fetched:', response.data);
      setStats(response.data);
    } catch (error) {
      console.error('❌ Error fetching stats:', error);
      // Continue even if stats fail
    } finally {
      setLoading(false);
    }
  };

  const renderTabContent = () => {
    switch (activeTab) {
      case 'overview':
        return <AdminStats stats={stats} loading={loading} />;
      case 'users':
        return <UserManagement />;
      case 'tasks':
        return <TaskManagement />;
      default:
        return <AdminStats stats={stats} loading={loading} />;
    }
  };

  return (
    <div style={{ padding: '20px', minHeight: '100vh', backgroundColor: '#f5f5f5' }}>
      {/* Header */}
      <header style={{ 
        display: 'flex', 
        justifyContent: 'space-between', 
        alignItems: 'center',
        marginBottom: '30px',
        backgroundColor: 'white',
        padding: '20px',
        borderRadius: '8px',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
      }}>
        <div>
          <h1 style={{ margin: 0, color: '#333' }}>Admin Dashboard</h1>
          <p style={{ margin: '5px 0 0 0', color: '#666' }}>
            Welcome, {user?.username}!
            {user?.role && <span> (Role: {user.role})</span>}
          </p>
        </div>
        <button 
          onClick={logout}
          style={{ 
            padding: '8px 16px',
            backgroundColor: '#dc3545',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer'
          }}
        >
          Logout
        </button>
      </header>

      {/* Navigation Tabs */}
      <div style={{ 
        display: 'flex', 
        gap: '10px', 
        marginBottom: '20px',
        backgroundColor: 'white',
        padding: '15px',
        borderRadius: '8px',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
      }}>
        <button
          onClick={() => setActiveTab('overview')}
          style={{
            padding: '10px 20px',
            backgroundColor: activeTab === 'overview' ? '#007bff' : 'transparent',
            color: activeTab === 'overview' ? 'white' : '#007bff',
            border: '1px solid #007bff',
            borderRadius: '4px',
            cursor: 'pointer'
          }}
        >
          Overview
        </button>
        <button
          onClick={() => setActiveTab('users')}
          style={{
            padding: '10px 20px',
            backgroundColor: activeTab === 'users' ? '#007bff' : 'transparent',
            color: activeTab === 'users' ? 'white' : '#007bff',
            border: '1px solid #007bff',
            borderRadius: '4px',
            cursor: 'pointer'
          }}
        >
          User Management
        </button>
        <button
          onClick={() => setActiveTab('tasks')}
          style={{
            padding: '10px 20px',
            backgroundColor: activeTab === 'tasks' ? '#007bff' : 'transparent',
            color: activeTab === 'tasks' ? 'white' : '#007bff',
            border: '1px solid #007bff',
            borderRadius: '4px',
            cursor: 'pointer'
          }}
        >
          Task Management
        </button>
      </div>

      {/* Tab Content */}
      <div style={{
        backgroundColor: 'white',
        padding: '20px',
        borderRadius: '8px',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
        minHeight: '400px'
      }}>
        {renderTabContent()}
      </div>
    </div>
  );
};

// Admin Stats Component
const AdminStats = ({ stats, loading }) => {
  if (loading) {
    return <div style={{ textAlign: 'center', padding: '40px' }}>Loading statistics...</div>;
  }

  return (
    <div>
      <h2>System Overview</h2>
      <p>Welcome to the Admin Dashboard! You have administrator privileges.</p>
      <div style={{ 
        display: 'grid', 
        gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', 
        gap: '20px',
        marginTop: '20px'
      }}>
        <div style={{ backgroundColor: '#e3f2fd', padding: '20px', borderRadius: '8px', textAlign: 'center' }}>
          <h3 style={{ margin: 0, fontSize: '2em', color: '#1976d2' }}>
            {stats?.totalUsers || '-'}
          </h3>
          <p style={{ margin: 0 }}>Total Users</p>
        </div>
        <div style={{ backgroundColor: '#e8f5e9', padding: '20px', borderRadius: '8px', textAlign: 'center' }}>
          <h3 style={{ margin: 0, fontSize: '2em', color: '#388e3c' }}>
            {stats?.totalTasks || '-'}
          </h3>
          <p style={{ margin: 0 }}>Total Tasks</p>
        </div>
        <div style={{ backgroundColor: '#fff3e0', padding: '20px', borderRadius: '8px', textAlign: 'center' }}>
          <h3 style={{ margin: 0, fontSize: '2em', color: '#f57c00' }}>
            {stats?.activeUsers || '-'}
          </h3>
          <p style={{ margin: 0 }}>Active Users</p>
        </div>
        <div style={{ backgroundColor: '#fce4ec', padding: '20px', borderRadius: '8px', textAlign: 'center' }}>
          <h3 style={{ margin: 0, fontSize: '2em', color: '#c2185b' }}>
            {stats?.completedTasks || '-'}
          </h3>
          <p style={{ margin: 0 }}>Completed Tasks</p>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;