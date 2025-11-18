import React, { useState } from 'react';
import { Entry, MessageProps } from '../types';
import { deleteEntry, updateEntry } from '../api/entriesApi';
import Message from './Message';
import { Download, CreditCard as Edit, Save, X } from 'lucide-react';

interface EntriesTableProps {
  entries: Entry[];
  loading: boolean;
  onEntryDeleted: () => void;
}

const EntriesTable: React.FC<EntriesTableProps> = ({ entries = [], loading, onEntryDeleted }) => {
  const [message, setMessage] = useState<MessageProps>({ text: '', type: 'success', visible: false });
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editForm, setEditForm] = useState({ amount: '', description: '', date: '' });

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this record?')) {
      return;
    }

    try {
      await deleteEntry(id);
      onEntryDeleted();
      setMessage({ text: 'Record deleted successfully!', type: 'error', visible: true });
    } catch (error) {
      console.error('Error deleting entry:', error);
      setMessage({ text: 'Failed to delete record. Please try again.', type: 'error', visible: true });
    }
  };

  const handleEdit = (entry: Entry) => {
    setEditingId(entry.id);
    setEditForm({
      amount: entry.amount.toString(),
      description: entry.description,
      date: entry.date
    });
  };

  const handleUpdate = async (id: number) => {
    try {
      await updateEntry(id, Number(editForm.amount), editForm.description, editForm.date);
      setEditingId(null);
      onEntryDeleted(); // Reuse this to refresh the data
      setMessage({ text: 'Record updated successfully!', type: 'success', visible: true });
    } catch (error) {
      console.error('Error updating entry:', error);
      setMessage({ text: 'Failed to update record. Please try again.', type: 'error', visible: true });
    }
  };

  const handleCancelEdit = () => {
    setEditingId(null);
    setEditForm({ amount: '', description: '', date: '' });
  };

  const handleDownload = () => {
    const headers = ['ID', 'Amount', 'Description', 'Date'];
    const csvContent = [
      headers.join(','),
      ...entries.map(entry => [entry.id, entry.amount, entry.description, entry.date].join(','))
    ].join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', 'entries.csv');
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div className="flex-grow bg-[#1f4068] rounded-lg shadow-lg p-5 ml-5 max-w-[1000px] flex flex-col relative">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl mt-5">Entries</h2>
        <button
          onClick={handleDownload}
          className="flex items-center gap-2 p-2 bg-[#1f78ff] text-white border-none rounded cursor-pointer hover:bg-[#145fc4] transition-colors duration-200"
          disabled={!entries || entries.length === 0}
        >
          <Download size={18} />
          Download CSV
        </button>
      </div>
      
      {loading && (
        <div className="text-[#1f78ff] text-lg absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 z-10">
          Loading...
        </div>
      )}
      
      <div className="flex-grow mt-5 block overflow-hidden">
        <table className="w-full border-collapse bg-[#162447] rounded-lg table-fixed">
          <thead className="sticky top-0 bg-[#162447] z-[2]">
            <tr>
              <th className="border border-[#1b1b2f] p-3 text-base text-left bg-[#1f78ff] text-white font-bold">ID</th>
              <th className="border border-[#1b1b2f] p-3 text-base text-left bg-[#1f78ff] text-white font-bold">Amount</th>
              <th className="border border-[#1b1b2f] p-3 text-base text-left bg-[#1f78ff] text-white font-bold">Description</th>
              <th className="border border-[#1b1b2f] p-3 text-base text-left bg-[#1f78ff] text-white font-bold">Date</th>
              <th className="border border-[#1b1b2f] p-3 text-base text-left bg-[#1f78ff] text-white font-bold">Actions</th>
            </tr>
          </thead>
        </table>
        
        <div className="max-h-[400px] overflow-y-auto block">
          <table className="w-full border-collapse bg-[#162447] rounded-lg table-fixed">
            <tbody className={`${loading ? 'opacity-50' : 'opacity-100'} transition-opacity duration-300`}>
              {!entries || entries.length === 0 ? (
                <tr>
                  <td colSpan={5} className="border border-[#1b1b2f] p-3 text-base text-center text-white">
                    No entries found
                  </td>
                </tr>
              ) : (
                entries.map((entry) => (
                  <tr key={entry.id}>
                    <td className="border border-[#1b1b2f] p-3 text-base text-left text-white">{entry.id}</td>
                    <td className="border border-[#1b1b2f] p-3 text-base text-left text-white">
                      {editingId === entry.id ? (
                        <input
                          type="number"
                          value={editForm.amount}
                          onChange={(e) => setEditForm({ ...editForm, amount: e.target.value })}
                          className="w-full p-1 bg-[#1b1b2f] text-white border border-[#162447] rounded"
                        />
                      ) : (
                        entry.amount
                      )}
                    </td>
                    <td className="border border-[#1b1b2f] p-3 text-base text-left text-white">
                      {editingId === entry.id ? (
                        <input
                          type="text"
                          value={editForm.description}
                          onChange={(e) => setEditForm({ ...editForm, description: e.target.value })}
                          className="w-full p-1 bg-[#1b1b2f] text-white border border-[#162447] rounded"
                        />
                      ) : (
                        entry.description
                      )}
                    </td>
                    <td className="border border-[#1b1b2f] p-3 text-base text-left text-white">
                      {editingId === entry.id ? (
                        <input
                          type="date"
                          value={editForm.date}
                          onChange={(e) => setEditForm({ ...editForm, date: e.target.value })}
                          className="w-full p-1 bg-[#1b1b2f] text-white border border-[#162447] rounded"
                        />
                      ) : (
                        new Date(entry.date).toLocaleDateString()
                      )}
                    </td>
                    <td className="border border-[#1b1b2f] p-3 text-base text-left text-white">
                      <div className="flex gap-2">
                        {editingId === entry.id ? (
                          <>
                            <button
                              onClick={() => handleUpdate(entry.id)}
                              className="flex items-center justify-center p-2 bg-green-600 text-white border-none rounded cursor-pointer hover:bg-green-700 transition-colors duration-200 flex-1"
                            >
                              <Save size={16} />
                            </button>
                            <button
                              onClick={handleCancelEdit}
                              className="flex items-center justify-center p-2 bg-gray-600 text-white border-none rounded cursor-pointer hover:bg-gray-700 transition-colors duration-200 flex-1"
                            >
                              <X size={16} />
                            </button>
                          </>
                        ) : (
                          <>
                            <button
                              onClick={() => handleEdit(entry)}
                              className="flex items-center justify-center p-2 bg-yellow-600 text-white border-none rounded cursor-pointer hover:bg-yellow-700 transition-colors duration-200 flex-1"
                            >
                              <Edit size={16} />
                            </button>
                            <button
                              onClick={() => handleDelete(entry.id)}
                              className="flex items-center justify-center p-2 bg-red-600 text-white border-none rounded cursor-pointer hover:bg-red-700 transition-colors duration-200 flex-1"
                            >
                              Delete
                            </button>
                          </>
                        )}
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
      
      <div className="mt-3">
        <Message message={message} setMessage={setMessage} />
      </div>
    </div>
  );
};

export default EntriesTable;