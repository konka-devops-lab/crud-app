import React, { useState } from 'react';
import { Entry, MessageProps } from '../types';
import { deleteEntry, deleteAll } from '../api/entriesApi';
import Message from './Message';
import { Download } from 'lucide-react';

interface EntriesTableProps {
  entries: Entry[];
  loading: boolean;
  onEntryDeleted: () => void;
  amount: number | string;
  setAmount: React.Dispatch<React.SetStateAction<number | string>>;
  description: string;
  setDescription: React.Dispatch<React.SetStateAction<string>>;
  date: string;
  setDate: React.Dispatch<React.SetStateAction<string>>;
}

const EntriesTable: React.FC<EntriesTableProps> = ({ entries = [], loading, onEntryDeleted, amount, setAmount, description, setDescription, date, setDate }) => {
  const [message, setMessage] = useState<MessageProps>({ text: '', type: 'success', visible: false });
  const [enableUpdate, setEnableUpdate] = useState<boolean>(false);

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
  const handleDeleteAll = async () => {
    if (!window.confirm('Are you sure you want to delete all records?')) {
      return;
    }
    try {
      await deleteAll();
      onEntryDeleted();
      setMessage({ text: 'All Records deleted successfully!', type: 'error', visible: true });
    } catch (error) {
      console.error('Error deleting all records:', error);
      setMessage({ text: 'Failed to delete records. Please try again.', type: 'error', visible: true });
    }
  };
  const handleUpdate = async (id: number) => {
    if (!window.confirm('Are you sure you want to update this records?')) {
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
  const handleDownload = () => {
    const headers = ['ID', 'Amount', 'Description', 'Date'];
    const csvContent = [
      headers.join(','),
      ...entries.map(entry => [entry.id, entry.amount, entry.description, entry.data].join(','))
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
          onClick={handleDeleteAll}
          className="flex items-center gap-2 p-2 bg-[#1f78ff] text-white border-none rounded cursor-pointer hover:bg-[#145fc4] transition-colors duration-200"
          disabled={!entries || entries.length === 0}
        >
          Delete All
        </button>
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
              <th className="border border-[#1b1b2f] p-3 text-base text-left bg-[#1f78ff] text-white font-bold">Action</th>
            </tr>
          </thead>
        </table>
        
        <div className="max-h-[400px] overflow-y-auto block">
          <table className="w-full border-collapse bg-[#162447] rounded-lg table-fixed">
            <tbody className={`${loading ? 'opacity-50' : 'opacity-100'} transition-opacity duration-300`}>
              {!entries || entries.length === 0 ? (
                <tr>
                  <td colSpan={4} className="border border-[#1b1b2f] p-3 text-base text-center text-white">
                    No entries found
                  </td>
                </tr>
              ) : (
                entries.map((entry) => (
                  <tr key={entry.id}>
                    <td className="border border-[#1b1b2f] p-3 text-base text-left text-white">{entry.id}</td>
                    <td className="border border-[#1b1b2f] p-3 text-base text-left text-white">
                     !enableUpdate ? {entry.amount} :
                     <input
                        type="number"
                        id="amount"
                        value={entry.amount}
                        onChange={(e) => setAmount(e.target.value)}
                        className="my-3 p-3.5 w-full text-base border border-[#162447] rounded bg-[#1b1b2f] text-white focus:border-[#1f78ff] focus:outline-none"
                      />
                    </td>
                    <td className="border border-[#1b1b2f] p-3 text-base text-left text-white">
                      !enableUpdate ? {entry.description} : 
                       <input
                        type="string"
                        id="description"
                        value={entry.description}
                        onChange={(e) => setDescription(e.target.value)}
                        className="my-3 p-3.5 w-full text-base border border-[#162447] rounded bg-[#1b1b2f] text-white focus:border-[#1f78ff] focus:outline-none"
                      />
                      </td>
                    <td className="border border-[#1b1b2f] p-3 text-base text-left text-white">
                      {!enableUpdate ? `${entry.date}` : 
                      <input
                        type="string"
                        id="date"
                        value={entry.date}
                        onChange={(e) => setDate(e.target.value)}
                        className="my-3 p-3.5 w-full text-base border border-[#162447] rounded bg-[#1b1b2f] text-white focus:border-[#1f78ff] focus:outline-none"
                      />}
                    </td>
                    }
                    <td className="border border-[#1b1b2f] p-3 text-base text-left text-white">
                      <button
                        onClick={() => {handleUpdate(entry.id); setEnableUpdate(true);}}
                        className="p-2 bg-[#1f78ff] text-white border-none rounded cursor-pointer hover:bg-[#145fc4] transition-colors duration-200 w-full"
                      >
                        {enableUpdate ? "Save": "Update"}
                      </button>
                      <button
                        onClick={() => handleDelete(entry.id)}
                        className="p-2 bg-[#1f78ff] text-white border-none rounded cursor-pointer hover:bg-[#145fc4] transition-colors duration-200 w-full"
                      >
                        Delete
                      </button>
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