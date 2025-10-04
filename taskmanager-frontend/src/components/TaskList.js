// components/TaskList.js - Update the TaskForm usage
import React, { useState } from 'react'; // ✅ ADD THIS IMPORT
import { taskAPI } from '../services/api';
import TaskForm from './TaskForm';

const TaskList = ({ tasks, onTaskUpdated, onTaskDeleted }) => {
  const [editingTask, setEditingTask] = useState(null);

  const handleDelete = async (taskId) => {
    if (window.confirm('Are you sure you want to delete this task?')) {
      try {
        await taskAPI.deleteTask(taskId);
        onTaskDeleted(taskId);
      } catch (error) {
        console.error('Error deleting task:', error);
        alert('Failed to delete task');
      }
    }
  };

  const handleEdit = (task) => {
    setEditingTask(task);
  };

  const handleTaskUpdated = (updatedTask) => {
    onTaskUpdated(updatedTask);
    setEditingTask(null);
  };

  if (tasks.length === 0) {
    return (
      <div style={{ 
        textAlign: 'center', 
        padding: '40px',
        color: '#6c757d'
      }}>
        <h3>No tasks yet</h3>
        <p>Create your first task to get started!</p>
      </div>
    );
  }

  return (
    <div>
      {editingTask && (
        <TaskForm
          initialData={editingTask}
          isEditing={true} // ✅ ADD THIS PROP
          onTaskUpdated={handleTaskUpdated} // ✅ CHANGE THIS from onTaskCreated
          onCancel={() => setEditingTask(null)}
        />
      )}

      <div style={{ display: 'grid', gap: '15px' }}>
        {tasks.map(task => (
          <div 
            key={task.id}
            style={{
              border: '1px solid #ddd',
              borderRadius: '8px',
              padding: '15px',
              backgroundColor: '#f8f9fa'
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
              <div style={{ flex: 1 }}>
                <h4 style={{ margin: '0 0 10px 0' }}>{task.title}</h4>
                {task.description && (
                  <p style={{ margin: '0 0 10px 0', color: '#555' }}>
                    {task.description}
                  </p>
                )}
                <small style={{ color: '#6c757d' }}>
                  Created: {new Date(task.createdAt).toLocaleDateString()}
                </small>
              </div>
              
              <div style={{ display: 'flex', gap: '10px' }}>
                <button 
                  onClick={() => handleEdit(task)}
                  style={{
                    padding: '5px 10px',
                    backgroundColor: '#ffc107',
                    color: 'black',
                    border: 'none',
                    borderRadius: '4px',
                    cursor: 'pointer'
                  }}
                >
                  Edit
                </button>
                
                <button 
                  onClick={() => handleDelete(task.id)}
                  style={{
                    padding: '5px 10px',
                    backgroundColor: '#dc3545',
                    color: 'white',
                    border: 'none',
                    borderRadius: '4px',
                    cursor: 'pointer'
                  }}
                >
                  Delete
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default TaskList;