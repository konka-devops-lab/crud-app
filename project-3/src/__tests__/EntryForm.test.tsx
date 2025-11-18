import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import EntryForm from '../components/EntryForm';
import { addEntry } from '../api/entriesApi';

vi.mock('../api/entriesApi', () => ({
  addEntry: vi.fn(),
}));

describe('EntryForm Component', () => {
  const mockOnEntryAdded = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders form inputs', () => {
    render(<EntryForm onEntryAdded={mockOnEntryAdded} />);
    expect(screen.getByLabelText(/Amount:/)).toBeInTheDocument();
    expect(screen.getByLabelText(/Description:/)).toBeInTheDocument();
    expect(screen.getByLabelText(/Date:/)).toBeInTheDocument();
  });

  it('submits form with valid data', async () => {
    render(<EntryForm onEntryAdded={mockOnEntryAdded} />);
    
    const amountInput = screen.getByLabelText(/Amount:/);
    const descriptionInput = screen.getByLabelText(/Description:/);
    const dateInput = screen.getByLabelText(/Date:/);
    
    fireEvent.change(amountInput, { target: { value: '100' } });
    fireEvent.change(descriptionInput, { target: { value: 'Test Entry' } });
    fireEvent.change(dateInput, { target: { value: '2023-01-01' } });
    
    fireEvent.click(screen.getByText('Add Entry'));
    
    expect(addEntry).toHaveBeenCalledWith(100, 'Test Entry', '2023-01-01');
  });

  it('shows success message after successful submission', async () => {
    (addEntry as any).mockResolvedValueOnce({});
    
    render(<EntryForm onEntryAdded={mockOnEntryAdded} />);
    
    fireEvent.change(screen.getByLabelText(/Amount:/), { target: { value: '100' } });
    fireEvent.change(screen.getByLabelText(/Description:/), { target: { value: 'Test Entry' } });
    fireEvent.change(screen.getByLabelText(/Date:/), { target: { value: '2023-01-01' } });
    fireEvent.click(screen.getByText('Add Entry'));
    
    await screen.findByText('Record added successfully!');
  });
});